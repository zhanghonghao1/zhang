package com.pinyougou.search.activemq.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;

/*消费者对solr做新增*/
public class ItemImportMessageListener extends AbstractAdaptableMessageListener{

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //接收消息,转换为itemList列表
        TextMessage textMessage= (TextMessage) message;
        List<TbItem> itemList= JSON.parseArray(textMessage.getText(),TbItem.class);

        //调用方法更新到solr中
        itemSearchService.importItemList(itemList);

    }
}
