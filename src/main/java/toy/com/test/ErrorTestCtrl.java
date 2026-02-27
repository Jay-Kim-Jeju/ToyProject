package toy.com.test;
import org.egovframe.rte.fdl.cmmn.exception.EgovBizException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/toy/test/error")
public class ErrorTestCtrl {

    // 1) DataAccessException 매핑 테스트: com/error/dataAccessFailure.jsp
    @GetMapping("/dataAccess.ac")
    public String dataAccessError() {
        // SimpleMappingExceptionResolver will map this to com/error/dataAccessFailure
        throw new DataRetrievalFailureException("Test DataAccessException");
    }

    // 2) TransactionException 매핑 테스트: com/error/transactionFailure.jsp
    @GetMapping("/tx.ac")
    public String txError() {
        throw new TransactionSystemException("Test TransactionException");
    }

    // 3) EgovBizException 매핑 테스트: com/error/egovError.jsp
    @GetMapping("/biz.ac")
    public String bizError() throws EgovBizException {
        // If EgovBizException class is on classpath
        throw new EgovBizException("Test EgovBizException");
    }

    // 4) MissingServletRequestParameterException: com/error/missingParameterError.jsp
    @GetMapping("/missingParam.ac")
    public String missingParam(@RequestParam("id") String id) {
        // Call without 'id' to trigger exception
        return "admin/main/adminMain";
    }

    // 5) TypeMismatchException: com/error/typeMismatchError.jsp
    @GetMapping("/typeMismatch.ac")
    public String typeMismatch(@RequestParam("page") int page) {
        // Call with page=abc to trigger type mismatch
        return "admin/main/adminMain";
    }

    // 6) HttpRequestMethodNotSupportedException: com/error/methodNotSupportedError.jsp
    @PostMapping("/postOnly.ac")
    public String postOnly() {
        // Call this URL with GET method to trigger HttpRequestMethodNotSupportedException
        return "admin/main/adminMain";
    }

    // 7) AccessDeniedException: com/error/accessDenied.jsp
    @GetMapping("/accessDenied.ac")
    public String accessDenied() {
        throw new AccessDeniedException("Test AccessDeniedException");
    }
}