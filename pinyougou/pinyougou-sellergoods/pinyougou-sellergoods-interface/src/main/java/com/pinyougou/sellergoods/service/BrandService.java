package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;

import java.util.List;

/**
 * Date2018/11/23
 * 品牌业务层接口
 */
public interface BrandService {
    /**
     * 查询全部商品
     */
    List<TbBrand> findAll();
}
