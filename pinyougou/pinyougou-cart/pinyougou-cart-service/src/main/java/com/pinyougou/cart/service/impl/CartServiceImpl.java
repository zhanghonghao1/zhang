package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService {

    //redis中购物车数据的key
    private static final String REDIS_CART_LIST="CART_LIST";

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据商品id查询到商品,将商品和数量保存到购物车列表
     *
     * @param cookieCartList
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addItemToCartList(List<Cart> cookieCartList, Long itemId, Integer num) {
        //1. 根据商品sku id查询商品并判断是否是否存在与已启用；
        TbItem item=itemMapper.selectByPrimaryKey(itemId);
        if (item==null){
            throw new RuntimeException("商品不存在!");
        }
        if (!"1".equals(item.getStatus())){
            throw new RuntimeException("商品状态不合法!");
        }
        //2. 判断商品对应的商家是否存在在购物车列表cookieCartList中
        String sellerId=item.getSellerId();//得到商家Id
        Cart cart=findCartBySellerId(cookieCartList, sellerId);
        //3. 商家不存在；则直接先添加一个商家，在该商家的商品列表中加入商品
        if (cart==null){
            if (num>0){
                cart =new Cart();
                cart.setSellerId(sellerId);
                cart.setSellerName(item.getSeller());
                //商品列表
                List<TbOrderItem> orderItemList=new ArrayList<>();
                TbOrderItem tbOrderItem=createOrderItem(item,num);
                orderItemList.add(tbOrderItem);
                //设置商品列表
                cart.setOrderItemList(orderItemList);

                cookieCartList.add(cart);
            }else {
                throw new RuntimeException("购买数量非法");
            }
        }else {
            //4. 商家存在,查看有无该商品
            TbOrderItem tbOrderItem=findOrderItemByItemId(cart.getOrderItemList(),itemId);
            if (tbOrderItem==null){
                //5. 在商家中商品如果不存在；将商品直接加入该商家的商品列表
                if (num>0){
                    tbOrderItem=createOrderItem(item,num);//创建订单商品
                    cart.getOrderItemList().add(tbOrderItem);//向商家的商品列表添加商品
                }else {
                    throw new RuntimeException("购买数量非法");
                }
            }else {
                //6. 在商家中商品如果存在；则将商品列表中对应的商品购买数量叠加。
                tbOrderItem.setNum(tbOrderItem.getNum()+num);
                tbOrderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*tbOrderItem.getNum()));//商品总和重新计算
                // 叠加之后购买数量为0则需要将该商品从商品列表中移除，
                if (tbOrderItem.getNum()<1){
                    cart.getOrderItemList().remove(tbOrderItem);
                }
                // 如果移除之后商品列表为空则需要将该商家（cart）从购物车列表中移除
                if (cart.getOrderItemList().size()==0){
                    cookieCartList.remove(cart);
                }
            }
        }
        return cookieCartList;
    }

    /**
     * 根据用户id查询redis中的购物车列表
     *
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListByUsername(String username) {
        //从redis中获取数据
        List<Cart> cartList= (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(username);
        if (cartList!=null){
            return cartList;
        }
        return new ArrayList<>();
    }

    /**
     * 将用户对应的购物车列表保存到redis
     *
     * @param newCartList
     * @param username
     */
    @Override
    public void saveCartListByUsername(List<Cart> newCartList, String username) {
        redisTemplate.boundHashOps(REDIS_CART_LIST).put(username,newCartList);
    }

    /**
     * 将reids和cookie中的购物车列表合并
     *
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList) {
        for (Cart cart : cookieCartList) {
            //获取cookie中每一个商家的商品列表
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            //遍历cookie的商品列表,将其添加到redis中
            for (TbOrderItem orderItem : orderItemList) {
                addItemToCartList(redisCartList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisCartList;
    }

    /**
     * 查找商家有无该商品
     * @param orderItemList 商家的商品列表
     * @param itemId 该商品的id
     * @return
     */
    private TbOrderItem findOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        if (orderItemList!=null&&orderItemList.size()>0){
            for (TbOrderItem tbOrderItem : orderItemList) {
                //如果商家的商品id和传过来的相同,说明有该商品
                if (itemId.equals(tbOrderItem.getItemId())){
                    return tbOrderItem;
                }
            }
        }
        return null;
    }

    /**
     *创建订单商品
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem tbOrderItem=new TbOrderItem();
        tbOrderItem.setNum(num);
        tbOrderItem.setPicPath(item.getImage());
        tbOrderItem.setTitle(item.getTitle());
        tbOrderItem.setPrice(item.getPrice());
        tbOrderItem.setGoodsId(item.getGoodsId());
        tbOrderItem.setItemId(item.getId());
        tbOrderItem.setSellerId(item.getSellerId());
        //总价
        tbOrderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));

        return tbOrderItem;
    }

    //判断购物车列表有无该商家
    private Cart findCartBySellerId(List<Cart> cookieCartList, String sellerId) {
        if (cookieCartList!=null&&cookieCartList.size()>0){
            for (Cart cart : cookieCartList) {
                if (sellerId.equals(cart.getSellerId())){
                    return cart;
                }
            }
        }
        return null;
    }
}
