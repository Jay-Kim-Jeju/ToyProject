package toy.com.vo.system.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.vo.common.PagingDefaultVO;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthMngrVO extends PagingDefaultVO {

    @NotBlank(message = "authUuid is required")
    @Size(max = 50, message = "authUuid must be <= 50 characters")
    private String authUuid;

    @NotBlank(message = "mngrUid is required")
    @Size(max = 50, message = "mngrUid must be <= 50 characters")
    private String mngrUid;
}
