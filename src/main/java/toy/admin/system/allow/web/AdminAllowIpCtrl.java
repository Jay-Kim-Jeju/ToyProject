package toy.admin.system.allow.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import toy.admin.system.accesslog.service.AdminAccessLogService;
import toy.admin.system.allow.service.AdminAllowIpService;
import toy.admin.system.mngr.service.AdminManagerService;
import toy.com.util.CmConstants;
import toy.com.util.EgovStringUtil;
import toy.com.util.PagingParamUtil;
import toy.com.util.ToyAdminAuthUtils;
import toy.com.vo.system.allow.AdminAllowIpVO;
import toy.com.vo.system.mngr.MngrVO;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/toy/admin/sys/allow")
public class AdminAllowIpCtrl {

    private final AdminAllowIpService adminAllowIpService;
    private final AdminManagerService adminManagerService;
    private final AdminAccessLogService adminAccessLogService;

    // Base menu map must be immutable; build a new map per request to avoid shared mutable state.
    private static final Map<String, String> MENU_BASE = Map.of("adminMenu1", "system");
    private static final String MENU_ROLE = "SYSTEM";

    /* =========================
     * View Pages (.do)
     * ========================= */

    @RequestMapping(value = "/list.do", method = RequestMethod.GET)
    public String viewAllowIpListPage(HttpServletRequest request, ModelMap model) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Allow IP > List Page", request);

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyView;
        }

        Map<String, String> menuActiveMap = new HashMap<>(MENU_BASE);
        menuActiveMap.put("adminMenu2", "allow");
        model.addAttribute("menuActiveMap", menuActiveMap);

        return "admin/system/allow/listAllowIp";
    }

    @RequestMapping(value = "/formPop.do", method = RequestMethod.GET)
    public String viewAllowIpFormPopup(@RequestParam(value = "allowIpUuid", required = false) String allowIpUuid,
                                       HttpServletRequest request,
                                       ModelMap model) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Allow IP > Form Popup", request);

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyView;
        }

        if (EgovStringUtil.isNotEmpty(allowIpUuid)) {
            AdminAllowIpVO detail = adminAllowIpService.selectAdminAllowIpDetail(allowIpUuid);
            if (detail == null) {
                return "redirect:/toy/admin/logout.ac?reason=" + CmConstants.LOGOUT_REASON_FORBIDDEN;
            }
            model.addAttribute("mode", "U");
            model.addAttribute("detail", detail);
        } else {
            model.addAttribute("mode", "C");
        }

        return "admin/system/allow/formPopAllowIp";
    }

    @RequestMapping(value = "/mngr/selectPop.do", method = RequestMethod.GET)
    public String viewSelectManagerPopup(HttpServletRequest request, ModelMap model) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Allow IP > Select Manager Popup", request);

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyView;
        }

        return "admin/system/allow/listPopSelectMngr";
    }

    /* =========================
     * AJAX JSON (.doax)
     * ========================= */

    @RequestMapping(value = "/list.doax", method = RequestMethod.POST)
    public ModelAndView ajaxSelectAllowIpList(@ModelAttribute("searchVO") AdminAllowIpVO searchVO,
                                              HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Allow IP > List", request);

        Map<String, Object> resultMap = new HashMap<>();
        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        PagingParamUtil.applyPagingDefaults(searchVO);

        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
        paginationInfo.setRecordCountPerPage(searchVO.getRecordCountPerPage());
        paginationInfo.setPageSize(searchVO.getPageSize());
        searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
        searchVO.setLastIndex(paginationInfo.getLastRecordIndex());

        normalizeUseYnForSearch(searchVO);

        Map<String, Object> serviceMap = adminAllowIpService.selectAdminAllowIpList(searchVO);
        resultMap.put("data", serviceMap.get("resultList"));
        resultMap.put("itemsCount", serviceMap.get("totalCnt"));
        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping(value = "/mngr/select/list.doax", method = RequestMethod.POST)
    public ModelAndView ajaxSelectManagerForPopup(@ModelAttribute("searchVO") MngrVO searchVO,
                                                  HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Allow IP > Select Manager List", request);

        Map<String, Object> resultMap = new HashMap<>();
        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        PagingParamUtil.applyPagingDefaults(searchVO);

        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
        paginationInfo.setRecordCountPerPage(searchVO.getRecordCountPerPage());
        paginationInfo.setPageSize(searchVO.getPageSize());
        searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
        searchVO.setLastIndex(paginationInfo.getLastRecordIndex());

        // Allow-IP manager picker defaults to active managers only.
        searchVO.setUseYn(CmConstants.FLAG_Y);

        int totalCnt = adminManagerService.selectMngrListCount(searchVO);
        List<MngrVO> list = adminManagerService.selectMngrList(searchVO);

        resultMap.put("data", list);
        resultMap.put("itemsCount", totalCnt);
        return new ModelAndView("jsonView", resultMap);
    }

    /* =========================
     * Actions (.ac) - Mutations
     * ========================= */

    @RequestMapping(value = "/insert.ac", method = RequestMethod.POST)
    public ModelAndView ajaxInsertAllowIp(@ModelAttribute("AdminAllowIpVO") AdminAllowIpVO vo,
                                          HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Allow IP > Insert", request);

        Map<String, Object> resultMap = new HashMap<>();
        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        normalizeUseYn(vo);

        int affected = adminAllowIpService.insertAdminAllowIp(vo);
        applyMutationResult(resultMap, affected, "Insert failed. Please contact the administrator.");
        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping(value = "/update.ac", method = RequestMethod.POST)
    public ModelAndView ajaxUpdateAllowIp(@ModelAttribute("AdminAllowIpVO") AdminAllowIpVO vo,
                                          HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Allow IP > Update", request);

        Map<String, Object> resultMap = new HashMap<>();
        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        normalizeUseYn(vo);

        int affected = adminAllowIpService.updateAdminAllowIpMeta(vo);
        applyMutationResult(resultMap, affected, "Update failed. Please contact the administrator.");
        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping(value = "/delete.ac", method = RequestMethod.POST)
    public ModelAndView ajaxSoftDeleteAllowIp(@RequestParam("allowIpUuid") String allowIpUuid,
                                              HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Allow IP > Soft Delete", request);

        Map<String, Object> resultMap = new HashMap<>();
        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        if (EgovStringUtil.isEmpty(allowIpUuid)) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "allowIpUuid is required.");
            return new ModelAndView("jsonView", resultMap);
        }

        int affected = adminAllowIpService.softDeleteAdminAllowIp(allowIpUuid);
        if (affected > 0) {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
        } else if (affected == CmConstants.RESULT_FAIL) {
            resultMap.put("result", "None");
            resultMap.put("errorMessage", "Allow IP does not exist.");
        } else if (affected == CmConstants.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data.");
        } else {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", "Delete failed. Please contact the administrator.");
        }

        return new ModelAndView("jsonView", resultMap);
    }

    /* =========================
     * Private helpers
     * ========================= */

    private ModelAndView denyJson(Map<String, Object> resultMap) {
        resultMap.put("result", "N");
        resultMap.put("redirectUrl", "/toy/admin/logout.ac?reason=" + CmConstants.LOGOUT_REASON_FORBIDDEN);
        resultMap.put("errorMessage", "Permission denied.");
        return new ModelAndView("jsonView", resultMap);
    }

    private void normalizeUseYn(AdminAllowIpVO vo) {
        // Accept checkbox true/false values and normalize to Y/N.
        if (vo == null || EgovStringUtil.isEmpty(vo.getUseYn())) {
            return;
        }
        if ("true".equalsIgnoreCase(vo.getUseYn())) {
            vo.setUseYn(CmConstants.FLAG_Y);
        } else if ("false".equalsIgnoreCase(vo.getUseYn())) {
            vo.setUseYn(CmConstants.FLAG_N);
        }
    }

    private void normalizeUseYnForSearch(AdminAllowIpVO vo) {
        // For search filters, unchecked means "all" not "N".
        if (vo == null || EgovStringUtil.isEmpty(vo.getUseYn())) {
            return;
        }
        if ("true".equalsIgnoreCase(vo.getUseYn())) {
            vo.setUseYn(CmConstants.FLAG_Y);
        } else {
            vo.setUseYn(null);
        }
    }

    private void applyMutationResult(Map<String, Object> resultMap, int affected, String defaultFailMsg) {
        if (affected > 0) {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
            return;
        }
        if (affected == CmConstants.RESULT_DUPLE) {
            resultMap.put("result", "Duple");
            resultMap.put("errorMessage", "Duplicate allow IP mapping.");
            return;
        }
        if (affected == CmConstants.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data.");
            return;
        }
        if (affected == CmConstants.RESULT_FORBIDDEN) {
            resultMap.put("result", "Forbidden");
            resultMap.put("errorMessage", "Soft-deleted rows cannot be re-activated. Please re-register.");
            return;
        }
        resultMap.put("result", "N");
        resultMap.put("errorMessage", defaultFailMsg);
    }
}
