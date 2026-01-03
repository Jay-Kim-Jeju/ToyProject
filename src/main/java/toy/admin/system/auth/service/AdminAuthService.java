package toy.admin.system.auth.service;

import toy.com.vo.system.auth.AdminAuthBatchResult;
import toy.com.vo.system.auth.AuthVO;
import toy.com.vo.system.mngr.MngrVO;

import java.util.List;

public interface AdminAuthService {

    // Result codes (align with AdminCodeService style)
    int RESULT_OK = 1;
    int RESULT_FAIL = 0;
    int RESULT_DUPLE = -1;
    int RESULT_INVALID = -2;
    int RESULT_FORBIDDEN = -3;

    /* =========================
     * Auth Role (TAUTH)
     * ========================= */
    List<AuthVO> selectAdminAuthRoleList(AuthVO searchVO) throws Exception;

    int selectAdminAuthRoleListCnt(AuthVO searchVO) throws Exception;

    AuthVO selectAdminAuthRoleDetail(String authUuid) throws Exception;

    int insertAdminAuthRole(AuthVO vo) throws Exception;

    int updateAdminAuthRole(AuthVO vo) throws Exception;

    int disableAdminAuthRole(String authUuid) throws Exception;

    /* =========================
     * Auth Assignment (TAUTH_MNGR)
     * ========================= */
    List<MngrVO> selectAdminAuthAssignedMngrList(MngrVO searchVO) throws Exception;

    int selectAdminAuthAssignedMngrListCnt(MngrVO searchVO) throws Exception;

    List<MngrVO> selectAdminAuthUnassignedMngrList(MngrVO searchVO) throws Exception;

    int selectAdminAuthUnassignedMngrListCnt(MngrVO searchVO) throws Exception;

    int insertAdminAuthMngr(String authUuid, String mngrUid) throws Exception;

    int deleteAdminAuthMngr(String authUuid, String mngrUid) throws Exception;

    AdminAuthBatchResult insertAdminAuthMngrList(String authUuid, List<String> mngrUidList) throws Exception;
}
