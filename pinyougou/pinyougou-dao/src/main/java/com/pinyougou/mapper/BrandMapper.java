package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Date2018/11/23
 * 品牌持久层接口,
 */
public interface BrandMapper extends Mapper<TbBrand> {

    /*自定义获得品牌下拉列表*/
    List<Map<String, Object>> selectOptionList();
}
