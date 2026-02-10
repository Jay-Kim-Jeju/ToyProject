package toy.com.vo.system.mngr;

import lombok.Data;

@Data
public class AdminMngrSmsResult {
    private int result;            // CmConstants.RESULT_*
    private String mngrUid;
    private String existingUseYn; // "Y" or "N" when duplicate exists (optional)
    private String smsStatus;      // OK / FAIL / SKIPPED
    private String smsMessage;     // user-facing message
    private String requestId;      // vendor request id
    private String mdfcnDt;        // yyyy-MM-dd HH:mm (format in JSP)
    private String pwdResetYn;    // "Y" if password was changed/reset in DB

}
