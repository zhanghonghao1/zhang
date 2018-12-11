package com.pinyougou.item.activemq.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

/*接收订阅消费者详情系统  删除*/
public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener{

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //创建接收对象
        ObjectMessage objectMessage= (ObjectMessage) message;
        //接收传递过来的数组,转换为长整型
        Long[] goodsIds= (Long[]) objectMessage.getObject();
        //遍历每一个id生成静态页面
        for (Long goodsId : goodsIds) {
            String filename=ITEM_HTML_PATH+goodsId+".html";
            File file=new File(filename);
            //判断指定路径下是否存在该文件
            if (file.exists()){
                //存在就调用删除方法
                file.delete();
            }
        }
    }
}
