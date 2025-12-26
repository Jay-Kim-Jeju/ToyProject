package toy.admin.system.code.service;

import org.springframework.ui.ModelMap;
import toy.com.vo.system.code.CdGrpVO;
import toy.com.vo.system.code.CdVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public interface AdminCodeService {

    int RESULT_DUPLE = -1;
    int RESULT_INVALID = -2;

    /* =========================
     * Code Group
     * ========================= */

    List<CdGrpVO> selectCodeGroupList(CdGrpVO codeGrpVO) throws Exception;

    int selectCdGrpListCnt(CdGrpVO codeGrpVO) throws Exception;

    int insertCdGrp(CdGrpVO codeGrpVO) throws Exception;

    int updateCdGrp(CdGrpVO codeGrpVO) throws Exception;

    /* =========================
     * Code
     * ========================= */

    List<CdVO> selectCdListByGroup(CdVO codeVO) throws Exception;

    int selectCdListCnt(CdVO codeVO) throws Exception;

    int insertCd(CdVO codeVO) throws Exception;

    CdVO selectCd(CdVO codeVO) throws Exception;

    int updateCd(CdVO codeVO) throws Exception;

    int deleteCd(CdVO codeVO) throws Exception;

    String selectCodeName(CdVO codeVO) throws Exception;

    String selectCodeInfo(CdVO codeVO) throws Exception;
}
