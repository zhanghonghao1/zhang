package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /*根据内容id查询内容列表
    * 根据内容id和启用状态降序查询数据
    * 先从缓存取,缓存没有的再从mysql取*/
    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        List<TbContent> list=null;
        //从缓存查找
        
        Example example=new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        //添加id条件
        criteria.andEqualTo("categoryId",categoryId);
        //添加状态条件
        criteria.andEqualTo("status","1");
        //降序排序
        example.orderBy("sortOrder").desc();
        List<TbContent> tbContents = contentMapper.selectByExample(example);
        return tbContents;
    }
}
