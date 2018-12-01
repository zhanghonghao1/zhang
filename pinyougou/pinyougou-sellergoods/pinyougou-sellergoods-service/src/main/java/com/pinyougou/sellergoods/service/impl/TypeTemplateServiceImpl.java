package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service(interfaceClass = TypeTemplateService.class)
public class TypeTemplateServiceImpl extends BaseServiceImpl<TbTypeTemplate> implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(typeTemplate.getName())){
            criteria.andLike("name", "%" + typeTemplate.getName() + "%");
        }

        List<TbTypeTemplate> list = typeTemplateMapper.selectByExample(example);
        PageInfo<TbTypeTemplate> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 根据分类模板id查询其对应的规格及其规格的选项
     *
     * @param id
     * @return
     */
    @Override
    public List<Map> findSpecList(Long id) {
        //查询分类模板,里包含部分规格
        TbTypeTemplate tbTypeTemplate = findOne(id);
       //规格列表不为空
       if (!StringUtils.isEmpty(tbTypeTemplate.getSpecIds())){
           //根据分类模版中的规格列表的每一个规格查询其对应的规格选项列表
           //将字符串的规格转换为规格列表
           List<Map> specList = JSONArray.parseArray(tbTypeTemplate.getSpecIds(), Map.class);
           for (Map map : specList) {
               //根据规格id查询规格对应的选项
               TbSpecificationOption tbSpecificationOption=new TbSpecificationOption();
               //查询条件为规格id
               tbSpecificationOption.setSpecId(Long.parseLong(map.get("id").toString()));
               List<TbSpecificationOption> optionList = specificationOptionMapper.select(tbSpecificationOption);
               map.put("options",optionList);
           }
           return specList;
       }
        return null;
    }
}
