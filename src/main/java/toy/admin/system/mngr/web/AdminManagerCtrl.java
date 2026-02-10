package toy.admin.system.mngr.web;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import toy.admin.system.mngr.service.AdminManagerService;
import toy.admin.system.accesslog.service.AdminAccessLogService; // NOTE: adjust package if different
import toy.com.util.BindingResultUtil;
import toy.com.util.CmConstants;
import toy.com.util.EgovStringUtil;
import toy.com.util.PagingParamUtil;
import toy.com.util.ToyAdminAuthUtils;
import toy.com.vo.system.mngr.AdminMngrSmsResult;
import toy.com.vo.system.mngr.MngrVO;
import toy.com.vo.common.AdminLoginResult;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/toy/admin/sys/mngr")
public class AdminManagerCtrl {

    private final AdminManagerService adminManagerService;
    private final AdminAccessLogService adminAccessLogService;

    // Base menu map must be immutable; build a new map per request to avoid shared mutable state.
    private static final Map<String, String> MENU_BASE = Map.of("adminMenu1", "system");
    private static final String MENU_ROLE = "SYSTEM";

    /* =========================
     * View Pages (.do)
     * ========================= */

    @RequestMapping(value = "/list.do", method = RequestMethod.GET)
    public String viewMngrListPage(HttpServletRequest request, ModelMap model) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Manager > List Page", request);

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyView;
        }

        Map<String, String> menuActiveMap = new HashMap<>(MENU_BASE);
        menuActiveMap.put("adminMenu2", "mngr");
        model.addAttribute("menuActiveMap", menuActiveMap);

        // NOTE: change to your real JSP path
        return "admin/system/mngr/listMngr";
    }

    @RequestMapping(value = "/insertPop.do", method = RequestMethod.GET)
    public String viewInsertPopup(HttpServletRequest request, ModelMap model) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Manager > Insert Popup", request);

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyView;
        }

        // NOTE: change to your real JSP path
        return "admin/system/mngr/insertPopMngr";
    }

    @RequestMapping(value = "/detailPop.do", method = RequestMethod.GET)
    public String viewDetailPopup(@RequestParam("mngrUid") String mngrUid,
                                  HttpServletRequest request,
                                  ModelMap model) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Manager > Detail Popup", request);

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyView;
        }

        if (EgovStringUtil.isEmpty(mngrUid)) {
            // NOTE: adjust if you have a standard error view
            return "redirect:/toy/admin/logout.ac?reason=" + CmConstants.LOGOUT_REASON_FORBIDDEN;
        }

        MngrVO detail = adminManagerService.selectMngrDetail(mngrUid);
        model.addAttribute("detail", detail);


        // NOTE: change to your real JSP path
        return "admin/system/mngr/detailPopMngr";
    }

    /* =========================
     * AJAX JSON (.doax)
     * ========================= */

    @RequestMapping(value = "/list.doax", method = RequestMethod.POST)
    public ModelAndView ajaxSelectMngrList(@ModelAttribute("searchVO") MngrVO searchVO,
                                           HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Manager > List", request);

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

        int totalCnt = adminManagerService.selectMngrListCount(searchVO);
        paginationInfo.setTotalRecordCount(totalCnt);

        List<MngrVO> list = adminManagerService.selectMngrList(searchVO);

        resultMap.put("data", list);
        resultMap.put("itemsCount", totalCnt);
        return new ModelAndView("jsonView", resultMap);
    }

    /* =========================
     * Actions (.ac) - Mutations
     * ========================= */

    @RequestMapping(value = "/insert.ac", method = RequestMethod.POST)
    public ModelAndView ajaxInsertMngr(@Validated(toy.com.validation.group.ValidationGroups.Create.class)
                                       @ModelAttribute("MngrVO") MngrVO vo,
                                       BindingResult bindingResult,
                                       HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Manager > Insert", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        if (bindingResult.hasErrors()) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", BindingResultUtil.firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", resultMap);
        }

        AdminMngrSmsResult sr = adminManagerService.insertMngrAndSendTempPassword(vo);

        // RESULT_OK
        if (sr.getResult() == CmConstants.RESULT_OK) {
            String smsStatus = nvl(sr.getSmsStatus());

            if ("FAIL".equalsIgnoreCase(smsStatus)) {
                resultMap.put("result", "SmsFail");   // separate result
            } else {
                resultMap.put("result", "Y");
            }

            resultMap.put("smsStatus", smsStatus);
            resultMap.put("requestId", nvl(sr.getRequestId()));
            resultMap.put("errorMessage", ""); // UI decides messages
            return new ModelAndView("jsonView", resultMap);
        }

        // RESULT_DUPLE
        if (sr.getResult() == CmConstants.RESULT_DUPLE) {
            resultMap.put("result", "Duple");
            resultMap.put("errorMessage", "Duplicate manager ID.");

            // Optional fields (you added mngrUid)
            resultMap.put("existingUseYn", nvl(sr.getExistingUseYn()));
            resultMap.put("mngrUid", nvl(sr.getMngrUid()));
            return new ModelAndView("jsonView", resultMap);
        }

        // RESULT_INVALID
        if (sr.getResult() == CmConstants.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data (constraint violation).");
            return new ModelAndView("jsonView", resultMap);
        }

        resultMap.put("result", "N");
        resultMap.put("errorMessage", "Insert failed. Please contact the administrator.");
        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping(value = "/update.ac", method = RequestMethod.POST)
    public ModelAndView ajaxUpdateMngr(@Validated(toy.com.validation.group.ValidationGroups.Update.class)
                                       @ModelAttribute("MngrVO") MngrVO vo,
                                       BindingResult bindingResult,
                                       HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Manager > Update", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        if (bindingResult.hasErrors()) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", BindingResultUtil.firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", resultMap);
        }

        normalizeUseYn(vo);

        int affected = adminManagerService.updateMngr(vo);

        if (affected > 0) {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
        } else if (affected == CmConstants.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data (constraint violation).");
        } else if (affected == CmConstants.RESULT_FAIL) {
            resultMap.put("result", "None");
            resultMap.put("errorMessage", "Update failed.");
        } else {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", "Failed. Please contact the administrator.");
        }

        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping(value = "/disable.ac", method = RequestMethod.POST)
    public ModelAndView ajaxDisableMngr(@RequestParam("mngrUid") String mngrUid,
                                        HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Manager > Disable", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        if (EgovStringUtil.isEmpty(mngrUid)) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "mngrUid is required.");
            return new ModelAndView("jsonView", resultMap);
        }

        int affected = adminManagerService.softDeleteMngr(mngrUid);

        if (affected > 0) {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
        } else if (affected == CmConstants.RESULT_FAIL) {
            resultMap.put("result", "None");
            resultMap.put("errorMessage", "Manager does not exist.");
        } else {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", "Disable failed. Please contact the administrator.");
        }

        return new ModelAndView("jsonView", resultMap);
    }

    @RequestMapping(value = "/resetPassword.ac", method = RequestMethod.POST)
    public ModelAndView ajaxResetPassword(@RequestParam("mngrUid") String mngrUid,
                                          HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Manager > Reset Password", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyJson(resultMap);
        }

        if (EgovStringUtil.isEmpty(mngrUid)) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "mngrUid is required.");
            return new ModelAndView("jsonView", resultMap);
        }

        // Q3: telno empty면 UI에서 버튼 숨기지만, 서버도 2중 방어 (detail에서 telno 조회)
        MngrVO cur = adminManagerService.selectMngrDetail(mngrUid);
        if (cur == null) {
            resultMap.put("result", "None");
            resultMap.put("errorMessage", "Manager does not exist.");
            return new ModelAndView("jsonView", resultMap);
        }
        if (EgovStringUtil.isEmpty(cur.getTelno())) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Phone number is empty.");
            return new ModelAndView("jsonView", resultMap);
        }

        AdminMngrSmsResult sr = adminManagerService.resetPasswordAndSendSms(mngrUid);

        // Q1: OK면 성공 alert, FAIL이면 실패 alert (팝업은 닫지 않음)
        // -> 그래서 서버는 상태값(smsStatus/requestId)만 충분히 전달
        if (sr.getResult() == CmConstants.RESULT_OK) {
            String smsStatus = nvl(sr.getSmsStatus());
            if ("FAIL".equalsIgnoreCase(smsStatus)) {
                resultMap.put("result", "SmsFail");
            } else {
                resultMap.put("result", "Y");
            }
            resultMap.put("smsStatus", smsStatus);
            resultMap.put("requestId", nvl(sr.getRequestId()));
            resultMap.put("errorMessage", ""); // UI decides message
            return new ModelAndView("jsonView", resultMap);
        }

        if (sr.getResult() == CmConstants.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data.");
            return new ModelAndView("jsonView", resultMap);
        }

        resultMap.put("result", "N");
        resultMap.put("errorMessage", "Reset password failed. Please contact the administrator.");
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

    private void normalizeUseYn(MngrVO vo) {
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

    private void normalizeUseYnForSearch(MngrVO vo) {
        // For search filters, unchecked checkbox should mean "no filter", not "N".
        if (vo == null || EgovStringUtil.isEmpty(vo.getUseYn())) {
            return;
        }
        if ("true".equalsIgnoreCase(vo.getUseYn())) {
            vo.setUseYn("Y");
        } else if ("false".equalsIgnoreCase(vo.getUseYn())) {
            vo.setUseYn(null);
        }
    }

    private String nvl(String s) {
        return (s == null) ? "" : s;
    }
}
