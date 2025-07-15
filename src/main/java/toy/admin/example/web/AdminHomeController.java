package toy.admin.example.web;

import org.apache.ibatis.transaction.TransactionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminHomeController {

    private static final Logger log = LogManager.getLogger(AdminHomeController.class);

    @RequestMapping("/admin/testError.ac")
    public String testError() {
        throw new RuntimeException("일부러 발생시킨 에러입니다.");
    }

    @RequestMapping("/admin/testDbError.ac")
    public String testDbError() {
        throw new DataAccessException("DB 접속 오류 발생") {};
    }

    @RequestMapping("/admin/testTransactionError.ac")
    public String testTxError() {
        throw new TransactionException("트랜잭션 실패") {};
    }

    @RequestMapping("/admin/testNullError.ac")
    public String testNullError() {
        String str = null;
        str.length();  // NullPointerException 발생
        return "admin/home";
    }



    @RequestMapping("/admin/home.ac")
    public String home() {
        log.debug("관리자 홈 요청 들어옴");

        try {
            String str = null;
            str.length(); // NPE 유도
        } catch (Exception e) {
            log.error("에러 발생: {}", e.getMessage(), e);
            throw e; // throw 해야 500 에러 페이지로 이동
        }


        return "admin/home"; //
        // /WEB-INF/jsp/admin/home.jsp 로 forward됨
    }
}