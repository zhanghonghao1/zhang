package com.pinyougou.solr;

/*导入item数据到solr中*/

import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext*.xml")
public class ItemImport2SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private ItemMapper itemMapper;


    @Test
    public void test(){
        //查询已启用数据
        TbItem tbItem=new TbItem();
        tbItem.setStatus("1");
        List<TbItem> itemList = itemMapper.select(tbItem);
        //转换json
        for (TbItem item : itemList) {
            Map map = JSONObject.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(map);
        }
        //导入
        solrTemplate.saveBeans(itemList);
        //提交
        solrTemplate.commit();
    }

}