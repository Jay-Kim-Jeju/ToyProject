package toy.com.vo.system;

import lombok.Data;
import toy.com.vo.common.PagingDefaultVO;

@Data
public class AuthVO extends PagingDefaultVO {
    private String authorGroupUuid;
    private String mngrUid;
}
