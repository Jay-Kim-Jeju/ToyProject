package toy.admin.system.accesslog.dao;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import toy.com.vo.system.accesslog.AdminAccessLogSearchVO;
import toy.com.vo.system.accesslog.AdminAccessLogVO;

import java.util.List;

@Mapper("AdminAccessLogDAO")
public interface AdminAccessLogDAO {

    // This query returns paged rows based on search conditions.
    List<AdminAccessLogVO> selectAdminAccessLogList(AdminAccessLogSearchVO searchVO);

    // This query returns total row count for paging.
    int selectAdminAccessLogCount(AdminAccessLogSearchVO searchVO);

    // This query inserts one access log record.
    int insertAdminAccessLog(AdminAccessLogVO vo);

}
