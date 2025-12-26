package toy.com.vo.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SingletonData {
    // Eager singleton is the simplest thread-safe approach.
    private static final SingletonData INSTANCE = new SingletonData();

    // Prefer immutable empty list to avoid accidental mutation leaks.
    private final List<CommonCdVO> dummyCd = java.util.Collections.emptyList();

    // Volatile ensures visibility of the latest reference across threads.
    private volatile Map<String, List<CommonCdVO>> codeList = java.util.Collections.emptyMap();

    public static SingletonData getInstance() {
        return INSTANCE;
    }

    public Map<String, List<CommonCdVO>> getCdList() {
        return this.codeList;
    }

    public void setCdList(Map<String, List<CommonCdVO>> codeList) {
        // Never store null to keep read paths null-safe.
        this.codeList = (codeList == null) ? java.util.Collections.emptyMap() : codeList;
    }

    public List<CommonCdVO> getCd(String codeGroup) {
        Map<String, List<CommonCdVO>> map = this.codeList;
        List<CommonCdVO> list = (map == null) ? null : map.get(codeGroup);
        return (list == null) ? dummyCd : list;
    }

    public String getCdDetail(String codeGroup, String code) {
        if (code == null) {
            return "";
        }
        List<CommonCdVO> list = getCd(codeGroup);
        for (CommonCdVO commonCd : list) {
            if (commonCd != null && code.equals(commonCd.getCd())) {
                return commonCd.getCdNm() == null ? "" : commonCd.getCdNm();
            }
        }
        return "";
    }

    
}
