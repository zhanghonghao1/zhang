app.controller("baseController",function ($scope) {
//初始化分页参数
    $scope.paginationConf = {
        currentPage: 1,// 当前页号
        totalItems: 10,// 总记录数
        itemsPerPage: 10,//页大小
        perPageOptions: [10, 20, 30, 40, 50],// 可选择的每页大小
        onChange: function () {// 当上述的参数发生变化了后触发
            $scope.reloadList();
        }
    };

// 加载表格数据
    $scope.reloadList = function () {
        //$scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

//定义是否选择的id的数据
    $scope.selectedIds = [];
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectedIds.push(id);
        } else {
            //得到当前选中的索引号
            var index = $scope.selectedIds.indexOf(id);
            $scope.selectedIds.splice(index, 1)
        }
    };

    //讲一个json列表字符串某个属性的值串起来 {"id":1,"text":"联想"}得到text的值 联想
    $scope.jsonToString=function (jsonArrayStr,key) {
        var str="";
        //将json字符串转换为json
        var jsonArray=JSON.parse(jsonArrayStr);//集合
        for (var i = 0; i < jsonArray.length; i++) {
            var jsonObj = jsonArray[i];
            if(str.length>0){
                str+=","+jsonObj[key];
            }else {
                str=jsonObj[key];
            }
        }
        return str;
    }
});