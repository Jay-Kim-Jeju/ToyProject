package toy.com.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 관리자 인증 인터셉터 - 로그인 여부 검사
 */
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object loginAdmin = session.getAttribute("loginAdmin");

        if (loginAdmin == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.ac");
            return false;
        }

        return true;
    }
}
