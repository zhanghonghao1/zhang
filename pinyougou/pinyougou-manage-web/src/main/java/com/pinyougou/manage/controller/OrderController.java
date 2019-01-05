package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.vo.ChangeLong;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/order")
@RestController
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/findAll")
    public List<TbOrder> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return orderService.findPage(page, rows);
    }

    /**
     * 状态查询
     * @param value
     * @return
     */
    @GetMapping("/findStatus")
    public List<ChangeLong> findStatus(@RequestParam String value) {
        return orderService.findStatus(value);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbOrder order) {
        try {
            orderService.add(order);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public ChangeLong findOne(Long id) {
        TbOrder tbOrder = orderService.findOne(id);
        ChangeLong changeLong=new ChangeLong();
        changeLong.setTbOrder(tbOrder);
        changeLong.setOrderId(tbOrder.getOrderId().toString());
        return changeLong;
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbOrder tbOrder, Long id) {
        try {
            orderService.updateArea(tbOrder,id);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            orderService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param order 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbOrder order, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        //设置只能查询自家产品
        //获得登录商家名称
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setSellerId(sellerId);
        return orderService.search(page, rows, order);
    }

    @PostMapping("/findAllOrder")
    public List<ChangeLong> findAllOrder() {
        /*//设置只能查询自家产品
        //获得登录商家名称
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbOrder order=new TbOrder();
        order.setSellerId(sellerId);*/
        return orderService.findAllOrder();
    }

}
