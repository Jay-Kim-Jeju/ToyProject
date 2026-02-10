package toy.com.vo.system.mngr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import toy.com.validation.group.ValidationGroups;
import toy.com.vo.common.PagingDefaultVO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class MngrVO extends PagingDefaultVO {

    @EqualsAndHashCode.Include
    @NotBlank(message = "mngrUid is required.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 50, message = "mngrUid must be <= 50 characters.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String mngrUid;

    /* Encoded password (stored) */
    @Size(max = 200, message = "pwdEncpt must be <= 200 characters.")
    private String pwdEncpt;

    @NotBlank(message = "mngrNm is required.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, message = "mngrNm must be <= 100 characters.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String mngrNm;

    @Email(message = "emlAdres must be a valid email format.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 200, message = "emlAdres must be <= 200 characters.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String emlAdres;

    /* Allow empty, but if present must match hyphen phone format: 010-0000-0000 */
    @Pattern(
            regexp = "^$|^\\d{2,3}-\\d{3,4}-\\d{4}$",
            message = "telno must match ###-####-#### format.",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}
    )
    @Size(max = 20, message = "telno must be <= 20 characters.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String telno;

    /* Keeping as String for MyBatis mapping consistency */
    private String lgnFailrNumtm;

    /* Keeping as String for MyBatis mapping consistency */
    private String lastLgnDt;

    @Pattern(regexp = "^[YN]$", message = "useYn must be 'Y' or 'N'.", groups = {ValidationGroups.Update.class})
    private String useYn;

    @Size(max = 50, message = "regUid must be <= 50 characters.")
    private String regUid;

    /* Keeping as String for MyBatis mapping consistency */
    private String regDt;

    @Size(max = 50, message = "mdfcnUid must be <= 50 characters.")
    private String mdfcnUid;

    /* Keeping as String for MyBatis mapping consistency */
    private String mdfcnDt;

    /* Raw password from UI for password change popup (do not persist as-is) */
    @Size(max = 100, message = "chgPwd must be <= 100 characters.")
    private String chgPwd;

    /* Encryption key if your service needs it (e.g., for password encoding) */
    private String encryptionKey;

    /* ===== List-only derived fields from SQL ===== */

    /* IFNULL(A.ACTIVE_AUTH_CNT, 0) AS ACTIVE_AUTH_CNT */
    private Integer activeAuthCnt;

    /* CASE WHEN ... THEN 'Y' ELSE 'N' END AS AUTH_APPLIED_YN */
    private String authAppliedYn;

    private String authUuid;

}
