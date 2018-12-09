package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.common.base.insert.InsertSelectiveMapper;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Transactional
@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoosDescMapper goosDescMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SellerMapper sellerMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

        //不查询删除了的商品
        criteria.andNotEqualTo("isDelete","1");

        //限定商家
        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andLike("sellerId",goods.getSellerId());
        }
        //根据状态查询
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andLike("auditStatus",goods.getAuditStatus());
        }
        //根据名称查询
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 自定义逻辑删除
     * @param ids
     */
    @Override
    public void deleteGoodsByIds(Long[] ids) {
        /*for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            int i = Integer.parseInt(tbGoods.getIsDelete());
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }*/
        TbGoods tbGoods=new TbGoods();
        tbGoods.setIsDelete("1");//将字段值改为1,已删除
        Example example=new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods,example);//将id的数据进行删除操作
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
        saveItemList(goods);
    }

    //自定义根据id查询基本,描述,sku信息
    @Override
    public Goods findGoodsById(Long id) {
        Goods goods=new Goods();
        //查询基本信息
        goods.setGoods(findOne(id));
        //查询描述信息
        goods.setGoodsDesc(goosDescMapper.selectByPrimaryKey(id));
        //根据goodsId查询sku信息
        TbItem tbItem=new TbItem();
        tbItem.setGoodsId(id);
        List<TbItem> tbItemList = itemMapper.select(tbItem);
        goods.setItemList(tbItemList);
        return goods;
    }

    /*自定义更新商品三大信息*/
    @Override
    public void updateGoods(Goods goods) {
        //更新基本信息
        //重新设置为未审核
        goods.getGoods().setAuditStatus("0");
        update(goods.getGoods());
        //更新描述信息
        goosDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());
        //更新sku列表(删除原有的,添加新的)
        TbItem tbItem=new TbItem();
        tbItem.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(tbItem);

        saveItemList(goods);
    }

    /*自定义更新审核状态*/
    @Override
    public void updateStatus(Long[] ids, String status) {
        /*//遍历ids,查询商品基本信息
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKeySelective(tbGoods);
        }*/
        TbGoods tbGoods=new TbGoods();
        tbGoods.setAuditStatus(status);
        Example example=new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        //根据条件更新
        goodsMapper.updateByExampleSelective(tbGoods,example);

        //运营商审核商品,审核通过(2)将sku的状态status为1已启用
        if ("2".equals(status)){
            TbItem tbItem=new TbItem();
            tbItem.setStatus("1");
            Example itemexample=new Example(TbItem.class);
            itemexample.createCriteria().andIn("goodsId",Arrays.asList(ids));
            itemMapper.updateByExampleSelective(tbItem,itemexample);
        }
    }

    /*上架*/
    @Override
    public void onmarketable(Long[] ids) {
        TbGoods tbGoods=new TbGoods();
        tbGoods.setIsMarketable("1");//将字段值改为1,上架
        Example example=new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods,example);//将id的数据进行修改操作
    }

    /*下架*/
    @Override
    public void upmarketable(Long[] ids) {
        TbGoods tbGoods=new TbGoods();
        tbGoods.setIsMarketable("0");

        Example example=new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods,example);
    }

    /*根据spuid和查询对应的sku列表*/
    @Override
    public List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status) {
        Example example=new Example(TbItem.class);
        example.createCriteria().andEqualTo("status",status).andIn("goodsId",Arrays.asList(ids));
        return itemMapper.selectByExample(example);
    }

    /**
     * 保存动态sku列表
     * @param goods 商品信息（基本、描述、sku列表）
     */
    private void saveItemList(Goods goods) {

        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            //启用规格
            if (goods.getItemList() != null && goods.getItemList().size() > 0) {
                for (TbItem item : goods.getItemList()) {

                    //标题=spu名称+所有规格的选项值
                    String title = goods.getGoods().getGoodsName();
                    //获取规格；{"网络":"移动3G","机身内存":"16G"}
                    Map<String, String> map = JSON.parseObject(item.getSpec(), Map.class);
                    Set<Map.Entry<String, String>> entries = map.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        title += " " + entry.getValue();
                    }
                    item.setTitle(title);

                    setItemValue(item, goods);

                    //保存sku
                    itemMapper.insertSelective(item);
                }
            }
        } else {
            //未启用规格
            //1. 创建item对象；大多数据数据来自spu设置到对象中；
            TbItem tbItem = new TbItem();
            //2. 如果spu中没有的数据，如：spec（｛｝），num（9999），status(0未启用)，isDefault(1默认)
            tbItem.setSpec("{}");
            tbItem.setPrice(goods.getGoods().getPrice());
            tbItem.setStatus("0");
            tbItem.setIsDefault("1");
            tbItem.setNum(9999);
            tbItem.setTitle(goods.getGoods().getGoodsName());

            //设置商品的其它信息
            setItemValue(tbItem, goods);

            //3. 保存到数据库中
            itemMapper.insertSelective(tbItem);
        }
    }

    private void setItemValue(TbItem item, Goods goods) {
        //商品分类 来自 商品spu的第3级商品分类id
        item.setCategoryid(goods.getGoods().getCategory3Id());
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(item.getCategoryid());
        item.setCategory(itemCat.getName());

        //图片；可以从spu中的图片地址列表中获取第1张图片
        if (!StringUtils.isEmpty(goods.getGoodsDesc().getItemImages())) {
            List<Map> imageList = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
            if (imageList.get(0).get("url") != null) {
                item.setImage(imageList.get(0).get("url").toString());
            }
        }

        item.setGoodsId(goods.getGoods().getId());

        //品牌
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());

        item.setCreateTime(new Date());
        item.setUpdateTime(item.getCreateTime());

        //卖家
        item.setSellerId(goods.getGoods().getSellerId());
        TbSeller seller = sellerMapper.selectByPrimaryKey(item.getSellerId());
        item.setSeller(seller.getName());
    }
}
