package toy.com.vo.system.accesslog;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.vo.common.PagingDefaultVO;

@Data
@EqualsAndHashCode(callSuper=false)
public class AdminAccessLogSearchVO extends PagingDefaultVO {

    // ====== Search fields ======
    private String sMngrUid;
    private String sAccessIp;
    private String sReqUri;
    private String sActionDesc;

    // yyyy-MM-dd
    private String sStartDt;
    private String sEndDt;

    public String getsMngrUid() {
        return sMngrUid;
    }

    public void setsMngrUid(String sMngrUid) {
        this.sMngrUid = sMngrUid;
    }

    public String getsAccessIp() {
        return sAccessIp;
    }

    public void setsAccessIp(String sAccessIp) {
        this.sAccessIp = sAccessIp;
    }

    public String getsReqUri() {
        return sReqUri;
    }

    public void setsReqUri(String sReqUri) {
        this.sReqUri = sReqUri;
    }

    public String getsActionDesc() {
        return sActionDesc;
    }

    public void setsActionDesc(String sActionDesc) {
        this.sActionDesc = sActionDesc;
    }

    public String getsStartDt() {
        return sStartDt;
    }

    public void setsStartDt(String sStartDt) {
        this.sStartDt = sStartDt;
    }

    public String getsEndDt() {
        return sEndDt;
    }

    public void setsEndDt(String sEndDt) {
        this.sEndDt = sEndDt;
    }
}
