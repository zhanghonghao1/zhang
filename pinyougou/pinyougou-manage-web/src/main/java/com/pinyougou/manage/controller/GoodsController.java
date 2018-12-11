package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.jms.*;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;
    /*@Reference
    private ItemSearchService itemSearchService;*/
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ActiveMQQueue itemSolrQueue;
    @Autowired
    private ActiveMQQueue itemSolrDeleteQueue;
    @Autowired
    private ActiveMQTopic itemTopic;
    @Autowired
    private ActiveMQTopic itemDeleteTopic;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return goodsService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //将商家名称传给goods类
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            //设置未提交审核状态
            goods.getGoods().setAuditStatus("0");
            //将所有数据保存到数据库中
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    /**
     * 根据id查询基本,描述.sku信息
     * @param id
     * @return
     */
    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsById(id);
    }

    /**
     * 自定义更新商品
     * @param goods
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.updateGoods(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    /**
     * 自定义逻辑删除
     * @param ids
     * @return
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteGoodsByIds(ids);
            //删除solr中对应商品索引数据
            //itemSearchService.deleteItemByGoodsIdList(Arrays.asList(ids));
            sendMQMsg(itemSolrDeleteQueue,ids);
            //发送商品删除的订阅消息
            sendMQMsg(itemDeleteTopic,ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 发送消息到MQ
     * @param destination
     * @param ids
     */
    private void sendMQMsg(Destination destination, Long[] ids) {
        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(ids);
            }
        });
    }

    /**
     * 分页查询列表
     * @param goods 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbGoods goods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return goodsService.search(page, rows, goods);
    }

    /**
     * 提交审核,同步发送消息到mq
     *
     * @return
     */
    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            //商品审核状态为2的sku就更新到solr中
            if ("2".equals(status)) {
                //根据spuid查询审核通过并且已上架(1)对应的sku列表
                List<TbItem> itemList = goodsService.findItemListByGoodsIdsAndStatus(ids, "1");
                //查询到需要更新的sku列表
                //itemSearchService.importItemList(itemList);
                //发送sku列表到MQ
                jmsTemplate.send(itemSolrQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        //将sku列表保存到text
                        TextMessage textMessage=session.createTextMessage();
                        textMessage.setText(JSON.toJSONString(itemList));
                        return textMessage;
                    }
                });
                //审核通过发送商品的发布信息
                sendMQMsg(itemTopic,ids);
            }
            if ("3".equals(status)) {
                //更新sku数据到sku列表
                //itemSearchService.deleteItemByGoodsIdList(Arrays.asList(ids));
                sendMQMsg(itemSolrDeleteQueue,ids);
                //驳回审核发送商品的发布信息
                sendMQMsg(itemDeleteTopic,ids);
            }
            return Result.ok("审核成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("审核失败");
    }
}
