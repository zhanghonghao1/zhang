package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service(interfaceClass = SeckillGoodsService.class)
public class SeckillGoodsServiceImpl extends BaseServiceImpl<TbSeckillGoods> implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    //秒杀商品在redis中对应的key名称
    public static final String SECKILL_GOODS="SECKILL_GOODS";

    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seckillGoods.get***())){
            criteria.andLike("***", "%" + seckillGoods.get***() + "%");
        }*/

        List<TbSeckillGoods> list = seckillGoodsMapper.selectByExample(example);
        PageInfo<TbSeckillGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 查询秒杀的商品列表
     *库存>0;通过审核; 时间符合
     * @return
     */
    @Override
    public List<TbSeckillGoods> findList() {
        List<TbSeckillGoods> seckillGoodsList=null;

        //从redis中查询秒杀商品列表
        seckillGoodsList = redisTemplate.boundHashOps(SECKILL_GOODS).values();

        if (seckillGoodsList==null||seckillGoodsList.size()==0){
            Example example=new Example(TbSeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andGreaterThan("stockCount",0);
            criteria.andEqualTo("status","1");
            criteria.andGreaterThan("endTime",new Date());//大于结束时间
            criteria.andLessThanOrEqualTo("startTime",new Date());//小于等于开始时间
            example.orderBy("startTime");//根据开始时间升序排序
            //从mysql查询
            seckillGoodsList = seckillGoodsMapper.selectByExample(example);

            //将查询到的商品保存到redis中
            if (seckillGoodsList!=null&&seckillGoodsList.size()>0){
                for (TbSeckillGoods seckillGoods : seckillGoodsList) {
                    redisTemplate.boundHashOps(SECKILL_GOODS).put(seckillGoods.getId(),seckillGoods);
                }
            }
        }
        return seckillGoodsList;
    }

    /**
     * 从redis获取指定商品
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillGoods findOneFromRedis(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS).get(id);
    }
}
