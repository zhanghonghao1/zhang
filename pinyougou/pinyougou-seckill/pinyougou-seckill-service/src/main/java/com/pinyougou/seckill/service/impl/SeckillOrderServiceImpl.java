package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.common.util.RedisLock;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service(interfaceClass = SeckillOrderService.class)
public class SeckillOrderServiceImpl extends BaseServiceImpl<TbSeckillOrder> implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;
    //秒杀商品在redis中的key名称
    private static final String SECKILL_GOODS="SECKILL_GOODS";
    //秒杀订单在redis中的key名称
    private static final String SECKILL_ORDERS="SECKILL_ORDERS";

    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seckillOrder.get***())){
            criteria.andLike("***", "%" + seckillOrder.get***() + "%");
        }*/

        List<TbSeckillOrder> list = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 生成秒杀订单并返回id
     *
     * @param username
     * @param seckillId
     * @return
     */
    @Override
    public Long submitOrder(String username, Long seckillId) throws Exception{
        //1.加分布式锁，锁定要秒杀的商品
        RedisLock redisLock=new RedisLock(redisTemplate);
        if (redisLock.lock(seckillId.toString())) {
            //2. 获取秒杀商品；判断秒杀商品是否存在和库存大于0；
            TbSeckillGoods tbSeckillGoods= (TbSeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS).get(seckillId);
            if (tbSeckillGoods==null){
                throw new RuntimeException("秒杀商品不存在");
            }
            if (tbSeckillGoods.getStockCount()==0){
                throw new RuntimeException("商品已抢完");
            }
            //3. 将商品的库存减1
            tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()-1);
            //2.1、库存为0的话那么需要将该秒杀商品更新回到mysql，并从redis中删除
            if (tbSeckillGoods.getStockCount()==0){
                seckillGoodsMapper.updateByPrimaryKeySelective(tbSeckillGoods);//更新回mysql
                redisTemplate.boundHashOps(SECKILL_GOODS).delete(seckillId);//删除redis中该商品
            }else {
                //2.2、库存不为0的话那么直接更新redis中的秒杀商品
                redisTemplate.boundHashOps(SECKILL_GOODS).put(seckillId,tbSeckillGoods);
            }
            //4. 释放锁
            redisLock.unlock(seckillId.toString());
            //5. 创建一个秒杀订单；
            TbSeckillOrder tbSeckillOrder=new TbSeckillOrder();
            tbSeckillOrder.setId(idWorker.nextId());
            tbSeckillOrder.setSeckillId(seckillId);
            tbSeckillOrder.setStatus("0");//未支付
            tbSeckillOrder.setUserId(username);
            tbSeckillOrder.setCreateTime(new Date());
            tbSeckillOrder.setSellerId(tbSeckillGoods.getSellerId());
            tbSeckillOrder.setMoney(tbSeckillGoods.getCostPrice());
            //6. 存入秒杀订单到redis中；
            redisTemplate.boundHashOps(SECKILL_ORDERS).put(tbSeckillOrder.getId().toString(),tbSeckillOrder);
            //7. 返回订单号
            return tbSeckillOrder.getId();
        }
        return null;
    }

    /**
     * 根据订单 id 查询放置在 redis 中的订单
     *
     * @param outTradeNo
     * @return
     */
    @Override
    public TbSeckillOrder getSeckillOrderInRedisByOrderId(String outTradeNo) {
        return (TbSeckillOrder) redisTemplate.boundHashOps(SECKILL_ORDERS).get(outTradeNo);
    }

    /**
     * 更新redis中的支付状态
     *
     * @param outTradeNo
     * @param transactionId
     */
    @Override
    public void updateOrderInRedisStatus(String outTradeNo, String transactionId) {
        //找到该订单
        TbSeckillOrder order = getSeckillOrderInRedisByOrderId(outTradeNo);
        if (order==null){
            throw new RuntimeException("订单不存在");
        }
        if (!outTradeNo.equals(order.getId().toString())){
            throw new RuntimeException("订单不相符");
        }
        //更新订单信息
        order.setPayTime(new Date());
        order.setTransactionId(transactionId);
        order.setStatus("1");// 已支付
        //保存到数据库中
        seckillOrderMapper.updateByPrimaryKey(order);
        //删除redis中的订单
        redisTemplate.boundHashOps(SECKILL_ORDERS).delete(outTradeNo);
    }

    /**
     * 删除redis中的订单
     *
     * @param outTradeNo
     */
    @Override
    public void deleteOrderInRedisByIds(String outTradeNo){
        TbSeckillOrder seckillOrder = getSeckillOrderInRedisByOrderId(outTradeNo);
        if (seckillOrder != null && outTradeNo.equals(seckillOrder.getId().toString())) {
            //1 、删除 redis 中对应的秒杀订单
            redisTemplate.boundHashOps(SECKILL_ORDERS).delete(outTradeNo);
            //2 、更新订单对应商品的库存
            RedisLock redisLock = new RedisLock(redisTemplate);
            try {
                if (redisLock.lock(outTradeNo.toString())) {// 加分布式锁
                    TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).get(seckillOrder.getSeckillId());
                    if (seckillGoods == null) {// ；如果在 redis 中不存在，则从数据库中找出来并加回库存后设置到 redis
                        seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
                    }
                    seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
                    redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).put(seckillGoods.getId(), seckillGoods);
                    // 释放分布式锁
                    redisLock.unlock(seckillOrder.getSeckillId().toString());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
