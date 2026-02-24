package toy.admin.system.allow.service;

import toy.com.vo.system.allow.AdminAllowIpVO;

import java.util.Map;

public interface AdminAllowIpService {

    /* =========================
     * Allow IP list (jsGrid)
     * ========================= */
    Map<String, Object> selectAdminAllowIpList(AdminAllowIpVO searchVO) throws Exception;

    /* =========================
     * Allow IP detail
     * ========================= */
    AdminAllowIpVO selectAdminAllowIpDetail(String allowIpUuid) throws Exception;

    /* =========================
     * Allow IP create / update / soft delete
     * ========================= */
    int insertAdminAllowIp(AdminAllowIpVO vo) throws Exception;

    int updateAdminAllowIpMeta(AdminAllowIpVO vo) throws Exception;

    int softDeleteAdminAllowIp(String allowIpUuid) throws Exception;

    /* =========================
     * Login policy helper
     * ========================= */
    boolean isAllowedLoginIp(String mngrUid, String accessIp) throws Exception;
}
