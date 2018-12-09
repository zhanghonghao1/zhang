package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /*自定义根据条件搜索商品*/
    Map<String,Object> search(Map<String, Object> searchMap);

    /*将审核通过的sku数据保存到solr库中*/
    void importItemList(List<TbItem> itemList);

    /*删除商品后将数据从solr中删除*/
    void deleteItemByGoodsIdList(List<Long> ids);
}
