package toy.com.vo.system.mngr;

import lombok.Data;

@Data
public class AdminMngrVerifResult {
    private int result;              // CmConstants.RESULT_*
    private String verifiedYn;       // "Y" / "N"

    private String smsStatus;        // OK / FAIL / SKIPPED
    private String requestId;        // vendor request id (optional)

    private int expireSeconds;       // from properties
    private long expireAtEpochMs;    // for UI countdown

    private int failCount;           // current fail count
    private int maxFailCount;        // 5
    private String lockedYn;         // "Y" when failCount reached max
}
