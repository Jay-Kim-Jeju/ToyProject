package toy.com.vo.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SessionAdminVO implements Serializable {
    private static final long serialVersionUID = 10L;
    private String mngrUid;
    private String pwdEncpt;
    private String mngrNm;
    private String emlAdres;
    private String telno;
    private String lastLgnDt;
    private String lgnFailrNumtm;
    private String authorCd;
    private String authorDc;
    private List<String> auth;
    private List<String> manageSiteList;
    private List<String> authName;
    private String isMemngr;

    public boolean checkAuth(String strAuth) {
        // Return false when auth list is missing.
        if (this.auth == null || this.auth.isEmpty() || strAuth == null) {
            return false;
        }
        for (String authorGrpId : this.auth) {
            if (strAuth.equals(authorGrpId)) {
                return true;
            }
        }
        return false;
    }

    public boolean getCheckAdmin() {

        // ADMINISTRATOR is treated as a super admin role in this project.
        return checkAuth("ADMINISTRATOR");
    }

}
