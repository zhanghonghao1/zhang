app.controller("payController",function ($scope, payService,$location, cartService) {
    //获取用户名
    $scope.getUsername=function () {
        cartService.getUsername().success(function (response) {
            $scope.username=response.username;
        });
    };

    //生成支付二维码
    $scope.createNative=function () {
        //得到地址栏支付日志id
        $scope.outTradeNo=$location.search()["outTradeNo"];
        payService.createNative($scope.outTradeNo).success(function (response) {
            //创建支付地址成功
            if ("SUCCESS"==response.result_code){
                //计算总金额
                $scope.money=(response.totalFee/100).toFixed(2);
                //生成支付二维码
                var qr = new QRious({
                    element:document.getElementById("qrious"),
                    size:250,
                    level:"Q",
                    value:response.code_url
                });
                //查询支付状态
                queryPayStatus($scope.outTradeNo);
            }else {
                alert("二维码生成失败")
            }
        })
    };
    queryPayStatus = function (outTradeNo) {
        payService.queryPayStatus(outTradeNo).success(function (response) {
            if(response.success){
                //支付成功跳转到支付成功的页面
                location.href= "paysuccess.html#?money=" + $scope.money;
            } else {
                if ("支付超时" == response.message) {
                    alert(response.message);
                    //重新生成二维码
                    $scope.createNative();
                } else {
                    //支付失败
                    location.href = "payfail.html";
                }
            }
        })
    };
    //获取总金额
    $scope.getMoney=function () {
        $scope.money=$location.search()["money"];
    };
});