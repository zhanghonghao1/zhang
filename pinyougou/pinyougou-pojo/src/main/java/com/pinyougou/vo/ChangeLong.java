package com.pinyougou.vo;


import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbSeckillOrder;

import java.io.Serializable;

public class ChangeLong implements Serializable{
    private String orderId;
    private TbOrder tbOrder;
    private String id;
    private TbSeckillOrder tbSeckillOrder;
    private PageResult pageResult;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TbSeckillOrder getTbSeckillOrder() {
        return tbSeckillOrder;
    }

    public void setTbSeckillOrder(TbSeckillOrder tbSeckillOrder) {
        this.tbSeckillOrder = tbSeckillOrder;
    }

    public TbOrder getTbOrder() {
        return tbOrder;
    }

    public void setTbOrder(TbOrder tbOrder) {
        this.tbOrder = tbOrder;
    }

    public PageResult getPageResult() {
        return pageResult;
    }

    public void setPageResult(PageResult pageResult) {
        this.pageResult = pageResult;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
