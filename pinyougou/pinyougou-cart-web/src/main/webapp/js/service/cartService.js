app.service("cartService",function ($http) {

    /**
     * 获取登录用户名
     */
    this.getUsername=function () {
        return $http.get("/cart/getUsername.do?r="+Math.random())
    };

    /**
     * 获取购物车列表
     */
    this.findCartList=function () {
        return $http.get("/cart/findCartList.do?r="+Math.random())
    };

    /**
     * 增减购物车商品
     */
    this.addItemToCartList=function (itemId,num) {
        return $http.get("/cart/addItemToCartList.do?itemId="+itemId+"&num="+num);
    };

    /**
     * 购买总数和总价格
     */
    this.sumTotalvalue=function (cartList) {
        var totalValue = {"totalNum":0, "totalMoney":0.0};
        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem = cart.orderItemList[j];
                totalValue.totalNum += orderItem.num;
                totalValue.totalMoney += orderItem.totalFee;
            }
        }
        return totalValue;
    };

    /**
     * 提交订单
     */
    this.submitOrder=function (order) {
        return $http.post("order/add.do",order);
    }
});