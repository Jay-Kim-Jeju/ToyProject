package toy.admin.example.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminHomeController {

    @RequestMapping("/admin/home.ac")
    public String home() {
        return "admin/home"; //
        // /WEB-INF/jsp/admin/home.jsp 로 forward됨
    }
}