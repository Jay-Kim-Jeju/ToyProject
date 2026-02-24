package toy.admin.main.service;

import toy.com.vo.common.AdminLoginResult;
import toy.com.vo.system.mngr.MngrVO;

public interface AdmMainService {
    AdminLoginResult adminLogin(MngrVO mngrVO, String accessIp) throws Exception;

    void updateLoginFailCo(String var1);
}
