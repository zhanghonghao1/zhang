package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.order.service.SeckillOrderService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.ChangeLong;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;


import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = SeckillOrderService.class)
public class SeckillOrderServiceImpl extends BaseServiceImpl<TbSeckillOrder> implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;


    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillOrder tbSeckillOrder) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(order.get***())){
            criteria.andLike("***", "%" + order.get***() + "%");
        }*/
        List<TbSeckillOrder> list = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 查询全部订单并处理id过长
     * @return
     */
    @Override
    public List<ChangeLong> findAllSeckillOrder() {
        try {

            //根据条件查询
            List<TbSeckillOrder> tbSeckillOrderList = seckillOrderMapper.selectAll();
            List<ChangeLong> changeLongs=new ArrayList<>();
            for (TbSeckillOrder seckillOrder : tbSeckillOrderList) {
                ChangeLong changeLong=new ChangeLong();
                String id = seckillOrder.getId().toString();
                changeLong.setId(id);
                changeLong.setTbSeckillOrder(seckillOrder);
                changeLongs.add(changeLong);

            }
            return changeLongs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 查询全部订单并处理id过长(商家)
     *
     * @param tbSeckillOrder
     * @return
     */
    @Override
    public List<ChangeLong> findAllSeckillOrderByUser(TbSeckillOrder tbSeckillOrder) {
        try {
            Example example = new Example(TbSeckillOrder.class);
            //限定只能查询自家商家的订单
            example.createCriteria().andEqualTo("sellerId",tbSeckillOrder.getSellerId());
            //根据条件查询
            List<TbSeckillOrder> tbSeckillOrderList = seckillOrderMapper.selectByExample(example);
            List<ChangeLong> changeLongs=new ArrayList<>();
            for (TbSeckillOrder seckillOrder : tbSeckillOrderList) {
                ChangeLong changeLong=new ChangeLong();
                String id = seckillOrder.getId().toString();
                changeLong.setId(id);
                changeLong.setTbSeckillOrder(seckillOrder);
                changeLongs.add(changeLong);

            }
            return changeLongs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 状态查询
     *
     * @param value
     * @return
     */
    @Override
    public List<ChangeLong> findStatus(String value) {
        try {
            //根据条件查询
            Example example=new Example(TbSeckillOrder.class);
            example.createCriteria().andEqualTo("status",value);
            //查询
            List<TbSeckillOrder> tbOrderList = seckillOrderMapper.selectByExample(example);
            List<ChangeLong> changeLongs=new ArrayList<>();
            for (TbSeckillOrder tbSeckillOrder : tbOrderList) {
                ChangeLong changeLong=new ChangeLong();
                String id = tbSeckillOrder.getId().toString();
                changeLong.setId(id);
                changeLong.setTbSeckillOrder(tbSeckillOrder);
                changeLongs.add(changeLong);

            }
            return changeLongs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
