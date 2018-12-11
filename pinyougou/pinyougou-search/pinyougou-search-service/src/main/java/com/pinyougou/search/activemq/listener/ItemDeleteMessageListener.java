package com.pinyougou.search.activemq.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.Arrays;

/*消费者对solr做删除*/
public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener{

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //接收数据,转换为数组
        ObjectMessage objectMessage= (ObjectMessage) message;
        Long[] goodsIds= (Long[]) objectMessage.getObject();
        //更新solr
        itemSearchService.deleteItemByGoodsIdList(Arrays.asList(goodsIds));
    }
}
