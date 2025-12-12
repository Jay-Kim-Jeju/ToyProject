package toy.admin.main.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
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
//import toy.com.vo.system.MngrVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import toy.admin.main.service.AdmMainService;
import toy.admin.test.service.AdmTestService;
import toy.admin.test.service.impl.AdmTestServiceImpl;
import toy.com.egov.EgovPropertiesUtils;
import toy.com.util.CmUtil;
import toy.com.util.EgovStringUtil;
import toy.com.vo.TestVO;
import toy.com.vo.common.AdminLoginResult;
import toy.com.vo.common.SessionAdminVO;
import toy.com.vo.system.MngrVO;

@RequiredArgsConstructor
@Controller
public class AdmMainCtrl {

    @Resource(name = "AdmMainService")
    private final AdmMainService admMainService;
    //private final CommonService commonService;
    private static final Logger LOG_DEBUG = LoggerFactory.getLogger(AdmMainCtrl.class);
    private final AdmTestService admTestService;

    @RequestMapping({"/toy/admin/login.ac"})
    public String toyAdmLogin(ModelMap model, HttpServletRequest request) throws Exception {

        // 세션 전체 invalidate
        // 새로고침할때마다 login.ac에서 매번 session.invalidate()를 호출해서 CSRF 토큰과 세션이 안 맞는 상태가 되면서 403발생
        //request.getSession().invalidate();

        // 1) 세션 전체 invalidate 대신, 필요한 값만 제거
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("sessionAdminVO");
            // 필요하면 추가로 제거
        }

        String returnURL = request.getParameter("returnURL");
        if (returnURL == null || returnURL.isEmpty()) {
            returnURL = "/toy/main.ac"; // or 원하는 기본 페이지
        }

        model.addAttribute("returnURL", returnURL);
        model.addAttribute("isLocal", EgovPropertiesUtils.getOptionalProp("IS_LOCAL"));

        return "admin/adminLogin";
    }

    @RequestMapping({"/toy/admin/loginAction.doax"})
    public ModelAndView toyAdmLoginAction(@ModelAttribute("userVO") MngrVO mngrVO, HttpServletRequest request) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", "N");
        resultMap.put("message", "로그인에 실패했습니다.");

        // 관리자 그룹 고정
        mngrVO.setAuthorGroupUuid("ADMINISTRATOR");

        AdminLoginResult loginResult = admMainService.adminLogin(mngrVO);

        // 1. 로그인 실패 케이스 처리
        if (!loginResult.isSuccess()) {
            if (loginResult.isLocked()) {
                // 잠금 상태
                resultMap.put("message", "로그인 횟수를 초과했습니다. 관리자에게 문의 바랍니다.");
            } else {
                // 일반 실패 (ID/비밀번호 불일치 등)
                resultMap.put("message", "아이디 또는 비밀번호가 일치하지 않거나, 권한이 없는 계정입니다.");
            }

            return new ModelAndView("jsonView", resultMap);
        }

        // 2. 로그인 성공 케이스
        SessionAdminVO sessionUserVO = loginResult.getSessionUser();

        // 권한 코드에 따라 세션 키 / masterType 분기
        String sessionStr = "ADMINISTRATOR".equals(sessionUserVO.getAuthorCd())
                ? "sessionAdminVO" : "sessionOtherVO";
        String masterType = "ADMINISTRATOR".equals(sessionUserVO.getAuthorCd())
                ? "master" : "entrps";

        request.getSession().setAttribute(sessionStr, sessionUserVO);

        // 기본 리다이렉트 URL (ToyProject에 맞게 수정 가능)
        String returnURL = "/toy/main.ac";
        if (EgovStringUtil.isNotEmpty(request.getParameter("returnURL"))) {
            returnURL = request.getParameter("returnURL");
        }

        resultMap.put("result", "Y");
        resultMap.put("message", "환영합니다!");
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

        return "redirect:/toy/admin/login.ac";
    }



    @RequestMapping({"/toy/main.ac"})
    public ModelAndView main(HttpServletRequest request) throws Exception {
        LOG_DEBUG.debug("/toy/main.ac");

        // 로그인기능 구현할떄 구현예정
        //SessionAdminVO sessionUserVO = (SessionAdminVO)request.getSession().getAttribute("sessionAdminVO");

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
