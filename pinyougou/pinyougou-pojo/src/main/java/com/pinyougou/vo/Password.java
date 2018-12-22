package com.pinyougou.vo;

public class Password {
    //原密码
    private String old;
    //新密码
    private String newOne;
    //确认密码吗
    private String newTwo;

    public String getOld() {
        return old;
    }

    public void setOld(String old) {
        this.old = old;
    }

    public String getNewOne() {
        return newOne;
    }

    public void setNewOne(String newOne) {
        this.newOne = newOne;
    }

    public String getNewTwo() {
        return newTwo;
    }

    public void setNewTwo(String newTwo) {
        this.newTwo = newTwo;
    }
}
