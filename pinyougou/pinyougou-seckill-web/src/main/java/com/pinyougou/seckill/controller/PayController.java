package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
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
    private SeckillOrderService seckillOrderService;
    @Reference
    private WeixinPayService weixinPayService;

    /**
     * 根据支付日志 id 到微信支付创建支付订单并返回支付二维码地址等信息
     * @return
     */
    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo) {
        // 根据订单 id 查询放置在 redis 中的订单
        TbSeckillOrder tbSeckillOrder=seckillOrderService.getSeckillOrderInRedisByOrderId(outTradeNo);
        if (tbSeckillOrder!=null){
            //到支付系统获取url
           String body="手机".toString();
            //获得总金额
            String totalFee=(long)(tbSeckillOrder.getMoney().doubleValue()*100)+"";
            return weixinPayService.createnative(outTradeNo,totalFee,body);
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
                    seckillOrderService.updateOrderInRedisStatus(outTradeNo,resultMap.get("transaction_id"));
                    break;
                }
                Thread.sleep(3000);
                count++;
                if (count>60){
                    result=Result.fail("支付超时");
                    //关闭微信支付的订单
                    resultMap=weixinPayService.closeOrder(outTradeNo);
                    //关闭过程被支付
                    if ("ORDERPAID".equals(resultMap.get("err_code"))){
                        result=Result.ok("支付成功");
                        //需要更新订单的支付状态
                        seckillOrderService.updateOrderInRedisStatus(outTradeNo,resultMap.get("transaction_id"));
                        break;
                    }
                    //删除redis中的订单
                    seckillOrderService.deleteOrderInRedisByIds(outTradeNo);
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
