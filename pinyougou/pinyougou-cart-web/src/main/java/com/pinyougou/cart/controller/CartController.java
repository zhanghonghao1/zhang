package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/cart")
@RestController
public class CartController {

    //cookie中购物车列表的名称
    private static final String COOKIE_CAET_LIST="PYG_CART_LIST";
    //cookie中购物车列表保存的时间
    private static final int COOKIE_CAET_LIST_MAX_AGE=3600*24;
    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     * 获取登录用户名
     * @return
     */
    @GetMapping("/getUsername")
    public Map<String,Object> getUsername(){
        Map<String,Object> map=new HashMap<>();
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",username);
        return map;
    }

    /**
     * 获取购物车列表
     * @return
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList(){
        try {
            //判断是否登录
            String username=SecurityContextHolder.getContext().getAuthentication().getName();
            //从cookie中获取购物车数据
            String cartListJSONStr=CookieUtils.getCookieValue(request,COOKIE_CAET_LIST,true);
            //商家列表
            List<Cart> cookieCartList=new ArrayList<>();
            if (!StringUtils.isEmpty(cartListJSONStr)) {
                //不为空转换为列表并赋值给商家列表
                cookieCartList = JSONArray.parseArray(cartListJSONStr, Cart.class);
            }
            //未登录；从cookie中获取购物车数据
            if ("anonymousUser".equals(username)) {
                return cookieCartList;
            }else {
                //已登录，从redis中获取购物车数据
                List<Cart> redisCartList=cartService.findCartListByUsername(username);
                //合并购物车列表
                if (cookieCartList.size()>0){
                    //cookie不为空,将cookie购物车列表和redis的合并
                    redisCartList=cartService.mergeCartList(cookieCartList,redisCartList);
                    //保存最新的购物车列表到redis中
                    cartService.saveCartListByUsername(redisCartList,username);
                   //删除cookie中的数据
                    CookieUtils.deleteCookie(request,response,COOKIE_CAET_LIST);
                }
                return redisCartList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 将商品加入购物车
     * @param itemId
     * @param num
     * @return
     */
    @GetMapping("/addItemToCartList")
    @CrossOrigin(origins = "http://item.pinyougou.com", allowCredentials = "true")
    public Result addItemToCartList(Long itemId,Integer num){
        Result result=Result.fail("加入购物车失败");
        try {
            // 设置允许跨域请求
            //response.setHeader("Access-Control-Allow-Origin","http://item.pinyougou.com");
            // 允许携带并接收 cookie
            //response.setHeader("Access-Control-Allow-Credentials", "true");

            //获取登录名称判断是否登录
            String username=SecurityContextHolder.getContext().getAuthentication().getName();
            //获取cookie购物车列表
            List<Cart> cookieCartList=findCartList();
            //将新的商品和购买数量更新到购物车列表；
            List<Cart> newCartList=cartService.addItemToCartList(cookieCartList,itemId,num);
            //未登录更新cookie中数据
            if ("anonymousUser".equals(username)){
                //将列表转为json
                String jsonString = JSON.toJSONString(newCartList);
                //将购物车列表写回到cookie
                CookieUtils.setCookie(request,response,COOKIE_CAET_LIST,jsonString,COOKIE_CAET_LIST_MAX_AGE,true);
            }else {
                //已登录, 将购物车列表更新到redis中
                cartService.saveCartListByUsername(newCartList,username);
            }
            result=Result.ok("加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
