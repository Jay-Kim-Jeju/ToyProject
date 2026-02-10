package toy.admin.system.mngr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import toy.admin.system.mngr.dao.AdminManagerDAO;
import toy.admin.system.mngr.service.AdminManagerService;

import toy.com.common.sms.service.SmsService;
import toy.com.vo.common.sms.SmsSendMsgVO;
import toy.com.vo.common.sms.SmsSendReqVO;
import toy.com.vo.common.sms.SmsSendResVO;
import toy.com.egov.EgovPropertiesUtils;
import toy.com.egov.EgovUserDetailsHelper;
import toy.com.util.CmConstants;
import toy.com.util.CmUtil;
import toy.com.vo.system.mngr.AdminMngrSmsResult;
import toy.com.vo.system.mngr.AdminMngrVerifResult;
import toy.com.vo.system.mngr.MngrVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Service("AdminManagerService")
@RequiredArgsConstructor
public class AdminManagerServiceImpl implements AdminManagerService {


    private final AdminManagerDAO adminManagerDAO;
    private final SmsService smsService;

    @Override
    public List<MngrVO> selectMngrList(MngrVO searchVO) throws Exception {
        return adminManagerDAO.selectMngrList(searchVO);
    }

    @Override
    public int selectMngrListCount(MngrVO searchVO) throws Exception {
        return adminManagerDAO.selectMngrListCount(searchVO);
    }

    @Override
    public MngrVO selectMngrDetail(String mngrUid) throws Exception {
        MngrVO vo = new MngrVO();
        vo.setMngrUid(mngrUid);
        return adminManagerDAO.selectMngr(vo);
    }

    @Override
    public int updateMngr(MngrVO vo) throws Exception {
        vo.setMdfcnUid(EgovUserDetailsHelper.getAdminUid());
        try {
            return adminManagerDAO.updateMngr(vo);
        } catch (DataIntegrityViolationException e) {
            log.warn("updateMngr constraint violation. mngrUid={}", vo.getMngrUid(), e);
            return CmConstants.RESULT_INVALID;
        } catch (Exception e) {
            log.error("updateMngr failed. mngrUid={}", vo.getMngrUid(), e);
            return CmConstants.RESULT_FAIL;
        }
    }

    @Override
    public int softDeleteMngr(String mngrUid) throws Exception {
        MngrVO vo = new MngrVO();
        vo.setMngrUid(mngrUid);
        vo.setMdfcnUid(EgovUserDetailsHelper.getAdminUid());
        try {
            int r = adminManagerDAO.softDeleteMngr(vo);
            return (r > 0) ? r : CmConstants.RESULT_FAIL;
        } catch (DataIntegrityViolationException e) {
            log.warn("softDeleteMngr constraint violation. mngrUid={}", mngrUid, e);
            return CmConstants.RESULT_INVALID;
        } catch (Exception e) {
            log.error("softDeleteMngr failed. mngrUid={}", mngrUid, e);
            return CmConstants.RESULT_FAIL;
        }
    }



    @Override
    public AdminMngrSmsResult insertMngrAndSendTempPassword(MngrVO vo) throws Exception {
        AdminMngrSmsResult result = new AdminMngrSmsResult();
        String adminUid = EgovUserDetailsHelper.getAdminUid();
        // NOTE: regUid is only for insert. mdfcnUid is for updates.
        try {

            // Telno is required because we must send initial temp password by SMS.
            if (vo == null || vo.getTelno() == null || vo.getTelno().trim().isEmpty()) {
                result.setResult(CmConstants.RESULT_INVALID);
                return result;
            }

            MngrVO existing = adminManagerDAO.selectMngrByUserId(vo); // returns mngrUid/useYn
            if (existing != null && existing.getMngrUid() != null) {

                // Duplicate exists (active or inactive). Reactivation is handled in detail popup via update(useYn).
                result.setResult(CmConstants.RESULT_DUPLE);
                result.setExistingUseYn(existing.getUseYn());
                result.setMngrUid(existing.getMngrUid());
                return result;

            } else {
                // New insert
                vo.setRegUid(adminUid);
                vo.setUseYn("Y");

                // Generate temp password  (local/prod)
                String rawTempPwd;
                if (EgovPropertiesUtils.isLocal()) {
                    rawTempPwd = EgovPropertiesUtils.getOptionalProp("LOCAL_TEMP_ADMIN_PASSWORD");
                } else {
                    rawTempPwd = CmUtil.generateRandomPassword();
                }

                // Guard for misconfigured local password
                if (!CmUtil.isPasswordValid(rawTempPwd)) {
                    log.warn("TEMP_ADMIN_PASSWORD does not meet policy. mngrUid={}", vo.getMngrUid());
                    rawTempPwd = CmUtil.generateRandomPassword();
                }

                String encKey = EgovPropertiesUtils.getProperty("DB.ENCRYPTION.KEY");
                vo.setPwdEncpt(CmUtil.encryptPassword(rawTempPwd, encKey));
                int r0 = adminManagerDAO.insertMngr(vo);

                if (r0 <= 0) {
                    result.setResult(CmConstants.RESULT_FAIL);
                    return result;
                }

                // Send SMS (no rollback)
                SmsSendReqVO smsReq = new SmsSendReqVO();
                smsReq.setSubject("Temporary Password");
                smsReq.setContent(buildTempPasswordContent(vo.getMngrUid(), rawTempPwd));
                SmsSendMsgVO msg = new SmsSendMsgVO();
                msg.setTo(vo.getTelno());
                smsReq.getMessages().add(msg);

                result.setMngrUid(vo.getMngrUid());
                result.setPwdResetYn("Y"); // password was stored in DB at insert time
                try {
                    SmsSendResVO smsRes = smsService.sendSMS(smsReq);
                    result.setRequestId(smsRes.getRequestId());
                    if (smsRes.isSuccess() && "SKIPPED".equals(smsRes.getResultCode())) {
                        result.setResult(CmConstants.RESULT_OK);
                        result.setSmsStatus("SKIPPED");
                        return result;
                    }
                    if (smsRes.isSuccess()) {
                        result.setResult(CmConstants.RESULT_OK);
                        result.setSmsStatus("OK");
                        return result;
                    }
                    // SMS failed but insert + password are already done (no rollback by policy)
                    result.setResult(CmConstants.RESULT_OK);
                    result.setSmsStatus("FAIL");
                    return result;
                } catch (Exception smsEx) {
                    // Do NOT rollback insert for SMS errors
                    log.error("SMS send failed after insert. mngrUid={}, to={}", vo.getMngrUid(), vo.getTelno(), smsEx);
                    result.setResult(CmConstants.RESULT_OK);
                    result.setSmsStatus("FAIL");
                    return result;
                }
            }
        } catch (DuplicateKeyException e) {
            result.setResult(CmConstants.RESULT_DUPLE);
            return result;
        } catch (DataIntegrityViolationException e) {
            log.warn("insertMngrAndSendTempPassword  invalid. mngrUid={}", vo.getMngrUid(), e);
            result.setResult(CmConstants.RESULT_INVALID);
            return result;
        } catch (Exception e) {
            log.error("insertMngrAndSendTempPassword  failed. mngrUid={}", vo.getMngrUid(), e);
            result.setResult(CmConstants.RESULT_FAIL);
            return result;
        }

    }

    @Override
    public AdminMngrSmsResult resetPasswordAndSendSms(String mngrUid) throws Exception {

        AdminMngrSmsResult result = new AdminMngrSmsResult();
        // 1) Load manager to get telno
        MngrVO cur = selectMngrDetail(mngrUid);
        if (cur == null) {
            result.setResult(CmConstants.RESULT_INVALID);
            result.setSmsStatus("FAIL");
            return result;
        }
        if (cur.getTelno() == null || cur.getTelno().trim().isEmpty()) {
            // Do not reset password if we cannot deliver it.
            result.setResult(CmConstants.RESULT_INVALID);
            result.setSmsStatus("FAIL");
            return result;
        }

        // 2) Generate temp password
        String rawTempPwd;
        if (EgovPropertiesUtils.isLocal()) {
            rawTempPwd = EgovPropertiesUtils.getOptionalProp("LOCAL_TEMP_ADMIN_PASSWORD");
        } else {
            rawTempPwd = CmUtil.generateRandomPassword();
        }
        // Guard for misconfigured local password
        if (!CmUtil.isPasswordValid(rawTempPwd)) {
            log.warn("LOCAL_TEMP_ADMIN_PASSWORD does not meet policy. mngrUid={}", mngrUid);
            rawTempPwd = CmUtil.generateRandomPassword();
        }
        // 3) Encrypt + update DB password
        MngrVO up = new MngrVO();
        up.setMngrUid(mngrUid);
        up.setMdfcnUid(EgovUserDetailsHelper.getAdminUid());
        String encKey = EgovPropertiesUtils.getProperty("DB.ENCRYPTION.KEY");
        up.setPwdEncpt(CmUtil.encryptPassword(rawTempPwd, encKey));
        try {
            int r = adminManagerDAO.updateMngrChangePassword(up);
            if (r <= 0) {
                result.setResult(CmConstants.RESULT_FAIL);
                result.setSmsStatus("FAIL");
                return result;
            }
        } catch (Exception e) {
            log.error("resetPasswordAndSendSms password update failed. mngrUid={}", mngrUid, e);
            result.setResult(CmConstants.RESULT_FAIL);
            result.setSmsStatus("FAIL");
            return result;
        }

        // 4) Reload to get mdfcnDt for UI
        MngrVO after = selectMngrDetail(mngrUid);
        if (after != null) {
            result.setMdfcnDt(after.getMdfcnDt());
        }

        // 5) Send SMS (no rollback even if fails)
        SmsSendReqVO smsReq = new SmsSendReqVO();
        smsReq.setSubject("Temporary Password");
        smsReq.setContent(buildTempPasswordContent(mngrUid, rawTempPwd));
        SmsSendMsgVO msg = new SmsSendMsgVO();
        msg.setTo(cur.getTelno());
        smsReq.getMessages().add(msg);

        SmsSendResVO smsRes = smsService.sendSMS(smsReq);
        result.setRequestId(smsRes.getRequestId());
        if (smsRes.isSuccess() && "SKIPPED".equals(smsRes.getResultCode())) {
            result.setResult(CmConstants.RESULT_OK);
            result.setSmsStatus("SKIPPED");
            return result;
        }
        if (smsRes.isSuccess()) {
            result.setResult(CmConstants.RESULT_OK);
            result.setSmsStatus("OK");
            return result;
        }
        // SMS failed but password has already been reset (no rollback by policy)
        result.setResult(CmConstants.RESULT_OK);
        result.setSmsStatus("FAIL");
        return result;
    }




    /* ============== helper ============== */

    private String getSessionMngrUidOrNull() {
        String uid = EgovUserDetailsHelper.getAdminUid();
        if (uid == null || uid.trim().isEmpty() || "anonymous".equals(uid)) {
            return null;
        }
        return uid;
    }

    private boolean isSelfTarget(String targetMngrUid) {
        String sessionUid = getSessionMngrUidOrNull();
        return sessionUid != null && sessionUid.equals(targetMngrUid);
    }

    private String buildTempPasswordContent(String mngrUid, String tempPwd) {
        return "ID: " + mngrUid + "\n"
                + "Temp PW: " + tempPwd + "\n"
                + "Please log in and change your password.";
    }



}
