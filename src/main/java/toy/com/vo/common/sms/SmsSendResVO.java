package toy.com.vo.common.sms;

import lombok.Data;

@Data
public class SmsSendResVO {

    /*발송 결과(성공여부, vendor 응답코드/메시지, requestId 등)*/

    private boolean success;
    private String requestId;       // vendor request id if any
    private String resultCode;      // vendor result code
    private String resultMessage;   // vendor result message
}