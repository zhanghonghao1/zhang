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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    //广告内容在redis中的名称
    private static final String REDIS_CONTENT="content";

    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增广告之后更新缓存
     *
     * @param tbContent
     */
    @Override
    public void add(TbContent tbContent) {
        super.add(tbContent);
        //同步更新内容分类对应在redis中的内容缓存
        updateContentListInRedisByCateGoryId(tbContent.getCategoryId());
    }
    //更新redis内容的方法
    private void updateContentListInRedisByCateGoryId(Long categoryId) {
        try {
            redisTemplate.boundHashOps(REDIS_CONTENT).delete(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据主键更新之后更新缓存
     *
     * @param tbContent
     */
    @Override
    public void update(TbContent tbContent) {
        //查询旧数据
        TbContent oldContent = findOne(tbContent.getId());
        super.update(tbContent);
        //当category_id不相同,删除旧缓存
        if (!tbContent.getCategoryId().equals(oldContent.getCategoryId())){
            updateContentListInRedisByCateGoryId(oldContent.getCategoryId());
        }
        //删除新缓存
        updateContentListInRedisByCateGoryId(tbContent.getCategoryId());
    }

    /**
     * 批量删除之后更新缓存
     *
     * @param ids
     */
    @Override
    public void deleteByIds(Serializable[] ids) {
        Example example=new Example(TbContent.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        //查询所有的内容
        List<TbContent> tbContentList = contentMapper.selectByExample(example);
        if (tbContentList!=null&& tbContentList.size()>0){
            for (TbContent tbContent : tbContentList) {
                //删除缓存
                updateContentListInRedisByCateGoryId(tbContent.getCategoryId());
            }
        }
        super.deleteByIds(ids);
    }

    /**
     * 条件查询的方法
     * @param page
     * @param rows
     * @param content
     * @return
     */
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
        try {
            //redia查到数据直接返回
            list= (List<TbContent>) redisTemplate.boundHashOps(REDIS_CONTENT).get(categoryId);
            if (list!=null){
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //redis查不到数据从mysql查,并保存到redis
        Example example=new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        //添加id条件
        criteria.andEqualTo("categoryId",categoryId);
        //添加状态条件
        criteria.andEqualTo("status","1");
        //降序排序
        example.orderBy("sortOrder").desc();
        list = contentMapper.selectByExample(example);

        try {
            //将从数据库中查到的保存到redis
            redisTemplate.boundHashOps(REDIS_CONTENT).put(categoryId,list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
