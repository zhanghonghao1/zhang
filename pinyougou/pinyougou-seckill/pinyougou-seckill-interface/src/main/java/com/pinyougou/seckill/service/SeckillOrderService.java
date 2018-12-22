package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface SeckillOrderService extends BaseService<TbSeckillOrder> {

    PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder);

    /**
     * 生成秒杀订单并返回id
     * @param username
     * @param seckillId
     * @return
     */
    Long submitOrder(String username, Long seckillId) throws Exception;

    /**
     * 根据订单 id 查询放置在 redis 中的订单
     * @param outTradeNo
     * @return
     */
    TbSeckillOrder getSeckillOrderInRedisByOrderId(String outTradeNo);

    /**
     * 更新redis中的支付状态,并保存到数据库中
     * @param outTradeNo
     * @param transactionId
     */
    void updateOrderInRedisStatus(String outTradeNo, String transactionId);

    /**
     * 删除redis中的订单
     * @param outTradeNo
     */
    void deleteOrderInRedisByIds(String outTradeNo);
}