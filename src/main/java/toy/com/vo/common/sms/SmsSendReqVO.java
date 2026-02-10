package toy.com.vo.common.sms;

import lombok.Data;
import toy.com.validation.group.ValidationGroups;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
public class SmsSendReqVO {

    /*발송 “요청” (type/from/content/subject 등 공통값 + messages 목록)*/
    @Pattern(regexp = "^$|^(SMS|LMS)$", message = "type must be SMS or LMS.", groups = {ValidationGroups.Create.class})
    private String type;


    @Pattern(regexp = "^$|^(COMM)$", message = "contentType must be COMM.", groups = {ValidationGroups.Create.class})
    private String contentType;


    @Pattern(
            regexp = "^$|^\\d{2,3}-\\d{3,4}-\\d{4}$|^\\d{10,11}$",
            message = "from must match phone format.",
            groups = {ValidationGroups.Create.class})
    @Size(max = 20, message = "from must be <= 20 characters.", groups = {ValidationGroups.Create.class})
    private String from;


    @Size(max = 100, message = "subject must be <= 100 characters.", groups = {ValidationGroups.Create.class})
    private String subject;

    @NotBlank(message = "content is required.", groups = {ValidationGroups.Create.class}) @Size(max = 2000, message = "content must be <= 2000 characters.", groups = {ValidationGroups.Create.class})
    private String content;

    @Size(max = 20, message = "reserveTime must be <= 20 characters.", groups = {ValidationGroups.Create.class})
    private String reserveTime;
    @Size(max = 20, message = "reserveTimeZone must be <= 20 characters.", groups = {ValidationGroups.Create.class})
    private String reserveTimeZone;

    @Size(max = 50, message = "scheduleCode must be <= 50 characters.", groups = {ValidationGroups.Create.class})
    private String scheduleCode;

    @NotNull(message = "messages is required.", groups = {ValidationGroups.Create.class})
    @Size(min = 1, message = "messages must have at least 1 item.", groups = {ValidationGroups.Create.class})
    @Valid
    private List<SmsSendMsgVO> messages = new ArrayList<>();
}
