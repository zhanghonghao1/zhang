package cn.itcast;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MQListener {

    @JmsListener(destination = "spring.boot.map.queue")
    public void recieveMsg(Map<String,String> map){
        System.out.println(map);
    }
}
