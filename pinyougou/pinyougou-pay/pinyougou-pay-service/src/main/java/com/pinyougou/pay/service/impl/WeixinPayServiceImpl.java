package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
@Service(interfaceClass = WeixinPayService.class)
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String mch_id;
    @Value("${notifyurl}")
    private String notify_url;
    @Value("${partnerkey}")
    private String partnerkey;
    /**
     * 到微信获取支付url
     *
     * @param outTradeNo
     * @param totalFee
     * @return 支付二维码等信息
     */
    @Override
    public Map<String, String> createnative(String outTradeNo, String totalFee,String body) {
        //微信返回的信息
        Map<String,String> returnMap=new HashMap<>();
        try {
            //封装微信需要参数
            Map<String,String> paramMap=new HashMap<>();
            paramMap.put("appid",appid);
            paramMap.put("mch_id",mch_id);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //paramMap.put("sign",);签名自动生成
            paramMap.put("body",body);//商品描述
            paramMap.put("out_trade_no",outTradeNo);
            paramMap.put("total_fee",totalFee);
            paramMap.put("spbill_create_ip","127.0.0.1");
            paramMap.put("notify_url",notify_url);
            paramMap.put("trade_type","NATIVE");
            //将参数转换为微信支付需要的xml
            String singedXml=WXPayUtil.generateSignedXml(paramMap,partnerkey);
            //3. 创建HttpClient对象发送请求；
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(singedXml);
            httpClient.post();
            //获取微信支付返回的数据
            String content = httpClient.getContent();
            //4. 处理返回结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            returnMap.put("result_code",resultMap.get("result_code"));
            returnMap.put("code_url",resultMap.get("code_url"));
            returnMap.put("outTradeNo",outTradeNo);
            returnMap.put("totalFee",totalFee);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 每隔三秒到微信系统查询支付状态
     *
     * @param outTradeNo
     */
    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {
        //微信返回的信息
        Map<String,String> returnMap=new HashMap<>();
        try {
            //封装微信需要参数
            Map<String,String> paramMap=new HashMap<>();
            paramMap.put("appid",appid);
            paramMap.put("mch_id",mch_id);
            paramMap.put("out_trade_no",outTradeNo);
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
            //paramMap.put("sign",);签名自动生成
            //转换为微信需要的xml
            String xml = WXPayUtil.generateSignedXml(paramMap,partnerkey);
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xml);
            httpClient.post();
            //获取微信支付返回的数据
            String content = httpClient.getContent();
            //4. 处理返回结果
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
