package toy.admin.main.web;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
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
import toy.admin.system.accesslog.service.impl.AdminAccessLogServiceImpl;
import toy.admin.test.service.AdmTestService;
import toy.com.egov.EgovPropertiesUtils;
import toy.com.util.EgovStringUtil;
import toy.com.vo.TestVO;
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
    private final AdmTestService admTestService;
    private final AdminAccessLogService adminAccessLogService;

    private static final Logger LOG_DEBUG = LoggerFactory.getLogger(AdmMainCtrl.class);


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

        return "admin/adminLogin";
    }

    @RequestMapping({"/toy/admin/loginAction.ac"})
    public ModelAndView toyAdmLoginAction(@ModelAttribute("userVO") MngrVO mngrVO, HttpServletRequest request) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        // Do not force a fixed role. Allow any registered role to login.
        // (Optionally, you can require at least one role in service layer.)
        mngrVO.setAuthUuid("ADMINISTRATOR");

        AdminLoginResult loginResult = admMainService.adminLogin(mngrVO);

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
        SessionAdminVO sessionUserVO = loginResult.getSessionUser();
        // Use a single session key to match interceptor & header usage
        request.getSession().setAttribute("sessionAdminVO", sessionUserVO);

        boolean isAdmin = (sessionUserVO.getAuth() != null && sessionUserVO.getAuth().contains("ADMINISTRATOR"));
        String masterType = isAdmin ? "master" : "entrps";

        // Leave Action Trace on SysLog
        this.adminAccessLogService.insertAdminAccessLog("Admin > Login", request);

        resultMap.put("masterType", masterType);
        resultMap.put("returnURL", returnURL);

        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping({"/toy/admin/logout.ac"})
    public String toyAdminLogout(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getSession().invalidate();
        } catch (Exception e) {
            LOG_DEBUG.info(e.toString());
        }

        return "redirect:/toy/admin/login.do";
    }



    @RequestMapping({"/toy/admin/main.do"})
    public ModelAndView main(HttpServletRequest request) throws Exception {
        LOG_DEBUG.debug("/toy/admin/main.do");

        // 로그인기능 구현할떄 구현예정
        SessionAdminVO sessionUserVO = (SessionAdminVO)request.getSession().getAttribute("sessionAdminVO");

        //공통레이아웃 적용 확인을 위한 테스트페이지
        // create empty search condition (no filters for now)
        TestVO searchVO = new TestVO();
        // get member list from DB
        List<TestVO> memberList = admTestService.selectTestList(searchVO);
        Map<String, Object> resultMap = new HashMap<>();
        // put list into model (JSP will use this attribute)
        resultMap.put("memberList", memberList);

        // ToyProject view name example:
        // /WEB-INF/jsp/toy/admin/adminHome.jsp
        return new ModelAndView("admin/adminHome", resultMap);

        // 추후 제대로된 메인페이지 만들면 그쪽으로 리다이렉트 예정
        //return new ModelAndView("redirect:/toy/listMngr.ac", resultMap);
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
