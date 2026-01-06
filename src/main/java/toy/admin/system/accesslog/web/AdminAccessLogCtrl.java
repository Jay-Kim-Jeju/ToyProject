package toy.admin.system.accesslog.web;

import lombok.RequiredArgsConstructor;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import toy.admin.system.accesslog.service.AdminAccessLogService;
import toy.com.util.CmConstants;
import toy.com.util.EgovStringUtil;
import toy.com.util.PagingParamUtil;
import toy.com.util.ToyAdminAuthUtils;
import toy.com.vo.system.accesslog.AdminAccessLogSearchVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/toy/admin/sys/accesslog")
public class AdminAccessLogCtrl {

    private final AdminAccessLogService adminAccessLogService;

    // Base menu map must be immutable; build a new map per request to avoid shared mutable state.
    private static final Map<String, String> MENU_BASE = Map.of("adminMenu1", "system");
    private static final String MENU_ROLE = "SYSTEM";

    @RequestMapping({"/listAdmAcssLog.do"})
    public String listAdmAcssLog(@ModelAttribute("searchVO") AdminAccessLogSearchVO searchVO,
                                 ModelMap model,
                                 HttpServletRequest request) throws Exception {

        this.adminAccessLogService.insertAdminAccessLog("Admin > System > Access_Log List Page", request);

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyView;
        }

        Map<String, String> menuActiveMap = new HashMap<>(MENU_BASE);
        menuActiveMap.put("adminMenu2", "accesslog");
        model.addAttribute("menuActiveMap", menuActiveMap);
        return "admin/system/accesslog/listAdmAcssLog";

    }

    @RequestMapping({"/selectAdmAcssLogList.doax"})
    public ModelAndView selectAdmAcssLogList(@ModelAttribute("searchVO") AdminAccessLogSearchVO searchVO,
                                             HttpServletRequest request) throws Exception {


        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            resultMap.put("result", "N");
            resultMap.put("redirectUrl", "/toy/admin/logout.ac?reason=" + CmConstants.LOGOUT_REASON_FORBIDDEN);
            resultMap.put("errorMessage", "Permission denied.");
            return new ModelAndView("jsonView", resultMap);
        }


        // Permission check passed, now normalize inputs.
        PagingParamUtil.applyPagingDefaults(searchVO);

        // This paging flow controller style.
        PaginationInfo paginationInfo = new PaginationInfo();

        // Inputs (set by client or defaults in PagingDefaultVO)
        paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
        paginationInfo.setRecordCountPerPage(searchVO.getRecordCountPerPage()); // rows per page
        paginationInfo.setPageSize(searchVO.getPageSize());                     // pager UI size

        // Calculated values (used by SQL LIMIT offset/size)
        searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
        searchVO.setLastIndex(paginationInfo.getLastRecordIndex());

        Map<String, Object> dataList = adminAccessLogService.selectAdminAccessLogList(searchVO);
        Integer totalCnt = (Integer) dataList.get("totalCnt");

        // 이 값이 들어가야 PaginationInfo가 내부적으로 전체 페이지 수, 페이지 리스트 시작/끝 번호 같은 “파생 값들”을 계산가능
        // Total count must be set so PaginationInfo can calculate derived paging values.
        paginationInfo.setTotalRecordCount(totalCnt == null ? 0 : totalCnt);

        resultMap.put("data", dataList.get("resultList"));
        resultMap.put("itemsCount", totalCnt == null ? 0 : totalCnt);

        return new ModelAndView("jsonView", resultMap);
    }



}
