package toy.com.vo.system;

import lombok.Data;
import toy.com.vo.common.PagingDefaultVO;

@Data
public class AuthGrpVO extends PagingDefaultVO {

    private String authorGroupUuid;
    private String authorDc;
    private String useYn;
    private String regUid;
    private String regDt;
}
