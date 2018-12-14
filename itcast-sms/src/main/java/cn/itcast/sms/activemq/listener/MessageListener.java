package cn.itcast.sms.activemq.listener;

import cn.itcast.sms.util.SmsUtil;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 消息监听器;
 * 从短信的消息队列中接收消息(要发的短信内容)
 */
@Component
public class MessageListener {

    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "itcast_sms_queue")
    public void recieveMsg(Map<String, String> map) {
        try {
            //发短信
            SendSmsResponse response = smsUtil.sendSms(map.get("mobile"),map.get("signName"),map.get("templateCode"),map.get("templateParam"));
            System.out.println("短信接口返回的数据----------------");
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
