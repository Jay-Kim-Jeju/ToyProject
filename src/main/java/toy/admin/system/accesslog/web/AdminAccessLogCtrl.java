package toy.admin.system.accesslog.web;

import lombok.RequiredArgsConstructor;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import toy.admin.system.accesslog.service.AdminAccessLogService;
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

    @Resource(name = "adminAccessLogService")
    private final AdminAccessLogService adminAccessLogService;

    private Map<String, String> menuActiveMap = new HashMap<String, String>() {
        {
            this.put("adminMenu1", "system");
        }
    };

    @RequestMapping({"/listAdmAcssLog.do"})
    public String listAdmAcssLog(@ModelAttribute("searchVO") AdminAccessLogSearchVO searchVO,
                                 ModelMap model,
                                 HttpServletRequest request) throws Exception {

        String chkStr = ToyAdminAuthUtils.chkAdminMenuPermission(null);
        if (EgovStringUtil.isNotEmpty(chkStr)) {
            return chkStr;
        }

        else {
            // These values should match your header/menu logic.
            menuActiveMap.put("adminMenu2", "accesslog");
            menuActiveMap.put("adminMenu3", "list");

            // This map is used by the header to highlight active menus.
            model.addAttribute("menuActiveMap", menuActiveMap);

            return "admin/system/accesslog/listAdmAcssLog";
        }

    }

    @RequestMapping({"/selectAdmAcssLogList.doax"})
    public ModelAndView selectAdmAcssLogList(@ModelAttribute("searchVO") AdminAccessLogSearchVO searchVO,
                                             HttpServletRequest request) throws Exception {

        /*
            1. 현재 페이지/페이지당 건수 세팅
            2. offset 계산(= firstIndex)
            3. list 조회 + count 조회
            4. count 결과를 totalRecordCount에 넣음(파생 값 계산 가능 상태)
        */

        Map<String, Object> resultMap = new HashMap<>();

        // This should be replaced with your real permission check util if you have one.
        String chkStr = ToyAdminAuthUtils.chkAdminMenuPermission(null);
        if (EgovStringUtil.isNotEmpty(chkStr)) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", chkStr);
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
        paginationInfo.setTotalRecordCount(totalCnt == null ? 0 : totalCnt);

        resultMap.put("data", dataList.get("resultList"));
        resultMap.put("itemsCount", totalCnt == null ? 0 : totalCnt);

        return new ModelAndView("jsonView", resultMap);
    }



}
