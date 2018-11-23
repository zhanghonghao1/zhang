package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Date2018/11/23
 * 商品控制器
 */
@RequestMapping("/brand")
@RestController
public class BrandController {

    //定义service
    @Reference
    private BrandService brandService;

    /**
     * 查询全部商品
     */
    @GetMapping("/findAll")
    public List<TbBrand> findAllBrand(){
        return brandService.findAll();
    }
}
