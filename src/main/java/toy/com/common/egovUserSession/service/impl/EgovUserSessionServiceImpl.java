package toy.com.common.egovUserSession.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import toy.com.common.egovUserSession.service.EgovUserSessionService;
import toy.com.util.EgovStringUtil;
import toy.com.vo.common.SessionAdminVO;

@Service("egovUserSessionService")
public class EgovUserSessionServiceImpl implements EgovUserSessionService {

    @Override
    public Object getAuthenticatedAdmin() {
        return RequestContextHolder.getRequestAttributes().getAttribute("sessionAdminVO", 1);
    }

    @Override
    public Object getAuthenticatedNoMbr() {
        return RequestContextHolder.getRequestAttributes().getAttribute("noMbrSession", 1);
    }

    @Override
    public Boolean isAuthenticated() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return false;
        } else {
            return RequestContextHolder.getRequestAttributes().getAttribute("userSession", 1) == null ? false : true;
        }    }

    @Override
    public Boolean isAuthenticatedNoMbr() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return false;
        } else {
            return RequestContextHolder.getRequestAttributes().getAttribute("noMbrSession", 1) == null ? false : true;
        }
    }

    @Override
    public Boolean isAdminAuthenticated() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return false;
        } else if (RequestContextHolder.getRequestAttributes().getAttribute("sessionAdminVO", 1) == null) {
            return false;
        } else {
            SessionAdminVO sessionAdminVO = (SessionAdminVO)RequestContextHolder.getRequestAttributes().getAttribute("sessionAdminVO", 1);
            return sessionAdminVO != null && sessionAdminVO.getCheckAdmin() ? true : false;
        }
    }

    @Override
    public String getAdminUid() {

        // If there is no request context, treat as guest.
        if (RequestContextHolder.getRequestAttributes() == null) {
            return "guest";
        } else {
            SessionAdminVO sessionAdminVO = (SessionAdminVO)RequestContextHolder
                    .getRequestAttributes()
                    .getAttribute("sessionAdminVO", RequestAttributes.SCOPE_SESSION);


            if (sessionAdminVO == null ) {
                return "guest";
            } else {

                // Return manager UID from admin session.
                String adminUid = "guest";
                if (sessionAdminVO != null  && EgovStringUtil.isNotEmpty(sessionAdminVO.getMngrUid())) {
                    adminUid = sessionAdminVO.getMngrUid();
                }

                return adminUid;
            }
        }
    }
}
