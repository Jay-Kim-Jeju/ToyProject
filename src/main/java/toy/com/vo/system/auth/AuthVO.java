package toy.com.vo.system.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.vo.common.PagingDefaultVO;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthVO extends PagingDefaultVO {

    @NotBlank(message = "authUuid is required")
    @Size(max = 50, message = "authUuid must be <= 50 characters")
    private String authUuid;

    @NotBlank(message = "authorDc is required")
    @Size(max = 200, message = "authorDc must be <= 200 characters")
    private String authorDc;

    @Pattern(regexp = "^[YN]$", message = "useYn must be 'Y' or 'N'")
    private String useYn;

    @Size(max = 50, message = "regUid must be <= 50 characters")
    private String regUid;

    /* Keeping as String for MyBatis mapping consistency */
    private String regDt;
}