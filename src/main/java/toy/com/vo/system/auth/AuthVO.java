package toy.com.vo.system.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.validation.group.ValidationGroups;
import toy.com.vo.common.PagingDefaultVO;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class AuthVO extends PagingDefaultVO {

    @EqualsAndHashCode.Include
    @NotBlank(message = "authUuid is required.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 50, message = "authUuid must be <= 50 characters.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String authUuid;

    @NotBlank(message = "authorDc is required.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 200, message = "authorDc must be <= 200 characters.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String authorDc;

    @NotBlank(message = "useYn is required.", groups = {ValidationGroups.Update.class})
    @Pattern(regexp = "^[YN]$", message = "useYn must be 'Y' or 'N'.", groups = {ValidationGroups.Update.class})
    private String useYn;

    @Size(max = 50, message = "regUid must be <= 50 characters")
    private String regUid;

    /* Keeping as String for MyBatis mapping consistency */
    private String regDt;
}