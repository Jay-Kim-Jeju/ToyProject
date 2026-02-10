package toy.com.util;

public class CmConstants {
    public static final String FLAG_Y = "Y";
    public static final String FLAG_N = "N";

    public static final String ADMIN_LOGIN_URL = "/toy/login.do";
    public static final String MANAGE_LOGIN_URL = "/toy/login.do";
    public static final String SESSION_ADMIN_KEY  = "sessionAdminVO";

    // Core roles (system safety)
    public static final String ROLE_ADMINISTRATOR = "ADMINISTRATOR";
    public static final String ROLE_SYSTEM = "SYSTEM";

    // Result codes (service layer standard)
    public static final int RESULT_OK = 1;
    public static final int RESULT_FAIL = 0;
    public static final int RESULT_DUPLE = -1;
    public static final int RESULT_INVALID = -2;
    public static final int RESULT_FORBIDDEN = -3;

    // Admin logout reasons
    public static final String LOGOUT_REASON_FORBIDDEN = "FORBIDDEN";
    public static final String LOGOUT_REASON_REVOKED = "REVOKED";
    public static final String LOGOUT_REASON_DISABLED = "DISABLED";
    public static final String LOGOUT_REASON_EXPIRED = "EXPIRED";

}