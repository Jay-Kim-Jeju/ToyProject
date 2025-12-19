package toy.com.util;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import toy.com.egov.EgovUserDetailsHelper;
import toy.com.vo.common.SessionAdminVO;

import java.util.List;

public class ToyAdminAuthUtils {

    private ToyAdminAuthUtils() {
        // This is a utility class; do not instantiate.
    }
    public static String chkAdminMenuPermission(String[] authGroups) {
        String returnStr = null;

        SessionAdminVO sessionAdminVO = (SessionAdminVO) EgovUserDetailsHelper.getAuthenticatedAdmin();
        List<String> authGrpArr = (sessionAdminVO != null && sessionAdminVO.getAuth() != null && !sessionAdminVO.getAuth().isEmpty())
                ? sessionAdminVO.getAuth()
                : null;


        boolean authFlag = false;

        // If authGroups is null/empty, default to ADMINISTRATOR only (same spirit as legacy).
        String[] targetGroups = (authGroups == null || authGroups.length == 0)
                ? new String[] { "ADMINISTRATOR" }
                : authGroups;

        if (authGrpArr != null) {
            for (String auth : authGrpArr) {
                for (String required : targetGroups) {
                    if (required != null && required.equals(auth)) {
                        authFlag = true;
                        break;
                    }
                }
                if (authFlag) {
                    break;
                }
            }
        }

        if (!authFlag) {
            returnStr = "redirect:/toy/admin/login.do";
        }

        return returnStr;
    }
}
