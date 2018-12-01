package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.GoodsMapper;
import com.pinyougou.mapper.GoosDescMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoosDescMapper goosDescMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 新建的添加商品(三个属性)的方法
     * @param goods
     */
    @Override
    public void addGoods(Goods goods) {
        //新增商品基本信息(controller添加的信息)
        goodsMapper.insertSelective(goods.getGoods());
        //新增商品描述信息()
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goosDescMapper.insertSelective(goods.getGoodsDesc());
        //新增商品sku
    }

    @Override
    public void deleteGoodsByIds(Long[] ids) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            int i = Integer.parseInt(tbGoods.getIsDelete());
                tbGoods.setIsDelete("1");
                goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }
}
