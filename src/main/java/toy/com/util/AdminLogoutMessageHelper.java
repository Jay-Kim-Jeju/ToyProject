package toy.com.util;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Pattern;

@Component("adminLogoutReasonHelper")
public class AdminLogoutMessageHelper {

    // Only allow safe reason tokens (defensive).
    private static final Pattern SAFE_REASON = Pattern.compile("^[A-Za-z0-9_\\-]+$");

    public AdminLogoutMessageHelper() {
        // Default constructor for Spring.
    }

    /**
     * Resolves logout alert message by reason code.
     *
     * TODO: Replace hardcoded messages with i18n message keys (message.properties) once system menu i18n is finalized.
     * TODO: Expand reason codes as logout scenarios grow (e.g., IP allowlist change, session sync, admin revocation).
     */
    public String resolveLogoutMessage(String reason, Locale locale) {
        String r = normalize(reason);
        if (r == null) {
            return null;
        }

        // For now, keep messages simple. i18n will replace these strings later.
        if (CmConstants.LOGOUT_REASON_FORBIDDEN.equals(r)) {
            return "접근 권한이 없어 로그아웃되었습니다.";
        }
        if (CmConstants.LOGOUT_REASON_REVOKED.equals(r)) {
            return "권한이 변경되어 로그아웃되었습니다.";
        }
        if (CmConstants.LOGOUT_REASON_DISABLED.equals(r)) {
            return "계정이 비활성화되어 로그아웃되었습니다.";
        }
        if (CmConstants.LOGOUT_REASON_EXPIRED.equals(r)) {
            return "세션이 만료되어 로그아웃되었습니다.";
        }

        return "로그아웃되었습니다.";
    }

    private String normalize(String reason) {
        if (reason == null) {
            return null;
        }
        String trimmed = reason.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (!SAFE_REASON.matcher(trimmed).matches()) {
            return null;
        }
        return trimmed.toUpperCase(Locale.ROOT);
    }
}