package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/pay")
@RestController
public class PayController {
    @Reference
    private OrderService orderService;
    @Reference
    private WeixinPayService weixinPayService;

    /**
     * 根据支付日志 id 到微信支付创建支付订单并返回支付二维码地址等信息
     * @return
     */
    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo) {
        //根据支付日志id查找支付日志信息
        TbPayLog payLog =orderService.findPayLogByOutTradeNo(outTradeNo);
        if (payLog!=null){
            //到支付系统获取url
            //获得商品描述
            List<TbOrderItem> bodyList = orderService.findBody(outTradeNo);
            String body = bodyList.get(0).getTitle();
            return weixinPayService.createnative(outTradeNo,payLog.getTotalFee().toString(),body);
        }
        return new HashMap<>();
    }

    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo) {
        Result result=Result.fail("支付失败");
        try {
            int count=0;
            while (true){
                //到微信支付系统查询支付状态
                Map<String,String> resultMap=weixinPayService.queryPayStatus(outTradeNo);
                //查询结果不为空并且支付结果为success则未支付成功
                if (resultMap==null){
                    break;
                }
                if ("SUCCESS".equals(resultMap.get("trade_state"))){
                    result=Result.ok("支付成功");
                    //支付成功更新订单信息,支付日志状态
                    orderService.updateOrderStatus(outTradeNo,resultMap.get("transaction_id"));
                    break;
                }
                Thread.sleep(3000);
                count++;
                if (count>60){
                    result=Result.fail("支付超时");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
