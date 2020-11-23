
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
		// crs: L.CRS.EPSG4326
	}).setView([39, 111], 4);
 
	var zoomControl = L.control.zoom({
        position: 'bottomright',
        zoomInTitle:"放大",
        zoomOutTitle:"缩小"
	});  
	map.addControl(zoomControl);
	
	//公开地图
	var osmMap=L.tileLayer('https://{s}.tile.osm.org/{z}/{x}/{y}.png', {
	    attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
	});
	
	
	//google 矢量地图
	var googleVectorUrl="https://localhost:8080/TileService/MapService/getTile.nut?x={x}&y={y}&z={z}";
	googleVectorUrl="https://mt3.google.cn/vt/lyrs=m@207000000&hl=zh-CN&gl=CN&src=app&s=Galile&x={x}&y={y}&z={z}";
	var googleVectorMap= L.tileLayer(googleVectorUrl, {id: 'mapbox.light', attribution: "Google 矢量地图",maxZoom:20});
	googleVectorMap.addTo(map);
	
	//google 影像地图
	var googleImageUrl="https://localhost:8080/TileService/ImageryService/getTile.nut?x={x}&y={y}&z={z}";
	googleImageUrl="https://mt2.google.cn/maps/vt?lyrs=s@804&gl=cn&x={x}&y={y}&z={z}";
	var googleImageMap= L.tileLayer(googleImageUrl, {id: 'mapbox.light', attribution: "Google 矢量地图",maxZoom:20});
	
	
	//高德地图
	var gaoDeiImageUrl="https://wprd03.is.autonavi.com/appmaptile?lang=zh_cn&size=1&style=7&x={x}&y={y}&z={z}&scl=1&ltype=3";
	var gaoDeiImageMap= L.tileLayer(gaoDeiImageUrl, {id: 'mapbox.light', attribution: "高德矢量地图"});

	//高德地图
	var jpurl="http://maps.gsi.go.jp/xyz/std/{z}/{x}/{y}.png?_=20200402a";
	var jpMap= L.tileLayer(jpurl, {id: 'mapbox.light', attribution: "jp"});

	//天地图影像
	var tdtImage = "http://t2.tianditu.gov.cn/img_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=img&tileMatrixSet=w&TileMatrix={z}&TileRow={y}&TileCol={x}&style=default&format=tiles&tk=b684b9e680b3efcc4c517800bd863a75";
	var tdtMap= L.tileLayer(tdtImage, {id: 'tianditu.light', attribution: "jp"});
	
	
	//geoserver wms
	var wmsLayer= L.tileLayer.wms("http://192.168.1.127:8080/geoserver/jp/wms?", {
        layers: 'jp:test001',//需要加载的图层
        format: 'image/png',//返回的数据格式
        transparent: true
        // crs:L.CRS.EPSG404000
        // crs: L.CRS.EPSG4326
    });
	
	//geoserver wms
	var wmsLayer2= L.tileLayer.wms("http://39.98.231.144:8080/geoserver/et/wms?", {
		    'VERSION': '1.1.1', 
		    layers: 'et:haihe-1km_20180101_TMAX1562670927',
	        format: 'image/png',//返回的数据格式
	        exceptions: 'application/vnd.ogc.se_inimage',
	        transparent: true
	        //crs:L.CRS.EPSG404000
	        //crs: L.CRS.EPSG4326
	 });
	//geoserver wms
	var wmsLayer21= L.tileLayer.wms("http://39.98.231.144:8080/geoserver/et/wms?", {
		    'VERSION': '1.1.1', 
		    layers: 'et:haihe-terra_MODIS-1km_20180101_qn1562754021',
	        format: 'image/png',//返回的数据格式
	        exceptions: 'application/vnd.ogc.se_inimage',
	        transparent: true
	        //crs:L.CRS.EPSG404000
	        //crs: L.CRS.EPSG4326
	 });
	
	
	//geoserver wms
	var wmsLayer3= L.tileLayer.wms("http://39.98.231.144:8080/geoserver/etwatch/wms?", {
		    'VERSION': '1.1.1', 
	        layers: 'etwatch:haihe-terra_MODIS-1km_20180101_qn_1563795068599',//需要加载的图层
	        format: 'image/png',//返回的数据格式
	        transparent: true,
	        exceptions: 'application/vnd.ogc.se_inimage'
	        //crs:L.CRS.EPSG404000
	        //crs: L.CRS.EPSG4326
	    });
	
   var layers={
		   wms:wmsLayer,
		   wms2:wmsLayer2,
		   wms21:wmsLayer21,
		   wms3:wmsLayer3
   };
		
	
	var baseLayers = {
		"公开地图":osmMap,
		"谷歌矢量": googleVectorMap,
		"谷歌影像": googleImageMap,
		"高德矢量":gaoDeiImageMap,
		"日本地图":jpMap,
		"天地图影像":tdtMap
	};
	L.control.layers(baseLayers,layers).addTo(map);
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
        	
        	//火星坐标
        	var gcjLonlat=latlng.lat.toFixed(7)+","+latlng.lng.toFixed(7);
        	//wgs84坐标
        	var wgs84Lonlat = GPS.gcj_encrypt(latlng.lat, latlng.lng);
        	//BD-09 百度坐标
        	var bd09Lonlat = GPS.bd_encrypt(latlng.lat, latlng.lng);
        	
        	var loadingUrl = baseUrl+"/static/images/icon/loading.gif";
        	html.push("<div class='popup'>");
	        	html.push("<div class='point gcj'>谷歌地图："+gcjLonlat+"</div>");
	        	html.push("<div class='point'>百度地图："+bd09Lonlat.lat.toFixed(7)+","+bd09Lonlat.lon.toFixed(7)+"</div>");
	        	html.push("<div class='point'>腾讯高德："+gcjLonlat+"</div>");
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
						html.push("<div class='point'>腾讯高德："+gcjLonlat+"</div>");
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
				}

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
