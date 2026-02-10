package toy.admin.my.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import toy.admin.my.dao.AdmMyMngrDAO;
import toy.admin.my.service.AdmMyMngrService;
import toy.com.common.sms.service.SmsService;
import toy.com.egov.EgovPropertiesUtils;
import toy.com.egov.EgovUserDetailsHelper;
import toy.com.util.CmConstants;
import toy.com.util.CmUtil;

import toy.com.vo.common.sms.SmsSendMsgVO;
import toy.com.vo.common.sms.SmsSendReqVO;
import toy.com.vo.common.sms.SmsSendResVO;
import toy.com.vo.system.mngr.MngrVO;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service("AdmMyMngrService")
@RequiredArgsConstructor
public class AdmMyMngrServiceImpl implements AdmMyMngrService {
    private final AdmMyMngrDAO admMyMngrDAO;
    private final SmsService smsService;

    /* Session keys (per popup session) */
    private static final String SESS_VERIF_CODE = "MY_VERIF_CODE";
    private static final String SESS_VERIF_EXPIRE_AT = "MY_VERIF_EXPIRE_AT";
    private static final String SESS_VERIF_OK = "MY_VERIF_OK";
    private static final String SESS_VERIF_FAIL_CNT = "MY_VERIF_FAIL_CNT";

    private static final int MAX_FAIL_COUNT = 5;

    private String getSessionMngrUidOrNull() {
        String uid = EgovUserDetailsHelper.getAdminUid();
        if (uid == null || uid.trim().isEmpty() || "anonymous".equals(uid)) {
            return null;
        }
        return uid;
    }

    private int getVerificationExpireSeconds() {
        String v = EgovPropertiesUtils.getOptionalProp("VERIFICATION_EXPIRE_SECONDS");
        try {
            int sec = Integer.parseInt(v);
            return sec > 0 ? sec : 300;
        } catch (Exception e) {
            return 300;
        }
    }

    private String generateVerificationNumber() {
        if (EgovPropertiesUtils.isLocal()) {
            String fixed = EgovPropertiesUtils.getOptionalProp("LOCAL_TEMP_VERIFICATION_NUMBER");
            if (fixed != null && fixed.trim().matches("^\\d{6}$")) {
                return fixed.trim();
            }
            // Local must be predictable for dev/testing
            log.warn("LOCAL_TEMP_VERIFICATION_NUMBER is missing/invalid. fallback to 123456 (local fixed).");
            return "123456";
        }
        int n = new Random().nextInt(1_000_000);
        return String.format("%06d", n);
    }

    @Override
    public MngrVO selectMyMngrDetail(HttpServletRequest request) throws Exception {
        String mngrUid = getSessionMngrUidOrNull();
        if (mngrUid == null) {
            return null;
        }
        return admMyMngrDAO.selectMyMngrDetail(mngrUid);
    }

    @Override
    public void resetMyVerificationState(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute(SESS_VERIF_CODE);
        session.removeAttribute(SESS_VERIF_EXPIRE_AT);
        session.removeAttribute(SESS_VERIF_OK);
        session.removeAttribute(SESS_VERIF_FAIL_CNT);
    }

    @Override
    public boolean isMyVerified(HttpServletRequest request) {
        Object ok = request.getSession().getAttribute(SESS_VERIF_OK);
        return ok != null && "Y".equals(String.valueOf(ok));
    }

    @Override
    public Map<String, Object> sendVerificationNumber(HttpServletRequest request) throws Exception {
        Map<String, Object> res = new HashMap<>();
        res.put("result", CmConstants.RESULT_FAIL);
        res.put("smsStatus", "FAIL");
        res.put("requestId", "");
        res.put("expireSeconds", getVerificationExpireSeconds());
        res.put("maxFailCount", MAX_FAIL_COUNT);

        String mngrUid = getSessionMngrUidOrNull();
        if (mngrUid == null) {
            res.put("result", CmConstants.RESULT_FORBIDDEN);
            return res;
        }

        // If already locked by max failures, allow resend immediately (this method is resend itself)
        // Always reset fail count on send attempt
        HttpSession session = request.getSession();
        session.setAttribute(SESS_VERIF_FAIL_CNT, 0);
        session.setAttribute(SESS_VERIF_OK, "N");

        // Load telno from DB (do not trust client)
        MngrVO cur = admMyMngrDAO.selectMyMngrDetail(mngrUid);
        if (cur == null || cur.getTelno() == null || cur.getTelno().trim().isEmpty()) {
            res.put("result", CmConstants.RESULT_INVALID);
            return res;
        }

        String code = generateVerificationNumber();
        int expireSeconds = getVerificationExpireSeconds();
        long expireAt = System.currentTimeMillis() + (expireSeconds * 1000L);

        // Local: skip actual SMS but behave as SKIPPED success
        if (EgovPropertiesUtils.isLocal()) {
            session.setAttribute(SESS_VERIF_CODE, code);
            session.setAttribute(SESS_VERIF_EXPIRE_AT, expireAt);

            res.put("result", CmConstants.RESULT_OK);
            res.put("smsStatus", "SKIPPED");
            res.put("expireAt", expireAt);
            return res;
        }

        // Build SMS
        SmsSendReqVO smsReq = new SmsSendReqVO();
        smsReq.setSubject("Verification");
        smsReq.setContent("Verification code: " + code);

        SmsSendMsgVO msg = new SmsSendMsgVO();
        msg.setTo(cur.getTelno());
        smsReq.getMessages().add(msg);

        SmsSendResVO smsRes = smsService.sendSMS(smsReq);
        String requestId = smsRes != null ? smsRes.getRequestId() : "";
        res.put("requestId", requestId);

        if (smsRes == null || !smsRes.isSuccess()) {
            // FAIL => client must NOT show input row
            log.warn("sendVerificationNumber failed. mngrUid={}, to={}, requestId={}",
                    mngrUid, cur.getTelno(), requestId);
            res.put("result", CmConstants.RESULT_FAIL);
            res.put("smsStatus", "FAIL");
            return res;
        }

        // Store code only when SMS succeeded
        session.setAttribute(SESS_VERIF_CODE, code);
        session.setAttribute(SESS_VERIF_EXPIRE_AT, expireAt);

        res.put("result", CmConstants.RESULT_OK);
        res.put("smsStatus", "OK");
        res.put("expireAt", expireAt);
        return res;
    }

    @Override
    public Map<String, Object> checkVerificationNumber(String inputCode, HttpServletRequest request) throws Exception {
        Map<String, Object> res = new HashMap<>();
        res.put("result", CmConstants.RESULT_FAIL);
        res.put("verifiedYn", "N");
        res.put("lockedYn", "N");
        res.put("failCount", 0);
        res.put("maxFailCount", MAX_FAIL_COUNT);

        String mngrUid = getSessionMngrUidOrNull();
        if (mngrUid == null) {
            res.put("result", CmConstants.RESULT_FORBIDDEN);
            return res;
        }

        HttpSession session = request.getSession();
        int failCount = 0;
        Object fc = session.getAttribute(SESS_VERIF_FAIL_CNT);
        if (fc != null) {
            try {
                failCount = Integer.parseInt(String.valueOf(fc));
            } catch (Exception ignored) {
                failCount = 0;
            }
        }

        if (failCount >= MAX_FAIL_COUNT) {
            res.put("result", CmConstants.RESULT_FORBIDDEN);
            res.put("lockedYn", "Y");
            res.put("failCount", failCount);
            return res;
        }

        Object expireObj = session.getAttribute(SESS_VERIF_EXPIRE_AT);
        Object codeObj = session.getAttribute(SESS_VERIF_CODE);
        if (expireObj == null || codeObj == null) {
            res.put("result", CmConstants.RESULT_INVALID);
            return res;
        }

        long expireAt;
        try {
            expireAt = Long.parseLong(String.valueOf(expireObj));
        } catch (Exception e) {
            res.put("result", CmConstants.RESULT_INVALID);
            return res;
        }

        long now = System.currentTimeMillis();
        if (now > expireAt) {
            res.put("result", CmConstants.RESULT_INVALID);
            return res;
        }

        String expected = String.valueOf(codeObj);
        String in = inputCode == null ? "" : inputCode.trim();

        if (!in.equals(expected)) {
            failCount++;
            session.setAttribute(SESS_VERIF_FAIL_CNT, failCount);

            res.put("result", CmConstants.RESULT_INVALID);
            res.put("failCount", failCount);
            if (failCount >= MAX_FAIL_COUNT) {
                res.put("lockedYn", "Y");
            }
            return res;
        }

        // Success
        session.setAttribute(SESS_VERIF_OK, "Y");

        res.put("result", CmConstants.RESULT_OK);
        res.put("verifiedYn", "Y");
        res.put("failCount", failCount);
        return res;
    }

    @Override
    public int updateMyMngrInfo(MngrVO vo, HttpServletRequest request) throws Exception {
        if (!isMyVerified(request)) {
            return CmConstants.RESULT_FORBIDDEN;
        }

        String mngrUid = getSessionMngrUidOrNull();
        if (mngrUid == null) {
            return CmConstants.RESULT_FORBIDDEN;
        }

        // Force self only
        vo.setMngrUid(mngrUid);
        vo.setMdfcnUid(mngrUid);

        try {
            return admMyMngrDAO.updateMyMngrInfo(vo);
        } catch (DataIntegrityViolationException e) {
            log.warn("updateMyMngrInfo constraint violation. mngrUid={}", mngrUid, e);
            return CmConstants.RESULT_INVALID;
        } catch (Exception e) {
            log.error("updateMyMngrInfo failed. mngrUid={}", mngrUid, e);
            return CmConstants.RESULT_FAIL;
        }
    }

    @Override
    public int changeMyPassword(String currentPassword, String newPassword, HttpServletRequest request) throws Exception {
        if (!isMyVerified(request)) {
            return CmConstants.RESULT_FORBIDDEN;
        }

        String mngrUid = getSessionMngrUidOrNull();
        if (mngrUid == null) {
            return CmConstants.RESULT_FORBIDDEN;
        }

        // Validate new password policy
        if (newPassword == null || !CmUtil.isPasswordValid(newPassword)) {
            return CmConstants.RESULT_INVALID;
        }

        String encKey = EgovPropertiesUtils.getProperty("DB.ENCRYPTION.KEY");

        // Check current password
        String storedEnc = admMyMngrDAO.selectMyPwdEncpt(mngrUid);
        if (storedEnc == null || storedEnc.trim().isEmpty()) {
            return CmConstants.RESULT_INVALID;
        }

        String curEnc = CmUtil.encryptPassword(currentPassword == null ? "" : currentPassword, encKey);
        if (!storedEnc.equals(curEnc)) {
            return CmConstants.RESULT_INVALID;
        }

        // Update to new password
        String newEnc = CmUtil.encryptPassword(newPassword, encKey);

        Map<String, Object> param = new HashMap<>();
        param.put("mngrUid", mngrUid);
        param.put("pwdEncpt", newEnc);
        param.put("mdfcnUid", mngrUid);

        try {
            int r = admMyMngrDAO.updateMyPassword(param);
            return r > 0 ? CmConstants.RESULT_OK : CmConstants.RESULT_FAIL;
        } catch (Exception e) {
            log.error("changeMyPassword failed. mngrUid={}", mngrUid, e);
            return CmConstants.RESULT_FAIL;
        }
    }
}
