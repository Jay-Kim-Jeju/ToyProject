package toy.com.vo.system.mngr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.vo.common.PagingDefaultVO;

@Data
@EqualsAndHashCode(callSuper = false)
public class MngrVO extends PagingDefaultVO {

    private String mngrUid;
    private String pwdEncpt;
    private String mngrNm;
    private String emlAdres;
    private String telno;
    private String lgnFailrNumtm;
    private String lastLgnDt;
    private String useYn;
    private String regUid;
    private String regDt;
    private String mdfcnUid;
    private String mdfcnDt;
    private String chgPwd;
    private String encryptionKey;
    private String authUuid;
}
