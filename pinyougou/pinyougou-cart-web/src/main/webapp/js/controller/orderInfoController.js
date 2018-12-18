app.controller("orderInfoController",function ($scope,cartService,addressService) {
    
    //获取用户名称
    $scope.getUsername=function () {
        cartService.getUsername().success(function (response) {
            $scope.username=response.username;
        });
    };

    //获取用户地址列表
    $scope.findAddressList=function () {
        addressService.findAddressList().success(function (response) {
            $scope.addressList=response;
            //默认选择默认地址
            for (var i=0;i<$scope.addressList.length;i++) {
                var address=$scope.addressList[i];
                if (address.isDefault=="1"){
                    $scope.address=address;
                    break;
                }
            }
        });
    };

    //判断地址是否选中
    $scope.isAddressSelected=function (address) {
        if ($scope.address==address){
            return true;
        }
        return false;
    };

    //选中地址
    $scope.selectAddress=function (address) {
        $scope.address=address;
    };

    //初始化支付方式, 1为微信支付(默认), 2为货到付款
    $scope.order={"paymentType":"1"};
    //选择支付方式
    $scope.selectPaymentType=function (type) {
        $scope.order.paymentType=type;
    };

    //加载商品列表,用于显示金额
    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;
            //计算购买总数和总价格
            $scope.totalValue=cartService.sumTotalvalue(response);
        });
    };
    
    //提交订单
    $scope.submitOrder=function () {
        //设置收件人信息
        $scope.order.receiver=$scope.address.contact;
        $scope.order.receiverAreaName=$scope.address.address;
        $scope.order.receiverMobile=$scope.address.moblie;
        cartService.submitOrder($scope.order).success(function (response) {
            if (response.success){
                //如果为微信支付的话跳转到支付页面,并携带订单日志id
               if ($scope.order.paymentType=="1"){
                   location.href="pay.html#?outTradeNo="+response.message;
               }else {
                   // 如果是货到付款直接提示支付成功;
                   location.href="paysuccess.html";
               }
            }else {
                alert(response.message);
            }
        });
    };
});