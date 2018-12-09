package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.FilterWriter;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController{

    @Reference
    private GoodsService goodsService;
    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return goodsService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //将商家名称传给goods类
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            //设置未提交审核状态
            goods.getGoods().setAuditStatus("0");
            //将所有数据保存到数据库中
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    /**
     * 根据id查询基本,描述,sku信息
     *
     * @param id
     * @return
     */
    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsById(id);
    }

    /**
     * 自定义更新商品
     *
     * @param goods
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.updateGoods(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteGoodsByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     *
     * @param goods 查询条件
     * @param page  页号
     * @param rows  每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        //设置只能查询自家产品
        //获得登录商家名称
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.search(page, rows, goods);
    }

    /**
     * 提交审核
     *
     * @return
     */
    @GetMapping("/updateStatus.do")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            return Result.ok("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新失败");
    }

    /**
     * 上架
     * @return
     */
    @GetMapping("/onmarketable")
    public Result onmarketable(Long[] ids){
        try {
            goodsService.onmarketable(ids);
            //根据spuid查询审核通过并且已上架(1)对应的sku列表
            List<TbItem> itemList = goodsService.findItemListByGoodsIdsAndStatus(ids, "1");
            //更新sku数据到sku列表
            itemSearchService.importItemList(itemList);
            return Result.ok("上架成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("上架失败");
    }

    /**
     * 下架
     * @return
     */
    @GetMapping("/upmarketable")
    public Result upmarketable(Long[] ids){
        try {
            goodsService.upmarketable(ids);
            //更新sku数据到sku列表
            itemSearchService.deleteItemByGoodsIdList(Arrays.asList(ids));
            return Result.ok("下架成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("下架失败");
    }
}