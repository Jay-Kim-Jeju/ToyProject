package toy.admin.system.auth.dao;

import org.apache.ibatis.annotations.Param;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import toy.com.vo.system.auth.AdminAuthGuardVO;
import toy.com.vo.system.auth.AuthVO;
import toy.com.vo.system.mngr.MngrVO;


import java.util.List;

@Mapper("AdminAuthDAO")
public interface AdminAuthDAO {
    /* =========================
     * Auth Role (TAUTH) - List / Count / Detail
     * ========================= */

    List<AuthVO> selectAdminAuthRoleList(AuthVO searchVO);

    int selectAdminAuthRoleListCnt(AuthVO searchVO);

    AuthVO selectAdminAuthRoleDetail(@Param("authUuid") String authUuid);

    int insertAdminAuthRole(AuthVO vo);

    int updateAdminAuthRole(AuthVO vo);

    int disableAdminAuthRole(@Param("authUuid") String authUuid);

    /* =========================
     * Auth Assignment (TAUTH_MNGR) - Assigned / Unassigned managers
     * ========================= */

    List<MngrVO> selectAdminAuthAssignedMngrList(MngrVO searchVO);

    int selectAdminAuthAssignedMngrListCnt(MngrVO searchVO);

    List<MngrVO> selectAdminAuthUnassignedMngrList(MngrVO searchVO);

    int selectAdminAuthUnassignedMngrListCnt(MngrVO searchVO);

    /* =========================
     * Auth Assignment (TAUTH_MNGR) - Insert / Delete
     * ========================= */

    int insertAdminAuthMngr(@Param("authUuid") String authUuid,
                            @Param("mngrUid") String mngrUid);

    int deleteAdminAuthMngr(@Param("authUuid") String authUuid,
                            @Param("mngrUid") String mngrUid);

    /* =========================
     * Auth Guard (session revocation) - Auth change detection
     * ========================= */
    AdminAuthGuardVO selectAdminAuthGuard(@Param("mngrUid") String mngrUid);
}
