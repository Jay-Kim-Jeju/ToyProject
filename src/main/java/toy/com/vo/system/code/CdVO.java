package toy.com.vo.system.code;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.vo.common.PagingDefaultVO;

@Data
@EqualsAndHashCode(callSuper = false)
public class CdVO extends PagingDefaultVO {

    private String groupCd;
    private String cdGroupNm;

    private String cd;
    private String cdNm;
    private String sortOrdr;
    private String useYn;
    private String regDt;
    private String regUid;

    private String aditInfo1;
    private String aditInfo2;

    private Integer prevSortOrder;
    private Integer newSortOrder;

    private CdGrpVO cdGrpVO;
}
