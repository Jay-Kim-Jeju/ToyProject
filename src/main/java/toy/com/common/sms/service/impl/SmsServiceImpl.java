package toy.com.common.sms.service.impl;

import org.apache.log4j.Logger;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import toy.com.common.sms.service.SmsService;
import toy.com.egov.EgovPropertiesUtils;
import toy.com.vo.common.sms.SmsSendMsgVO;
import toy.com.vo.common.sms.SmsSendReqVO;
import toy.com.vo.common.sms.SmsSendResVO;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service("SmsService")
public class SmsServiceImpl  extends EgovAbstractServiceImpl implements SmsService {

    private static final Logger LOGGER = Logger.getLogger(SmsServiceImpl.class);
    private static final String DEFAULT_HOST = "https://sens.apigw.ntruss.com";
    private static final String DEFAULT_TYPE = "LMS";
    private static final String DEFAULT_CONTENT_TYPE = "COMM";

    @Override
    public SmsSendResVO sendSMS(String to, String subject, String content) {
        SmsSendReqVO req = new SmsSendReqVO();
        req.setSubject(subject);
        req.setContent(content);
        SmsSendMsgVO msg = new SmsSendMsgVO();
        msg.setTo(to);
        req.getMessages().add(msg);
        return sendSMS(req);
    }

    @Override
    public SmsSendResVO sendSMS(SmsSendReqVO reqVO) {
        SmsSendResVO res = new SmsSendResVO();
        // 1) Local environment: skip real SMS sending
        if (EgovPropertiesUtils.isLocal()) {
            res.setSuccess(true);
            res.setResultCode("SKIPPED");
            res.setResultMessage("Skipped in local environment.");
            return res;
        }
        // 2) Required properties (do not hardcode secrets)
        String accessKey = getRequiredProp("SMS_ACCESS_KEY");
        String secretKey = getRequiredProp("SMS_SECRET_KEY");
        String serviceId = getRequiredProp("SMS_SERVICE_ID");
        String fromTel = getRequiredProp("SMS_FROM_TEL");
        String host = EgovPropertiesUtils.getOptionalPropOrDefault("SMS_HOST_URL", DEFAULT_HOST);
        String method = "POST";
        String timestamp = Long.toString(System.currentTimeMillis());
        String requestPath = "/sms/v2/services/" + serviceId + "/messages";
        String apiUrl = host + requestPath;
        try {
            // 3) Build request body JSON
            JSONObject bodyJson = new JSONObject();
            JSONArray msgArr = new JSONArray();
            if (reqVO.getMessages() == null || reqVO.getMessages().isEmpty()) {
                res.setSuccess(false);
                res.setResultCode("INVALID");
                res.setResultMessage("SMS messages is empty.");
                return res;
            }
            for (SmsSendMsgVO m : reqVO.getMessages()) {
                JSONObject msgJson = new JSONObject(); // IMPORTANT: create per loop
                msgJson.put("to", normalizePhone(m.getTo()));
                // If provider supports per-message override, you can use it.
                // Otherwise, keep common content/subject at root level.
                if (m.getContent() != null && !m.getContent().trim().isEmpty()) {
                    msgJson.put("content", m.getContent());
                }
                msgArr.add(msgJson);
            }
            bodyJson.put("messages", msgArr);
            bodyJson.put("subject", nullToEmpty(reqVO.getSubject()));
            bodyJson.put("content", nullToEmpty(reqVO.getContent()));
            bodyJson.put("type", isEmpty(reqVO.getType()) ? DEFAULT_TYPE : reqVO.getType());
            bodyJson.put("contentType", isEmpty(reqVO.getContentType()) ? DEFAULT_CONTENT_TYPE : reqVO.getContentType());
            bodyJson.put("from", normalizePhone(isEmpty(reqVO.getFrom()) ? fromTel : reqVO.getFrom()));
            if (!isEmpty(reqVO.getReserveTime())) {
                bodyJson.put("reserveTime", reqVO.getReserveTime());
            }
            if (!isEmpty(reqVO.getReserveTimeZone())) {
                bodyJson.put("reserveTimeZone", reqVO.getReserveTimeZone());
            }
            if (!isEmpty(reqVO.getScheduleCode())) {
                bodyJson.put("scheduleCode", reqVO.getScheduleCode());
            }
            String body = bodyJson.toJSONString();
            // 4) HTTP call
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod(method);
            con.setRequestProperty("content-type", "application/json; charset=UTF-8");
            con.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
            con.setRequestProperty("x-ncp-iam-access-key", accessKey);
            con.setRequestProperty("x-ncp-apigw-signature-v2", makeSignature(requestPath, timestamp, method, accessKey, secretKey));
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(body.getBytes(StandardCharsets.UTF_8));
                wr.flush();
            }
            int httpStatus = con.getResponseCode();
            String responseBody = readResponse(con, httpStatus);
            // 5) Parse response
            res.setSuccess(httpStatus == 202);
            res.setResultCode(String.valueOf(httpStatus));
            res.setResultMessage(res.isSuccess() ? "OK" : "FAIL");
            // Try to parse requestId if response is JSON
            try {
                Object parsed = new JSONParser().parse(responseBody);
                if (parsed instanceof JSONObject) {
                    JSONObject jo = (JSONObject) parsed;
                    Object requestId = jo.get("requestId");
                    if (requestId != null) {
                        res.setRequestId(String.valueOf(requestId));
                    }
                    // Some providers use statusCode / statusName / statusMessage
                    Object statusMessage = jo.get("statusMessage");
                    if (statusMessage != null) {
                        res.setResultMessage(String.valueOf(statusMessage));
                    }
                    Object statusCode = jo.get("statusCode");
                    if (statusCode != null) {
                        res.setResultCode(String.valueOf(statusCode));
                    }
                }
            } catch (Exception ignore) {
                // Ignore parse errors; keep http status-based result
            }
            // 6) Logging (Do not log content, because it can contain temp password)
            String toMasked = maskPhone(reqVO.getMessages().get(0).getTo());
            LOGGER.info("[SMS] httpStatus=" + httpStatus
                    + ", to=" + toMasked
                    + ", requestId=" + nullToEmpty(res.getRequestId())
                    + ", resultCode=" + nullToEmpty(res.getResultCode())
                    + ", resultMessage=" + nullToEmpty(res.getResultMessage()));
            if (!res.isSuccess()) {
                LOGGER.warn("[SMS] responseBody=" + responseBody);
            }
            return res;
        } catch (Exception e) {
            res.setSuccess(false);
            res.setResultCode("EXCEPTION");
            res.setResultMessage(e.getMessage());
            LOGGER.error("[SMS] exception occurred", e);
            return res;
        }
    }

    private String makeSignature(String url, String timestamp, String method, String accessKey, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String message = method + space + url + newLine + timestamp + newLine + accessKey;
        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    private String readResponse(HttpURLConnection con, int httpStatus) throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(httpStatus == 202 ? con.getInputStream() : con.getErrorStream(), StandardCharsets.UTF_8)
        )) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    private String normalizePhone(String tel) {
        if (tel == null) {
            return "";
        }
        return tel.replace("-", "").trim();
    }

    private String maskPhone(String tel) {
        if (tel == null) {
            return "";
        }
        String n = normalizePhone(tel);
        if (n.length() < 4) {
            return "****";
        }
        return "****" + n.substring(n.length() - 4);
    }

    private String getRequiredProp(String key) {
        String v = EgovPropertiesUtils.getOptionalProp(key);
        if (v == null || v.trim().isEmpty() || "99".equals(v.trim())) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return v.trim();
    }

    private boolean isEmpty(String v) {
        return v == null || v.trim().isEmpty();
    }

    private String nullToEmpty(String v) {
        return v == null ? "" : v;
    }

}
