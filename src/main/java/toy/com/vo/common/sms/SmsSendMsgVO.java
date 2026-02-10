package toy.com.vo.common.sms;

import lombok.Data;
import toy.com.validation.group.ValidationGroups;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SmsSendMsgVO {

    /*수신자 단위(to) + (필요하면 content override 가능)*/
    @NotBlank(message = "to is required.", groups = {ValidationGroups.Create.class})
    @Pattern(
            regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$|^\\d{10,11}$",
            message = "to must match phone format (e.g., 010-0000-0000).",
            groups = {ValidationGroups.Create.class})
    @Size(max = 20, message = "to must be <= 20 characters.", groups = {ValidationGroups.Create.class})
    private String to;

    /* Optional: only use when provider supports per-message content */
    @Size(max = 2000, message = "content must be <= 2000 characters.", groups = {ValidationGroups.Create.class})
    private String content;
}