package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);

    /**
     * 新建的添加商品(三个属性)的方法
     * @param goods
     */
    void addGoods(Goods goods);

    //自定义逻辑删除
    void deleteGoodsByIds(Long[] ids);

    //自定义根据id查询基本,描述,sku信息
    Goods findGoodsById(Long id);

    /*自定义更新商品三大信息操作*/
    void updateGoods(Goods goods);

    /*自定义更新审核状态*/
    void updateStatus(Long[] ids, String status);

    /*上架*/
    void onmarketable(Long[] ids);

    /*下架*/
    void upmarketable(Long[] ids);

    /*根据spuid查询对应的sku列表*/
    List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status);

    /**
     * 详情页面需要的数据; 根据id获得商品基本,描述,sku信息(根据是否默认排序,将序排序),123级中文名称
     * @param goodsId id
     * @param status 状态
     * @return goods
     */
    Goods findGoodsByIdAndStatus(Long goodsId, String status);
}