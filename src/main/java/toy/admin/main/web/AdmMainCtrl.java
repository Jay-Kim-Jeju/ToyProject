package toy.admin.main.web;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import toy.com.common.service.CommonService;
//import toy.com.egov.EgovPropertiesUtils;
//import toy.com.util.EgovStringUtil;
//import toy.com.vo.common.SessionAdminVO;
//import toy.com.vo.system.mngr.MngrVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import toy.admin.main.service.AdmMainService;
import toy.admin.system.accesslog.service.AdminAccessLogService;
import toy.com.egov.EgovPropertiesUtils;
import toy.com.util.AdminLogoutMessageHelper;
import toy.com.util.EgovStringUtil;
import toy.com.vo.common.AdminLoginResult;
import toy.com.vo.common.SessionAdminVO;
import toy.com.vo.system.mngr.MngrVO;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RequiredArgsConstructor
@Controller
public class AdmMainCtrl {

    @Resource(name = "AdmMainService")
    private final AdmMainService admMainService;
    private final AdminAccessLogService adminAccessLogService;

    private static final Logger LOG_DEBUG = LoggerFactory.getLogger(AdmMainCtrl.class);

    @Resource(name = "adminLogoutReasonHelper")
    private AdminLogoutMessageHelper adminLogoutMessageHelper;

    @Resource(name = "messageSource")
    private MessageSource messageSource;

    @RequestMapping({"/toy/admin/login.do"})
    public String toyAdmLogin(ModelMap model, HttpServletRequest request) throws Exception {

        // 세션 전체 invalidate
        // 새로고침할때마다 login.do에서 매번 session.invalidate()를 호출해서 CSRF 토큰과 세션이 안 맞는 상태가 되면서 403발생
        //request.getSession().invalidate();

        // 1) 세션 전체 invalidate 대신, 필요한 값만 제거
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("sessionAdminVO");
            // 필요하면 추가로 제거
        }

        String returnURL = request.getParameter("returnURL");
        if (returnURL == null || returnURL.isEmpty()) {
            returnURL = "/toy/admin/main.do"; // or 원하는 기본 페이지
        }

        model.addAttribute("returnURL", returnURL);
        model.addAttribute("isLocal", EgovPropertiesUtils.getOptionalProp("IS_LOCAL"));

        String reason = request.getParameter("reason");
        String logoutMessage = adminLogoutMessageHelper.resolveLogoutMessage(reason, request.getLocale());
        if (logoutMessage != null) {
            model.addAttribute("logoutMessage", logoutMessage);
        }
        // TODO: Logout reasons may expand (i18n, session revocation, allow-IP sync). Centralize reason codes/message keys if it grows.

        return "admin/main/adminLogin";
    }

    @RequestMapping({"/toy/admin/loginAction.ac"})
    public ModelAndView toyAdmLoginAction(@ModelAttribute("userVO") MngrVO mngrVO, HttpServletRequest request) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        // Do not trust client-provided role filters. Login should not be restricted by a fixed role here.
        // The service should load the user's actual roles from DB.
        mngrVO.setAuthUuid(null);

        String accessIp = resolveClientIp(request);
        AdminLoginResult loginResult = admMainService.adminLogin(mngrVO, accessIp);

        // Default redirect URL (admin main)
        String returnURL = "/toy/admin/main.do";
        if (EgovStringUtil.isNotEmpty(request.getParameter("returnURL"))) {
            returnURL = request.getParameter("returnURL");
        }

        // Resolve localized message by messageCode
        String messageCode = loginResult.getMessageCode();
        String message = messageSource.getMessage(
                messageCode,
                loginResult.getMessageArgs(),
                "Login failed.",
                LocaleContextHolder.getLocale()
        );

        // Unified response fields
        resultMap.put("result", loginResult.isSuccess() ? "Y" : "N");
        resultMap.put("messageCode", messageCode);
        resultMap.put("message", message);


        // 1. Failure case
        if (!loginResult.isSuccess()) {
            return new ModelAndView("jsonView", resultMap);
        }

        // 2. Success Case
        SessionAdminVO sessionAdminVO = loginResult.getSessionUser();
        // Use a single session key to match interceptor & header usage
        request.getSession().setAttribute("sessionAdminVO", sessionAdminVO);

        boolean isAdmin = (sessionAdminVO.getAuth() != null && sessionAdminVO.getAuth().contains("ADMINISTRATOR"));
        String masterType = isAdmin ? "master" : "entrps";

        // Leave Action Trace on SysLog
        this.adminAccessLogService.insertAdminAccessLog("Admin > Login", request);

        resultMap.put("masterType", masterType);
        resultMap.put("returnURL", returnURL);

        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping({"/toy/admin/logout.ac"})
    public String toyAdminLogout(HttpServletRequest request, HttpServletResponse response,                                  @RequestParam(value = "reason", required = false) String reason) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        } catch (Exception e) {
            LOG_DEBUG.info(e.toString());
        }

        if (reason == null || reason.trim().isEmpty()) {
            return "redirect:/toy/admin/login.do";
        }

        return "redirect:/toy/admin/login.do?reason=" + reason.trim();
    }

    // Prefer proxy headers when present; fallback to servlet remote address.
    private String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String xff = request.getHeader("X-Forwarded-For");
        if (EgovStringUtil.isNotEmpty(xff)) {
            // XFF can contain a chain. The left-most IP is the original client in common setups.
            String first = xff.split(",")[0];
            if (first != null && !first.trim().isEmpty() && !"unknown".equalsIgnoreCase(first.trim())) {
                return first.trim();
            }
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (EgovStringUtil.isNotEmpty(xRealIp) && !"unknown".equalsIgnoreCase(xRealIp.trim())) {
            return xRealIp.trim();
        }

        String remoteAddr = request.getRemoteAddr();
        return (remoteAddr == null) ? "" : remoteAddr.trim();
    }



    @RequestMapping({"/toy/admin/main.do"})
    public String main(HttpServletRequest request) throws Exception {
        LOG_DEBUG.debug("/toy/admin/main.do");

        // Return admin main page
        return "admin/main/adminMain";
    }

    // Encrypt Password Test API
    /*@RequestMapping("/toy/admin/encryptTest.do")
    public ModelAndView encryptTest(@RequestParam("pw") String pw) throws Exception {
        Map<String, Object> map = new HashMap<>();

        String key = EgovPropertiesUtils.getProperty("DB.ENCRYPTION.KEY");
        String enc = CmUtil.encryptPassword(pw, key);

        map.put("raw", pw);
        map.put("encrypted", enc);

        return new ModelAndView("jsonView", map);
    }*/

}
