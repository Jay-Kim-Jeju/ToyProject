package toy.com.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import toy.com.util.CmConstants;
import toy.com.vo.common.SessionAdminVO;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 관리자 인증 인터셉터 - 로그인 여부 검사
 */
public class AdminAuthInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        SessionAdminVO admin = (session != null)
                ? (SessionAdminVO) session.getAttribute(CmConstants.SESSION_ADMIN_KEY)
                : null;

        String uri = request.getRequestURI(); // includes context path
        boolean isDoax = uri.endsWith(".doax");
        boolean isAc = uri.endsWith(".ac");
        boolean isAjaxLike = isDoax || isAc || "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));

        if (admin == null) {
            String returnURL = buildReturnUrl(request);
            if (isAjaxLike) {
                writeJsonRedirect(response, request.getContextPath() + "/toy/admin/login.do?returnURL=" + returnURL);
            } else {
                response.sendRedirect(request.getContextPath() + "/toy/admin/login.do?returnURL=" + returnURL);
            }
            return false;
        }

        // Step2A minimal safety: enforce menu permission by URL prefix (server-side guard).
        // - /toy/admin/sys/** : require SYSTEM or ADMINISTRATOR (for .do/.doax/.ac)
        // - other /toy/admin/** : authenticated-only for now (will expand later)
        boolean isSystemArea = uri.startsWith(request.getContextPath() + "/toy/admin/sys/");
        if (isSystemArea) {
            if (!hasAnyRole(admin, CmConstants.ROLE_SYSTEM, CmConstants.ROLE_ADMINISTRATOR)) {
                String logoutUrl = request.getContextPath()
                        + "/toy/admin/logout.ac?reason=" + CmConstants.LOGOUT_REASON_FORBIDDEN;
                if (isAjaxLike) {
                    writeJsonRedirect(response, logoutUrl);
                } else {
                    response.sendRedirect(logoutUrl);
                }
                return false;
            }
        }

        return true;
    }

    private boolean hasAnyRole(SessionAdminVO admin, String... roles) {
        if (admin == null || roles == null || roles.length == 0) {
            return false;
        }

        List<String> auth = admin.getAuth();
        if (auth == null || auth.isEmpty()) {
            return false;
        }

        for (String a : auth) {
            if (a == null) {
                continue;
            }
            String norm = a.trim();
            for (String r : roles) {
                if (r != null && r.equalsIgnoreCase(norm)) {
                    return true;
                }
            }
        }

        return false;
    }


    private String buildReturnUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String qs = request.getQueryString();
        String full = (qs == null || qs.isEmpty()) ? uri : (uri + "?" + qs);
        return URLEncoder.encode(full, StandardCharsets.UTF_8);
    }
    private void writeJsonRedirect(HttpServletResponse response, String redirectUrl) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.write("{\"result\":\"N\",\"redirectUrl\":\"" + escapeJson(redirectUrl) + "\"}");
            out.flush();
        }
    }
    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
