/*自定义controller*/
app.controller("seckillorderController",function ($scope,$filter,$controller,seckillorderService) {

    //继承brandController；参数1：要继承的处理器名称，参数2：传递本处理器的信息到父处理器
    $controller("baseController",{$scope:$scope});

    // 分页查询全部列表
    $scope.findPage = function (page, rows) {
        seckillorderService.findPage(page,rows).success(function (response) {
            // 更新记录列表
            $scope.list = response.rows;
            // 更新总记录数
            $scope.paginationConf.totalItems = response.total;
        });
    };

    // 保存
    $scope.update = function (id) {
        seckillorderService.update($scope.enery.tbOrder,id).success(function (response) {
            //刷新显示列表(调用上面的加载方法)
            if (response.success) {
                alert(response.message);
                // 重新加载列表
                $scope.findAllOrder();
            } else {
                alert(response.message);
            }
        })
    };

    //根据品牌id查询品牌数据
    $scope.findOne=function (id) {
        seckillorderService.findOne(id).success(function (response) {
            $scope.enery=response;
        });
    };

    //状态查询
    $scope.findStatus=function (value) {
        seckillorderService.findStatus(value).success(function (response) {
            $scope.list = response;
        });
    };

    //分页条件查询
    //定义一个没有查询条件的对象,
    $scope.searchEntity={};
    $scope.search=function (page,rows) {
        seckillorderService.search(page,rows,$scope.searchEntity).success(function (response) {
            $scope.list=response.rows;
            $scope.paginationConf.totalItems=response.total;
        })
    };

    //显示全部数据
    $scope.findAllSeckillOrder=function () {
        seckillorderService.findAllSeckillOrder().success(function (response) {
            $scope.list = response;
        });
    };

    //支付状态
    $scope.orderStatus = ["","未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"];
    //支付类型
    $scope.paymentType=["在线支付","货到付款"];
    //评价状态
    $scope.orderRate = ["未评价","已评价"];
    //发票类型
    $scope.invoiceType=["普通发票","电子发票","增值税发票"];
    //订单来源
    $scope.sourceType=["app端","pc端","M端","微信端","手机qq端"];

});