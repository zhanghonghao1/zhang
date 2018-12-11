package com.pinyougou.item.activemq.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/*接收订阅消费者详情系统  更新*/
public class ItemAuditMessageListener extends AbstractAdaptableMessageListener{
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Reference
    private GoodsService goodsService;
    @Reference
    private ItemCatService itemCatService;
    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //创建接收对象
        ObjectMessage objectMessage= (ObjectMessage) message;
        //接收传递过来的数组,转换为长整型
        Long[] goodsIds= (Long[]) objectMessage.getObject();
        for (Long goodsId : goodsIds) {
            //遍历每一个id生成静态页面
            genItemHtml(goodsId);
        }
    }

    //生成静态页面
    private void genItemHtml(Long goodsId) {
        try {
            //	第一步： 创建一个 Configuration 对象; 调用配置文件的内容
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //	第四步：获取模板
            Template template = configuration.getTemplate("item.ftl");
            //	第五步： 创建一个模板使用的数据集用来添加数据， 可以是 pojo 也可以是 map； 一般是 Map
            Map<String,Object> dataModel=new HashMap<>();
            //根据id获得商品基本,描述,sku信息(根据是否默认排序,将序排序),123级中文名称
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId,"1");
            //基本信息
            dataModel.put("goods",goods.getGoods());
            //描述信息
            dataModel.put("goodsDesc",goods.getGoodsDesc());
            //sku信息
            dataModel.put("itemList",goods.getItemList());
            //	itemCat1  第1级商品分类中文名称
            TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataModel.put("itemCat1",itemCat1.getName());
            //	itemCat2  第2级商品分类中文名称
            TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataModel.put("itemCat2",itemCat2.getName());
            //	itemCat3  第3级商品分类中文名称
            TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataModel.put("itemCat3",itemCat3.getName());
            //	第六步：创建一个 Writer 对象，一般创建 FileWriter 对象，指定生成的文件名
            String filename=ITEM_HTML_PATH+goodsId+".html";
            //FileWriter fileWriter=new FileWriter(new File(filename));
            Writer fileWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(filename), "UTF-8"));
            //	第七步：调用第四步的模板对象的 process 方法将第五步的数据输出给第六步的文件
            template.process(dataModel,fileWriter);
            //	第八步：关闭流
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
