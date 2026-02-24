package toy.admin.system.allow.dao;

import org.apache.ibatis.annotations.Param;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import toy.com.vo.system.allow.AdminAllowIpVO;

import java.util.List;

@Mapper("AdminAllowIpDAO")
public interface AdminAllowIpDAO {

    /* =========================
     * Allow IP (TADM_ALLOW_IP) - List / Count / Detail
     * ========================= */
    List<AdminAllowIpVO> selectAdminAllowIpList(AdminAllowIpVO searchVO);

    int selectAdminAllowIpListCnt(AdminAllowIpVO searchVO);

    AdminAllowIpVO selectAdminAllowIpDetail(@Param("allowIpUuid") String allowIpUuid);

    /* =========================
     * Allow IP (TADM_ALLOW_IP) - Create / Update / Soft delete
     * ========================= */
    int insertAdminAllowIp(AdminAllowIpVO vo);

    int updateAdminAllowIpMeta(AdminAllowIpVO vo);

    int softDeleteAdminAllowIp(AdminAllowIpVO vo);

    /* =========================
     * Validation / Duplicate checks
     * ========================= */
    int selectExistsMngrUid(@Param("mngrUid") String mngrUid);

    int selectActiveDuplicateAllowIpCount(AdminAllowIpVO vo);

    int selectAnyDuplicateAllowIpCount(AdminAllowIpVO vo);

    /* =========================
     * Login allow checks (for future interceptor/auth service usage)
     * ========================= */
    int selectEffectiveAllowIpCountByMngrUid(@Param("mngrUid") String mngrUid);

    int selectEffectiveAllowIpMatchCount(@Param("mngrUid") String mngrUid,
                                         @Param("accessIp") String accessIp);
}
