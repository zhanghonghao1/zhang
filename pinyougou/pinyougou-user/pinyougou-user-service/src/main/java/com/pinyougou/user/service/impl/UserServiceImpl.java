package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service(interfaceClass = UserService.class)
public class UserServiceImpl extends BaseServiceImpl<TbUser> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ActiveMQQueue itcastSmsQueue;
    @Value("${signName}")
    private String signName;
    @Value("${templateCode}")
    private String templateCode;

    @Override
    public PageResult search(Integer page, Integer rows, TbUser user) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(user.get***())){
            criteria.andLike("***", "%" + user.get***() + "%");
        }*/

        List<TbUser> list = userMapper.selectByExample(example);
        PageInfo<TbUser> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 发送手机验证码
     *
     * @param phone
     */
    @Override
    public void sendSmsCode(String phone) {
        //随机生成6位数
        String smsCode=(long)(Math.random()*1000000)+"";
        System.out.println("验证码:"+smsCode);
        //存入redis并设置5分钟过期时间
        redisTemplate.boundValueOps(phone).set(smsCode);
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.MINUTES);
        //发送短信相关参数到MQ
        jmsTemplate.send(itcastSmsQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage=session.createMapMessage();
                mapMessage.setString("mobile",phone);
                mapMessage.setString("signName",signName);
                mapMessage.setString("templateCode",templateCode);
                mapMessage.setString("templateParam","{\"code\":"+smsCode+"}");
                return mapMessage;
            }
        });
    }

    /**
     * 校验验证码是否正确
     *
     * @param phone
     * @param smsCode
     * @return
     */
    @Override
    public boolean checkSmsCode(String phone, String smsCode) {
        //从redis中取出验证码
        String code = (String) redisTemplate.boundValueOps(phone).get();
        //与传过来的验证码进行对比
        if (code.equals(smsCode)){
            //验证成功删除redis中的验证码
            redisTemplate.delete(phone);
            return true;
        }
        return false;
    }
}
