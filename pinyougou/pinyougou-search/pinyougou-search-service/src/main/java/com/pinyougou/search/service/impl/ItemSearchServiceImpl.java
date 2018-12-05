package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService{

    @Autowired
    private SolrTemplate solrTemplate;

    /*根据条件搜索商品*/
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        //查询结果的集合
        Map<String,Object> resultMap= new HashMap<>();

        //创建查询对象
        //SimpleQuery query=new SimpleQuery();
        SimpleHighlightQuery query=new SimpleHighlightQuery();
        //设置查询条件,is会对关键字分词
        Criteria criteria=new Criteria("item_title").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //设置高亮配置信息
        HighlightOptions highlightOptions=new HighlightOptions();
        //高亮哪部分内容
        highlightOptions.addField("item_title");
        //高亮的起始标签
        highlightOptions.setSimplePrefix("<font style='color:red'>");
        //高亮的结束标签
        highlightOptions.setSimplePostfix("</font>");
        //将配置信息添加到条件
        query.setHighlightOptions(highlightOptions);

        //根据条件进行查询
        //ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);

        /*处理高亮标签*/
        //得到高亮域内容
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();
        if (highlighted!=null&&highlighted.size()>0){
            //内容不为空,遍历得到每一个域
            for (HighlightEntry<TbItem> entry : highlighted) {
                //得到标签的高亮标题
                List<HighlightEntry.Highlight> highlights = entry.getHighlights();
                if (highlights!=null&&highlights.size()>0&&highlights.get(0).getSnipplets()!=null){
                    //第一个0则表示高亮的域，取第一个；第二个get(0)表示域中的第一个高亮标题
                    String title = highlights.get(0).getSnipplets().get(0);
                    //将高亮标题给查询得到商品
                    entry.getEntity().setTitle(title);
                }
            }
        }
        resultMap.put("rows",tbItems.getContent());
        return resultMap;
    }
}
