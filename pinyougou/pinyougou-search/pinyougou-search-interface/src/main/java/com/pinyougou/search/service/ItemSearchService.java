package com.pinyougou.search.service;

import java.util.Map;

public interface ItemSearchService {

    /*自定义根据条件搜索商品*/
    Map<String,Object> search(Map<String, Object> searchMap);
}
