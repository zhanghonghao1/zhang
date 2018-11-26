package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

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
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 分页查询全部商品http://localhost:9100/brand/testPage.do?page=1&rows=5
     */
    @GetMapping("/findPage")
    public PageResult findAllpages(Integer page,Integer rows){
        return brandService.findPage(page,rows);
    }

    /**
     * 条件分页查询
     */
    @PostMapping("/add")
    public Result addBrand(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return Result.ok("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("添加失败");
    }
}
