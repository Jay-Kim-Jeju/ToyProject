package toy.admin.system.code.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.DataIntegrityViolationException;

import toy.admin.system.code.dao.AdminCodeDAO;
import toy.admin.system.code.service.AdminCodeService;
import toy.com.egov.EgovUserDetailsHelper;
import toy.com.vo.system.code.CdGrpVO;
import toy.com.vo.system.code.CdVO;

import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Service("AdminCodeService")
@RequiredArgsConstructor
public class AdminCodeServiceImpl implements AdminCodeService {

    private final AdminCodeDAO adminCodeDAO;

    @Override
    public List<CdGrpVO> selectCodeGroupList(CdGrpVO codeGrpVO) throws Exception {
        return adminCodeDAO.selectCodeGroupList(codeGrpVO);
    }

    @Override
    public int selectCdGrpListCnt(CdGrpVO codeGrpVO) throws Exception {
        return adminCodeDAO.selectCdGrpListCnt(codeGrpVO);
    }

    @Override
    public int insertCdGrp(CdGrpVO codeGrpVO) throws Exception {
        codeGrpVO.setRegUid(EgovUserDetailsHelper.getAdminUid());
        try {
            return adminCodeDAO.insertCdGrp(codeGrpVO);
        } catch (DuplicateKeyException e) {
            // Duplicate GROUP_CD (PK)
            return RESULT_DUPLE;
        } catch (DataIntegrityViolationException e) {
            log.warn("insertCdGrp constraint violation. groupCd={}", codeGrpVO.getGroupCd(), e);
            return RESULT_INVALID;
        }

    }

    @Override
    public int updateCdGrp(CdGrpVO codeGrpVO) throws Exception {
        codeGrpVO.setUpdUid(EgovUserDetailsHelper.getAdminUid());
        try{
            return adminCodeDAO.updateCdGrp(codeGrpVO);
        } catch (DataIntegrityViolationException e) {
            log.warn("updateCdGrp constraint violation. groupCd={}", codeGrpVO.getGroupCd(), e);
            return  RESULT_INVALID;
        }
    }

    @Override
    public List<CdVO> selectCdListByGroup(CdVO codeVO) throws Exception {
        return adminCodeDAO.selectCdListByGroup(codeVO);
    }

    @Override
    public int selectCdListCnt(CdVO codeVO) throws Exception {
        return adminCodeDAO.selectCdListByGroupCnt(codeVO);
    }


    @Override
    public int insertCd(CdVO codeVO) throws Exception {
        codeVO.setRegUid(EgovUserDetailsHelper.getAdminUid());
        try{
            return adminCodeDAO.insertCd(codeVO);
        } catch (DuplicateKeyException e) {
            // Duplicate (GROUP_CD, CD)
            return RESULT_DUPLE;
        } catch (DataIntegrityViolationException e) {
            log.warn("insertCd constraint violation. groupCd={}, cd={}", codeVO.getGroupCd(), codeVO.getCd(), e);
            return RESULT_INVALID;
        }
    }

    @Override
    public CdVO selectCd(CdVO codeVO) throws Exception {
        return adminCodeDAO.selectCd(codeVO);
    }

    @Override
    public int updateCd(CdVO codeVO) throws Exception {
        codeVO.setUpdUid(EgovUserDetailsHelper.getAdminUid());
        try {
            return adminCodeDAO.updateCd(codeVO);
        } catch (DataIntegrityViolationException e) {
            log.warn("updateCd constraint violation. groupCd={}, cd={}", codeVO.getGroupCd(), codeVO.getCd(), e);
            return RESULT_INVALID;
        }
    }

    @Override
    public int deleteCd(CdVO codeVO) throws Exception {
        try {
            return adminCodeDAO.deleteCd(codeVO);
        } catch (DataIntegrityViolationException e) {
            // FK constraint, etc.
            log.warn("deleteCd constraint violation. groupCd={}, cd={}", codeVO.getGroupCd(), codeVO.getCd(), e);
            return RESULT_INVALID;
        }

    }

    @Override
    public String selectCodeName(CdVO codeVO) throws Exception {
        return adminCodeDAO.selectCodeName(codeVO);
    }

    @Override
    public String selectCodeInfo(CdVO codeVO) throws Exception {
        return adminCodeDAO.selectCodeInfo(codeVO);
    }
}
