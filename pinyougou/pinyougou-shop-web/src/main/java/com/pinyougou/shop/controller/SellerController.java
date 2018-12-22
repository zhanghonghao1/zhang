package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Password;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/seller")
@RestController
public class SellerController {

    @Reference
    private SellerService sellerService;

    @RequestMapping("/findAll")
    public List<TbSeller> findAll() {
        return sellerService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return sellerService.findPage(page, rows);
    }

    /**
     * 保存商家信息
     * @param seller
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbSeller seller) {
        try {
            seller.setStatus("0");//初始化审核状态为0未审核
            //将密码加密
            BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
            seller.setPassword(passwordEncoder.encode(seller.getPassword()));
            sellerService.add(seller);
            return Result.ok("商家注册成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("商家注册失败");
    }

    @GetMapping("/findOne")
    public TbSeller findOne(String id) {
        return sellerService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbSeller seller) {
        try {
            sellerService.update(seller);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(String[] ids) {
        try {
            sellerService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param seller 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbSeller seller, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return sellerService.search(page, rows, seller);
    }

    /**
     * 修改商家密码
     * @param entity
     * @return
     */
    @PostMapping("/changePassword")
    public Result changePassword(@RequestBody Password entity){
        Result result=Result.fail("密码修改失败");
        //得到当前登录用户名
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        //根据用户名得到数据库中对应密码
        TbSeller tbSeller = sellerService.findOneByUsername(username);//当前用户
        String password = tbSeller.getPassword();//当前用户的密码
        //调用加密对象
        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        //将原密码与数据库密码对比,相同就将新密码保存到当前用户
        if (encoder.matches(entity.getOld(),password)){
            //将新密码加密保存到数据库中
            tbSeller.setPassword(encoder.encode(entity.getNewOne()));
            sellerService.update(tbSeller);
            result=Result.ok("密码修改成功");
        }
        return result;
    }
}
