package toy.com.util;

import toy.com.vo.common.PagingDefaultVO;

public class PagingParamUtil {
    private static final int DEFAULT_PAGE_INDEX = 1;
    private static final int DEFAULT_ROWS_PER_PAGE = 10;   // LIMIT size
    private static final int DEFAULT_PAGER_SIZE = 10;      // pager UI size
    private static final int MAX_ROWS_PER_PAGE = 200;      // safety cap

    private PagingParamUtil() {
        // This is a utility class; do not instantiate.
    }

    public static void applyPagingDefaults(PagingDefaultVO vo) {
        // Validate and normalize paging inputs before building PaginationInfo.
        if (vo.getPageIndex() <= 0) {
            vo.setPageIndex(DEFAULT_PAGE_INDEX);
        }
        if (vo.getRecordCountPerPage() <= 0) {
            vo.setRecordCountPerPage(DEFAULT_ROWS_PER_PAGE);
        }
        if (vo.getPageSize() <= 0) {
            vo.setPageSize(DEFAULT_PAGER_SIZE);
        }

        // Clamp rows-per-page to prevent accidental huge queries.
        if (vo.getRecordCountPerPage() > MAX_ROWS_PER_PAGE) {
            vo.setRecordCountPerPage(MAX_ROWS_PER_PAGE);
        }
    }
}
