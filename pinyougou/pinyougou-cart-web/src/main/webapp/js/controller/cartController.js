app.controller("cartController",function ($scope, cartService) {
    /**
     * 获取登录用户名
     */
    $scope.getUsername=function () {
        cartService.getUsername().success(function (response) {
            $scope.username=response.username;
        });
    };

    /**
     * 获取购物车列表
     */
    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;
            //计算购买总数和总价格
            $scope.totalValue=cartService.sumTotalvalue(response);
        });
    };

    /**
     * 增减购物车商品
     */
    $scope.addItemToCartList=function (itemId,num) {
        cartService.addItemToCartList(itemId,num).success(function (response) {
            //如果后台返回的是添加成功true就刷新列表
            if (response.success){
                $scope.findCartList()
            }else {
                alert(response.message)
            }
        });
    };
});