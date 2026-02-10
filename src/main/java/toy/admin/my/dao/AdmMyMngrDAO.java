package toy.admin.my.dao;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import toy.com.vo.system.mngr.MngrVO;

import java.util.Map;

@Mapper("AdmMyMngrDAO")
public interface AdmMyMngrDAO {

    /* =========================
     * My Account - Detail
     * ========================= */
    MngrVO selectMyMngrDetail(String mngrUid);

    /* =========================
     * My Account - Update Info
     * ========================= */
    int updateMyMngrInfo(MngrVO vo);

    /* =========================
     * My Account - Password
     * ========================= */
    String selectMyPwdEncpt(String mngrUid);

    int updateMyPassword(Map<String, Object> param);
}
