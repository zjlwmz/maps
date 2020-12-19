
var map=null;
//定位点图层
var locationLayer = null;
//线路图层
var lineLayer=null;

//查询起点、终点
var routeStartLayer=null,routeEndLayer=null;
// 地图marker查询
var searchMarkerLayer=null;

/**
 * 查询中
 */
var loading=false;
var yxReg=/^[-\+]?\d+(\.\d+)\,[-\+]?\d+(\.\d+)$/;


var $=null,element,layer;
layui.use(['element','layer'], function(){
    $ = layui.jquery,element = layui.element,layer = layui.layer;
    init();

});



/**
 * 初始化
 */
function init() {
    //初始化Dom
    initDom();

    //初始化绑定事件
    initBindEvent();

    //初始化地图
    initMap();

    //添加地图基础底图数据
    addBaiduBaseLayer();

    //查询天气
    findWeatherInfo();

};

var baiduLayerControl;

/**
 * 初始化dom 样式绑定
 */
function initDom(){

    var left = $("#id_search").css("left");
    $("#tips").css({
        "left" : left
    });

    element.on('tab(direction-tab)', function(elem){
        var  prevObject = $(elem.prevObject);
        if(!prevObject.hasClass("integrated")){
            $("#route-list").empty().hide();
        }

        /**
         * 导航查询
         */
        directionSearch();
    });

    //切换百度地图
    $("#switch-baidu").click(function(){


    });
};


/**
 * 地图初始化
 */
function initMap(){

    var height=$(window).height();
    $("#map").css("height",height+"px");


    $(window).resize(function(){
        height=$(window).height();
        $("#map").css("height",height+"px");
    });

    map = L.map('map',{
        inertia:true,
        zoomAnimation:true,
        zoomControl:false,
        crs: L.CRS.Baidu,
        minZoom:3
    }).setView([39, 111], 4);

    var zoomControl = L.control.zoom({
        position: 'bottomright',
        zoomInTitle:"放大",
        zoomOutTitle:"缩小"
    });
    map.addControl(zoomControl);


    L.control.mousePosition().addTo(map);

    /**
     * 比例尺
     */
    L.control.scale().addTo(map);


    locationLayer = L.layerGroup([]).addTo(map);
    lineLayer = L.layerGroup([]).addTo(map);

    routeStartLayer =L.layerGroup([]).addTo(map);
    routeEndLayer =L.layerGroup([]).addTo(map);

    searchMarkerLayer= L.layerGroup([]).addTo(map);

    //地图图形开始创建
    map.on("click", function (event) {
        $("#tips").hide();
        //关键字查询
        if(inputType == 0){
            searchMarkerLayer.clearLayers();
            var latlng=event.latlng;
            var marker=L.marker(latlng);
            searchMarkerLayer.addLayer(marker);
            var html=[];

            //GCJ-02 坐标系
            var gcjLonlat = GPS.bd_decrypt(latlng.lat, latlng.lng);

            //wgs84坐标
            var wgs84Lonlat = GPS.gcj_decrypt(gcjLonlat.lat, gcjLonlat.lon);


            var loadingUrl = baseUrl+"/static/images/icon/loading.gif";
            html.push("<div class='popup'>");
            html.push("<div class='point gcj'>谷歌地图："+gcjLonlat.lat.toFixed(7)+","+gcjLonlat.lon.toFixed(7)+"</div>");
            html.push("<div class='point'>百度地图："+latlng.lat.toFixed(7)+","+latlng.lng.toFixed(7)+"</div>");
            html.push("<div class='point wg84'>谷歌地球："+wgs84Lonlat.lat.toFixed(7)+","+wgs84Lonlat.lon.toFixed(7)+"</div>");
            html.push("<div>地址：<span class='popup-address' data-x='"+gcjLonlat.lon+"' data-y='"+gcjLonlat.lat+"'><img src='"+loadingUrl+"' /></span></div>");
            html.push("<div>");
            var htmlStr=html.join("");
            marker.bindPopup(htmlStr).openPopup();
        }
        //导航
        else if(inputType == 1){
            var latlng=event.latlng;
            var marker=L.marker(latlng);

            var location=latlng.lng+","+latlng.lat;
            MapServerUtils.amap.regeo(location,function(data){
                if(data.infocode=="10000"){
                    var formatted_address = data.regeocode.formatted_address;
                    var addressComponent = data.regeocode.addressComponent;
                    var city = addressComponent.city;
                    if(city.length==0){
                        city = addressComponent.province;
                    }

                    //起点
                    if(!$("#startLocation").data().address){
                        $("#startLocation").val(formatted_address);
                        $("#startLocation").data({
                            city:city,
                            address:formatted_address,
                            latlng:latlng
                        }).parent().find(".input-clear").show();
                        $("#endLocation").focus();

                        routeStartLayer.addLayer(marker);

                    }else if(!$("#endLocation").data().address){
                        $("#endLocation").val(formatted_address);
                        $("#endLocation").data({
                            city:city,
                            address:formatted_address,
                            latlng:latlng
                        }).parent().find(".input-clear").show();

                        routeEndLayer.addLayer(marker);
                    }

                    //导航查询
                    if($("#startLocation").data().address && $("#endLocation").data().address){
                        directionSearch();
                    }
                }
            });
        }

    });

    //popup 打开事件
    map.on("popupopen",function(){
        var data=$(".popup-address").data();
        if(null!=data && null!=data.x && null!=data.y){
            findXyByAddress(data.x+","+data.y,function(data){
                var formatted_address = data.formatted_address;
                $(".popup-address").empty().append(formatted_address);
            });
        }
    });

};

/**
 * 自定义样式地图，customid可选值：dark,midnight,grayscale,hardedge,light,redalert,googlelite,grassgreen,pink,darkgreen,bluish
 * 添加百度地图
 */
function addBaiduBaseLayer(){
    //控制地图底图
    baiduLayerControl=L.control.layers({
        "百度地图": L.tileLayer.baidu({ layer: 'vec' }).addTo(map),
        "百度卫星": L.tileLayer.baidu({ layer: 'img' }),
        "百度地图-大字体": L.tileLayer.baidu({ layer: 'vec', bigfont: true }),
        "百度卫星-大字体": L.tileLayer.baidu({ layer: 'img', bigfont: true }),
        "自定义样式-黑色地图": L.tileLayer.baidu({ layer: 'custom', customid: 'dark' }),
        "自定义样式-蓝色地图": L.tileLayer.baidu({ layer: 'custom', customid: 'midnight' })
    }, {
        "实时交通信息": L.tileLayer.baidu({ layer: 'time' })
    }, { position: "topright" }).addTo(map);
};

/**
 * 查询加载
 */
function addLoading(){
    loading=true;
    $(".route").addClass("loading");
};


/**
 * 查询加载
 */
function clearLoading(){
    loading=false;
    $(".route").removeClass("loading");
};





var inputType = 0;//0 关键字查询;1导航

/**
 * 初始化事件绑定
 */
function initBindEvent(){

    /**
     * 查询事件绑定
     */
    $('#button_search').click(function() {
        if(loading)return;
        /**
         * 路径查询
         */
        if($(".route").hasClass("cancel")){
            directionSearch();
        }else{
            var keyword = $('#search').val();
            if (keyword.length > 0) {
                /**
                 * 逆地址编码服务
                 * xy坐标定位
                 */
                if(yxReg.test(keyword)){
                    addLoading();
                    findArredssByLocation(keyword);
                }
                /**
                 * 地址定位
                 */
                else{
                    addLoading();
                    findLocationXY(keyword);
                    $("#tips").hide();
                }
            }
        }
    });

    // 查询路径事件绑定
    $("#button_location").click(function(){
        var startAddress = $("#startLocation").val();
        var endAddress = $("#endLocation").val();
        findLocationZuobiao(startAddress,function(startXY){
            findLocationZuobiao(endAddress,function(endXY){
                if(startXY!=null && endXY!=""){
                    findLineInfo(startXY,endXY);
                }
            });
        });
    });

    //输入框回车事件绑定
    var i = 0;
    $('#search').keydown(function(event) {
        //回车事件绑定
        if (event.keyCode == 13) {
            $("#tips").hide();
            $('#button_search').trigger("click");
        }
        //即上方面键
        else if(event.keyCode == 38){
            $(".poi").removeClass("selected");
            var poi = $($(".poi").get(i)).addClass("selected");
            var keyWord=poi.text();
            $("#search").val(keyWord).focus();
            if(i==0){
                i=10;
            }
            i--;
        }else if(event.keyCode == 40){
            $(".poi").removeClass("selected");
            var poi = $($(".poi").get(i)).addClass("selected");
            var keyWord=poi.text();
            $("#search").val(keyWord).focus();
            i++;
            if(i==10){
                i=0;
            }
        }
        else {
            var keywords = $('#search').val();
            if (keywords.length >=2 ) {
                var locationByZb = keywords.split(",");
                if(!(locationByZb !='' && !isNaN(parseInt(locationByZb[0])))){
                    findinputtips(keywords);
                }
            } else {
                $("#tips").hide();
            }
        }
    });



    /**
     * 线路查询切换事件
     */
    $(".route").click(function(){
        clearLayers();
        /**
         * 导航查询
         */
        if($(this).hasClass("cancel")){
            $(this).removeClass("cancel");
            $("#id_location").hide();
            $("#search").focus();
            inputType = 0;
        }
        /**
         * 关键字查询
         */
        else{
            $("#id_location").show();
            $(this).addClass("cancel");
            $("#startLocation").focus();
            inputType = 1;
        }
    });


    /**
     * 输入框清空事件绑定
     */
    $(".input-clear").click(function(){
        var inputDom=$(this).parent().find("input");
        var data=inputDom.data();
        $.each(data,function(key,values){
            inputDom.removeData(key);
        });
        inputDom.data({});
        inputDom.val("");
        $(this).hide();
        var layuiRow = $(this).parent();

        //线路图层
        lineLayer.clearLayers();
        //开始
        if(layuiRow.hasClass("start")){
            routeStartLayer.clearLayers();
        }
        //结束
        else if(layuiRow.hasClass("end")){
            routeEndLayer.clearLayers();
        }
        $("#route-list").empty().hide();
    });

}

/**
 * 路径规划查询
 */
function directionSearch(){
    if(!($("#startLocation").data().address && $("#endLocation").data().address)){
        return;
    }

    var startData=$("#startLocation").data();
    var endData=$("#endLocation").data();


    var origin=startData.latlng.lng+","+startData.latlng.lat,destination=endData.latlng.lng+","+endData.latlng.lat,city=startData.city;


    var direction=$("#id_location .layui-this");
    //公交
    if(direction.hasClass("integrated")){
        addLoading();
        MapServerUtils.amap.integrated(origin,destination,city,function(data){
            MapServerUtils.amap.utils.integratedLine(data);
            $("#route-list").show();
            clearLoading();
        });
    }
    //驾车
    else if(direction.hasClass("driving")){
        addLoading();
        MapServerUtils.amap.driving(origin, destination, function(data){
            MapServerUtils.amap.utils.drivingLine(data);
            $("#route-list").show();
            clearLoading();
        });
    }//步行
    else if(direction.hasClass("walking")){
        addLoading();
        MapServerUtils.amap.walking(origin,destination,function(data){
            MapServerUtils.amap.utils.walkingLine(data);
            $("#route-list").show();
            clearLoading();
        });
    }//骑行
    else if(direction.hasClass("bicycling")){
        addLoading();
        MapServerUtils.amap.bicycling(origin,destination,function(data){
            MapServerUtils.amap.utils.bicyclingLine(data);
            $("#route-list").show();
            clearLoading();
        });
    }

}
/**
 * 天气查询
 */
function findWeatherInfo(){
    // 调用天气接口,暂时查询北京的天气
    $.ajax({
        url:"https://restapi.amap.com/v3/weather/weatherInfo",
        dataType : "json",
        data:{
            city:"110000",
            key : "853ca443b4e7edc49849b8e36da1e70b",
            output : "json"
        },
        success:function(data){
            if(data.status == 1){
                $(".weather").html(data.lives[0].weather);
                $(".temperature").html(data.lives[0].temperature+"℃");
            }
        }
    });
};

function findinputtips(keywords) {
    $.ajax({
        url : "http://api.map.baidu.com/place/v2/suggestion",
        dataType: 'jsonp',
        jsonp:'callback',
        jsonpCallback:"jsonpCallback",
        timeout: 5000,
        contentType: 'application/json; charset=utf-8',
        async:false,
        data : {
            query : keywords,
            region:"全国",
            city_limit:false,
            ak : "O4pjwYgRRxLyAnfmp8C5IBCuOWovzqqC",
            output : "json"
        },
        success : function(data) {
            console.log(data);
            if (data.status == 0) {
                if(!loading){
                    var tips = data.result;
                    $("#tips").empty();
                    $(tips).each(function(index, obj) {
                        $("#tips").append("<div class='poi'>" + obj.district + ""+obj.name+"</div>");
                    });

                    if (tips.length > 0) {
                        $("#tips").find(".poi").click(function() {
                            var poi = $(this).text();
                            $('#search').val(poi).focus();
                            $('#button_search').trigger("click");
                            $("#tips").hide();
                        });
                        $("#tips").show();
                    }
                }

            }
        }
    });
}



/**
 * 逆地理编码
 * @param keyword
 */
function findArredssByLocation(xy) {
    locationLayer.clearLayers();//清空
    $.ajax({
        url : "http://api.map.baidu.com/reverse_geocoding/v3/",
        dataType: 'jsonp',
        jsonp:'callback',
        jsonpCallback:"jsonpCallback",
        timeout: 5000,
        contentType: 'application/json; charset=utf-8',
        async:false,
        data : {
            location : xy,
            output : "json",
            ak : "O4pjwYgRRxLyAnfmp8C5IBCuOWovzqqC"
        },
        success : function(data) {
            if (data.infocode == "10000") {
                var regeocode = data.regeocode;
                var addressComponent = regeocode.addressComponent;
                var formatted_address = regeocode.formatted_address;
                var building = addressComponent.building;
                if (addressComponent && addressComponent.streetNumber) {
                    var streetNumber = addressComponent.streetNumber;
                    var location = streetNumber.location;
                    var xyList = xy.split(",");
                    var marker = L.marker([ xyList[1], xyList[0] ]);

                    var lat = parseFloat(xyList[1]).toFixed(7);
                    var lon=parseFloat(xyList[0]).toFixed(7);

                    //火星坐标
                    var gcjLonlat=lat+","+lon;
                    //wgs84坐标
                    var wgs84Lonlat = GPS.gcj_encrypt(lat, lon);
                    //BD-09 百度坐标
                    var bd09Lonlat = GPS.bd_encrypt(lat, lon);


                    var html = [];
                    html.push("<div class='location-popup popup'>");

                    html.push("<div class='point gcj'>谷歌地图："+gcjLonlat+"</div>");
                    html.push("<div class='point'>百度地图："+bd09Lonlat.lat.toFixed(7)+","+bd09Lonlat.lon.toFixed(7)+"</div>");
                    html.push("<div class='point'>腾讯高德："+gcjLonlat+"</div>");
                    html.push("<div class='point wg84'>谷歌地球："+parseFloat(wgs84Lonlat.lat).toFixed(7)+","+parseFloat(wgs84Lonlat.lon).toFixed(7)+"</div>");

                    html.push("<div class='point'>所在位置："+formatted_address+"</div>");

                    html.push("</div>");
                    var popupDom = $(html.join(""));
                    popupDom.data(addressComponent);
                    marker.bindPopup(popupDom[0]);
                    locationLayer.addLayer(marker);

                    // map.panTo([xy[1], xy[0]]);
                    map.setView([ xyList[1], xyList[0] ], map.getMaxZoom());


                    marker.openPopup();
                }else{
                    var xyList = xy.split(",");
                    var marker = L.marker([ xyList[1], xyList[0]]);
                    var lat = parseFloat(xyList[1]).toFixed(7);
                    var lon=parseFloat(xyList[0]).toFixed(7);

                    //
                    var gcjLonlat=lat+","+lon;
                    var html = [];
                    html.push("<div class='location-popup popup'>");

                    html.push("<div class='point gcj'>坐标："+gcjLonlat+"</div>");

                    html.push("<div class='point'>所在位置："+formatted_address+"</div>");

                    html.push("</div>");
                    var popupDom = $(html.join(""));
                    popupDom.data(addressComponent);
                    marker.bindPopup(popupDom[0]);
                    locationLayer.addLayer(marker);
                    map.setView([ xyList[1], xyList[0] ], map.getMaxZoom());
                }

            }


            clearLoading();
        }

    });
}


/**
 * 地理 地址编码查询
 * @param keyword
 */
function findLocationXY(keyword) {
    locationLayer.clearLayers();//清空
    //http://api.map.baidu.com/geocoding/v3/?address=北京市海淀区上地十街10号&output=json&ak=您的ak&callback=showLocation
    $.ajax({
        url : "http://api.map.baidu.com/geocoding/v3/",
        dataType: 'jsonp',
        jsonp:'callback',
        jsonpCallback:"jsonpCallback",
        timeout: 5000,
        contentType: 'application/json; charset=utf-8',
        async:false,
        data : {
            address : keyword,
            output : "json",
            ak : "O4pjwYgRRxLyAnfmp8C5IBCuOWovzqqC"
        },
        success : function(data) {
            if (data.status == 0) {
                var geocodes = data.result;
                if (geocodes.location) {
                    var location = geocodes.location;

                    var lon=parseFloat(location.lng).toFixed(7);
                    var lat=parseFloat(location.lat).toFixed(7);

                    var marker = L.marker([ lat, lon ]);

                    //百度坐标
                    var gcjLonlat=lat+","+lon;
                    //wgs84坐标
                    var wgs84Lonlat = GPS.gcj_encrypt(lat, lon);
                    //BD-09 百度坐标
                    var bd09Lonlat = GPS.bd_encrypt(lat, lon);


                    var html = [];
                    html.push("<div class='location-popup popup'>");

                    html.push("<div class='point gcj'>谷歌地图："+gcjLonlat+"</div>");
                    html.push("<div class='point'>百度地图："+bd09Lonlat.lat.toFixed(7)+","+bd09Lonlat.lon.toFixed(7)+"</div>");
                    html.push("<div class='point'>腾讯高德："+gcjLonlat+"</div>");
                    html.push("<div class='point wg84'>谷歌地球："+parseFloat(wgs84Lonlat.lat).toFixed(7)+","+parseFloat(wgs84Lonlat.lon).toFixed(7)+"</div>");

                    html.push("</div>");
                    var popupDom = $(html.join(""));
                    popupDom.data(geocodes);
                    marker.bindPopup(popupDom[0]);

                    locationLayer.addLayer(marker);
                    map.panTo([ lat, lon ]);

                    map.setView([lat, lon],map.getMaxZoom());
                    marker.openPopup();
                }

            }else{
                var xy = keyword.split(",");
                var marker = L.marker([ xy[1], xy[0] ]);
                var lat=parseFloat(xy[1]).toFixed(7);
                var lon=parseFloat(xy[0]).toFixed(7);

                locationLayer.addLayer(marker);
                map.panTo([ xy[1], xy[0] ]);

                map.setView([xy[1], xy[0]],map.getMaxZoom());

            }


            clearLoading();
        }

    });
}

function findLocationZuobiao(keyword,callback){
    $.ajax({
        url : "https://restapi.amap.com/v3/geocode/geo",
        dataType : "json",
        data : {
            address : keyword,
            output : "output",
            key : "853ca443b4e7edc49849b8e36da1e70b"
        },
        success : function(data) {
            if (data.infocode == "10000") {
                var geocodes = data.geocodes;
                if (geocodes.length > 0) {
                    var geocode = geocodes[0];
                    var location = geocode.location;
                    if(callback)callback(location);
                }
            }

            clearLoading();
        }
    });
}

/**
 * 逆地址编码服务
 * @param location 地址  116.481488,39.990464
 * @param callback 回调函数
 */
function findXyByAddress(location,callback){
    $.ajax({
        url : "https://restapi.amap.com/v3/geocode/regeo",
        dataType : "json",
        data : {
            location : location,
            output : "output",
            key : "853ca443b4e7edc49849b8e36da1e70b"
        },
        success : function(data) {
            if (data.infocode == "10000") {
                var regeocode = data.regeocode;
                if (null!=regeocode) {
                    if(callback)callback(regeocode);
                }
            }
        }
    });
}



function findLineInfo(startDestion,endDestion){
    lineLayer.clearLayers();//清空
    $.ajax({
        url:"https://restapi.amap.com/v3/direction/walking",
        dataType:"json",
        data:{
            origin:startDestion,
            destination:endDestion,
            key : "853ca443b4e7edc49849b8e36da1e70b"
        },
        success:function(data){
            if(data.status == '1'){
                var paths=data.route.paths;
                var steps=paths[0].steps;
                //起点
                var origin=data.route.origin.split(",");
                var startMarker=L.marker([origin[1], origin[0]]);
                lineLayer.addLayer(startMarker);

                //终点
                var destination=data.route.destination.split(",");
                var myIcon = L.divIcon({className: 'end-marker'});
                var endMarker=L.marker([destination[1], destination[0]],{
                    icon:myIcon
                });
                lineLayer.addLayer(endMarker);

                var stepsPoint=[];
                $(steps).each(function(index,obj){
                    var polyline=obj.polyline;
                    var pointList=polyline.split(";");
                    var latlngs =[];
                    $(pointList).each(function(m,n){
                        var lonlat=n.split(",");
                        latlngs.push([lonlat[1],lonlat[0]]);

                        stepsPoint.push([lonlat[1],lonlat[0]]);
                    });
                    var polyline = L.polyline(latlngs, {color: 'red'});
                    lineLayer.addLayer(polyline);

                });
                map.fitBounds(stepsPoint);
            }
        }
    });
}


/**
 * 清空
 */
function clearLayers(){
    clearKeywordLayer();
    clearRouteLayer();
}
/**
 * 关键字搜索图层清空
 */
function clearKeywordLayer(){
    //定位点图层
    locationLayer.clearLayers();
    // 地图marker查询
    searchMarkerLayer.clearLayers();
}

/**
 * 线路搜索图层清空
 */
function clearRouteLayer(){
    //线路图层
    lineLayer.clearLayers();
    //查询起点、终点
    routeStartLayer.clearLayers();
    routeEndLayer.clearLayers();

    $(".input-clear").each(function(index,obj){
        var inputDom=$(this).parent().find("input");
        var data=inputDom.data();
        $.each(data,function(key,values){
            inputDom.removeData(key);
        });
        inputDom.data({});
        inputDom.val("");
        $(this).hide();
    }).hide();
    $("#startLocation").val("");
    $("#endLocation").val("");


    $("#route-list").empty().hide();
}
