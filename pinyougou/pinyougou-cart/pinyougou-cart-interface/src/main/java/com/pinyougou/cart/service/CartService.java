package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List;

public interface CartService {
    /**
     * 根据商品id查询到商品,将商品和数量保存到购物车列表
     * @param cookieCartList
     * @param itemId
     * @param num
     * @return
     */
    List<Cart> addItemToCartList(List<Cart> cookieCartList, Long itemId, Integer num);

    /**
     * 根据用户id查询redis中的购物车列表
     * @param username
     * @return
     */
    List<Cart> findCartListByUsername(String username);

    /**
     * 将用户对应的购物车列表保存到redis
     * @param newCartList
     * @param username
     */
    void saveCartListByUsername(List<Cart> newCartList, String username);

    /**
     * 将reids和cookie中的购物车列表合并
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList);
}
