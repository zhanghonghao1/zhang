package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Date2018/11/23
 */
@Service(interfaceClass =BrandService.class)
public class BrandServiceImpl implements BrandService {

    //定义dao
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 查询全部商品
     */
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.findAll();
    }
}
