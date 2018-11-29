package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SpecificationService.class)
public class SpecificationServiceImpl extends BaseServiceImpl<TbSpecification> implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbSpecification specification) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(specification.getSpecName())){
            criteria.andLike("specName", "%" + specification.getSpecName() + "%");
        }

        List<TbSpecification> list = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /*新建的add方法*/
    @Override
    public void add(Specification specification) {
        //新增规格
        specificationMapper.insertSelective(specification.getSpecification());
        //新增规格选项
        if (specification.getSpecificationOptionList()!=null&&specification.getSpecificationOptionList().size()>0){
            //遍历传递过来的规格选项列表
            for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
                tbSpecificationOption.setSpecId(specification.getSpecification().getId());
                specificationOptionMapper.insertSelective(tbSpecificationOption);
            }
        }
    }

    /*新建的findOne方法*/
    @Override
    public Specification findOne(Long id) {
        //获得Sp对象
        Specification specification=new Specification();

        //查询规格并设置
        specification.setSpecification(specificationMapper.selectByPrimaryKey(id));
        //查询规格选项列表并设置
        /*获得Tb对象并将spid传入*/
        TbSpecificationOption tbSpecificationOption=new TbSpecificationOption();
        tbSpecificationOption.setSpecId(id);
        //查询tb列表
        List<TbSpecificationOption> specificationOptionList=specificationOptionMapper.select(tbSpecificationOption);
        specification.setSpecificationOptionList(specificationOptionList);
        return specification;
    }

    /*新建的update方法*/
    @Override
    public void update(Specification specification) {
        //将规格更改
        specificationMapper.updateByPrimaryKey(specification.getSpecification());
        //根据规格id删除所有规格选项
        TbSpecificationOption tbSpecificationOption=new TbSpecificationOption();
        tbSpecificationOption.setSpecId(specification.getSpecification().getId());//将规格id传给规格选项
        specificationOptionMapper.delete(tbSpecificationOption);//删除规格选项
        //将新的规格列表添加
        if (specification.getSpecificationOptionList()!=null&&specification.getSpecificationOptionList().size()>0){
            for (TbSpecificationOption tb : specification.getSpecificationOptionList()) {
                tb.setSpecId(specification.getSpecification().getId());
                specificationOptionMapper.insertSelective(tb);
            }
        }
    }

    /*新增的删除的方法, 删除规格和相关的所有选项*/
    @Override
    public void deleteSpecificationByIds(Long[] ids) {
        //删除规格
        deleteByIds(ids);
        //删除规格选项
        /*for (Long id : ids) {
            TbSpecificationOption tbSpecificationOption=new TbSpecificationOption();
            tbSpecificationOption.setSpecId(id);
            specificationOptionMapper.delete(tbSpecificationOption);
        }*/
        Example example=new Example(TbSpecificationOption.class);
        example.createCriteria().andIn("specId", Arrays.asList(ids));
        specificationOptionMapper.deleteByExample(example);
    }

    //自定义规格下拉列表
    @Override
    public List<Map<String, Object>> selectOptionList() {
        return specificationMapper.selectOptionList();
    }
}
