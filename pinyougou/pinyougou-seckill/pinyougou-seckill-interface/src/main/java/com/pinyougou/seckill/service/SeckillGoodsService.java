package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface SeckillGoodsService extends BaseService<TbSeckillGoods> {

    PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods);

    /**
     * 查询秒杀的商品列表
     * @return
     */
    List<TbSeckillGoods> findList();

    /**
     * 从redis获取指定商品
     * @param id
     * @return
     */
    TbSeckillGoods findOneFromRedis(Long id);
}