package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/*商品详情的处理器*/
@Controller
public class ItemController {

    @Reference
    private GoodsService goodsService;
    @Reference
    private ItemCatService itemCatService;

    @GetMapping("/{goodsId}")
    public ModelAndView toLtemPage(@PathVariable Long goodsId){
        ModelAndView model=new ModelAndView("item");
        //根据id获得商品基本,描述,sku信息(根据是否默认排序,将序排序),123级中文名称
        Goods goods = goodsService.findGoodsByIdAndStatus(goodsId,"1");
        //基本信息
       model.addObject("goods",goods.getGoods());
       //描述信息
        model.addObject("goodsDesc",goods.getGoodsDesc());
        //sku信息
        model.addObject("itemList",goods.getItemList());
        //	itemCat1  第1级商品分类中文名称
        TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
        model.addObject("itemCat1",itemCat1.getName());
        //	itemCat2  第2级商品分类中文名称
        TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
        model.addObject("itemCat2",itemCat2.getName());
        //	itemCat3  第3级商品分类中文名称
        TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
        model.addObject("itemCat3",itemCat3.getName());
        return model;
    }
}
