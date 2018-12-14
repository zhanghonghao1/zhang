package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface UserService extends BaseService<TbUser> {

    PageResult search(Integer page, Integer rows, TbUser user);

    /**
     * 发送手机验证码
     * @param phone
     */
    void sendSmsCode(String phone);

    /**
     * 校验验证码是否正确
     * @param phone
     * @param smsCode
     * @return
     */
    boolean checkSmsCode(String phone, String smsCode);
}