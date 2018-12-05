package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/itemSearch")
@RestController
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;

    /**
     * 根据搜索条件查询商品
     * @param searchMap 搜索条件
     * @return 搜索结果
     */
    @PostMapping("/search")
    public Map<String,Object> search(@RequestBody Map<String,Object> searchMap){
        return itemSearchService.search(searchMap);
    }
}
