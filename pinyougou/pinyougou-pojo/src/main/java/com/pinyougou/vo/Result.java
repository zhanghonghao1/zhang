package com.pinyougou.vo;

/*执行结果实体类*/
public class Result {
    private Boolean success;//执行是否成功
    private String message;//返回的语句

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static Result ok(String message){
        return new Result(true,message);
    }
    public static Result fail(String message){
        return new Result(false,message);
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
