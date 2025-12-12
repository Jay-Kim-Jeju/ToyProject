package toy.admin.test.service;

import toy.com.vo.TestVO;
import java.util.List;

public interface AdmTestService {

    List<TestVO> selectTestList(TestVO testVO) throws Exception;

}
