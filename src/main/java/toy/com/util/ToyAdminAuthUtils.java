package toy.com.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import toy.com.egov.EgovUserDetailsHelper;
import toy.com.vo.common.SessionAdminVO;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ToyAdminAuthUtils {

    private ToyAdminAuthUtils() {
        // This is a utility class; do not instantiate.
    }

    private static final String ROLE_ADMIN = "ADMINISTRATOR";
    private static final String ROLE_GUEST = "GUEST";


    private static List<String> getSessionAuthList() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpSession session = attrs.getRequest().getSession(false);
        SessionAdminVO sessionAdminVO = (session != null) ? (SessionAdminVO) session.getAttribute("sessionAdminVO") : null;
        return (sessionAdminVO != null) ? sessionAdminVO.getAuth() : null;
        // TODO: If you migrate admin auth to Spring Security later, switch this back to SecurityContext-based lookup.
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

        Set<String> normalizedAuth = authList.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        for (String r : roles) {
            if (r != null && normalizedAuth.contains(r.trim().toUpperCase())) {
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
        return ok ? null : "redirect:/toy/admin/logout.ac?reason=" + CmConstants.LOGOUT_REASON_FORBIDDEN;
    }

    public static String chkAdminCrudPermission(String menuRole) {
        // Admin can do anything
        if (isAdmin()) {
            return null;
        }

        // Guest cannot do any mutation
        if (isGuest()) {
            return "redirect:/toy/admin/logout.ac?reason=" + CmConstants.LOGOUT_REASON_FORBIDDEN;
        }

        // Non-admin must have the menu role
        boolean ok = hasAnyRole(menuRole);
        return ok ? null : "redirect:/toy/admin/logout.ac?reason=" + CmConstants.LOGOUT_REASON_FORBIDDEN;
    }
}
