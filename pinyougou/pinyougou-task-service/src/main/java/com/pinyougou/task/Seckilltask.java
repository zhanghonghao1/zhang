package com.pinyougou.task;

import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class Seckilltask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    private static final String SECKILL_GOODS="SECKILL_GOODS";

    /**
     * 新增秒杀商品到redis
     */
    @Scheduled(cron = "0/3 * * * * ?")
    public void refreshSeckillGoods(){
        //2.在任务调度方法里面查询出redis中所有符合条件的秒杀商品的id集合(key)
        List ids = new ArrayList( redisTemplate.boundHashOps(SECKILL_GOODS).keys() );
        Example example=new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1");
        criteria.andGreaterThan("stockCount",0);
        criteria.andLessThanOrEqualTo("startTime",new Date());
        criteria.andGreaterThan("endTime",new Date());
        //如果redis中有秒杀商品,就在mysql中查询除了这些商品的其他商品
        if (ids.size()>0){
            criteria.andNotIn("id",ids);
        }
        //3.在mysql数据库中查询除了redis已经存在的秒杀商品;
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        if (seckillGoodsList!=null&&seckillGoodsList.size()>0){
            for (TbSeckillGoods tbSeckillGoods : seckillGoodsList) {
                //4.将这些商品添加到Redis中
                redisTemplate.boundHashOps(SECKILL_GOODS).put(tbSeckillGoods.getId(),tbSeckillGoods);
            }
            System.out.println(" 已将"+seckillGoodsList.size()+" 条秒杀商品装入到缓存中。");
        }
    }

    /**
     * 移除已经过期的商品
     */
    @Scheduled(cron = "0/3 * * * * ?")
    public void removeSeckillGoods(){
        //2.获取到redis中每一个秒杀的商品判断商品结束时间是否小于等于当前时间
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps(SECKILL_GOODS).values();
        if (seckillGoods!=null&&seckillGoods.size()>0) {
            for (TbSeckillGoods seckillGood : seckillGoods) {
                if (seckillGood.getEndTime().getTime()>=new Date().getTime()){
                    //3.将符合上述条件(过期)商品从redis中删除并更新到redis中
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGood);
                    redisTemplate.boundHashOps(SECKILL_GOODS).delete(seckillGood.getId());//从redis中移除

                    System.out.println(" 移除秒杀商品：" + seckillGood.getId());
                }
            }
        }
    }
}
