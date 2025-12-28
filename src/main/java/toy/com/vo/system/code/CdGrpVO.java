package toy.com.vo.system.code;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.vo.common.PagingDefaultVO;
import toy.com.validation.group.ValidationGroups;
import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class CdGrpVO extends PagingDefaultVO {

    @EqualsAndHashCode.Include
    @NotBlank(message = "groupCd is required.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String groupCd;

    @NotBlank(message = "cdGroupNm is required.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String cdGroupNm;

    @NotBlank(message = "useYn is required.", groups = {ValidationGroups.Update.class})
    private String useYn;
    private String regDt;
    private String regUid;
    private String updDt;
    private String updUid;

    public String getUpdDt() {
        return updDt;
    }

    public void setUpdDt(String updDt) {
        this.updDt = updDt;
    }

    public String getUpdUid() {
        return updUid;
    }

    public void setUpdUid(String updUid) {
        this.updUid = updUid;
    }

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
