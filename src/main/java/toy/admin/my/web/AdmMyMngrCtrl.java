package toy.admin.my.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import toy.admin.my.service.AdmMyMngrService;
import toy.com.util.BindingResultUtil;
import toy.com.util.CmConstants;
import toy.com.validation.group.ValidationGroups;
import toy.com.vo.system.mngr.MngrVO;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/toy/admin/my")
public class AdmMyMngrCtrl {
    private final AdmMyMngrService admMyMngrService;

    /**
     * My Account - detail popup
     */
    @RequestMapping("/detailPopMngr.do")
    public String myDetailPopMngr(ModelMap model, HttpServletRequest request) throws Exception {

        // Reset verification state each time the popup is opened
        admMyMngrService.resetMyVerificationState(request);

        MngrVO detail = admMyMngrService.selectMyMngrDetail(request);
        model.addAttribute("detail", detail);
        // Masked password display is a UI-only concept
        model.addAttribute("pwdMask", "********");
        model.addAttribute("verifiedYn", admMyMngrService.isMyVerified(request) ? "Y" : "N");

        return "admin/my/myDetailPopMngr";
    }

    /**
     * My Account - change password popup (entry should be allowed only after verification)
     */
    @RequestMapping("/changePWPopMngr.do")
    public String myChangePWPopMngr(ModelMap model, HttpServletRequest request) throws Exception {
        if (!admMyMngrService.isMyVerified(request)) {
            model.addAttribute("result", CmConstants.RESULT_FORBIDDEN);
            return "admin/my/myChangePWPopMngr";
        }
        model.addAttribute("result", CmConstants.RESULT_OK);
        return "admin/my/myChangePWPopMngr";
    }

    /**
     * Send verification number (SMS)
     */
    @RequestMapping("/sendVerificationNumber.doax")
    public ModelAndView sendVerificationNumber(HttpServletRequest request) throws Exception {
        Map<String, Object> r = admMyMngrService.sendVerificationNumber(request);
        return new ModelAndView("jsonView", r);
    }

    /**
     * Check verification number
     */
    @RequestMapping("/checkVerificationNumber.doax")
    public ModelAndView checkVerificationNumber(@RequestParam("code") String code,
                                                HttpServletRequest request) throws Exception {
        Map<String, Object> r = admMyMngrService.checkVerificationNumber(code, request);
        return new ModelAndView("jsonView", r);
    }

    /**
     * Update my account info (requires verified)
     */
    @RequestMapping("/updateMyMngrInfo.ac")
    public ModelAndView updateMyMngrInfo(@Validated(ValidationGroups.Update.class) MngrVO vo,
                                      BindingResult bindingResult,
                                      HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (bindingResult.hasErrors()) {
            map.put("result", CmConstants.RESULT_INVALID);
            map.put("errors", BindingResultUtil.firstErrorMessage(bindingResult));
            return new ModelAndView("jsonView", map);
        }

        int r = admMyMngrService.updateMyMngrInfo(vo, request);
        map.put("result", r);
        return new ModelAndView("jsonView", map);
    }

    /**
     * Change my password (requires verified)
     */
    @RequestMapping("/changeMyPassword.ac")
    public ModelAndView changeMyPassword(@RequestParam("currentPassword") String currentPassword,
                                         @RequestParam("newPassword") String newPassword,
                                         @RequestParam("newPasswordConfirm") String newPasswordConfirm,
                                         HttpServletRequest request) throws Exception {

        Map<String, Object> map = new HashMap<>();

        if (newPasswordConfirm == null || !newPasswordConfirm.equals(newPassword)) {
            map.put("result", CmConstants.RESULT_INVALID);
            return new ModelAndView("jsonView", map);
        }

        int r = admMyMngrService.changeMyPassword(currentPassword, newPassword, request);
        map.put("result", r);
        return new ModelAndView("jsonView", map);
    }


}
