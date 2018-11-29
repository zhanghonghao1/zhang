/*自定义controller*/
app.controller("brandController",function ($scope,$controller,brandService) {

    //继承brandController；参数1：要继承的处理器名称，参数2：传递本处理器的信息到父处理器
    $controller("baseController",{$scope:$scope});

    // 分页查询全部列表
    $scope.findPage = function (page, rows) {
        brandService.findPage(page,rows).success(function (response) {
            // 更新记录列表
            $scope.list = response.rows;
            // 更新总记录数
            $scope.paginationConf.totalItems = response.total;
        });
    };

    // 保存
    $scope.save=function () {
        //定义后端执行方法
        var obj;
        if ($scope.entity.id!=null){
            obj=brandService.update($scope.entity);
        }else {
            obj=brandService.add($scope.entity);
        }
        obj.success(function (response) {
            //刷新显示列表(调用上面的加载方法)
            if (response.success) {
                // 重新加载列表
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        })
    };

    //根据品牌id查询品牌数据
    $scope.findOne=function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity=response;
        });
    };

    //根据id删除数据
    //执行删除
    $scope.delete=function () {
        //判断有误数据
        if ($scope.selectedIds.length<1){
            alert("请选择要删除的数据");
            return;
        }
        //确认
        if (confirm("确认要删除吗")){
            brandService.delete($scope.selectedIds).success(function (response) {
                //如果返回true
                if (response.success){
                    $scope.reloadList();
                    $scope.selectedIds=[];
                }else {
                    alert(response.message);
                }
            })
        }
    };

    //分页条件查询
    //定义一个没有查询条件的对象,
    $scope.searchEntity={};
    $scope.search=function (page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(function (response) {
            $scope.list=response.rows;
            $scope.paginationConf.totalItems=response.total;
        })
    };
});