package toy.admin.main.dao;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import toy.com.vo.common.SessionAdminVO;
import toy.com.vo.system.mngr.MngrVO;

import java.util.List;

@Mapper("AdmMainDAO")
public interface AdmMainDAO {

    SessionAdminVO selectAdminUserLogin(MngrVO mngrVO);

    List<String> selectAdminUserAuthList(MngrVO var1);

    void updateLastLogin(String var1);

    void updateLoginFailrCo(String var1);
}
