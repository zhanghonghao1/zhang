package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.ChangeLong;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface OrderService extends BaseService<TbOrder> {

    PageResult search(Integer page, Integer rows, TbOrder order);

    /**
     *保存订单数据并返回日志id
     * @param order
     * @return
     */
    String addOrder(TbOrder order);

    /**
     * 根据支付日志id查找支付日志信息
     * @param outTradeNo
     * @return
     */
    TbPayLog findPayLogByOutTradeNo(String outTradeNo);

    /**
     * 获得商品描述
     * @param outTradeNo
     * @return
     */
    List<TbOrderItem> findBody(String outTradeNo);

    /**
     * 更新订单状态和信息
     * @param outTradeNo
     * @param transactionId
     */
    void updateOrderStatus(String outTradeNo, String transactionId);

    /**
     * 查询全部订单并处理id过长
     * @return
     */
    List<ChangeLong> findAllOrder();

    /**
     * 查询全部订单并处理id过长(商家)
     * @param order
     * @return
     */
    List<ChangeLong> findAllOrderByUser(TbOrder order);

    /**
     * 修改地址
     * @param
     */
    void updateArea(TbOrder tbOrder,Long id);

    /**
     *状态查询
     * @param value
     * @return
     */
    List<ChangeLong> findStatus(String value);

}