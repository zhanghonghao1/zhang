package com.pinyougou.user.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    //username,用户从前台传递过来的用户名
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            //将前端传入的密码与数据库的密码进行匹配,如果一致登录认证成功
            return new User(username,"", authorities);
    }
}
