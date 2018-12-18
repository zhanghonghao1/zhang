app.service("addressService",function ($http) {

    //获取用户地址列表
    this.findAddressList=function () {
        return $http.get("address/findAddressList.do");
    };
});