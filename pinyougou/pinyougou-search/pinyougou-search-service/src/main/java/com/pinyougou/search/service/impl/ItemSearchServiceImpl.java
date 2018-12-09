package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;

import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ItemSearchServiceImpl implements ItemSearchService{

    @Autowired
    private SolrTemplate solrTemplate;

    /*根据条件搜索商品*/
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        //查询结果的集合
        Map<String,Object> resultMap= new HashMap<>();

        //处理搜索关键字的空格
        if (!StringUtils.isEmpty(searchMap.get("keywords"))){
            searchMap.put("keywords",searchMap.get("keywords").toString().replaceAll(" ",""));
        }

        //创建查询对象
        //SimpleQuery query=new SimpleQuery();
        SimpleHighlightQuery query=new SimpleHighlightQuery();
        //设置名称查询条件,is会对关键字分词
        Criteria criteria=new Criteria("item_title").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //设置分类查询
        if (!StringUtils.isEmpty(searchMap.get("category"))){
            //创建条件对象
            Criteria categoryCriteria=new Criteria("item_category").is(searchMap.get("category"));
            //创建过滤查询对象
            SimpleFilterQuery categoryFilterQuery = new SimpleFilterQuery();
            //将条件添加到查询对象中
            categoryFilterQuery.addCriteria(categoryCriteria);
            //添加过滤条件
            query.addFilterQuery(categoryFilterQuery);
        }

        //设置品牌查询
        if (!StringUtils.isEmpty(searchMap.get("brand"))){
            //创建条件对象
            Criteria brandCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            //创建过滤查询对象
            SimpleFilterQuery brandFilterQuery = new SimpleFilterQuery();
            //将条件添加到查询对象中
            brandFilterQuery.addCriteria(brandCriteria);
            //添加过滤条件
            query.addFilterQuery(brandFilterQuery);
        }

        //设置规格查询
        if (searchMap.get("spec")!=null){
            //获取每一个规格和值组成的map集合
            Map<String,String> specMap= (Map<String, String>) searchMap.get("spec");
            //遍历每一个规格和值
            Set<Map.Entry<String, String>> entries = specMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                //创建条件对象,拼接规格名和规格值
                Criteria specCriteria=new Criteria("item_spec_"+entry.getKey()).is(entry.getValue());
                //创建过滤查询对象
                SimpleFilterQuery specFilterQuery = new SimpleFilterQuery();
                //将条件添加到查询对象中
                specFilterQuery.addCriteria(specCriteria);
                //添加过滤条件
                query.addFilterQuery(specFilterQuery);
            }
        }

        //设置价格查询
        if (!StringUtils.isEmpty(searchMap.get("price"))){
            //分隔价格字符串,将起始结束价格存到一个数组中
            String[] prices=searchMap.get("price").toString().split("-");

            /*价格大于等于起始价格*/
            //创建条件对象
            Criteria startPriceCriteria=new Criteria("item_price").greaterThanEqual(prices[0]);
            //创建过滤查询对象
            SimpleFilterQuery startPriceFilterQuery = new SimpleFilterQuery();
            //将条件添加到查询对象中
            startPriceFilterQuery.addCriteria(startPriceCriteria);
            //添加过滤条件
            query.addFilterQuery(startPriceFilterQuery);

            /*价格小于等于结束价格,如果没有上限,不需要做判断处理*/
            if (!"*".equals(prices[1])){
                //创建条件对象
                Criteria endPriceCriteria=new Criteria("item_price").lessThanEqual(prices[1]);
                //创建过滤查询对象
                SimpleFilterQuery endPriceFilterQuery = new SimpleFilterQuery();
                //将条件添加到查询对象中
                endPriceFilterQuery.addCriteria(endPriceCriteria);
                //添加过滤条件
                query.addFilterQuery(endPriceFilterQuery);
            }
        }

        //得到传过来的分页信息(页号,页大小)
        Integer pageNo=1;//默认第一页
        if (searchMap.get("pageNo")!=null){
            pageNo=Integer.parseInt(searchMap.get("pageNo").toString());
        }
        Integer pageSize=10;//默认一页10条
        if (searchMap.get("pageSize")!=null){
            pageSize=Integer.parseInt(searchMap.get("pageSize").toString());
        }
        //设置起始索引号
        query.setOffset((pageNo-1)*pageSize);
        //设置页大小
        query.setRows(pageSize);

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

        //设置排序查询
        if (!StringUtils.isEmpty(searchMap.get("sortField")) && !StringUtils.isEmpty(searchMap.get("sort"))){
            //名称
            String sortField=searchMap.get("sortField").toString();
            //排序
            String sortStr=searchMap.get("sort").toString();
            
            //添加排序,参数一: 排序方式;  参数二: 名称
            Sort sort = new Sort("DESC".equals(sortStr) ? Sort.Direction.DESC : Sort.Direction.ASC, "item_" + sortField);
            query.addSort(sort);
        }

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
        //总页数
        resultMap.put("totalPages",tbItems.getTotalPages());
        //总记录数
        resultMap.put("total",tbItems.getTotalElements());

        return resultMap;
    }

    /**
     *将审核通过的sku数据保存到solr库中
     * @param itemList
     */
    @Override
    public void importItemList(List<TbItem> itemList) {
        //遍历sku列表
        for (TbItem tbItem : itemList) {
            //将规格参数转换为json格式
            Map specMap= JSON.parseObject(tbItem.getSpec(),Map.class);
            tbItem.setSpecMap(specMap);
        }
        //将sku商品添加到solr中
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    /**
     * 删除商品后将数据从solr中删除
     * @param ids
     */
    @Override
    public void deleteItemByGoodsIdList(List<Long> ids) {
        //solr方法对象
        Criteria criteria=new Criteria("item_goodsid").in(ids);
        //solr处理对象
        SimpleQuery query=new SimpleQuery(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
