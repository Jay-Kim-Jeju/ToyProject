package toy.com.egov;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import toy.com.common.egovUserSession.service.EgovUserSessionService;

@Component
public class EgovUserDetailsHelper {

    static EgovUserSessionService egovUserDetailsService;

    public EgovUserSessionService getEgovUserDetailsService() {
        return egovUserDetailsService;
    }

    @Autowired
    public void setEgovUserDetailsService(EgovUserSessionService egovUserDetailsService) {
        // This injects the session service into the static helper.
        EgovUserDetailsHelper.egovUserDetailsService = egovUserDetailsService;
    }

    public static Object getAuthenticatedAdmin() {
        return egovUserDetailsService.getAuthenticatedAdmin();
    }


    /*public static Object getAuthenticatedUser() {
        return egovUserDetailsService.getAuthenticatedUser();
    }*/

    public static Object getAuthenticatedNoMbr() {
        return egovUserDetailsService.getAuthenticatedNoMbr();
    }

    public static Boolean isAuthenticated() {
        return egovUserDetailsService.isAuthenticated();
    }

    public static Boolean isAuthenticatedNoMbr() {
        return egovUserDetailsService.isAuthenticatedNoMbr();
    }

    public static Boolean isAdminAuthenticated() {
        return egovUserDetailsService.isAdminAuthenticated();
    }


    public static String getAdminUid() {
        return egovUserDetailsService.getAdminUid();
    }

    /*public static String getUserUid() {
        return egovUserDetailsService.getUserUid();
    }*/

}
