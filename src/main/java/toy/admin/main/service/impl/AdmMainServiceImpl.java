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
import toy.com.vo.common.AdminLoginResult;
import toy.com.vo.common.SessionAdminVO;
import toy.com.vo.system.mngr.MngrVO;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

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

        // 5) Authorization: fetch ALL auths for session
        // TODO: If you ever need "login requires a specific role", implement it server-side (whitelist) and NOT from client input.

        MngrVO authQueryVO = new MngrVO();
        authQueryVO.setMngrUid(mngrVO.getMngrUid());
        authQueryVO.setAuthUuid(null); // IMPORTANT: fetch all roles

        List<String> authListAllRaw = admMainDAO.selectAdminUserAuthList(authQueryVO);
        List<String> authListAll = normalizeAuthList(authListAllRaw);

        // If the account has no active roles, do not allow login.
        if (authListAll == null || authListAll.isEmpty()) {
            result.setSuccess(false);
            result.setLocked(false);
            result.setReasonCode("NO_AUTH");
            result.setMessageCode("admin.login.fail.noAuth");
            return result;
        }


        // Put full auth list into session VO for later permission checks
        sessionUserVO.setAuth(authListAll);


        // Set a display-only role label for header UI (do not depend on session-based utils during login)
        if (authListAll.contains("ADMINISTRATOR")) {
            sessionUserVO.setDisplayRoleName("Administrator");
        } else if (!authListAll.isEmpty()) {
            sessionUserVO.setDisplayRoleName(formatDisplayRoleName(authListAll));
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

    private String formatDisplayRoleName(List<String> authListAll) {
        // Display: "<firstRole>" or "<firstRole> (and N more)"
        String first = authListAll.get(0);
        long distinctCount = authListAll.stream()
                .filter(v -> v != null && !v.trim().isEmpty())
                .map(String::trim)
                .map(String::toUpperCase)
                .distinct()
                .count();
        long more = Math.max(0, distinctCount - 1);
        return (more == 0) ? first : (first + " (and " + more + " more)");
    }

    private List<String> normalizeAuthList(List<String> raw) {
        if (raw == null) {
            return new ArrayList<>();
        }
        // Normalize: trim + uppercase + distinct (preserve order)
        return raw.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .map(String::toUpperCase)
                .distinct()
                .collect(Collectors.toList());
    }

}
