package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService extends BaseService<TbSpecification> {

    PageResult search(Integer page, Integer rows, TbSpecification specification);

    //新建的add方法
    void add(Specification specification);

    //新建的findOne方法
    Specification findOne(Long id);

    //新建的update方法
    void update(Specification specification);

    //新建的删除方法
    void deleteSpecificationByIds(Long[] ids);

    //自定义规格下拉列表
    List<Map<String,Object>> selectOptionList();
}