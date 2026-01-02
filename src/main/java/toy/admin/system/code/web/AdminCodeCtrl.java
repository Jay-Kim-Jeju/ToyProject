package toy.admin.system.code.web;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import toy.admin.system.accesslog.service.AdminAccessLogService;
import toy.admin.system.code.service.AdminCodeService;
import toy.com.common.common.service.CommonService;
import toy.com.util.EgovStringUtil;
import toy.com.util.PagingParamUtil;
import toy.com.util.ToyAdminAuthUtils;
import toy.com.vo.system.code.CdGrpVO;
import toy.com.vo.system.code.CdVO;
import toy.com.validation.group.ValidationGroups;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/toy/admin/sys/code")
public class AdminCodeCtrl {

    private final AdminCodeService adminCodeService;
    private final AdminAccessLogService adminAccessLogService;
    private final CommonService commonService;

    /*@Autowired
    private Validator javaxValidator;*/

    private void refreshCommonCodeSafely() {
        try {
            commonService.refreshSingletonCommonCd();
        } catch (Exception e) {
            // Do not fail the request if DB write already succeeded.
            log.warn("Common code cache refresh failed after DB write.", e);
        }
    }

    // Base menu map must be immutable; build a new map per request to avoid shared mutable state.
    private static final Map<String, String> MENU_BASE = Map.of("adminMenu1", "system");
    private static final String MENU_ROLE = "system";



    /* =========================
     * View Pages (.do)
     * ========================= */

    @RequestMapping(value = "/grp/list.do", method = RequestMethod.GET)
    public String viewCodeGroupPage(HttpServletRequest request, ModelMap model) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Code > GroupCode List Page", request);

        //chek Aop Proxy log applying
        /*log.debug("isAopProxy={}, isCglibProxy={}, isJdkProxy={}, class={}",
                AopUtils.isAopProxy(adminCodeService),
                AopUtils.isCglibProxy(adminCodeService),
                AopUtils.isJdkDynamicProxy(adminCodeService),
                adminCodeService.getClass());*/

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            return denyView;
        }


        Map<String, String> menuActiveMap = new HashMap<>(MENU_BASE);
        menuActiveMap.put("adminMenu2", "code");
        menuActiveMap.put("adminMenu3", "list");
        model.addAttribute("menuActiveMap", menuActiveMap);

        return "admin/system/code/listCode";

    }

    @RequestMapping(value = "/cd/detail.do", method = RequestMethod.GET)
    public ModelAndView viewCodeDetailPopup(@Validated(ValidationGroups.Key.class) @ModelAttribute("CdVO") CdVO codeVO,
                                            BindingResult bindingResult, HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Code > Code Detail Popup", request);

        //auth check
        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            // Popup is an HTML view request -> redirect is the natural flow
            return new ModelAndView(denyView); // "redirect:/toy/admin/login.do"
        }

        //validation check
        ModelAndView mv = new ModelAndView("admin/system/code/detailPopCode");
        if (bindingResult.hasErrors()) {
            mv.addObject("result", "N");
            mv.addObject("errorMessage", firstErrorMessage(bindingResult));
            return mv;
        }

        //null check
        CdVO detail = adminCodeService.selectCd(codeVO);
        if (detail == null) {
            mv.addObject("result", "None");
            mv.addObject("errorMessage", "Code does not exist.");
            return mv;
        }

        mv.addObject("code", detail);
        return mv;
    }

    /* =========================
     * AJAX JSON (.doax)
     * ========================= */

    // Code Group List (Paging + Search)
    @RequestMapping(value = "/grp/list.doax", method = RequestMethod.POST)
    public ModelAndView ajaxSelectCodeGroupList(@ModelAttribute("CdGrpVO") CdGrpVO searchVO,
                                                HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Code > GroupCode List", request);
        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            resultMap.put("result", "N");
            resultMap.put("redirectUrl", "/toy/admin/login.do");
            resultMap.put("errorMessage", "Authentication required.");
            return new ModelAndView("jsonView", resultMap);
        }
        else{
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

            normalizeUseYnForSearch(searchVO);

            int totalCnt = adminCodeService.selectCdGrpListCnt(searchVO);
            paginationInfo.setTotalRecordCount(totalCnt);

            List<CdGrpVO> list = adminCodeService.selectCodeGroupList(searchVO);

            resultMap.put("data", list);
            resultMap.put("itemsCount", totalCnt);
            return new ModelAndView("jsonView", resultMap);
        }
        
    }

    // Insert Code Group
    @RequestMapping(value = "/grp/insert.ac", method = RequestMethod.POST)
    public ModelAndView ajaxInsertCdGrp(@Validated(ValidationGroups.Create.class) @ModelAttribute("CdGrpVO") CdGrpVO vo,
                                        BindingResult bindingResult,
                                        HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Code > GroupCode Insert", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            resultMap.put("result", "N");
            resultMap.put("redirectUrl", "/toy/admin/login.do");
            resultMap.put("errorMessage", "Authentication required.");
            return new ModelAndView("jsonView", resultMap);
        }


        if (bindingResult.hasErrors()) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", resultMap);
        }

        int affected = adminCodeService.insertCdGrp(vo);
        if (affected > 0) {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
        } else if (affected == AdminCodeService.RESULT_DUPLE) {
            resultMap.put("result", "Duple");
            resultMap.put("errorMessage", "Duplicate group code.");
        } else if (affected == AdminCodeService.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data (constraint violation).");
        } else {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", "Insert failed. Please contact the administrator.");
        }

        return new ModelAndView("jsonView", resultMap);
    }

    // Update Code Group
    @RequestMapping(value = "/grp/update.ac", method = RequestMethod.POST)
    public ModelAndView ajaxUpdateCdGrp(@Validated(ValidationGroups.Update.class) @ModelAttribute("CdGrpVO") CdGrpVO vo,
                                        BindingResult bindingResult,
                                        HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Code > GroupCode Update", request);

        Map<String, Object> resultMap = new HashMap<>();



        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            resultMap.put("result", "N");
            resultMap.put("redirectUrl", "/toy/admin/login.do");
            resultMap.put("errorMessage", "Authentication required.");
            return new ModelAndView("jsonView", resultMap);
        }


        if (bindingResult.hasErrors()) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", resultMap);
        }

        normalizeUseYn(vo);


        /*
        check validation apply
        Set<ConstraintViolation<CdGrpVO>> v = javaxValidator.validate(vo, ValidationGroups.Update.class);
        log.info("BeanValidation violations size={}", v.size());
        log.info("bindingResult errorCount={}", bindingResult.getErrorCount());
        */

        int affected = adminCodeService.updateCdGrp(vo);
        if (affected > 0) {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
        } else if (affected == AdminCodeService.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data (constraint violation).");
        } else if (affected == 0) {
            resultMap.put("result", "None");
            resultMap.put("errorMessage", "Group code does not exist.");
        } else {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", "Update failed. Please contact the administrator.");
        }

        return new ModelAndView("jsonView", resultMap);
    }

    // Code List by Group (optionally paging-ready)
    @RequestMapping(value = "/cd/list.doax", method = RequestMethod.POST)
    public ModelAndView ajaxSelectCdListByGroup(@Validated(ValidationGroups.GroupKey.class) @ModelAttribute("CdVO") CdVO searchVO,
                                                BindingResult bindingResult,
                                                HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Code > Code List", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            resultMap.put("result", "N");
            resultMap.put("redirectUrl", "/toy/admin/login.do");
            resultMap.put("errorMessage", "Authentication required.");
            return new ModelAndView("jsonView", resultMap);
        }

        if (bindingResult.hasErrors()) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", resultMap);
        }

        // Paging values are prepared here for consistency.
        // Note: If your SQL does NOT use LIMIT, the list will still return all rows.
        PagingParamUtil.applyPagingDefaults(searchVO);

        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
        paginationInfo.setRecordCountPerPage(searchVO.getRecordCountPerPage());
        paginationInfo.setPageSize(searchVO.getPageSize());

        searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
        searchVO.setLastIndex(paginationInfo.getLastRecordIndex());

        int totalCnt = adminCodeService.selectCdListCnt(searchVO);
        paginationInfo.setTotalRecordCount(totalCnt);

        List<CdVO> list = adminCodeService.selectCdListByGroup(searchVO);

        resultMap.put("data", list);
        resultMap.put("itemsCount", totalCnt);
        return new ModelAndView("jsonView", resultMap);
    }

    // Insert Code
    @RequestMapping(value = "/cd/insert.ac", method = RequestMethod.POST)
    public ModelAndView ajaxInsertCd(@Validated(ValidationGroups.Create.class) @ModelAttribute("CdVO") CdVO vo,
                                     BindingResult bindingResult,
                                     HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Code > Code Insert", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            resultMap.put("result", "N");
            resultMap.put("redirectUrl", "/toy/admin/login.do");
            resultMap.put("errorMessage", "Authentication required.");
            return new ModelAndView("jsonView", resultMap);
        }


        if (bindingResult.hasErrors()) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", resultMap);
        }

        int affected = adminCodeService.insertCd(vo);
        if (affected > 0) {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
            refreshCommonCodeSafely();
        } else if (affected == AdminCodeService.RESULT_DUPLE) {
            resultMap.put("result", "Duple");
            resultMap.put("errorMessage", "Duplicate code in the same group.");
        } else if (affected == AdminCodeService.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data (constraint violation).");
        } else {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", "Insert failed. Please contact the administrator.");
        }

        return new ModelAndView("jsonView", resultMap);
    }

    // Update Code (No sort order logic)
    @RequestMapping(value = "/cd/update.ac", method = RequestMethod.POST)
    public ModelAndView ajaxUpdateCd( @Validated(ValidationGroups.Update.class) @ModelAttribute("CdVO") CdVO vo,
                                     BindingResult bindingResult,
                                     HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Code > Code Update", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            resultMap.put("result", "N");
            resultMap.put("redirectUrl", "/toy/admin/login.do");
            resultMap.put("errorMessage", "Authentication required.");
            return new ModelAndView("jsonView", resultMap);
        }


        if (bindingResult.hasErrors()) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", resultMap);
        }

        normalizeUseYn(vo);

        int affected = adminCodeService.updateCd(vo);
        if(affected > 0)
        {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
            refreshCommonCodeSafely();
        } else if (affected == AdminCodeService.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Invalid data (constraint violation).");
        } else if (affected == 0) {
            resultMap.put("result", "None");
            resultMap.put("errorMessage", "Code does not exist.");
        } else {
            resultMap.put("result", "N");
            resultMap.put("errorMessage","Update failed. Please contact the administrator.");
        }
        return new ModelAndView("jsonView", resultMap);
    }

    // Delete Code (No sort order logic)
    @RequestMapping(value = "/cd/delete.ac", method = RequestMethod.POST)
    public ModelAndView ajaxDeleteCd(@Validated(ValidationGroups.Delete.class) @ModelAttribute("CdVO") CdVO vo,
                                     BindingResult bindingResult,
                                     HttpServletRequest request) throws Exception {
        adminAccessLogService.insertAdminAccessLog("Admin > System > Code > Code Delete", request);

        Map<String, Object> resultMap = new HashMap<>();

        String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
        if (EgovStringUtil.isNotEmpty(denyView)) {
            resultMap.put("result", "N");
            resultMap.put("redirectUrl", "/toy/admin/login.do");
            resultMap.put("errorMessage", "Authentication required.");
            return new ModelAndView("jsonView", resultMap);
        }

        if (bindingResult.hasErrors()) {
            resultMap.put("result", "N");
            resultMap.put("errorMessage", firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", resultMap);
        }

        int affected = adminCodeService.deleteCd(vo);
        if(affected > 0)
        {
            resultMap.put("result", "Y");
            resultMap.put("errorMessage", "");
            refreshCommonCodeSafely();
        } else if (affected == AdminCodeService.RESULT_INVALID) {
            resultMap.put("result", "Invalid");
            resultMap.put("errorMessage", "Cannot delete (constraint violation).");
        } else if (affected == 0) {
            resultMap.put("result", "None");
            resultMap.put("errorMessage", "Code does not exist.");
        } else {
            resultMap.put("result", "N");
            resultMap.put("errorMessage","Delete failed. Please contact the administrator.");
        }

        return new ModelAndView("jsonView", resultMap);
    }




    /* =========================
     * Private helpers
     * ========================= */

    private void normalizeUseYn(CdGrpVO vo) {
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

    private void normalizeUseYn(CdVO vo) {
        if (vo == null || EgovStringUtil.isEmpty(vo.getUseYn())) {
            return;
        }
        if ("true".equalsIgnoreCase(vo.getUseYn())) {
            vo.setUseYn("Y");
        } else if ("false".equalsIgnoreCase(vo.getUseYn())) {
            vo.setUseYn("N");
        }
    }

    private void normalizeUseYnForSearch(CdGrpVO vo) {
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

    // Controller-level manual validations removed (replaced by Bean Validation groups)

}
