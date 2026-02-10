package toy.admin.system.mngr.dao;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import toy.com.vo.system.mngr.MngrVO;

import java.util.List;

@Mapper("AdminManagerDAO")
public interface AdminManagerDAO {

    /* =========================     * Manager (TMNGR) - List / Count     * ========================= */
    List<MngrVO> selectMngrList(MngrVO searchVO);
    int selectMngrListCount(MngrVO searchVO);


    /* =========================     * Manager (TMNGR) - Detail     * ========================= */
    MngrVO selectMngr(MngrVO vo);


    /* =========================     * Manager (TMNGR) - Create / Update / Soft delete     * ========================= */
    int insertMngr(MngrVO vo);
    int updateMngr(MngrVO vo);
    int softDeleteMngr(MngrVO vo);


    /* =========================     * Manager (TMNGR) - Password     * ========================= */
    int updateMngrChangePassword(MngrVO vo);


    /* =========================     * Duplicate / Reactivate support     * ========================= */
    MngrVO selectMngrByUserId(MngrVO vo);
}
