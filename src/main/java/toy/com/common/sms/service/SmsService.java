package toy.com.common.sms.service;

import toy.com.vo.common.sms.SmsSendReqVO;
import toy.com.vo.common.sms.SmsSendResVO;

public interface SmsService
{
    SmsSendResVO sendSMS(String to, String subject, String content);
    SmsSendResVO sendSMS(SmsSendReqVO reqVO);
}
