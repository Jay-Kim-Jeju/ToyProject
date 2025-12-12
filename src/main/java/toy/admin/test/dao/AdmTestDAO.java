package toy.admin.test.dao;

import toy.com.vo.TestVO;
import java.util.List;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;

@Mapper("AdmTestDAO")
public interface AdmTestDAO {
    List<TestVO> selectTestList(TestVO testVO) throws Exception;
}
