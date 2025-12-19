package toy.com.vo.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PagingDefaultVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rn;
    private String sortField;
    private String sortOrder;

    private String searchCondition = "";
    private String searchKeyword = "";
    private String searchUseYn = "";

    private int pageIndex = 1;

    // rows per page (LIMIT size)
    private int recordCountPerPage = 10;

    // pager UI size (page link count)
    private int pageSize = 10;

    // calculated values (set by controller via PaginationInfo)
    private int firstIndex = 0;
    private int lastIndex = 0;

    // (Optional) common date ranges; keep if you really reuse them widely
    private String sStartDt;
    private String sEndDt;
    private String rsvtStartDt;
    private String rsvtEndDt;
}
