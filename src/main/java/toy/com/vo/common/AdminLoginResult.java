package toy.com.vo.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminLoginResult implements Serializable {
    private static final long serialVersionUID = 1L;

    // true: 로그인 성공
    private boolean success;

    // true: 계정 잠김 (로그인 실패 횟수 초과)
    private boolean locked;

    // 로그인 실패 사유 코드 (예: "ID_PW_MISMATCH", "LOCKED", ...)
    private String reasonCode;

    // 현재 실패 횟수
    private int failCount;

    // 로그인에 성공했을 때의 세션용 정보
    private SessionAdminVO sessionUser;
}
