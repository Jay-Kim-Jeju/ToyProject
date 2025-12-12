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
import toy.com.vo.system.MngrVO;

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

        // 1. 관리자 계정 조회
        SessionAdminVO sessionUserVO = admMainDAO.selectAdminUserLogin(mngrVO);

        if (sessionUserVO == null) {
            // 아이디가 없거나, USE_YN != 'Y' 인 경우
            // 실패 카운트 증가 시도 (존재하지 않는 ID면 영향 없음)
            admMainDAO.updateLoginFailrCo(mngrVO.getMngrUid());

            result.setSuccess(false);
            result.setReasonCode("ID_PW_MISMATCH");
            result.setFailCount(0);
            return result;
        }

        // 2. 현재 실패 카운트 파싱
        int failCnt = 0;
        try {
            String failStr = sessionUserVO.getLgnFailrNumtm();
            if (failStr != null && failStr.trim().length() > 0) {
                failCnt = Integer.parseInt(failStr);
            }
        } catch (NumberFormatException e) {
            failCnt = 0; // parse 실패하면 0으로 가정
        }

        // 3. 잠금 여부 체크 (5회 이상 실패)
        if (failCnt >= 5) {
            result.setSuccess(false);
            result.setLocked(true);
            result.setReasonCode("LOCKED");
            return result;
        }

        // 4. 비밀번호 검증
        String encryptPw = CmUtil.encryptPassword(
                mngrVO.getPwdEncpt(),
                EgovPropertiesUtils.getProperty("DB.ENCRYPTION.KEY")
        );
        LOG_DEBUG.debug("encrypted password: {}", encryptPw);

        if (!encryptPw.equals(sessionUserVO.getPwdEncpt())) {
            // 비밀번호 불일치 → 실패 카운트 증가
            admMainDAO.updateLoginFailrCo(mngrVO.getMngrUid());

            result.setSuccess(false);
            result.setReasonCode("ID_PW_MISMATCH");
            // failCnt + 1 이지만 DB 에서 실제 값이 관리되므로 여기서는 그대로 두어도 괜찮음
            return result;
        }

        // 5. 로그인 성공 처리
        List<String> authList = admMainDAO.selectAdminUserAuthList(mngrVO);
        if (authList == null || authList.isEmpty()) {
            authList.add("LOGIN");
        }
        sessionUserVO.setAuth(authList);

        // 최종 로그인 시간 & 실패 횟수 초기화
        admMainDAO.updateLastLogin(mngrVO.getMngrUid());

        result.setSuccess(true);
        result.setLocked(false);
        result.setReasonCode("SUCCESS");
        result.setSessionUser(sessionUserVO);


        return result;
    }

    public void updateLoginFailrCo(String mngrId) {
        this.admMainDAO.updateLoginFailrCo(mngrId);
    }

}
