package toy.admin.member.service.impl;

import toy.com.vo.MemberVO;
import java.util.List;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;

@Mapper("admMemberMapper")
public interface AdmMemberMapper {
    List<MemberVO> selectMemberList(MemberVO memberVO) throws Exception;
}
