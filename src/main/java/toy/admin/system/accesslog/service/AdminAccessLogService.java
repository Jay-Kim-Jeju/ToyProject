package toy.admin.system.accesslog.service;

import toy.com.vo.system.accesslog.AdminAccessLogSearchVO;
import toy.com.vo.system.accesslog.AdminAccessLogVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface AdminAccessLogService {

    // This method returns both resultList and totalCnt for jsGrid paging.
    Map<String, Object> selectAdminAccessLogList(AdminAccessLogSearchVO searchVO) throws Exception;

    // This method inserts one access log record.
    void insertAdminAccessLog(String actionDesc, HttpServletRequest request) throws Exception;
}
