
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
	addMapBaseLayer();

	//查询天气
	findWeatherInfo();

};


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




	var citySelectInput=new Vcity.CitySelector({input:'citySelect'});
	$("#citySelect").change(function(){
		console.log($(this).val());
		var selectCity = $(this).val();
		var code = null;
		if(selectCity){
			for(var i=0;i<cityCode.length;i++){
				if(cityCode[i].name ==selectCity){
					code = cityCode[i].adcode;
					break;
				}
			}
			if(code){
				findWeatherInfo(code);
			}
		}
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
		zoomControl:false
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
        	
        	//百度坐标
        	var baiduLonlat=latlng.lat.toFixed(7)+","+latlng.lng.toFixed(7);
        	//wgs84坐标
        	var wgs84Lonlat = GPS.gcj_encrypt(latlng.lat, latlng.lng);
        	//BD-09 百度坐标
        	var bd09Lonlat = GPS.bd_encrypt(latlng.lat, latlng.lng);
        	
        	var loadingUrl = baseUrl+"/static/images/icon/loading.gif";
        	html.push("<div class='popup'>");
	        	html.push("<div class='point gcj'>百度坐标："+baiduLonlat+"</div>");
	        	html.push("<div class='point'>百度地图："+bd09Lonlat.lat.toFixed(7)+","+bd09Lonlat.lon.toFixed(7)+"</div>");
	        	html.push("<div class='point wg84'>谷歌地球："+wgs84Lonlat.lat.toFixed(7)+","+wgs84Lonlat.lon.toFixed(7)+"</div>");
	        	html.push("<div>地址：<span class='popup-address' data-x='"+latlng.lng+"' data-y='"+latlng.lat+"'><img src='"+loadingUrl+"' /></span></div>");
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
 * 添加地图基础图层
 *
 * //https://www.earthol.com/
 */
function addMapBaseLayer(){
	//google 矢量地图
	var googleVectorUrl="https://mt{s}.google.cn/vt/lyrs=m@207000000&hl=zh-CN&gl=CN&src=app&s=Galile&x={x}&y={y}&z={z}";
	var googleVectorMap= L.tileLayer(googleVectorUrl, {id: 'mapbox.light', attribution: "Google 矢量地图",maxZoom:20,subdomains:[0,1,2,3]});
	googleVectorMap.addTo(map);


	//公开地图
	var osmMap=L.tileLayer('https://{s}.tile.osm.org/{z}/{x}/{y}.png', {
		attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
	});

	//google 地形地图
	var google_terrain_url="http://mt{s}.google.cn/vt/lyrs=t@132,r@292000000&hl=zh-CN&gl=cn&src=app&x={x}&y={y}&z={z}&scale=2&s=Gal";
	var googleterrainMap= L.tileLayer(google_terrain_url, {id: 'mapbox.light', attribution: "Google 地形地图",maxZoom:20,subdomains:[0,1,2,3]});

	//google 影像地图
	var google_satellite_image_url="http://mt{s}.google.cn/vt/lyrs=s@110&hl=zh-CN&gl=cn&src=app&x={x}&y={y}&z={z}";
	var googleImageMap= L.tileLayer(google_satellite_image_url, {id: 'mapbox.light', attribution: "Google 影像地图",maxZoom:20,subdomains:[0,1,2,3]});

	var googleImageWgs84Map= L.tileLayer("http://mt{s}.google.cn/vt/lyrs=s@110&x={x}&y={y}&z={z}", {id: 'mapbox.light', attribution: "Google 矢量地图",maxZoom:20,subdomains:[0,1,2,3]});


	//mapbox 灰色地图
	var mapbox_map_url="https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
	var mapboxGreyVectorMap= L.tileLayer(mapbox_map_url, {id: 'mapbox.light', attribution: "mapbox 灰色地图",maxZoom:20});

	//mapbox 彩色地图
	var mapbox_colour_url="https://api.mapbox.com/styles/v1/mapbox/streets-v11/tiles/256/{z}/{x}/{y}?access_token=pk.eyJ1IjoiemhhbmdqaWFsdSIsImEiOiJjajRvOW44eDcwOGtqMzNxNnFvemQ2ZTlyIn0.i01rCdfpdvooSqkHQBxPBA";
	var mapboxColourVectorMap= L.tileLayer(mapbox_colour_url, {id: 'mapbox.light', attribution: "mapbox 彩色地图",maxZoom:20});


	//mapbox 影像地图
	var mapbox_image_url="https://api.mapbox.com/styles/v1/mapbox/satellite-v9/tiles/256/{z}/{x}/{y}?access_token=pk.eyJ1IjoiemhhbmdqaWFsdSIsImEiOiJjajRvOW44eDcwOGtqMzNxNnFvemQ2ZTlyIn0.i01rCdfpdvooSqkHQBxPBA";
	var mapboxImageVectorMap= L.tileLayer(mapbox_image_url, {id: 'mapbox.light', attribution: "mapbox 影像地图",maxZoom:20});

	//geoq 彩色版地图
	var arcgis_map_url="http://map.geoq.cn/ArcGIS/rest/services/ChinaOnlineCommunity/MapServer/tile/{z}/{y}/{x}";
	var arcgisVectorMap= L.tileLayer(arcgis_map_url, {id: 'geoq.light', attribution: "geoq 彩色版地图",maxZoom:20});

	//geoq 暖色版地图
	var arcgis_map_warm_url="http://map.geoq.cn/ArcGIS/rest/services/ChinaOnlineStreetWarm/MapServer/tile/{z}/{y}/{x}";
	var arcgisWarmVectorMap= L.tileLayer(arcgis_map_warm_url, {id: 'geoq.light', attribution: "geoq 暖色版地图",maxZoom:20});

	// geoq 灰色版地图
	var arcgis_map_gray_url="http://map.geoq.cn/ArcGIS/rest/services/ChinaOnlineStreetGray/MapServer/tile/{z}/{y}/{x}";
	var arcgisGrayVectorMap= L.tileLayer(arcgis_map_gray_url, {id: 'geoq.light', attribution: "geoq 灰色版地图",maxZoom:20});

	//geoq 蓝黑版地图
	var arcgis_map_color_url="http://map.geoq.cn/ArcGIS/rest/services/ChinaOnlineStreetPurplishBlue/MapServer/tile/{z}/{y}/{x}";
	var arcgisColorVectorMap= L.tileLayer(arcgis_map_color_url, {id: 'geoq.light', attribution: "geoq 蓝黑版地图",maxZoom:20});

	//geoq 水系地图
	var arcgis_map_hydro_url="http://thematic.geoq.cn/arcgis/rest/services/ThematicMaps/WorldHydroMap/MapServer/tile/{z}/{y}/{x}";
	var arcgisHydroVectorMap= L.tileLayer(arcgis_map_hydro_url, {id: 'geoq.light', attribution: "geoq 水系地图",maxZoom:20});

	//geoq 行政区划
	var arcgis_division_map_color_url="http://thematic.geoq.cn/arcgis/rest/services/ThematicMaps/administrative_division_boundaryandlabel/MapServer/tile/{z}/{y}/{x}";
	var arcgisDivisionVectorMap= L.tileLayer(arcgis_division_map_color_url, {id: 'geoq.light', attribution: "geoq 行政区划",maxZoom:20});


	//arcgis 卫星地图
	var arcgis_map_satellite_url="http://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}";
	var arcgisGraysatelliteMap= L.tileLayer(arcgis_map_satellite_url, {id: 'mapbox.light', attribution: "arcgis 矢量地图",maxZoom:20});

	var arcgis_map_satellite_address_url="https://services.arcgisonline.com/ArcGIS/rest/services/Reference/World_Boundaries_and_Places/MapServer/tile/{z}/{y}/{x}";
	var arcgisGraysatelliteAddressMap= L.tileLayer(arcgis_map_satellite_address_url, {id: 'mapbox.light', attribution: "arcgis 矢量地图",maxZoom:20});


	//高德街道(带标注)
	var gaoDeUrl="http://webrd0{s}.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}";
	var gaoDeMap= L.tileLayer(gaoDeUrl, {id: 'appmaptile.light', attribution: "高德街道(带标注)",subdomains:[1,2,3]});

	//高德街道(无标注)
	var gaoDeUrl2="https://wprd0{s}.is.autonavi.com/appmaptile?lang=zh_cn&size=1&style=7&x={x}&y={y}&z={z}&scl=1&ltype=3";
	var gaoDeMapNo= L.tileLayer(gaoDeUrl2, {id: 'appmaptile.light', attribution: "高德街道无标注",subdomains:[1,2,3]});

	//高德卫星
	var gaoDe_SatelliteUrl="http://webst03.is.autonavi.com/appmaptile?style=6&x={x}&y={y}&z={z}";
	var gaoDe_SatelliteMap= L.tileLayer(gaoDe_SatelliteUrl, {id: 'appmaptile.light', attribution: "高德卫星"});
	//高德卫星 - 地名地址
	var gaoDe_Satellite_address = "http://webst03.is.autonavi.com/appmaptile?style=8&x={x}&y={y}&z={z}";
	var gaoDe_SatelliteMapAddress= L.tileLayer(gaoDe_Satellite_address, {id: 'appmaptile.light', attribution: "高德卫星"});


	//天地图街道
	var td_image_url="//t1.tianditu.gov.cn/DataServer?T=vec_w&X={x}&Y={y}&L={z}&tk=b684b9e680b3efcc4c517800bd863a75";
	var tdMap= L.tileLayer(td_image_url, {id: 'mapbox.light', attribution: "天地图街道",maxZoom:20});

	//天地图街道-地名
	var td_image_address_url="//t1.tianditu.gov.cn/DataServer?T=cva_w&X={x}&Y={y}&L={z}&tk=b684b9e680b3efcc4c517800bd863a75";
	var td_addressMap= L.tileLayer(td_image_address_url, {id: 'mapbox.light', attribution: "天地图街道-地名",maxZoom:20});

	//天地图-影像地图
	var td_wx_image_url="//t1.tianditu.gov.cn/DataServer?T=img_w&X={x}&Y={y}&L={z}&tk=b684b9e680b3efcc4c517800bd863a75";
	var td_wxMap= L.tileLayer(td_wx_image_url, {id: 'tianditu.light', attribution: "影像地图",maxZoom:20});

	// //天地图影像
	// var tdtImage = "http://t2.tianditu.gov.cn/img_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=img&tileMatrixSet=w&TileMatrix={z}&TileRow={y}&TileCol={x}&style=default&format=tiles&tk=b684b9e680b3efcc4c517800bd863a75";
	// var tdtMap= L.tileLayer(tdtImage, {id: 'tianditu.light', attribution: "天地图影像"});


	//天地图-卫星-地名
	var td_wx_image_address_url="//t1.tianditu.gov.cn/DataServer?T=cia_w&X={x}&Y={y}&L={z}&tk=b684b9e680b3efcc4c517800bd863a75";
	var td_wx_addressMap= L.tileLayer(td_wx_image_address_url, {id: 'tianditu.light', attribution: "卫星-地名",maxZoom:20});

	//天地图-地形
	var td_dx_image_url="//t1.tianditu.gov.cn/DataServer?T=ter_w&X={x}&Y={y}&L={z}&tk=b684b9e680b3efcc4c517800bd863a75";
	var td_dxMap= L.tileLayer(td_dx_image_url, {id: 'tianditu.light', attribution: "天地图地形",maxZoom:20});

	//天地图-地形-地名
	var td_dx_image_address_url="//t1.tianditu.gov.cn/DataServer?T=cta_w&X={x}&Y={y}&L={z}&tk=b684b9e680b3efcc4c517800bd863a75";
	var td_dx_addressMap= L.tileLayer(td_dx_image_address_url, {id: 'tianditu.light', attribution: "天地图街道",maxZoom:20});


	//日本地理院-矢量地图
	var jp_vertor_url="http://maps.gsi.go.jp/xyz/std/{z}/{x}/{y}.png?_=20200402a";
	var jpVectorMap= L.tileLayer(jp_vertor_url, {id: 'gsi.light', attribution: "矢量地图"});

	//日本地理院-影像地图
	var jp_image_url="http://maps.gsi.go.jp/xyz/seamlessphoto/{z}/{x}/{y}.jpg";
	var jpImageMap= L.tileLayer(jp_image_url, {id: 'gsi.light', attribution: "影像地图"});





	var layers={
		"高德地名(GCJ02)":gaoDe_SatelliteMapAddress,
		"arcgis地名":arcgisGraysatelliteAddressMap,
		"天地图地名(wg84)":td_addressMap,
		"天地图卫星地名(wg84)":td_wx_addressMap,
		"天地图地形地名(wg84)":td_dx_addressMap
	};


	var baseLayers = {
		"公开地图(wg84)":osmMap,
		"谷歌矢量(GCJ02)": googleVectorMap,
		"谷歌地形(GCJ02)":googleterrainMap,
		"谷歌影像(GCJ02)": googleImageMap,
		"谷歌影像(wg84)":googleImageWgs84Map,
		"天地图街道(wg84)":tdMap,
		"天地图地形":td_dxMap,
		"天地图影像(wg84)":td_wxMap,
		"高德街道带标注(GCJ02)":gaoDeMap,
		"高德街道(GCJ02)":gaoDeMapNo,
		"高德卫星(GCJ02)":gaoDe_SatelliteMap,
		"mapbox灰色地图(wg84)":mapboxGreyVectorMap,
		"mapbox彩色地图(wg84)":mapboxColourVectorMap,
		"mapbox影像地图(wg84)":mapboxImageVectorMap,
		"arcgis彩色地图":arcgisVectorMap,
		"arcgis暖色地图":arcgisWarmVectorMap,
		"arcgis灰色地图":arcgisGrayVectorMap,
		"arcgis蓝黑地图":arcgisColorVectorMap,
		"arcgis水系地图":arcgisHydroVectorMap,
		"arcgis行政区划":arcgisDivisionVectorMap,
		"arcgis影像地图":arcgisGraysatelliteMap,
		"gsi.go标准地图":jpVectorMap,
		"gsi.go影像地图":jpImageMap
	};
	L.control.layers(baseLayers,layers).addTo(map);

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
	$('#search').keyup(function() {
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
function findWeatherInfo(city){
	//$(".weather-item.weather-info").empty();
	if(null == city){
		city = "110000";
	}
	// 调用天气接口,暂时查询北京的天气
	$.ajax({
		 url:"https://restapi.amap.com/v3/weather/weatherInfo",
		 dataType : "json",
		 data:{
			 city:city,
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
		url : "https://restapi.amap.com/v3/assistant/inputtips",
		dataType : "json",
		async:false,
		data : {
			keywords : keywords,
			key : "853ca443b4e7edc49849b8e36da1e70b",
			output : "json"
		},
		success : function(data) {
			if (data.status == 1) {
				if(!loading){
					var tips = data.tips;
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
		url : "https://restapi.amap.com/v3/geocode/regeo",
		dataType : "json",
		data : {
			location : xy,
			output : "json",
			extensions : "base",
			key : "853ca443b4e7edc49849b8e36da1e70b"
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
					var xy = location.split(",");
					var marker = L.marker([ xy[1], xy[0] ]);
					
					var lat=parseFloat(xy[1]).toFixed(7);
					var lon=parseFloat(xy[0]).toFixed(7);
					
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
						html.push("<div class='point wg84'>谷歌地球："+parseFloat(wgs84Lonlat.lat).toFixed(7)+","+parseFloat(wgs84Lonlat.lon).toFixed(7)+"</div>");
						
						html.push("<div class='point'>靠近位置："+ geocode.formatted_address + "</div>");
						
					html.push("</div>");
					var popupDom = $(html.join(""));
					popupDom.data(geocode);
					marker.bindPopup(popupDom[0]);

					locationLayer.addLayer(marker);
					map.panTo([ xy[1], xy[0] ]);
					
					
					map.setView([xy[1], xy[0]],map.getMaxZoom());
					marker.openPopup();
				}else{
					var xy = keyword.split(",");
					var marker = L.marker([ xy[1], xy[0] ]);
					var lat=parseFloat(xy[1]).toFixed(7);
					var lon=parseFloat(xy[0]).toFixed(7);

					locationLayer.addLayer(marker);
					map.panTo([ xy[1], xy[0] ]);

					map.setView([xy[1], xy[0]],map.getMaxZoom());
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
