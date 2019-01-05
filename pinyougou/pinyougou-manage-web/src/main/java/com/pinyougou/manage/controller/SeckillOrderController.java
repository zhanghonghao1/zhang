package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.SeckillOrderService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.vo.ChangeLong;
import com.pinyougou.vo.PageResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/seckillorder")
@RestController
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;


    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return seckillOrderService.findPage(page, rows);
    }


    @GetMapping("/findOne")
    public ChangeLong findOne(Long id) {
        TbSeckillOrder tbSeckillOrder = seckillOrderService.findOne(id);
        ChangeLong changeLong=new ChangeLong();
        changeLong.setTbSeckillOrder(tbSeckillOrder);
        changeLong.setId(tbSeckillOrder.getId().toString());
        return changeLong;
    }

    /**
     * 状态查询
     * @param value
     * @return
     */
    @GetMapping("/findStatus")
    public List<ChangeLong> findStatus(@RequestParam String value) {
        return seckillOrderService.findStatus(value);
    }

    /*@PostMapping("/update")
    public Result update(@RequestBody TbSeckillOrder tbSeckillOrder, Long id) {
        try {
            seckillOrderService.updateArea(tbSeckillOrder,id);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }*/


    /**
     * 分页查询列表
     * @param tbSeckillOrder 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbSeckillOrder tbSeckillOrder, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        //设置只能查询自家产品
        //获得登录商家名称
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        tbSeckillOrder.setSellerId(sellerId);
        return seckillOrderService.search(page, rows, tbSeckillOrder);
    }

    @PostMapping("/findAllSeckillOrder")
    public List<ChangeLong> findAllSeckillOrder() {
       /* //设置只能查询自家产品
        //获得登录商家名称
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder tbSeckillOrder=new TbSeckillOrder();
        tbSeckillOrder.setSellerId(sellerId);*/
        return seckillOrderService.findAllSeckillOrder();
    }

}
