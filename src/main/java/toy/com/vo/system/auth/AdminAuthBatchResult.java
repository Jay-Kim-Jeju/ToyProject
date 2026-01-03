package toy.com.vo.system.auth;

import lombok.Data;

@Data
public class AdminAuthBatchResult {
    private int insertedCount;
    private int duplicateCount;
    private int invalidCount;
    private int requestedCount;
}
