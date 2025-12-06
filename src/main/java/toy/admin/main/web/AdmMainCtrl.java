package toy.admin.main.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import toy.com.common.service.CommonService;
//import toy.com.egov.EgovPropertiesUtils;
//import toy.com.util.EgovStringUtil;
//import toy.com.vo.common.SessionAdminVO;
//import toy.com.vo.system.MngrVO;
import toy.admin.main.service.AdmMainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;
import toy.admin.member.service.AdmMemberService;
import toy.com.vo.MemberVO;

@RequiredArgsConstructor
@Controller
public class AdmMainCtrl {

    //yet to implement
    //private final AdmMainService admMainService;
    //private final CommonService commonService;
    private final AdmMemberService admMemberService;


    private static final Logger LOG_DEBUG = LoggerFactory.getLogger(AdmMainCtrl.class);


    @RequestMapping({"/toy/main.ac"})
    public ModelAndView main(HttpServletRequest request) throws Exception {
        LOG_DEBUG.debug("/toy/main.ac");

        // 로그인기능 구현할떄 구현예정
        //SessionAdminVO sessionUserVO = (SessionAdminVO)request.getSession().getAttribute("sessionAdminVO");

        //공통레이아웃 적용 확인을 위한 테스트페이지
        // create empty search condition (no filters for now)
        MemberVO searchVO = new MemberVO();
        // get member list from DB
        List<MemberVO> memberList = admMemberService.selectMemberList(searchVO);
        Map<String, Object> resultMap = new HashMap<>();
        // put list into model (JSP will use this attribute)
        resultMap.put("memberList", memberList);

        // ToyProject view name example:
        // /WEB-INF/jsp/toy/admin/adminHome.jsp
        return new ModelAndView("toy/admin/adminHome", resultMap);

        // 추후 제대로된 메인페이지 만들면 그쪽으로 리다이렉트 예정
        //return new ModelAndView("redirect:/miniadm/today/listToday.ac", resultMap);
    }

}
