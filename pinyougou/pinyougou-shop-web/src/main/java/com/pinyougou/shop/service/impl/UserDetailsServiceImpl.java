package com.pinyougou.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {
    //定义service,远程注入
    @Reference
    private SellerService sellerService;

    //username,用户从前台传递过来的用户名
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据登录名到数据库中查询用户
        TbSeller seller = sellerService.findOne(username);
        //该用户存在并且已经审核通过
        if (seller != null && "1".equals(seller.getStatus())) {
            //构造用户的角色列表
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
            //将前端传入的密码与数据库的密码进行匹配,如果一致登录认证成功
            return new User(username, seller.getPassword(), authorities);
        }
        return null;
    }
}
