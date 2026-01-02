package toy.admin.main.dao;

import org.apache.ibatis.annotations.Param;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import toy.com.vo.common.SessionAdminVO;
import toy.com.vo.system.mngr.MngrVO;

import java.util.List;

@Mapper("AdmMainDAO")
public interface AdmMainDAO {

    SessionAdminVO selectAdminUserLogin(MngrVO mngrVO);

    List<String> selectAdminUserAuthList(MngrVO mngrVO);

    void updateLastLogin(String mngrUid);

    void updateLoginFailCo(String mngrUid);
}
