package toy.admin.system.code.dao;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import toy.com.vo.system.code.CdGrpVO;
import toy.com.vo.system.code.CdVO;

import java.util.List;

@Mapper("AdminCodeDAO")
public interface AdminCodeDAO {

    /* =========================
     * Code Group
     * ========================= */

    // Select code group list with paging + search filters.
    List<CdGrpVO> selectCodeGroupList(CdGrpVO searchVO);

    // Count code group list for paging.
    int selectCdGrpListCnt(CdGrpVO searchVO);

    // Insert a new code group.
    int insertCdGrp(CdGrpVO vo);

    // Update an existing code group.
    int updateCdGrp(CdGrpVO vo);

    /* =========================
     * Code
     * ========================= */

    // Select codes by group code (ordered by creation time).
    List<CdVO> selectCdListByGroup(CdVO vo);

    // Count codes by group code.
    int selectCdListByGroupCnt(CdVO vo);

    // Select a single code detail by (groupCd, cd).
    CdVO selectCd(CdVO vo);

    // Insert a new code (no manual sort order).
    int insertCd(CdVO vo);

    // Update code fields (no manual sort order).
    int updateCd(CdVO vo);

    // Delete a code by (groupCd, cd).
    int deleteCd(CdVO vo);

    /* =========================
     * Simple lookups (groupCd + cd)
     * ========================= */

    // Lookup code name by (groupCd, cd).
    String selectCodeName(CdVO vo);

    // Lookup additional info by (groupCd, cd).
    String selectCodeInfo(CdVO vo);
}
