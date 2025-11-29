package toy.admin.member.service;

import toy.com.vo.MemberVO;
import java.util.List;

public interface AdmMemberService {

    List<MemberVO> selectMemberList(MemberVO memberVO) throws Exception;

}
