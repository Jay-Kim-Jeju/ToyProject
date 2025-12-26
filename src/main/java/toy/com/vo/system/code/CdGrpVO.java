package toy.com.vo.system.code;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.vo.common.PagingDefaultVO;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class CdGrpVO extends PagingDefaultVO {

    @EqualsAndHashCode.Include
    private String groupCd;
    private String cdGroupNm;
    private String useYn;
    private String regDt;
    private String regUid;



    public String getRegUid() {
        return regUid;
    }

    public void setRegUid(String regUid) {
        this.regUid = regUid;
    }

    public String getRegDt() {
        return regDt;
    }

    public void setRegDt(String regDt) {
        this.regDt = regDt;
    }

    public String getUseYn() {
        return useYn;
    }

    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }

    public String getCdGroupNm() {
        return cdGroupNm;
    }

    public void setCdGroupNm(String cdGroupNm) {
        this.cdGroupNm = cdGroupNm;
    }

    public String getGroupCd() {
        return groupCd;
    }

    public void setGroupCd(String groupCd) {
        this.groupCd = groupCd;
    }

    @Override
    public String toString() {
        return "CdGrpVO{" +
                "groupCd='" + groupCd + '\'' +
                ", cdGroupNm='" + cdGroupNm + '\'' +
                ", useYn='" + useYn + '\'' +
                ", regDt='" + regDt + '\'' +
                ", regUid='" + regUid + '\'' +
                '}';
    }
}
