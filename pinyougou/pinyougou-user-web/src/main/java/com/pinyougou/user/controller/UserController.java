package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.PhoneFormatCheckUtils;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

@RequestMapping("/user")
@RestController
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return userService.findPage(page, rows);
    }

    //注册保存用户信息
    @PostMapping("/add")
    public Result add(@RequestBody TbUser user,String smsCode) {
        Result result=Result.fail("注册失败");
        try {
            //验证手机号
            if (PhoneFormatCheckUtils.isPhoneLegal(user.getPhone())){
                //校验手机号
                if(userService.checkSmsCode(user.getPhone(),smsCode)){
                    //给用户信息添加创建时间和修改时间
                    user.setCreated(new Date());
                    user.setUpdated(user.getCreated());
                    //将用户密码加密
                    user.setPassword(DigestUtils.md5Hex(user.getPassword()));
                   /* BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
                    user.setPassword(passwordEncoder.encode(user.getPassword()));*/
                    userService.add(user);
                    result= Result.ok("注册成功");
                }else {
                    result=Result.fail("验证码错误");
                }

            }else {
                result=Result.fail("手机号格式错误");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @GetMapping("/sendSmsCode")
    public Result sendSmsCode(String phone){
        Result result=Result.fail("发送失败");
        try {
            //验证手机号
            if (PhoneFormatCheckUtils.isPhoneLegal(phone)){
                //发送验证码
                userService.sendSmsCode(phone);
                result= Result.ok("发送成功");
            }else {
                result=Result.fail("手机号格式错误");
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping("/findOne")
    public TbUser findOne(Long id) {
        return userService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbUser user) {
        try {
            userService.update(user);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            userService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param user 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbUser user, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return userService.search(page, rows, user);
    }

    @GetMapping("/getUsername")
    public Map<String,Object> getUsername(){
        Map<String,Object> map=new HashMap<>();
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",username);
        return map;

    }

}
