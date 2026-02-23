package toy.admin.system.auth.service.impl;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import toy.admin.system.auth.dao.AdminAuthDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import toy.admin.system.auth.service.AdminAuthService;
import toy.com.egov.EgovUserDetailsHelper;
import toy.com.util.CmConstants;
import toy.com.vo.system.auth.AdminAuthBatchResult;
import toy.com.vo.system.auth.AdminAuthGuardVO;
import toy.com.vo.system.auth.AuthVO;
import toy.com.vo.system.mngr.MngrVO;

import java.util.List;

@Slf4j
@Service("AdminAuthService")
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminAuthDAO admAuthDAO;


    /* =========================
     * Auth Role (TAUTH)
     * ========================= */

    @Override
    public List<AuthVO> selectAdminAuthRoleList(AuthVO searchVO) throws Exception {
        return admAuthDAO.selectAdminAuthRoleList(searchVO);
    }

    @Override
    public int selectAdminAuthRoleListCnt(AuthVO searchVO) throws Exception {
        return admAuthDAO.selectAdminAuthRoleListCnt(searchVO);
    }

    @Override
    public AuthVO selectAdminAuthRoleDetail(String authUuid) throws Exception {
        return admAuthDAO.selectAdminAuthRoleDetail(authUuid);
    }

    @Override
    public int insertAdminAuthRole(AuthVO vo) throws Exception {
        vo.setRegUid(EgovUserDetailsHelper.getAdminUid());
        try {
            return admAuthDAO.insertAdminAuthRole(vo);
        } catch (DuplicateKeyException e) {
            // Duplicate AUTH_UUID
            return CmConstants.RESULT_DUPLE;
        } catch (DataIntegrityViolationException e) {
            log.warn("insertAdminAuthRole constraint violation. authUuid={}, authorDc={}",
                    vo.getAuthUuid(), vo.getAuthorDc(), e);
            return CmConstants.RESULT_INVALID;
        } catch (Exception e) {
            log.error("insertAdminAuthRole failed. authUuid={}", vo.getAuthUuid(), e);
            return CmConstants.RESULT_FAIL;
        }
    }

    @Override
    public int updateAdminAuthRole(AuthVO vo) throws Exception {
        try {
            return admAuthDAO.updateAdminAuthRole(vo);
        } catch (DataIntegrityViolationException e) {
            log.warn("updateAdminAuthRole constraint violation. authUuid={}, useYn={}",
                    vo.getAuthUuid(), vo.getUseYn(), e);
            return CmConstants.RESULT_INVALID;
        } catch (Exception e) {
            log.error("updateAdminAuthRole failed. authUuid={}", vo.getAuthUuid(), e);
            return CmConstants.RESULT_FAIL;
        }
    }

    @Override
    public int disableAdminAuthRole(String authUuid) throws Exception {
        try {
            return admAuthDAO.disableAdminAuthRole(authUuid);
        } catch (DataIntegrityViolationException e) {
            log.warn("disableAdminAuthRole constraint violation. authUuid={}", authUuid, e);
            return CmConstants.RESULT_INVALID;
        } catch (Exception e) {
            log.error("disableAdminAuthRole failed. authUuid={}", authUuid, e);
            return CmConstants.RESULT_FAIL;
        }
    }

    /* =========================
     * Auth Guard (Session Revalidation)
     * ========================= */
    @Override
    public AdminAuthGuardVO selectAdminAuthGuard(String mngrUid) throws Exception {
        return admAuthDAO.selectAdminAuthGuard(mngrUid);
    }


    /* =========================
     * Auth Assignment (TAUTH_MNGR)
     * ========================= */

    @Override
    public List<MngrVO> selectAdminAuthAssignedMngrList(MngrVO searchVO) throws Exception {
        return admAuthDAO.selectAdminAuthAssignedMngrList(searchVO);
    }

    @Override
    public int selectAdminAuthAssignedMngrListCnt(MngrVO searchVO) throws Exception {
        return admAuthDAO.selectAdminAuthAssignedMngrListCnt(searchVO);
    }

    @Override
    public List<MngrVO> selectAdminAuthUnassignedMngrList(MngrVO searchVO) throws Exception {
        return admAuthDAO.selectAdminAuthUnassignedMngrList(searchVO);
    }

    @Override
    public int selectAdminAuthUnassignedMngrListCnt(MngrVO searchVO) throws Exception {
        return admAuthDAO.selectAdminAuthUnassignedMngrListCnt(searchVO);
    }

    @Override
    public int insertAdminAuthMngr(String authUuid, String mngrUid) throws Exception {
        try {
            return admAuthDAO.insertAdminAuthMngr(authUuid, mngrUid);
        } catch (DuplicateKeyException e) {
            // Duplicate (AUTH_UUID, MNGR_UID)
            return CmConstants.RESULT_DUPLE;
        } catch (DataIntegrityViolationException e) {
            log.warn("insertAdminAuthMngr constraint violation. authUuid={}, mngrUid={}",
                    authUuid, mngrUid, e);
            return CmConstants.RESULT_INVALID;
        } catch (Exception e) {
            log.error("insertAdminAuthMngr failed. authUuid={}, mngrUid={}", authUuid, mngrUid, e);
            return CmConstants.RESULT_FAIL;
        }
    }

    @Override
    public int deleteAdminAuthMngr(String authUuid, String mngrUid) throws Exception {
        try {
            return admAuthDAO.deleteAdminAuthMngr(authUuid, mngrUid);
        } catch (DataIntegrityViolationException e) {
            log.warn("deleteAdminAuthMngr constraint violation. authUuid={}, mngrUid={}", authUuid, mngrUid, e);
            return CmConstants.RESULT_INVALID;
        } catch (Exception e) {
            log.error("deleteAdminAuthMngr failed. authUuid={}, mngrUid={}", authUuid, mngrUid, e);
            return CmConstants.RESULT_FAIL;
        }
    }


    // Assigns a given auth role to multiple managers in bulk, skipping duplicates and returning a summary of inserted/duplicate counts.
    @Override
    public AdminAuthBatchResult insertAdminAuthMngrList(String authUuid, List<String> mngrUidList) throws Exception {
        AdminAuthBatchResult result = new AdminAuthBatchResult();
        if (mngrUidList == null || mngrUidList.isEmpty()) {
            result.setRequestedCount(0);
            return result;
        }

        int inserted = 0;
        int dup = 0;
        int invalid = 0;
        int requested = 0;
        for (String mngrUid : mngrUidList) {
            if (mngrUid == null || mngrUid.trim().isEmpty()) {
                continue;
            }
            requested++;
            int r = insertAdminAuthMngr(authUuid, mngrUid);
            if (r == CmConstants.RESULT_INVALID) {
                invalid++;
                // Stop on invalid data; AOP transaction should roll back if you throw.
                // If you want full rollback on invalid, throw an exception here.
                throw new IllegalStateException("Invalid data during batch auth assignment.");
            }
            if (r == CmConstants.RESULT_DUPLE) {
                dup++;
                continue;
            }
            if (r > 0) {
                inserted += r;
            }
        }
        result.setInsertedCount(inserted);
        result.setDuplicateCount(dup);
        result.setInvalidCount(invalid);
        result.setRequestedCount(requested);
        return result;
    }
}
