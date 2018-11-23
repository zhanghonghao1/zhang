package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;

import java.util.List;

/**
 * Date2018/11/23
 * 品牌持久层接口
 */
public interface BrandMapper {
    /**
     * 查询全部商品
     */
    List<TbBrand> findAll();
}
