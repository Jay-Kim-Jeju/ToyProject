package toy.admin.main.service;

import toy.com.vo.common.AdminLoginResult;
import toy.com.vo.system.mngr.MngrVO;

public interface AdmMainService {
    AdminLoginResult adminLogin(MngrVO mngrVO) throws Exception;

    void updateLoginFailrCo(String var1);
}
