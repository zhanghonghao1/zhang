package cn.itcast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/*发送MQ消息*/
@RequestMapping("/mq")
@RestController
public class MQController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @GetMapping("/send")
    public String sendMapMsg(){
        Map<String,String> map=new HashMap<>();
        map.put("姓名:","张宏浩");
        jmsMessagingTemplate.convertAndSend("spring.boot.map.queue",map);
        return "发送成功";
    }

    /*发送短信mq消息*/
    @GetMapping("/send")
    public String sendSmsMapMsg(){
        Map<String,String> map=new HashMap<>();
        map.put("mobile:","张宏浩");
        map.put("signName:","张宏浩");
        map.put("templateCode:","张宏浩");
        map.put("templateParam:","张宏浩");
        jmsMessagingTemplate.convertAndSend("itcast_sms_queue",map);
        return "发送成功";
    }
}
