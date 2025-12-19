package toy.com.vo.system.allow;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.vo.common.PagingDefaultVO;

@Data
@EqualsAndHashCode(callSuper = false)
public class AdminAllowIpVO extends PagingDefaultVO {

    private String allowIpUuid;   // Allow IP row id (uuid)
    private String mngrUid;       // Admin id
    private String allowIp;       // IPv4/IPv6 string
    private Integer cidrPrefix;   // Optional: CIDR prefix length

    private String useYn;         // Y/N
    private String startDt;       // Optional validity start
    private String endDt;         // Optional validity end
    private String memo;          // Optional memo

    private String regDt;
    private String regUid;
    private String mdfcnDt;
    private String mdfcnUid;
}
