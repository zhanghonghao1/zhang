package com.pinyougou.pay.service;

import java.util.List;
import java.util.Map;

public interface WeixinPayService {
    /**
     * 到微信获取支付url
     * @param outTradeNo
     * @param totalFee
     * @return
     */
    Map<String,String> createnative(String outTradeNo, String totalFee, String body);

    /**
     * 每隔三秒到微信系统查询支付状态
     * @param outTradeNo
     */
    Map<String,String> queryPayStatus(String outTradeNo);

    /**
     * 关闭微信支付的订单
     * @param outTradeNo
     * @return
     */
    Map<String,String> closeOrder(String outTradeNo);
}
