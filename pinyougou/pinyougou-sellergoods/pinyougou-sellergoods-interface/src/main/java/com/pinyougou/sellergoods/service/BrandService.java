package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * Date2018/11/23
 * 品牌业务层接口
 */
public interface BrandService extends BaseService<TbBrand>{

    //自定义条件分页查询
    PageResult search(Integer page, Integer rows, TbBrand brand);

    //自定义获得品牌下拉列表
    List<Map<String,Object>> selectOptionList();
}
