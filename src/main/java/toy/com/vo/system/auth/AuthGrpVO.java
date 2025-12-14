package toy.com.vo.system.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.vo.common.PagingDefaultVO;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthGrpVO extends PagingDefaultVO {

    private String authorGroupUuid;
    private String authorDc;
    private String useYn;
    private String regUid;
    private String regDt;
}
