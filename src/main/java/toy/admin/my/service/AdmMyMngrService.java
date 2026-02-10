package toy.admin.my.service;

import toy.com.vo.system.mngr.MngrVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface AdmMyMngrService {
    /* =========================
     * My Account - Detail
     * ========================= */
    MngrVO selectMyMngrDetail(HttpServletRequest request) throws Exception;

    /* =========================
     * Verification (SMS)
     * ========================= */
    void resetMyVerificationState(HttpServletRequest request);

    Map<String, Object> sendVerificationNumber(HttpServletRequest request) throws Exception;

    Map<String, Object> checkVerificationNumber(String inputCode, HttpServletRequest request) throws Exception;

    boolean isMyVerified(HttpServletRequest request);

    /* =========================
     * My Account - Update / Password
     * ========================= */
    int updateMyMngrInfo(MngrVO vo, HttpServletRequest request) throws Exception;

    int changeMyPassword(String currentPassword, String newPassword, HttpServletRequest request) throws Exception;

}
