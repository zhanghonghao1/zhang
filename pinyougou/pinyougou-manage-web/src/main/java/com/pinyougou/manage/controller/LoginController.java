package com.pinyougou.manage.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/*获取登录人信息控制器*/
@RequestMapping("/login")
@RestController
public class LoginController {

    //从Holder对象中取出数据
    @GetMapping("/getUsername")
    public Map<String,String> getUserName(){
        Map<String,String> map=new HashMap<>();
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",username);
        return map;
    }
}
