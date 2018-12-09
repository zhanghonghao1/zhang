app.controller("searchController",function ($scope,$location,searchService) {

    //根据关键字搜索商品
    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap=response;
            //构建分页导航条
            buildPageInfo();
        });
    };

    //定义提交到后台的对象
    $scope.searchMap={"keywords":"","category":"","brand":"","spec":{},"price":"","pageNo":1,"pageSize":10,"sortField":"","sort":""};

    //点击搜索条件的事件
    $scope.addSearchItem=function (key,value) {
        // 如果点击的是品牌或者分类的话
        if ("category"==key||"brand"==key||"price"==key){
            $scope.searchMap[key]=value;
        }else {
            // 如果点击的是规格的话
            $scope.searchMap.spec[key]=value;
        }
        $scope.searchMap.pageNo=1;
        //选择搜索条件之后调用搜索方法重新搜索
        $scope.search();
    };

    //撤销搜索条件的事件
    $scope.removeSearchItem=function (key) {
        // 如果点击的是品牌或者分类的话
        if ("category"==key||"brand"==key||"price"==key){
            $scope.searchMap[key]='';
        }else {
            // 如果点击的是规格的话,删除集合里面key和value,不能只使value=''
            delete $scope.searchMap.spec[key];
        }
        $scope.searchMap.pageNo=1;
        //撤销搜索条件之后调用搜索方法重新搜索
        $scope.search();
    };

    /*构建分页导航条的方法*/
    buildPageInfo=function () {
        //在页面要显示的页号数组
        $scope.pageNoList=[];
        //起始页号
        var startPageNo=1;
        //结束页号
        var endPageNo=$scope.resultMap.totalPages;
        //要显示几个页号
        var showPageNoTotal=5;

        //如果总页数大于要显示的页数,就按要求显示
        if ($scope.resultMap.totalPages>showPageNoTotal){
            //设置当前页的间隔数(要显示的页号/2)floor向下取整5/2=2
            var interval=Math.floor(showPageNoTotal/2);

            //计算起始页号(当前页-间隔数),将字符串转为数字类型
            startPageNo=parseInt($scope.searchMap.pageNo)-interval;
            //计算结束页号(当前页+间隔数)
            endPageNo=parseInt($scope.searchMap.pageNo)+interval;

            /*判断特殊情况*/
            //计算得到的起始页号<1,起始页显示第一页,结束页为要显示的页号
            if (startPageNo<1){
                startPageNo=1;
                endPageNo=showPageNoTotal;
                //计算得到的结束页号>总页号
            }else if (endPageNo>$scope.resultMap.totalPages){
                startPageNo=$scope.resultMap.totalPages-showPageNoTotal+1;
                endPageNo=$scope.resultMap.totalPages;
            }
        }
        //前面三个点
        $scope.frontDot=false;
        //起始页号大于1就显示前面三个点
        if (startPageNo>1){
            $scope.frontDot=true;
        }
        //后面三个点
        $scope.backDot=false;
        //结束页号小于总页号就显示后面的三个点
        if (endPageNo<$scope.resultMap.totalPages){
            $scope.backDot=true;
        }

        //遍历结束页号(总页号<=要显示的页数,就显示全部页数)添加到页号数组中
        for (var i = startPageNo; i <= endPageNo; i++) {
            $scope.pageNoList.push(i);
        }
    };

    //判断是否当前页号,处理当前页号为红色
    $scope.isCurrentPage=function (pageNo) {
      return $scope.searchMap.pageNo==pageNo;
    };

    //上下页跳转
    $scope.queryByPageNo=function (pageNo) {
        if (0 < pageNo && (pageNo <= $scope.resultMap.totalPages)){
            //设置页号
            $scope.searchMap.pageNo=pageNo;
            $scope.search();
        }
    };

    //下一页
    $scope.nextPage = function () {
        $scope.queryByPageNo(parseInt($scope.searchMap.pageNo)+1);
    };

    //排序查询
    $scope.sortSearch=function (sortField,sort) {
        //设置传递到后台的值
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    };

    //获取参数的方法
    $scope.loadKeywords=function () {
        $scope.searchMap.keywords=$location.search()["keywords"];
        $scope.search();
    }
});