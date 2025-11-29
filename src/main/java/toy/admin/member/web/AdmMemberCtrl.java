package toy.admin.member.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

import toy.admin.member.service.AdmMemberService;
import toy.com.vo.MemberVO;

@Controller
public class AdmMemberCtrl {

    @Resource(name = "admMemberService")
    private AdmMemberService admMemberService;

    @RequestMapping("/index.do")
    public String index(Model model) throws Exception {

        MemberVO searchVO = new MemberVO();   // 지금은 검색조건 없음
        List<MemberVO> memberList = admMemberService.selectMemberList(searchVO);

        model.addAttribute("memberList", memberList);

        // webapp 루트의 index.jsp로 forward (ViewResolver 안 타고 직접 JSP로 감)
        return "forward:/index.jsp";
    }
}
