// 服务层
app.service("brandService",function ($http) {

    // 分页查询全部列表,this代表BrandService
    this.findPage = function (page, rows) {
        return $http.get("../brand/findPage.do?page=" + page + "&rows=" + rows);
    };

    //新增
    this.add = function (eneity) {
        return $http.post("../brand/add.do",eneity);
    };

    //修改
    this.update = function (eneity) {
        return $http.post("../brand/update.do",eneity);
    };

    //根据品牌id查询品牌数据
    this.findOne=function (id) {
        return $http.get("../brand/findOne.do?id="+id);
    };

    //执行删除
    this.delete=function (selectedIds) {
        return $http.get("../brand/delete.do?ids="+selectedIds)
    };

    //分页条件查询(搜索)
    this.search=function (page,rows,searchEntity) {
        return $http.post("../brand/search.do?page=" + page + "&rows=" + rows, searchEntity);
    };

    //获取格式化的品牌列表
    this.selectOptionList=function () {
        return $http.get("../brand/selectOptionList.do");
    }
});