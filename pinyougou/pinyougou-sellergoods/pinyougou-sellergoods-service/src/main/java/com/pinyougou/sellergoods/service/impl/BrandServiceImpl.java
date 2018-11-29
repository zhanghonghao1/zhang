package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Date2018/11/23
 */
@Service(interfaceClass =BrandService.class)
public class BrandServiceImpl extends BaseServiceImpl<TbBrand> implements BrandService  {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbBrand brand) {
        //设置分页
        PageHelper.startPage(page,rows);
        //查询需要的条件,从这张表查询,相当于from
        Example example=new Example(TbBrand.class);
        //创建查询条件对象,相当于where
        Example.Criteria criteria=example.createCriteria();
        //名字模糊查询
        if(!StringUtils.isEmpty(brand.getName())) {//判断字符串是否为空
            criteria.andLike("name", "%"+brand.getName()+"%");
        }
        //首字母查询
        if (!StringUtils.isEmpty(brand.getFirstChar())){
            criteria.andEqualTo("firstChar",brand.getFirstChar());
        }
        //执行查询
        List<TbBrand> list = brandMapper.selectByExample(example);
        //封装结果
        PageInfo pageInfo=new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /*自定义获得品牌下拉列表*/
    @Override
    public List<Map<String, Object>> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
