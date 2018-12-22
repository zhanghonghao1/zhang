package com.pinyougou.seckill.service.impl;

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
        // 用户认证的工作已经交由 cas 认证，当前类只作查询用户权限
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new User(username,"", authorities);
    }
}
