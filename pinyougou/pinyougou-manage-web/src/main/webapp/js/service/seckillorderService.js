app.service("seckillorderService", function ($http) {
    //加载列表数据
    this.findAll = function () {
        return $http.get("../seckillorder/findAll.do");
    };

    this.findPage = function (page, rows) {
        return $http.get("../seckillorder/findPage.do?page=" + page + "&rows=" + rows);
    };

    this.update = function (entity,id) {
        return $http.post("../seckillorder/update.do?id="+id, entity);
    };

    this.findOne = function (id) {
        return $http.get("../seckillorder/findOne.do?id=" + id);
    };

    this.search = function (page, rows, searchEntity) {
        return $http.post("../seckillorder/search.do?page=" + page + "&rows=" + rows, searchEntity);

    };

    this.findAllSeckillOrder = function () {
        return $http.post("../seckillorder/findAllSeckillOrder.do");
    };

    //状态查询
    this.findStatus = function (value) {
        return $http.get("../seckillorder/findStatus.do?value="+value);
    };
});