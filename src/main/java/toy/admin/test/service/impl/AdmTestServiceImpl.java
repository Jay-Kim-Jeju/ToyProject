package toy.admin.test.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

import toy.admin.test.dao.AdmTestDAO;
import toy.admin.test.service.AdmTestService;
import toy.com.vo.TestVO;

@Service("AdmTestService")
public class AdmTestServiceImpl implements AdmTestService {

    @Resource(name = "AdmTestDAO")
    private AdmTestDAO admTestDAO;

    @Override
    public List<TestVO> selectTestList(TestVO testVO) throws Exception {
        // 지금은 검색조건이 없으니 memberVO는 비어 있는 상태로 들어와도 됨
        return admTestDAO.selectTestList(testVO);
    }
}
