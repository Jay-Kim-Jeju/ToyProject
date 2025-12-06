package toy.admin.member.web;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import toy.admin.member.service.AdmMemberService;
import toy.com.vo.MemberVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class AdmMemberCtrl {

    // inject AdmMemberService (member list, detail, etc.)
    private final AdmMemberService admMemberService;

    private static final Logger LOG_DEBUG = LoggerFactory.getLogger(AdmMemberCtrl.class);

    @RequestMapping("/toy/admin/member/list.ac")
    public ModelAndView list(HttpServletRequest request) throws Exception {

        LOG_DEBUG.debug("/toy/admin/member/list.ac");

        MemberVO searchVO = new MemberVO(); // TODO: bind real search condition later

        List<MemberVO> memberList = admMemberService.selectMemberList(searchVO);

        Map<String, Object> resultMap = new HashMap<>();
        // same attribute name as main page to reuse JSP if needed
        resultMap.put("memberList", memberList);

        // ex) /WEB-INF/jsp/toy/admin/member/memberList.jsp
        return new ModelAndView("toy/admin/member/memberList", resultMap);
    }
}
