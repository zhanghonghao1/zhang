package com.pinyougou.order.service;

import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.ChangeLong;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface SeckillOrderService extends BaseService<TbSeckillOrder> {

    PageResult search(Integer page, Integer rows, TbSeckillOrder tbSeckillOrder);

    /**
     * 查询全部订单并处理id过长
     * @return
     */
    List<ChangeLong> findAllSeckillOrder();

    /**
     * 查询全部订单并处理id过长(商家)
     * @param tbSeckillOrder
     * @return
     */
    List<ChangeLong> findAllSeckillOrderByUser(TbSeckillOrder tbSeckillOrder);


    /**
     * 状态查询
     * @param value
     * @return
     */
    List<ChangeLong> findStatus(String value);

}