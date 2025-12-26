package toy.com.common.common.dao;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import toy.com.vo.system.code.CdVO;

import java.util.List;

@Mapper("commonDAO")
public interface CommonDAO {
    List<CdVO> selectCdList();
}
