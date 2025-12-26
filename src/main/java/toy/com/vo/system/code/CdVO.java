package toy.com.vo.system.code;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.vo.common.PagingDefaultVO;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class CdVO extends PagingDefaultVO {
    @EqualsAndHashCode.Include
    private String groupCd;
    private String cdGroupNm;
    @EqualsAndHashCode.Include
    private String cd;
    private String cdNm;
    private String useYn;
    private String regDt;
    private String regUid;

    private String aditInfo1;
    private String aditInfo2;

    private CdGrpVO cdGrpVO;

    public String getGroupCd() {
        return groupCd;
    }

    public void setGroupCd(String groupCd) {
        this.groupCd = groupCd;
    }

    public String getCdGroupNm() {
        return cdGroupNm;
    }

    public void setCdGroupNm(String cdGroupNm) {
        this.cdGroupNm = cdGroupNm;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getCdNm() {
        return cdNm;
    }

    public void setCdNm(String cdNm) {
        this.cdNm = cdNm;
    }


    public String getUseYn() {
        return useYn;
    }

    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }

    public String getRegDt() {
        return regDt;
    }

    public void setRegDt(String regDt) {
        this.regDt = regDt;
    }

    public String getRegUid() {
        return regUid;
    }

    public void setRegUid(String regUid) {
        this.regUid = regUid;
    }

    public String getAditInfo1() {
        return aditInfo1;
    }

    public void setAditInfo1(String aditInfo1) {
        this.aditInfo1 = aditInfo1;
    }

    public String getAditInfo2() {
        return aditInfo2;
    }

    public void setAditInfo2(String aditInfo2) {
        this.aditInfo2 = aditInfo2;
    }

    public CdGrpVO getCdGrpVO() {
        return cdGrpVO;
    }

    public void setCdGrpVO(CdGrpVO cdGrpVO) {
        this.cdGrpVO = cdGrpVO;
    }

    @Override
    public String toString() {
        return "CdVO{" +
                "groupCd='" + groupCd + '\'' +
                ", cdGroupNm='" + cdGroupNm + '\'' +
                ", cd='" + cd + '\'' +
                ", cdNm='" + cdNm + '\'' +
                ", useYn='" + useYn + '\'' +
                ", regDt='" + regDt + '\'' +
                ", regUid='" + regUid + '\'' +
                ", aditInfo1='" + aditInfo1 + '\'' +
                ", aditInfo2='" + aditInfo2 + '\'' +

                ", cdGrpVO=" + cdGrpVO +
                '}';
    }


}
