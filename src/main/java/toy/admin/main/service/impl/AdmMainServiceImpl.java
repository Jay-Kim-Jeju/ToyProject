package toy.admin.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import toy.admin.main.dao.AdmMainDAO;
import toy.admin.main.service.AdmMainService;
import toy.com.egov.EgovPropertiesUtils;
import toy.com.util.CmUtil;
import toy.com.util.ToyAdminAuthUtils;
import toy.com.vo.common.AdminLoginResult;
import toy.com.vo.common.SessionAdminVO;
import toy.com.vo.system.mngr.MngrVO;

import javax.annotation.Resource;
import java.util.List;

@Service("AdmMainService")
@RequiredArgsConstructor
public class AdmMainServiceImpl extends EgovAbstractServiceImpl implements AdmMainService {

    private static final Logger LOG_DEBUG = LoggerFactory.getLogger(AdmMainServiceImpl.class);

    @Resource(name = "AdmMainDAO")
    private final AdmMainDAO admMainDAO;

    public AdminLoginResult adminLogin(MngrVO mngrVO) throws Exception {
        AdminLoginResult result = new AdminLoginResult();

        // 1) Load account (TMNGR) only (no auth join here)
        SessionAdminVO sessionUserVO = admMainDAO.selectAdminUserLogin(mngrVO);

        if (sessionUserVO == null) {
            // Account not found or inactive (USE_YN != 'Y')
            // Increase fail count attempt (no effect if the ID does not exist)
            admMainDAO.updateLoginFailCo(mngrVO.getMngrUid());

            result.setSuccess(false);
            result.setLocked(false);
            result.setReasonCode("ID_PW_MISMATCH");
            result.setMessageCode("admin.login.fail.mismatchOrNoAuth"); // CHANGED
            //result.setFailCount(0);
            return result;
        }

        // 2. Parse fail count
        int failCnt = 0;
        try {
            String failStr = sessionUserVO.getLgnFailrNumtm();
            if (failStr != null && !failStr.trim().isEmpty()) {
                failCnt = Integer.parseInt(failStr);
            }
        } catch (NumberFormatException e) {
            failCnt = 0; // parse 실패하면 0으로 가정
        }

        // 3. Lock check
        if (failCnt >= 5) {
            result.setSuccess(false);
            result.setLocked(true);
            result.setReasonCode("LOCKED");
            result.setMessageCode("admin.login.fail.locked"); // CHANGED
            return result;
        }

        // 4. Password verification
        String encryptPw = CmUtil.encryptPassword(
                mngrVO.getPwdEncpt(),
                EgovPropertiesUtils.getProperty("DB.ENCRYPTION.KEY")
        );
        LOG_DEBUG.debug("encrypted password: {}", encryptPw);

        if (!encryptPw.equals(sessionUserVO.getPwdEncpt())) {
            // increase fail count in DB
            admMainDAO.updateLoginFailCo(mngrVO.getMngrUid());

            // If this attempt reaches the lock threshold, return LOCKED immediately
            int nextFailCnt = failCnt + 1;
            if (nextFailCnt >= 5) {
                result.setSuccess(false);
                result.setLocked(true);
                result.setReasonCode("LOCKED");
                result.setMessageCode("admin.login.fail.locked");
                return result;
            }

            result.setSuccess(false);
            result.setLocked(false);
            result.setReasonCode("ID_PW_MISMATCH");
            result.setMessageCode("admin.login.fail.mismatch");
            return result;
        }

        // 5) Authorization: fetch ALL auths and optionally verify a required authUuid
        String requiredAuthUuid = (mngrVO.getAuthUuid() == null) ? "" : mngrVO.getAuthUuid().trim();

        MngrVO authQueryVO = new MngrVO();
        authQueryVO.setMngrUid(mngrVO.getMngrUid());
        authQueryVO.setAuthUuid(null); // IMPORTANT: fetch all roles

        List<String> authListAll = admMainDAO.selectAdminUserAuthList(authQueryVO);

        boolean requiresAuth = !requiredAuthUuid.isEmpty();
        boolean hasRequiredAuth = !requiresAuth || (authListAll != null && authListAll.contains(requiredAuthUuid));

        if (!hasRequiredAuth) {
            // Password is correct, but the required auth is missing
            result.setSuccess(false);
            result.setLocked(false);
            result.setReasonCode("ID_PW_OR_NO_AUTH");
            result.setMessageCode("admin.login.fail.mismatchOrNoAuth");
            return result;
        }
        // Put full auth list into session VO for later permission checks
        sessionUserVO.setAuth(authListAll);
        // Set a display-only role label for header UI.
        if (ToyAdminAuthUtils.isAdmin()) {
            sessionUserVO.setDisplayRoleName("Administrator");
        } else if (ToyAdminAuthUtils.isGuest()) {
            sessionUserVO.setDisplayRoleName("Guest");
        } else if (authListAll != null && !authListAll.isEmpty()) {
            sessionUserVO.setDisplayRoleName(authListAll.get(0)); // simple label (or fetch description later)
        }

        // 6) Success: reset fail count and update last login time
        admMainDAO.updateLastLogin(mngrVO.getMngrUid());

        result.setSuccess(true);
        result.setLocked(false);
        result.setReasonCode("SUCCESS");
        result.setMessageCode("admin.login.success");
        result.setSessionUser(sessionUserVO);

        return result;
    }

    public void updateLoginFailCo(String mngrId) {
        this.admMainDAO.updateLoginFailCo(mngrId);
    }

}
