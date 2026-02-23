package toy.com.vo.system.auth;
import lombok.Data;

import java.io.Serializable;
/**
 * Minimal auth guard snapshot used by interceptor.
 * Note: debugAuthConcat is for troubleshooting only. Do not log it.
 */
@Data
public class AdminAuthGuardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mngrUid;
    private String mngrUseYn;

    private String authDigest;
    private Integer authCnt;

    // Debug only (concatenated AUTH_UUID list used to calculate authDigest)
    private String debugAuthConcat;
}
