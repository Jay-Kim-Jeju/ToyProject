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
    private int pageUnit = 10;
    private int pageSize = 10;
    private int firstIndex = 1;
    private int lastIndex = 1;
    private int recordCountPerPage = 10;
    private String sStartDt;
    private String sEndDt;
    private String rsvtStartDt;
    private String rsvtEndDt;
}
