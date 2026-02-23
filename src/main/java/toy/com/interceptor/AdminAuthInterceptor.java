package toy.com.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import toy.admin.system.auth.service.AdminAuthService;
import toy.com.util.CmConstants;
import toy.com.vo.common.SessionAdminVO;
import toy.com.vo.system.auth.AdminAuthGuardVO;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 인증 인터셉터 - 로그인 여부 검사
 * Admin authentication interceptor (includes minimal auth-guard).
 */
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LogManager.getLogger(AdminAuthInterceptor.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private AdminAuthService adminAuthService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        SessionAdminVO admin = (session != null)
                ? (SessionAdminVO) session.getAttribute(CmConstants.SESSION_ADMIN_KEY)
                : null;

        String uri = request.getRequestURI(); // includes context path
        String ctx = request.getContextPath();

        boolean isDoax = uri.endsWith(".doax");
        boolean isAc = uri.endsWith(".ac");
        boolean isAjaxLike = isDoax || isAjaxRequest(request);

        // Prevent infinite redirect loops for login/logout endpoints
        String loginPath = ctx + "/toy/admin/login.do";
        String logoutPath = ctx + "/toy/admin/logout.ac";
        String actionLoginPath = ctx + "/toy/admin/loginAction.ac";
        if (uri.equals(loginPath) || uri.equals(logoutPath) || uri.equals(actionLoginPath)) {
            return true;
        }

        if (admin == null) {
            String returnURL = buildReturnUrl(request);
            if (isAjaxLike) {
                writeJsonRedirect(
                        response,
                        ctx + "/toy/admin/login.do?returnURL=" + returnURL,
                        "LOGIN_REQUIRED"
                );
            } else {
                response.sendRedirect(ctx + "/toy/admin/login.do?returnURL=" + returnURL);
            }
            return false;
        }

        // StepX minimal safety: auth guard (detect DB auth changes and force logout)
        // - *.doax / *.ac : always-check
        // - *.do         : TTL-check (10s)
        if (shouldCheckAuthGuard(request, session, isDoax, isAc)) {
            String mngrUid = safeTrim(admin.getMngrUid());
            // Initialize session digest from session roles if missing (baseline)
            String sessionDigest = getOrInitSessionAuthDigest(session, admin);
            AdminAuthGuardVO guard = adminAuthService.selectAdminAuthGuard(mngrUid);
            String dbUseYn = (guard != null) ? safeTrim(guard.getMngrUseYn()) : "";
            String dbDigest = (guard != null) ? safeTrim(guard.getAuthDigest()) : "";
            boolean ok = (guard != null)
                    && "Y".equalsIgnoreCase(dbUseYn)
                    && sessionDigest.equals(dbDigest);
            if (!ok) {
                // Do not log sensitive fields (digest/concat). Keep it minimal.
                log.warn("Admin auth guard rejected. mngrUid={}, reason={}, uri={}",
                        mngrUid, CmConstants.LOGOUT_REASON_AUTH_CHANGED, uri);
                return forceLogout(request, response, isAjaxLike, CmConstants.LOGOUT_REASON_AUTH_CHANGED);
            }
            // Update cache timestamp/digest after successful check
            session.setAttribute(CmConstants.SESSION_AUTH_GUARD_DIGEST, dbDigest);
            session.setAttribute(CmConstants.SESSION_AUTH_GUARD_LAST_CHK_AT, System.currentTimeMillis());
        }

        // Step2A: enforce admin-route permission for all /toy/admin/** paths.
        // - Any protected admin route: require at least one assigned role
        // - /toy/admin/sys/**      : require SYSTEM or ADMINISTRATOR
        if (isProtectedAdminPath(uri, ctx)) {
            if (!hasAnyAssignedRole(admin)) {
                return forceLogout(request, response, isAjaxLike, CmConstants.LOGOUT_REASON_FORBIDDEN);
            }

            String[] requiredRoles = resolveRequiredRolesForUri(uri, ctx);
            if (requiredRoles != null && requiredRoles.length > 0 && !hasAnyRole(admin, requiredRoles)) {
                return forceLogout(request, response, isAjaxLike, CmConstants.LOGOUT_REASON_FORBIDDEN);
            }
        }

        return true;
    }


    private boolean shouldCheckAuthGuard(HttpServletRequest request, HttpSession session, boolean isDoax, boolean isAc) {
        String uri = request.getRequestURI();
        // *.doax / *.ac are always-checked
        if (isDoax || isAc) {
            return true;
        }
        // Only *.do uses TTL-check
        if (!uri.endsWith(".do")) {
            return false;
        }
        if (session == null) {
            return true;
        }
        Object v = session.getAttribute(CmConstants.SESSION_AUTH_GUARD_LAST_CHK_AT);
        if (!(v instanceof Long)) {
            return true;
        }
        long last = (Long) v;
        long now = System.currentTimeMillis();
        return (now - last) >= CmConstants.AUTH_GUARD_TTL_VIEW_MS;
    }

    private String getOrInitSessionAuthDigest(HttpSession session, SessionAdminVO admin) throws Exception {
        Object v = session.getAttribute(CmConstants.SESSION_AUTH_GUARD_DIGEST);
        if (v instanceof String && !((String) v).isEmpty()) {
            return (String) v;
        }
        // Baseline digest is derived from session roles loaded at login time
        String digest = calcAuthDigestFromSession(admin);
        session.setAttribute(CmConstants.SESSION_AUTH_GUARD_DIGEST, digest);
        return digest;
    }

    private String calcAuthDigestFromSession(SessionAdminVO admin) throws Exception {
        List<String> auth = (admin != null) ? admin.getAuth() : null; // SessionAdminVO.auth exists :contentReference[oaicite:1]{index=1}
        if (auth == null || auth.isEmpty()) {
            return "";
        }
        List<String> cleaned = new ArrayList<>();
        for (String a : auth) {
            String t = safeTrim(a);
            if (!t.isEmpty()) {
                cleaned.add(t);
            }
        }
        if (cleaned.isEmpty()) {
            return "";
        }
        Collections.sort(cleaned);
        String joined = String.join(",", cleaned);
        return md5Hex(joined);
    }

    private String md5Hex(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = md.digest((s == null ? "" : s).getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte x : b) {
            sb.append(String.format("%02x", x));
        }
        return sb.toString();
    }

    private String safeTrim(String s) {
        return (s == null) ? "" : s.trim();
    }

    private boolean isProtectedAdminPath(String uri, String ctx) {
        String adminRoot = ctx + "/toy/admin/";
        if (!uri.startsWith(adminRoot)) {
            return false;
        }
        return !(uri.equals(ctx + "/toy/admin/login.do")
                || uri.equals(ctx + "/toy/admin/loginAction.ac")
                || uri.equals(ctx + "/toy/admin/logout.ac"));
    }

    private String[] resolveRequiredRolesForUri(String uri, String ctx) {
        if (uri.startsWith(ctx + "/toy/admin/sys/")) {
            return new String[] { CmConstants.ROLE_SYSTEM, CmConstants.ROLE_ADMINISTRATOR };
        }
        return null;
    }

    private boolean hasAnyAssignedRole(SessionAdminVO admin) {
        if (admin == null) {
            return false;
        }

        List<String> auth = admin.getAuth();
        if (auth == null || auth.isEmpty()) {
            return false;
        }

        for (String a : auth) {
            if (a != null && !a.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean forceLogout(HttpServletRequest request, HttpServletResponse response, boolean isAjaxLike, String reason) throws Exception {
        // Invalidate session immediately to enforce forced logout
        HttpSession session = request.getSession(false);
        if (session != null) {
            try {
                session.invalidate();
            } catch (IllegalStateException ignore) {
                // Session might already be invalidated
            }
        }

        String ctx = request.getContextPath();
        String logoutUrl = ctx + "/toy/admin/logout.ac?reason="
                + URLEncoder.encode(reason, StandardCharsets.UTF_8.name());
        if (isAjaxLike) {
            writeJsonRedirect(response, logoutUrl, reason);
        } else {
            response.sendRedirect(logoutUrl);
        }
        return false;
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

    private boolean isAjaxRequest(HttpServletRequest request) {
        String xrw = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(xrw)) {
            return true;
        }
        String accept = request.getHeader("Accept");
        return accept != null && accept.toLowerCase().contains("application/json");
    }


    private void writeJsonRedirect(HttpServletResponse response, String redirectUrl, String reason) throws Exception {
        int status = "LOGIN_REQUIRED".equalsIgnoreCase(reason)
                ? HttpServletResponse.SC_UNAUTHORIZED
                : HttpServletResponse.SC_FORBIDDEN;
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        // Keep JSON schema fixed: { result, reason, redirectUrl }
        Map<String, String> body = new LinkedHashMap<>();
        body.put("result", String.valueOf(CmConstants.RESULT_FORBIDDEN));
        body.put("reason", reason);
        body.put("redirectUrl", redirectUrl);
        PrintWriter out = response.getWriter();
        OBJECT_MAPPER.writeValue(out, body);
        out.flush();
    }
}
