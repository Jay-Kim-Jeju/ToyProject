package toy.admin.system.mngr.service;

import toy.com.vo.system.mngr.AdminMngrSmsResult;
import toy.com.vo.system.mngr.AdminMngrVerifResult;
import toy.com.vo.system.mngr.MngrVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AdminManagerService {

    List<MngrVO> selectMngrList(MngrVO searchVO) throws Exception;
    int selectMngrListCount(MngrVO searchVO) throws Exception;

    MngrVO selectMngrDetail(String mngrUid) throws Exception;

    int updateMngr(MngrVO vo) throws Exception;

    int softDeleteMngr(String mngrUid) throws Exception;

    // Insert new manager OR reactivate (USE_YN='N') + issue temp password + send SMS
    AdminMngrSmsResult insertMngrAndSendTempPassword(MngrVO vo) throws Exception;

    // For password popup: issue a NEW temp password + send SMS (Send/Resend)
    AdminMngrSmsResult resetPasswordAndSendSms(String mngrUid) throws Exception;


    // NOTE: Admin manager module does not use verification flow.
    // Verification is handled in "My Account" module only.
}
