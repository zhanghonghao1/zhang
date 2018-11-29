package com.pinyougou.mapper;

import com.pinyougou.pojo.TbSpecification;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecificationMapper extends Mapper<TbSpecification> {

    //自定义规格下拉列表
    List<Map<String,Object>> selectOptionList();
}
