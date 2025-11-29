package toy.admin.member.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

import toy.admin.member.service.AdmMemberService;
import toy.com.vo.MemberVO;

@Service("admMemberService")
public class AdmMemberServiceImpl implements AdmMemberService {

    @Resource(name = "admMemberMapper")
    private AdmMemberMapper admMemberMapper;

    @Override
    public List<MemberVO> selectMemberList(MemberVO memberVO) throws Exception {
        // 지금은 검색조건이 없으니 memberVO는 비어 있는 상태로 들어와도 됨
        return admMemberMapper.selectMemberList(memberVO);
    }
}
