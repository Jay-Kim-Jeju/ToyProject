package toy.admin.system.accesslog.service.impl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import toy.admin.main.service.impl.AdmMainServiceImpl;
import toy.admin.system.accesslog.dao.AdminAccessLogDAO;
import toy.admin.system.accesslog.service.AdminAccessLogService;
import toy.com.egov.EgovUserDetailsHelper;
import toy.com.vo.system.accesslog.AdminAccessLogSearchVO;
import toy.com.vo.system.accesslog.AdminAccessLogVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("adminAccessLogService")
@RequiredArgsConstructor
public class AdminAccessLogServiceImpl implements AdminAccessLogService {

    private static final Logger LOG_DEBUG = LoggerFactory.getLogger(AdmMainServiceImpl.class);

    @Resource(name = "AdminAccessLogDAO")
    private final AdminAccessLogDAO adminAccessLogDAO;

    @Override
    public Map<String, Object> selectAdminAccessLogList(AdminAccessLogSearchVO searchVO) throws Exception {
        // This method is designed for jsGrid: it needs both data list and total count.
        List<AdminAccessLogVO> resultList = adminAccessLogDAO.selectAdminAccessLogList(searchVO);
        int totalCnt = adminAccessLogDAO.selectAdminAccessLogCount(searchVO);

        Map<String, Object> map = new HashMap<>();
        map.put("resultList", resultList);
        map.put("totalCnt", totalCnt);
        return map;
    }

    @Override
    public void insertAdminAccessLog(String actionDesc, HttpServletRequest request) throws Exception {

        AdminAccessLogVO adminAccessLogVO = new AdminAccessLogVO();
        adminAccessLogVO.setMngrUid(EgovUserDetailsHelper.getAdminUid());
        String var10001 = UUID.randomUUID().toString();
        adminAccessLogVO.setLogUuid("ACL_" + var10001.replace("-", ""));
        String userIp = request.getRemoteAddr();
        adminAccessLogVO.setAccessIp(userIp);
        adminAccessLogVO.setReqUri(request.getRequestURI());
        adminAccessLogVO.setActionDesc(actionDesc);

        try {
            adminAccessLogDAO.insertAdminAccessLog(adminAccessLogVO);
        } catch (Exception e) {
            // Print the real root cause for MyBatis/Mapper proxy failures.
            LOG_DEBUG.error("AccessLog insert failed.", e);
            throw e;
        }

    }
}
