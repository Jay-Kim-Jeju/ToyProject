package toy.admin.system.allow.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import toy.admin.system.allow.dao.AdminAllowIpDAO;
import toy.admin.system.allow.service.AdminAllowIpService;
import toy.com.egov.EgovUserDetailsHelper;
import toy.com.util.CmConstants;
import toy.com.vo.system.allow.AdminAllowIpVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service("AdminAllowIpService")
@RequiredArgsConstructor
public class AdminAllowIpServiceImpl implements AdminAllowIpService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AdminAllowIpDAO adminAllowIpDAO;

    @Override
    public Map<String, Object> selectAdminAllowIpList(AdminAllowIpVO searchVO) throws Exception {
        // jsGrid 표출을 위해 목록 + 총건수를 함께 반환한다.
        List<AdminAllowIpVO> resultList = adminAllowIpDAO.selectAdminAllowIpList(searchVO);
        int totalCnt = adminAllowIpDAO.selectAdminAllowIpListCnt(searchVO);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("resultList", resultList);
        resultMap.put("totalCnt", totalCnt);
        return resultMap;
    }

    @Override
    public AdminAllowIpVO selectAdminAllowIpDetail(String allowIpUuid) throws Exception {
        return adminAllowIpDAO.selectAdminAllowIpDetail(allowIpUuid);
    }

    @Override
    public int insertAdminAllowIp(AdminAllowIpVO vo) throws Exception {
        if (vo == null) {
            return CmConstants.RESULT_INVALID;
        }

        // 대상 관리자와 허용 IP는 등록 필수값이다.
        if (isBlank(vo.getMngrUid()) || isBlank(vo.getAllowIp())) {
            return CmConstants.RESULT_INVALID;
        }

        try {
            normalizeAndValidatePeriod(vo);
        } catch (IllegalArgumentException e) {
            log.warn("insertAdminAllowIp invalid input. mngrUid={}, allowIp={}",
                    vo.getMngrUid(), vo.getAllowIp(), e);
            return CmConstants.RESULT_INVALID;
        }

        if (adminAllowIpDAO.selectExistsMngrUid(vo.getMngrUid()) <= 0) {
            return CmConstants.RESULT_INVALID;
        }

        // 활성 데이터 기준 중복 방지
        if (adminAllowIpDAO.selectActiveDuplicateAllowIpCount(vo) > 0) {
            return CmConstants.RESULT_DUPLE;
        }

        vo.setAllowIpUuid(generateAllowIpUuid());
        vo.setUseYn(isBlank(vo.getUseYn()) ? CmConstants.FLAG_Y : vo.getUseYn());
        vo.setRegUid(EgovUserDetailsHelper.getAdminUid()); // 실제 작업자

        try {
            return adminAllowIpDAO.insertAdminAllowIp(vo);
        } catch (DuplicateKeyException e) {
            // NOTE:
            // 현재 DB에는 (MNGR_UID, ALLOW_IP, CIDR_PREFIX) UNIQUE 제약이 있어
            // soft-delete 후 동일 tuple 재등록 시에도 중복으로 막힐 수 있다.
            log.warn("insertAdminAllowIp duplicate. mngrUid={}, allowIp={}, cidrPrefix={}",
                    vo.getMngrUid(), vo.getAllowIp(), vo.getCidrPrefix(), e);
            return CmConstants.RESULT_DUPLE;
        } catch (DataIntegrityViolationException e) {
            log.warn("insertAdminAllowIp constraint violation. mngrUid={}, allowIp={}",
                    vo.getMngrUid(), vo.getAllowIp(), e);
            return CmConstants.RESULT_INVALID;
        } catch (Exception e) {
            log.error("insertAdminAllowIp failed. mngrUid={}, allowIp={}",
                    vo.getMngrUid(), vo.getAllowIp(), e);
            return CmConstants.RESULT_FAIL;
        }
    }

    @Override
    public int updateAdminAllowIpMeta(AdminAllowIpVO vo) throws Exception {
        if (vo == null || isBlank(vo.getAllowIpUuid()) || isBlank(vo.getMngrUid())) {
            return CmConstants.RESULT_INVALID;
        }

        // 상세 수정에서는 ALLOW_IP/CIDR_PREFIX 자체를 바꾸지 않는 정책.
        AdminAllowIpVO current = adminAllowIpDAO.selectAdminAllowIpDetail(vo.getAllowIpUuid());
        if (current == null) {
            return CmConstants.RESULT_INVALID;
        }

        // Policy: a soft-deleted row (USE_YN='N') is terminal and cannot be re-activated.
        // Use re-register flow instead to preserve audit history.
        String currentUseYn = valueOrDefault(current.getUseYn(), CmConstants.FLAG_Y);
        String requestedUseYn = isBlank(vo.getUseYn()) ? currentUseYn : vo.getUseYn();
        if (CmConstants.FLAG_N.equalsIgnoreCase(currentUseYn)
                && CmConstants.FLAG_Y.equalsIgnoreCase(requestedUseYn)) {
            log.warn("updateAdminAllowIpMeta forbidden reactivation. allowIpUuid={}", vo.getAllowIpUuid());
            return CmConstants.RESULT_FORBIDDEN;
        }

        if (adminAllowIpDAO.selectExistsMngrUid(vo.getMngrUid()) <= 0) {
            return CmConstants.RESULT_INVALID;
        }

        // 대상 관리자 변경 시 활성 중복 여부 재검사 (IP/CIDR는 기존값 기준)
        // 메타수정 정책: 빈 값이면 현재값 유지 (컨트롤러에서 전송 누락 대비)
        vo.setUseYn(isBlank(vo.getUseYn()) ? valueOrDefault(current.getUseYn(), CmConstants.FLAG_Y) : vo.getUseYn());
        if (vo.getMemo() == null) {
            vo.setMemo(current.getMemo());
        }
        if (vo.getStartDt() == null) {
            vo.setStartDt(current.getStartDt());
        }
        if (vo.getEndDt() == null) {
            vo.setEndDt(current.getEndDt());
        }
        try {
            normalizeAndValidatePeriod(vo);
        } catch (IllegalArgumentException e) {
            log.warn("updateAdminAllowIpMeta invalid input. allowIpUuid={}", vo.getAllowIpUuid(), e);
            return CmConstants.RESULT_INVALID;
        }

        vo.setAllowIp(current.getAllowIp());
        vo.setCidrPrefix(current.getCidrPrefix());
        if (CmConstants.FLAG_Y.equalsIgnoreCase(valueOrDefault(vo.getUseYn(), current.getUseYn()))
                && adminAllowIpDAO.selectActiveDuplicateAllowIpCount(vo) > 0) {
            return CmConstants.RESULT_DUPLE;
        }

        vo.setMdfcnUid(EgovUserDetailsHelper.getAdminUid());

        try {
            return adminAllowIpDAO.updateAdminAllowIpMeta(vo);
        } catch (DuplicateKeyException e) {
            log.warn("updateAdminAllowIpMeta duplicate. allowIpUuid={}, mngrUid={}",
                    vo.getAllowIpUuid(), vo.getMngrUid(), e);
            return CmConstants.RESULT_DUPLE;
        } catch (DataIntegrityViolationException e) {
            log.warn("updateAdminAllowIpMeta constraint violation. allowIpUuid={}", vo.getAllowIpUuid(), e);
            return CmConstants.RESULT_INVALID;
        } catch (Exception e) {
            log.error("updateAdminAllowIpMeta failed. allowIpUuid={}", vo.getAllowIpUuid(), e);
            return CmConstants.RESULT_FAIL;
        }
    }

    @Override
    public int softDeleteAdminAllowIp(String allowIpUuid) throws Exception {
        if (isBlank(allowIpUuid)) {
            return CmConstants.RESULT_INVALID;
        }

        AdminAllowIpVO vo = new AdminAllowIpVO();
        vo.setAllowIpUuid(allowIpUuid);
        vo.setMdfcnUid(EgovUserDetailsHelper.getAdminUid());

        try {
            int r = adminAllowIpDAO.softDeleteAdminAllowIp(vo);
            return (r > 0) ? r : CmConstants.RESULT_FAIL;
        } catch (DataIntegrityViolationException e) {
            log.warn("softDeleteAdminAllowIp constraint violation. allowIpUuid={}", allowIpUuid, e);
            return CmConstants.RESULT_INVALID;
        } catch (Exception e) {
            log.error("softDeleteAdminAllowIp failed. allowIpUuid={}", allowIpUuid, e);
            return CmConstants.RESULT_FAIL;
        }
    }

    @Override
    public boolean isAllowedLoginIp(String mngrUid, String accessIp) throws Exception {
        // 정책: 유효 허용IP가 하나라도 등록된 계정만 IP 체크를 강제한다.
        if (isBlank(mngrUid) || isBlank(accessIp)) {
            return false;
        }

        int effectiveCnt = adminAllowIpDAO.selectEffectiveAllowIpCountByMngrUid(mngrUid);
        if (effectiveCnt <= 0) {
            return true; // 미등록 계정은 초기 도입 단계에서 허용
        }

        return adminAllowIpDAO.selectEffectiveAllowIpMatchCount(mngrUid, accessIp) > 0;
    }

    /* =========================
     * Helper methods
     * ========================= */

    // CIDR_PREFIX is the subnet mask length (e.g., 24 -> 255.255.255.0, 32 -> exact IP match).
    private void normalizeAndValidatePeriod(AdminAllowIpVO vo) {
        vo.setStartDt(normalizeDateTimeInput(vo.getStartDt(), true));
        vo.setEndDt(normalizeDateTimeInput(vo.getEndDt(), false));

        if (!isBlank(vo.getUseYn())
                && !CmConstants.FLAG_Y.equalsIgnoreCase(vo.getUseYn())
                && !CmConstants.FLAG_N.equalsIgnoreCase(vo.getUseYn())) {
            throw new IllegalArgumentException("Invalid USE_YN value.");
        }

        if (vo.getCidrPrefix() != null && (vo.getCidrPrefix() < 0 || vo.getCidrPrefix() > 128)) {
            throw new IllegalArgumentException("CIDR_PREFIX out of range.");
        }

        if (!isBlank(vo.getStartDt()) && !isBlank(vo.getEndDt())) {
            LocalDateTime start = LocalDateTime.parse(vo.getStartDt(), DATE_TIME_FMT);
            LocalDateTime end = LocalDateTime.parse(vo.getEndDt(), DATE_TIME_FMT);
            if (start.isAfter(end)) {
                throw new IllegalArgumentException("START_DT must be before or equal to END_DT.");
            }
        }
    }

    private String normalizeDateTimeInput(String input, boolean isStart) {
        if (input == null) {
            return null;
        }

        String v = input.trim();
        if (v.isEmpty()) {
            return null;
        }

        try {
            // Full datetime input
            LocalDateTime dt = LocalDateTime.parse(v, DATE_TIME_FMT);
            return dt.format(DATE_TIME_FMT);
        } catch (DateTimeParseException ignore) {
            // fallback to yyyy-MM-dd
        }

        try {
            LocalDate d = LocalDate.parse(v, DATE_FMT);
            LocalDateTime dt = isStart ? d.atStartOfDay() : d.atTime(23, 59, 59);
            return dt.format(DATE_TIME_FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd or yyyy-MM-dd HH:mm:ss", e);
        }
    }

    private String generateAllowIpUuid() {
        return "AIP_" + UUID.randomUUID().toString().replace("-", "");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String valueOrDefault(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }
}
