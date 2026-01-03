package toy.admin.system.auth.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import toy.admin.system.accesslog.service.AdminAccessLogService;
import toy.admin.system.auth.service.AdminAuthService;
import toy.com.util.EgovStringUtil;
import toy.com.util.PagingParamUtil;
import toy.com.util.ToyAdminAuthUtils;
import toy.com.validation.group.ValidationGroups;
import toy.com.vo.system.auth.AdminAuthBatchResult;
import toy.com.vo.system.auth.AuthVO;
import toy.com.vo.system.mngr.MngrVO;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/toy/admin/sys/auth")
public class AdminAuthCtrl {

    private final AdminAuthService adminAuthService;
    private final AdminAccessLogService adminAccessLogService;

    // Base menu map must be immutable; build a new map per request to avoid shared mutable state.
    private static final Map<String, String> MENU_BASE = Map.of("adminMenu1", "system");
    private static final String MENU_ROLE = "system";

    /* =========================
     * View Pages (.do)
     * ========================= */

    @RequestMapping(value = "/role/list.do", method = RequestMethod.GET)
    public String viewAuthRolePage(HttpServletRequest request, ModelMap model) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Auth > Role List Page", request);

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyView;
        }

        Map<String, String> menuActiveMap = new HashMap<>(MENU_BASE);
        menuActiveMap.put("adminMenu2", "auth");
        menuActiveMap.put("adminMenu3", "list");
        model.addAttribute("menuActiveMap", menuActiveMap);

        return "admin/system/auth/listAuth";
    }

    @RequestMapping(value = "/mngr/assignPop.do", method = RequestMethod.GET)
    public String viewAssignManagerPopup(@RequestParam("authUuid") String authUuid,
                                         HttpServletRequest request,
                                         ModelMap model) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Auth > Assign Manager Popup", request);

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyView;
        }

        model.addAttribute("authUuid", authUuid);

        return "admin/system/auth/listPopAssignMngr";
    }

    /* =========================
     * AJAX JSON (.doax)
     * ========================= */

    @RequestMapping(value = "/role/list.doax", method = RequestMethod.POST)
    public ModelAndView ajaxSelectAuthRoleList(@ModelAttribute("searchVO") AuthVO searchVO,
                                               HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Auth > Role List", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        // GUEST policy: allow entering menu, but return empty data for list pages.
        if (ToyAdminAuthUtils.isGuest()) {
            resultMap.put("data", List.of());
            resultMap.put("itemsCount", 0);
            return new ModelAndView("jsonView", resultMap);
        }

        PagingParamUtil.applyPagingDefaults(searchVO);

        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
        paginationInfo.setRecordCountPerPage(searchVO.getRecordCountPerPage());
        paginationInfo.setPageSize(searchVO.getPageSize());

        searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
        searchVO.setLastIndex(paginationInfo.getLastRecordIndex());

        normalizeUseYnForSearch(searchVO);

        int totalCnt = adminAuthService.selectAdminAuthRoleListCnt(searchVO);
        paginationInfo.setTotalRecordCount(totalCnt);

        List<AuthVO> list = adminAuthService.selectAdminAuthRoleList(searchVO);

        resultMap.put("data", list);
        resultMap.put("itemsCount", totalCnt);
        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping(value = "/mngr/assigned/list.doax", method = RequestMethod.POST)
    public ModelAndView ajaxSelectAssignedMngrList(@ModelAttribute("searchVO") MngrVO searchVO,
                                                   HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Auth > Assigned Manager List", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        if (ToyAdminAuthUtils.isGuest()) {
            resultMap.put("data", List.of());
            resultMap.put("itemsCount", 0);
            return new ModelAndView("jsonView", resultMap);
        }

        PagingParamUtil.applyPagingDefaults(searchVO);

        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
        paginationInfo.setRecordCountPerPage(searchVO.getRecordCountPerPage());
        paginationInfo.setPageSize(searchVO.getPageSize());

        searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
        searchVO.setLastIndex(paginationInfo.getLastRecordIndex());

        int totalCnt = adminAuthService.selectAdminAuthAssignedMngrListCnt(searchVO);
        paginationInfo.setTotalRecordCount(totalCnt);

        List<MngrVO> list = adminAuthService.selectAdminAuthAssignedMngrList(searchVO);

        resultMap.put("data", list);
        resultMap.put("itemsCount", totalCnt);
        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping(value = "/mngr/unassigned/list.doax", method = RequestMethod.POST)
    public ModelAndView ajaxSelectUnassignedMngrList(@ModelAttribute("searchVO") MngrVO searchVO,
                                                     HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Auth > Unassigned Manager List", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        if (ToyAdminAuthUtils.isGuest()) {
            resultMap.put("data", List.of());
            resultMap.put("itemsCount", 0);
            return new ModelAndView("jsonView", resultMap);
        }

        PagingParamUtil.applyPagingDefaults(searchVO);

        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
        paginationInfo.setRecordCountPerPage(searchVO.getRecordCountPerPage());
        paginationInfo.setPageSize(searchVO.getPageSize());

        searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
        searchVO.setLastIndex(paginationInfo.getLastRecordIndex());

        normalizeUseYnForSearch(searchVO);

        int totalCnt = adminAuthService.selectAdminAuthUnassignedMngrListCnt(searchVO);
        paginationInfo.setTotalRecordCount(totalCnt);

        List<MngrVO> list = adminAuthService.selectAdminAuthUnassignedMngrList(searchVO);

        resultMap.put("data", list);
        resultMap.put("itemsCount", totalCnt);
        return new ModelAndView("jsonView", resultMap);
    }

    /* =========================
     * Actions (.ac) - Mutations
     * ========================= */

    @RequestMapping(value = "/role/insert.ac", method = RequestMethod.POST)
    public ModelAndView ajaxInsertAuthRole(@Validated(ValidationGroups.Create.class) @ModelAttribute("AuthVO") AuthVO vo,
                                           BindingResult bindingResult,
                                           HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Auth > Role Insert", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        if (bindingResult.hasErrors()) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", resultMap);
        }

        int affected = adminAuthService.insertAdminAuthRole(vo);

        if (affected > 0) {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
        } else if (affected == AdminAuthService.RESULT_DUPLE) {
            resultMap.put("result", "Duple");
            resultMap.put("errorMessage", "Duplicate auth role ID.");
        } else if (affected == AdminAuthService.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data (constraint violation).");
        } else {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", "Insert failed. Please contact the administrator.");
        }

        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping(value = "/role/update.ac", method = RequestMethod.POST)
    public ModelAndView ajaxUpdateAuthRole(@Validated(ValidationGroups.Update.class) @ModelAttribute("AuthVO") AuthVO vo,
                                           BindingResult bindingResult,
                                           HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Auth > Role Update", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        if (bindingResult.hasErrors()) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", resultMap);
        }

        normalizeUseYn(vo);

        // Block disabling default roles via update path (useYn toggle in edit row).
        if (isDefaultRole(vo.getAuthUuid()) && "N".equals(vo.getUseYn())) {
            resultMap.put("result", "Forbidden");
            resultMap.put("errorMessage", "Default roles cannot be disabled.");
            return new ModelAndView("jsonView", resultMap);
        }

        int affected = adminAuthService.updateAdminAuthRole(vo);

        if (affected > 0) {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
        } else if (affected == AdminAuthService.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data (constraint violation).");
        } else if (affected == 0) {
            resultMap.put("result", "None");
            resultMap.put("errorMessage", "Auth role does not exist.");
        } else {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", "Update failed. Please contact the administrator.");
        }

        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping(value = "/role/disable.ac", method = RequestMethod.POST)
    public ModelAndView ajaxDisableAuthRole(@Validated(ValidationGroups.Key.class) @ModelAttribute("AuthVO") AuthVO vo,
                                            BindingResult bindingResult,
                                            HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Auth > Role Disable", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        if (bindingResult.hasErrors()) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", resultMap);
        }


        // Prevent disabling system default roles to avoid breaking global auth behavior.
        if ("ADMINISTRATOR".equals(vo.getAuthUuid()) || "GUEST".equals(vo.getAuthUuid())) {
            resultMap.put("result", "Forbidden");
            resultMap.put("errorMessage", "Default roles cannot be disabled.");
            return new ModelAndView("jsonView", resultMap);
        }

        int affected = adminAuthService.disableAdminAuthRole(vo.getAuthUuid());

        if (affected > 0) {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
        } else if (affected == AdminAuthService.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data (constraint violation).");
        } else if (affected == 0) {
            resultMap.put("result", "None");
            resultMap.put("errorMessage", "Auth role does not exist.");
        } else {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", "Disable failed. Please contact the administrator.");
        }

        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping(value = "/mngr/assign.ac", method = RequestMethod.POST)
    public ModelAndView ajaxAssignAuthToManagers(@RequestParam("authUuid") String authUuid,
                                                 @RequestBody(required = false) List<String> mngrUidList,
                                                 HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Auth > Assign Managers", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        if (mngrUidList == null || mngrUidList.isEmpty()) {
            resultMap.put("result", "None");
            resultMap.put("errorMessage", "No managers selected.");
            return new ModelAndView("jsonView", resultMap);
        }

        try {
            AdminAuthBatchResult batch = adminAuthService.insertAdminAuthMngrList(authUuid, mngrUidList);

            String msg = buildBatchMessage(batch);

            resultMap.put("result", "Y");
            resultMap.put("errorMessage", msg);

            resultMap.put("requestedCount", batch.getRequestedCount());
            resultMap.put("insertedCount", batch.getInsertedCount());
            resultMap.put("duplicateCount", batch.getDuplicateCount());

            return new ModelAndView("jsonView", resultMap);

        } catch (IllegalStateException e) {
            // Invalid data occurred during batch insert (service throws to trigger rollback)
            log.warn("Batch auth assignment failed. authUuid={}", authUuid, e);
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data (constraint violation).");
            return new ModelAndView("jsonView", resultMap);
        }
    }

    @RequestMapping(value = "/mngr/unassign.ac", method = RequestMethod.POST)
    public ModelAndView ajaxUnassignAuthFromManager(@Validated(ValidationGroups.Key.class) @ModelAttribute("MngrVO") MngrVO vo,
                                                    BindingResult bindingResult,
                                                    HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Auth > Unassign Manager", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        if (bindingResult.hasErrors()) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", resultMap);
        }

        int affected = adminAuthService.deleteAdminAuthMngr(vo.getAuthUuid(), vo.getMngrUid());

        if (affected > 0) {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
        } else if (affected == 0) {
            resultMap.put("result", "None");
            resultMap.put("errorMessage", "Assignment does not exist.");
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
        resultMap.put("redirectUrl", "/toy/admin/login.do");
        resultMap.put("errorMessage", "Authentication required.");
        return new ModelAndView("jsonView", resultMap);
    }

    private void normalizeUseYn(AuthVO vo) {
        // Accept "true/false" (from some UI controls) and normalize to "Y/N".
        if (vo == null || EgovStringUtil.isEmpty(vo.getUseYn())) {
            return;
        }
        if ("true".equalsIgnoreCase(vo.getUseYn())) {
            vo.setUseYn("Y");
        } else if ("false".equalsIgnoreCase(vo.getUseYn())) {
            vo.setUseYn("N");
        }
    }

    private void normalizeUseYnForSearch(AuthVO vo) {
        // For search filters, unchecked checkbox should mean "no filter", not "N".
        if (vo == null || EgovStringUtil.isEmpty(vo.getUseYn())) {
            return;
        }
        if ("true".equalsIgnoreCase(vo.getUseYn())) {
            vo.setUseYn("Y");
        } else {
            vo.setUseYn(null);
        }
    }

    private void normalizeUseYnForSearch(MngrVO vo) {
        // For search filters, unchecked checkbox should mean "no filter", not "N".
        if (vo == null || EgovStringUtil.isEmpty(vo.getUseYn())) {
            return;
        }
        if ("true".equalsIgnoreCase(vo.getUseYn())) {
            vo.setUseYn("Y");
        } else {
            vo.setUseYn(null);
        }
    }

    private String firstErrorMessage(BindingResult bindingResult) {
        // Return only the first error message to keep the response simple for UI.
        if (bindingResult == null || !bindingResult.hasErrors()) {
            return "";
        }
        return bindingResult.getAllErrors().get(0).getDefaultMessage();
    }

    private String buildBatchMessage(AdminAuthBatchResult batch) {
        // Create a user-friendly message for UI.
        int inserted = batch.getInsertedCount();
        int dup = batch.getDuplicateCount();
        int requested = batch.getRequestedCount();

        StringBuilder sb = new StringBuilder();
        sb.append("Requested ").append(requested).append(". ");

        sb.append("Assigned ").append(inserted).append(". ");

        if (dup > 0) {
            sb.append("Skipped ").append(dup).append(" duplicate(s).");
        }

        return sb.toString();
    }


    private boolean isDefaultRole(String authUuid) {
        return "ADMINISTRATOR".equals(authUuid) || "GUEST".equals(authUuid);
    }
}
