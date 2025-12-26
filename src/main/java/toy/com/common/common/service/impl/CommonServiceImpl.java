package toy.com.common.common.service.impl;

import lombok.RequiredArgsConstructor;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import toy.com.common.common.dao.CommonDAO;
import toy.com.common.common.service.CommonService;
import toy.com.vo.common.CommonCdVO;
import toy.com.vo.common.SingletonData;
import toy.com.vo.system.code.CdVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl extends EgovAbstractServiceImpl implements CommonService {
    private static final Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);
    private final CommonDAO commonDAO;

    @Override
    public void refreshSingletonCommonCd() throws Exception {
        SingletonData singleton = SingletonData.getInstance();

        List<CdVO> codeList = commonDAO.selectCdList();

        // Build a new map first, then replace the reference at once.
        // This avoids partially-updated data being visible to readers.
        Map<String, List<CommonCdVO>> codeMap = new HashMap<>();
        if (codeList != null) {
            for (CdVO code : codeList) {
                if (code == null || code.getGroupCd() == null) {
                    continue;
                }
                CommonCdVO commonCd = new CommonCdVO();
                commonCd.setCd(code.getCd());
                commonCd.setCdNm(code.getCdNm());
                commonCd.setAditInfo(code.getAditInfo1());
                commonCd.setUseYn(code.getUseYn());

                codeMap.computeIfAbsent(code.getGroupCd(), k -> new ArrayList<>()).add(commonCd);
            }
        }

        singleton.setCdList(codeMap);
        log.info("Common code cache refreshed. groupCount={}", codeMap.size());
    }
}
