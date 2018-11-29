package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
     * 分页查询全部品牌http://localhost:9100/brand/testPage.do?page=1&rows=5
     */
    @GetMapping("/findPage")
    public PageResult findAllpages(Integer page,Integer rows){
        return brandService.findPage(page,rows);
    }

    /**
     * 添加品牌
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

    /**
     * 根据id查询品牌
     */
    @GetMapping("/findOne")
    public TbBrand findBrandById(Long id){
        return brandService.findOne(id);
    }

    /**
     * 修改品牌数据
     */
    @PostMapping("/update")
    public Result updateBrand(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    /**
     * 删除数据
     */
    @GetMapping("/delete")
    public Result deleteBrand(Long[] ids){
        try {
            brandService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页条件查询品牌
     */
    @PostMapping("/search")
    public PageResult searchBrand(Integer page,Integer rows,@RequestBody TbBrand brand){
        return brandService.search(page, rows, brand);
    }

    /**
     * 获得品牌下拉框需要的品牌列表
     */
    @GetMapping("/selectOptionList")
    public List<Map<String,Object>> selectOptionList(){
        return brandService.selectOptionList();
    }
}
