package toy.com.vo.system.code;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.vo.common.PagingDefaultVO;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class CdGrpVO extends PagingDefaultVO {
    private String groupCd;
    private String cdGroupNm;
    private String useYn;
    private String regDt;
    private String regUid;

    private String cd;
    private String cdNm;
    private String aditInfo;

    private List<CdVO> cdList;
}
