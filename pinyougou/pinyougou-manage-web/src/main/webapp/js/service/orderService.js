app.service("orderService", function ($http) {
    //加载列表数据
    this.findAll = function () {
        return $http.get("../order/findAll.do");
    };

    this.findPage = function (page, rows) {
        return $http.get("../order/findPage.do?page=" + page + "&rows=" + rows);
    };

    this.update = function (entity,id) {
        return $http.post("../order/update.do?id="+id, entity);
    };

    this.findOne = function (id) {
        return $http.get("../order/findOne.do?id=" + id);
    };

    this.search = function (page, rows, searchEntity) {
        return $http.post("../order/search.do?page=" + page + "&rows=" + rows, searchEntity);

    };

    this.findAllOrder = function () {
        return $http.post("../order/findAllOrder.do");
    };

    //状态查询
    this.findStatus = function (value) {
        return $http.get("../order/findStatus.do?value="+value);
    };
});