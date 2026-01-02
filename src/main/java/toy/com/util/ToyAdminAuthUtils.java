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

    private static final String ROLE_ADMIN = "ADMINISTRATOR";
    private static final String ROLE_GUEST = "GUEST";


    private static List<String> getSessionAuthList() {
        SessionAdminVO sessionAdminVO = (SessionAdminVO) EgovUserDetailsHelper.getAuthenticatedAdmin();
        return (sessionAdminVO != null) ? sessionAdminVO.getAuth() : null;
    }


    public static boolean hasAnyRole(String... roles) {
        List<String> authList = getSessionAuthList();

        if (authList == null || authList.isEmpty()) {
            return false;
        }

        // Defensive default: if roles are not provided, allow only ADMINISTRATOR.
        if (roles == null || roles.length == 0) {
            return authList.contains(ROLE_ADMIN);
        }

        for (String r : roles) {
            if (r != null && authList.contains(r)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAdmin() {
        return hasAnyRole(ROLE_ADMIN);
    }

    public static boolean isGuest() {
        return hasAnyRole(ROLE_GUEST);
    }


    public static String chkAdminMenuPermission(String auth) {
        return chkAdminMenuPermission(new String[] { auth });
    }
    public static String chkAdminMenuPermission(String[] authGroups) {
        // Admin can enter any menu
        if (isAdmin()) {
            return null;
        }

        // GUEST can enter any menu (policy)
        if (isGuest()) {
            return null;
        }

        // If authGroups is null/empty, default to ADMINISTRATOR only (defensive).
        String[] targetGroups = (authGroups == null || authGroups.length == 0)
                ? new String[] { ROLE_ADMIN }
                : authGroups;

        boolean ok = hasAnyRole(targetGroups);
        return ok ? null : "redirect:/toy/admin/login.do";
    }

    public static String chkAdminCrudPermission(String menuRole) {
        // Admin can do anything
        if (isAdmin()) {
            return null;
        }

        // Guest cannot do any mutation
        if (isGuest()) {
            return "redirect:/toy/admin/login.do";
        }

        // Non-admin must have the menu role
        boolean ok = hasAnyRole(menuRole);
        return ok ? null : "redirect:/toy/admin/login.do";
    }
}
