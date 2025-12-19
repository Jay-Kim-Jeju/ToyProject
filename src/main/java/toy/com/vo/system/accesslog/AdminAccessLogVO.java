package toy.com.vo.system.accesslog;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class AdminAccessLogVO{

    // ====== Key fields ======
    private String logUuid;
    private String mngrUid;

    // ====== Access info ======
    private String accessIp;    // Client IP address
    private String reqUri;      // Requested URL (e.g., /toy/admin/main.do)

    // ====== Action description ======
    private String actionDesc;  // Breadcrumb-like description (e.g., "통합관리자 > 시스템관리 > 코드관리 > 코드조회")

    // ====== Etc ======
    private String memo;        // Optional memo
    private String regDt;       // Access datetime
}
