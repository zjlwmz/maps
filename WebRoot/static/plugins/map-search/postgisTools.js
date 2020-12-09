
var map=null;
//定位点图层
var locationLayer = null;
//线路图层
var lineLayer=null;

//查询起点、终点
var routeStartLayer=null,routeEndLayer=null,resultLayer=null,drawnItems=null;
// 地图marker查询
var searchMarkerLayer=null;

/**
 * 查询中
 */
var loading=false;
var yxReg=/^[-\+]?\d+(\.\d+)\,[-\+]?\d+(\.\d+)$/;


var $=null,element,layer,form,table,tree,util;
layui.use(['element','layer','form','table','tree', 'util'], function(){
	$ = layui.jquery,element = layui.element,layer = layui.layer,form = layui.form,table=layui.table,tree=layui.tree,util=layui.util;
	
	
	//监听提交
	form.on('submit(form_relation)', function(data){
		//清空图层
		resultLayer.clearLayers();
		var json = JSON.stringify(data.field);
		processingPostgis(data.field,function(jsondata){
			layer.msg(jsondata.msg);
			if(jsondata.code==0){
				var list=jsondata.data;
				$(list).each(function(index,obj){
					var geom  = eval('('+obj.geom+')');
					var geometry=loadgeoJSON2(geom);
					resultLayer.addLayer(geometry);
				});
			}
		});
	    return false;
	});
	
	//保存结果
	form.on('submit(form_save_relation)', function(data){
		var json = JSON.stringify(data.field);
		var layersList = resultLayer.getLayers();
		$(layersList).each(function(index,obj){
			var geojson =obj.getLayers()[0].toGeoJSON();
			geojson = JSON.stringify(geojson);
			var type = obj.getLayers()[0].toGeoJSON().geometry.type;
			var latlngs;
			if(type=="Point"){
				latlngs = obj.getLayers()[0].getLatLng();
				latlngs = JSON.stringify(latlngs);
			}else{
				latlngs = obj.getLayers()[0].getLatLngs();
				latlngs = JSON.stringify(latlngs);
			}
			var name= $("#plotting-container").find("#name").val();
			var url = baseUrl+"/api/plotting/insert";
			ajaxPost(url,{
				"geojson":geojson,
				"latLngs":latlngs,
				"name":name,
				"type":""
			},function(jsondata){
				layer.msg(jsondata.msg);
				if(jsondata.code==0){
					$(".map-layer-list").trigger("change");
				}
			});
		});
		
	    return false;
	});
	
	
	/**
	 * 标绘表格查询
	 */
	form.on('submit(plotting_search)', function(data){
		layuiTableList(data.field);
		return false;
	});
	
	init();
	
});



/**
 * 初始化
 */
function init() {
	//初始化Dom
	initDom();

	//初始化地图
	initMap();
	
	//标绘工具
	drawTools();
	
	//标绘查询
	//findPlottingList();
	
	//标绘表格数据查询
	layuiTableList();
};

/**
 * 初始化dom 样式绑定
 */
function initDom(){
	$("#custom_tools .layui-icon").click(function(){
		$("#plotting-container").hide();
		$("#my-map-div").hide();
		$("#layui-table-container").hide();
		$("#layui-comptoos-container").hide();
		$(this).siblings().removeClass("selected");
		$(this).addClass("selected");
		if($(this).hasClass("calculation")){//post函数计算
			$("#plotting-container").show();
		}else if($(this).hasClass("add-plotting")){//添加标绘
			$("#my-map-div").show();
		}else if($(this).hasClass("tablelist")){
			//标绘查询
			layuiTableList();
			$("#layui-table-container").show();
		}
		//组件
		else if($(this).hasClass("comp-toos")){
			$("#layui-comptoos-container").show();
			initPosgisFunctionTree();
		}
	});



	$("#mymap").click(function(){
		var mylistUl = $(".my-map-list ul");
		mylistUl.empty();
		findMapList(function (data) {
			let list = data.list;
			$(list).each(function(index,obj){
				var li = $("<li data-id='"+obj.id+"'>"+obj.mapName+"</li>");
				li.click(function(){
					let mapName = $(this).text();
					let mapId = li.data().id;
					$("#mymap").text(mapName);
					$("#mymap").data("id",mapId);
					$(".my-map-list").hide();
					findLayerList(mapId,function(layerData){
						let mapLayList = $(".map-layer-list");
						let layerList = layerData.data;
						mapLayList.empty().append("<option value=''>请选择图层</option>");
						$(layerList).each(function(m,n){
							mapLayList.append("<option value='"+n.id+"'>"+n.layer_name+"</option>");
						});

						//清空地图
						drawnItems.clearLayers();
						$("#leaflet-draw-control").hide();
					});
					$(".map-layer-list").change(function(){
						var layerId = $(this).val();
						if(layerId){
							$("#leaflet-draw-control").show();
							getLayerData(layerId);
						}else{
							drawnItems.clearLayers();
							$("#leaflet-draw-control").hide();
						}
					});


				});
				mylistUl.append(li);
			});
			$(".my-map-list").show();
		});
	});



	$("#add-map-btn").click(function(){
		addMapForm();
	});


	$("#export").click(function(){
		var layerId = $(".map-layer-list").val();
		if(layerId){
			window.location.href= ioc.api.url.domain +"/api/layer/data/"+layerId+"/geojson";
		}
	});
};



function addMapForm(properties){
	parent.layer.closeAll();
	var layerIndex = parent.layer.open({
		type: 2,
		maxmin: true,
		// shade:0,
		offset: 'auto',
		area: ['520px', '400px'],
		shadeClose: true, //点击遮罩关闭
		content: baseUrl+"/view/map/form",
		title: "我的地图",
		skin: 'demo-class',
		success:function(){

		}
	});
};
function reloadMapList(){
	table.reload('plotting-list');
};

function editMapForm(properties){
	parent.layer.closeAll();
	var layerIndex = parent.layer.open({
		type: 2,
		maxmin: true,
		// shade:0,
		offset: 'auto',
		area: ['520px', '400px'],
		shadeClose: true, //点击遮罩关闭
		content: baseUrl+"/view/map/form",
		title: "我的地图",
		skin: 'demo-class',
		success:function(){
			var body = layer.getChildFrame('body', layerIndex);
			$(body).find("#id").val(properties.id);
			$(body).find("#mapName").val(properties.mapName);
			$(body).find("#mapDescribe").val(properties.mapDescribe);
		}
	});
};


function getMapLayerList(mapId){
	parent.layer.closeAll();
	var layerIndex = parent.layer.open({
		type: 2,
		maxmin: true,
		shade:0,
		offset: 'auto',
		area: ['1024px', '580px'],
		shadeClose: true, //点击遮罩关闭
		content: baseUrl+"/view/map/layer/list",
		title: "我的地图",
		skin: 'demo-class',
		success:function(){
			var body = layer.getChildFrame('body', layerIndex);
			$(body).find("#mapId").val(mapId);
			$(body).find("#mapIndex").val(layerIndex);
		}
	});
};


/**
 * 地图列表
 */
function findMapList(callback){
	$.ajax({
		url:ioc.api.url.domain+"/api/map/list",
		method:"POST",
		dataType:"json",
		data:{
			pageNo:1,
			pageSize:100
		},
		success:function(data){
			if(callback)callback(data);
		}
	});
};

function findLayerList(mapId,callback){
	$.ajax({
		url:ioc.api.url.domain+"/api/layer/list",
		method:"POST",
		dataType:"json",
		data:{
			mapId:mapId
		},
		success:function(data){
			if(callback)callback(data);
		}
	});
};

function getLayerData(layerId){
	$.ajax({
		url:ioc.api.url.domain+"/api/layer/data/query",
		method:"POST",
		dataType:"json",
		data:{
			id:layerId
		},
		success:function(data){
			console.log(data);
			if(data.code == 0){
				var jsonData = data.data;
				var list = jsonData.list;
				showLayerData(list);
			}
		}
	});
}


/**
 * 地图初始化
 */
function initMap(){
	
	var height=$(window).height();
	$("#map").css("height",height+"px");

	var labelTextCollision = new L.LabelTextCollision({
		collisionFlg : true
	});
	var streetLabelsRenderer = new L.StreetLabels({
		collisionFlg : true,
		propertyName : 'text',
		showLabelIf: function(layer) {
			return true; //layer.properties.type == "primary";
		},
		fontStyle: {
			dynamicFontSize: false,
			fontSize: 12,
			fontSizeUnit: "px",
			lineWidth: 4.0,
			fillStyle: "black",
			strokeStyle: "white",
		},
	});

	map = L.map('map',{
		renderer : labelTextCollision,
		// inertia:true,
		zoomAnimation:true,
		zoomControl:false
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
	
	
	
	locationLayer = L.layerGroup([]).addTo(map);
	lineLayer = L.layerGroup([]).addTo(map);
	
	routeStartLayer =L.layerGroup([]).addTo(map);
	routeEndLayer =L.layerGroup([]).addTo(map);
	
	searchMarkerLayer= L.layerGroup([]).addTo(map);
	
	//标绘图层
	drawnItems = L.featureGroup().on('click',function(event){
		var layer = event.layer;
		var geojson = layer.toGeoJSON();
		var properties = layer.options.properties;
		editPlottingForm(properties,JSON.stringify(geojson),geojson.geometry.type);
	}).addTo(map);
	
	//结果图层
	resultLayer = L.layerGroup([]).addTo(map);
	
	
	var baseLayers = {
		"公开地图":osmMap,
		"谷歌矢量": googleVectorMap,
		"谷歌影像": googleImageMap,
		"高德矢量":gaoDeiImageMap
	};
	var myLayers={
			"locationLayer":locationLayer,
			"lineLayer":lineLayer,
			"routeStartLayer":routeStartLayer,
			"routeEndLayer":routeEndLayer,
			"searchMarkerLayer":searchMarkerLayer,
			"drawnItems":drawnItems,
			"resultLayer":resultLayer
	};
	L.control.layers(baseLayers,myLayers,{ position: 'topright', collapsed: false }).addTo(map);
	
	L.control.mousePosition({
		position: 'bottomright'
	}).addTo(map);
	
	/**
	 * 比例尺
	 */
	L.control.scale({position: 'bottomright'}).addTo(map);
	
	
	
	resiezeMap();
};

function is_map_serach(){
	var isFocus=$("#search").is(":focus");
	return isFocus;
}

/**
 * 地图点击事件
 */
var is_open_map_click=true;



/**
 * 查询加载
 */
function addLoading(){
	loading=true;
	$(".route").addClass("loading");
};


/**
 * postgis 函数目录数
 */
function initPosgisFunctionTree(){
	var data1 = [{
	    title: '几何对象关系函数'
	    ,id: 1
	    ,children: [{
	      title: 'ST_Distance'
	      ,id: 1000
	    },{
	      title: 'ST_DWithin'
	      ,id: 1001
	    },{
	      title: 'ID ST_Equals'
	      ,id: 1002
	    }]
	  },{
	    title: '几何对象处理函数'
	    ,id: 2
	    ,children: [{
	      title: 'ST_Centroid'
	      ,id: 2000
	    },{
	      title: 'ST_Area'
	      ,id: 2001
	    }]
	  },{
	    title: '几何对象存取函数'
	    ,id: 3
	    ,children: [{
	      title: 'ST_AsText'
	      ,id: 3000
	    },{
	      title: 'ST_AsBinary'
	      ,id: 3001
	    }]
	  }]
	
	
	
	//常规用法
	  tree.render({
	    elem: '#test1' //默认是点击节点可进行收缩
	    ,data: data1
	  });
	  
}



/**
 * 查询加载
 */
function clearLoading(){
	loading=false;
	$(".route").removeClass("loading");
};





var inputType = 0;//0 关键字查询;1导航




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




/**
 * 绘制工具
 */
function drawTools(){
	
	map.addControl(new L.Control.Draw({
		position: 'topright',
        edit: {
            featureGroup: drawnItems,
            poly: {
                allowIntersection: false
            }
        },
        draw: {
            polygon: {
                allowIntersection: false,
                showArea: true
            },
			circlemarker:false
        }
    }));
	
	//开始创建
	map.on(L.Draw.Event.DRAWSTART, function (event) {
		is_open_map_click=false;
    });
	
	//创建完成后
    map.on(L.Draw.Event.CREATED, function (event) {
    	debugger;
        var layer = event.layer;
        drawnItems.addLayer(layer);
        is_open_map_click=true;
        var geojsonObj = layer.toGeoJSON();
        var layerType = event.layerType;
        if(layerType == "circle"){
        	var radius = layer.getRadius()/1000;
			var options = {steps: 64, units: 'kilometers', properties: {foo: 'bar'}};
			geojsonObj = turf.circle(geojsonObj.geometry.coordinates, radius, options);
        }else if(layerType=="circlemarker"){
			var radius = layer.getRadius()/1000;
			var options = {steps: 64, units: 'kilometers', properties: {foo: 'bar'}};
			geojsonObj = turf.circle(geojsonObj.geometry.coordinates, radius, options);
        }
        var geojson=JSON.stringify(geojsonObj);
        console.log(geojson);
        openPlottingForm(geojson,geojsonObj.geometry.type);
    });
  	
    
    //删除后
    map.on(L.Draw.Event.DELETED, function (event) {
    	console.log(event);
    	var layers = event.layers.getLayers();
    	var dataIdList=[];
    	$(layers).each(function(index,obj){
    		var options = obj.options;
    		dataIdList.push(options.id);
    	});
    	if(dataIdList.length>0){
    		var ids = dataIdList.join(",");
			var layerId = $(".map-layer-list").val();
    		//标绘删除
    		var url=ioc.api.url.domain+"/api/layer/data/del";
    		ajaxPost(url,{
    			id:ids,
				layerId:layerId
    		},function(jsondata){
    			layer.msg(jsondata.msg);
				if(jsondata.code==0){
					$(".map-layer-list").trigger("change");
				}
    		});
    	}
    });
    
    
    //编辑后
    map.on(L.Draw.Event.EDITED, function (event) {
    	console.log(event);
    	var layers = event.layers.getLayers();
    	$(layers).each(function(index,obj){
    		var geojson =obj.toGeoJSON();
    		var type = geojson.geometry.type;
    		geojson.properties["type"]=type;
			geojson = JSON.stringify(geojson);
			var latlngs;
			if(type=="Point"){
				latlngs = obj.getLatLng()
			}else{
				latlngs = obj.getLatLngs()
			}
			
    		var options = obj.options;
			var properties = options.properties;
    		var id = options.id;
    		//标绘数据更新
    		var url=ioc.api.url.domain+"/api/layer/data/add";
    		ajaxPost(url,{
    			id:id,
				layerId:properties.layer_id,
				mapId:properties.map_id,
				name:properties.name,
				describe:properties.describe,
    			type:type,
    			geojson:geojson
    		},function(jsondata){
    			layer.msg(jsondata.msg);
				if(jsondata.code==0){
					$(".map-layer-list").trigger("change");
				}
    		});
    	});
    });
}

/**
 * 数据上传
 */
function openPlottingForm(geojson,type){
	var mapId = $("#mymap").data("id");
	var layerId = $(".map-layer-list").val();
	var layerIndex=parent.layer.open({
	      type: 2,
	      maxmin: true,
	      shade:0,
	      offset: 'rb',
	      area: ['300px', '400px'],
	      shadeClose: true, //点击遮罩关闭
		  content: baseUrl+"/view/plotting/form",
		  title: "标绘管理",
		  skin: 'demo-class',
		  success:function(){
			  var body = layer.getChildFrame('body', layerIndex);
			  $(body).find("#geojson").val(geojson);
			  $(body).find("#mapId").val(mapId);
			  $(body).find("#layerId").val(layerId);
			  $(body).find("#type").val(type);
		  }
	});
}

function editPlottingForm(properties,geojson,type){
	if(null == properties){
		return;
	}
	parent.layer.closeAll();
	var mapId = $("#mymap").data("id");
	var layerId = $(".map-layer-list").val();
	var layerIndex=parent.layer.open({
		type: 2,
		maxmin: true,
		shade:0,
		offset: 'rb',
		area: ['300px', '400px'],
		shadeClose: true, //点击遮罩关闭
		content: baseUrl+"/view/plotting/form",
		title: "标绘管理",
		skin: 'demo-class',
		success:function(){
			var body = layer.getChildFrame('body', layerIndex);
			$(body).find("#id").val(properties.id);
			$(body).find("#name").val(properties.name);
			$(body).find("#geojson").val(geojson);
			$(body).find("#mapId").val(mapId);
			$(body).find("#layerId").val(layerId);
			$(body).find("#type").val(type);
		}
	});
}



/**
 * 图层数据
 */
function showLayerData(list){
	drawnItems.clearLayers();
	$(list).each(function(index,obj){
		var geometry = obj.geometry;
		var properties = obj.properties;
		var type = obj.properties.type;
		if (type=="Point"){
			var latLng = L.geoJSON(geometry).getLayers()[0]._latlng;
			var marker = L.marker(latLng,{id:obj.id});
			drawnItems.addLayer(marker);
		}else if(type=="circle"){

			var geojson=eval('('+obj.geom+')');
			var latLng =eval('('+obj.latLngs+')');
			var radius = obj.radius;

			var circle=L.circle(latLng,radius, {
				color: 'red',//颜色
				fillColor: '#f03',
				fillOpacity:0.5,//透明度
				dashArray: '5',  //设置虚线
				id:obj.id
			});
			drawnItems.addLayer(circle);
		}else if(type=="circlemarker"){
			var radius = obj.radius;
			var option={
				id:obj.id,
				radius:radius,
				stroke: true,
				color: '#3388ff',
				weight: 4,
				opacity: 0.5,
				fill: true,
				fillColor: null, //same as color by default
				fillOpacity: 0.2,
				clickable: true,
				zIndexOffset: 2000 // This should be > than the highest z-index any markers
			}
			var latLng =eval('('+obj.latLngs+')');
			var circle=L.circleMarker(latLng, option);
			drawnItems.addLayer(circle);
		}else if(type=="Polygon" || type=="polygon" || type == "MultiPolygon"){
			var latLng = L.geoJSON(geometry).getLayers()[0].getLatLngs();
			var polygon2 = L.polygon(latLng, {
				color: 'green',
				fillColor: '#f03',
				fillOpacity: 0.5,
				id:properties.id,
				text:properties.name,
				properties:properties
			});
			drawnItems.addLayer(polygon2);
		}else if(type=="rectangle"){
			var geojson=eval('('+obj.geom+')');
			var latLngs =eval(obj.latLngs);
			var rectangle = L.polygon(latLngs, {
				clickable: true,
				color: "#3388ff",
				fill: true,
				fillColor: null,
				fillOpacity: 0.2,
				opacity: 0.5,
				showArea: true,
				stroke: true,
				weight: 4,
				text:properties.name,
				id:properties.id
			});
			drawnItems.addLayer(rectangle);
		}else if(type=="Polyline" || type=="polyline" || type=="LineString" || type=="MultiLineString"){
			var latLng = L.geoJSON(geometry).getLayers()[0].getLatLngs();
			var polyline = L.polyline(latLng, { color: 'red',id:properties.id,properties:properties,text:properties.name });
			drawnItems.addLayer(polyline);
		}else if(type=="marker"){
			var latLng =eval('('+obj.latLngs+')');
			var marker = L.marker(latLng,{id:obj.id});
			drawnItems.addLayer(marker);
		}
		else{

		}

	});


	var bound = drawnItems.getBounds();
	if(bound.isValid()){
		map.fitBounds(bound);
	}
}



/**
 * 标绘保存回调处理
 */
function addPlottingCallback(data){
	layer.open({
		  content: data.msg,
		  yes: function(index, layero){
		    if(data.code==0){
		    	//findPlottingList();
				$(".map-layer-list").trigger("change");
		    }
		    layer.closeAll();
		  }
	});
};




/**
 * 几何对象处理函数
 */
function processingPostgis(data,callback){
	$.ajax({
		url:baseUrl+"/api/plotting/processing",
		method:"POST",
		dataType:"json",
		data:data,
		success:function(data){
			if(callback)callback(data);
		}
	});
};

/**
 * post请求
 */
function ajaxPost(url,data,callback){
	$.ajax({
		url:url,
		method:"POST",
		dataType:"json",
		data:data,
		success:function(data){
			if(callback)callback(data);
		}
	});
};


/**
 * 随机生成颜色
 */
function rgb3(){
	var r=Math.floor(Math.random()*256);
    var g=Math.floor(Math.random()*256);
    var b=Math.floor(Math.random()*256);
    return "rgb("+r+','+g+','+b+")";//所有方法的拼接都可以用ES6新特性`其他字符串{$变量名}`替换
}
function color16(){
	var color = '#'+ Math.random().toString(16).substr(-6);
	return color;
}

/**
 * 加载geojson
 */
function loadgeoJSON(geojson){
	var type=geojson.type;
	var options={};
	if(type=="LineString"){
		options={ color: 'red' };
	}else if(type=="MultiPolygon" || type=="Polygon"){
		options={"stroke":true,"color":"red","weight":1,"opacity":0.5,"fill":true,"fillColor":"#ffff00","fillOpacity":0.4,"clickable":true};
	}else{
		options={"stroke":true,"color":"red","weight":1,"opacity":0.5,"fill":true,"fillColor":"white","fillOpacity":0.4,"clickable":true};
	}
	var layerpolygon=L.geoJSON(geojson,{
        style: function (feature) {
            return options;
        }
	 });
	return layerpolygon;
};

/**
 * 加载geojson
 */
function loadgeoJSON2(geojson){
	var type=geojson.type;
	var options={};
	if(type=="LineString"){
		var color  = color16();
		options={ color: color };
	}else if(type=="MultiPolygon" || type=="Polygon"){
		options={"stroke":true,"color":"red","weight":1,"opacity":0.5,"fill":true,"fillColor":"#ffff00","fillOpacity":0.4,"clickable":true};
	}else{
		options={"stroke":true,"color":"red","weight":1,"opacity":0.5,"fill":true,"fillColor":"white","fillOpacity":0.4,"clickable":true};
	}
	var layerpolygon=L.geoJSON(geojson,{
        style: function (feature) {
            return options;
        }
	 });
	return layerpolygon;
};

/**
 * 表格数据
 */
function layuiTableList(params){
	var url = ioc.api.url.domain+"/api/map/list";
	table.render({
	    elem: '#plotting-list'
	    ,url:url
	    ,where:params
	    ,toolbar: false
	    ,title: '标绘数据表'
	    ,totalRow: false
	    ,cols: [[
	      {field:'id', title:'ID', width:280, fixed: 'left', unresize: true, sort: true, totalRowText: '合计行'}
	      ,{field:'mapName', title:'名称'}
	      ,{field:'mapDescribe', title:'描述', width:180, sort: true, totalRow: true}
	      ,{field:'createDate', title:'创建时间', width:180, sort: true}
	      ,{field:'updateDate', title:'更新时间', width:180, sort: true}
	      ,{field:'remarks', title:'备注', width:180, sort: true}
	      ,{fixed: 'right', title:'操作', toolbar: "#barDemo", width:200}
	    ]]
	    ,page: true
	    ,response: {
	      statusCode: 200 //重新规定成功的状态码为 200，table 组件默认为 0
	    }
	    ,parseData: function(res){ //将原始数据解析成 table 组件所规定的数据
	      return {
	        "code": 200, //解析接口状态
	        "msg": 'ok', //解析提示文本
	        "count": res.count, //解析数据长度
	        "data": res.list //解析数据列表
	      };
	    }
	  });


	//头工具栏事件
	table.on('toolbar(plotting_list_event)', function(obj){
		  var checkStatus = table.checkStatus(obj.config.id);
		  switch(obj.event){
		    case 'getgeojson':
		      layer.msg('添加getgeojson');
		    break;
		    case 'delete':
		      layer.msg('删除');
		    break;
		    case 'update':
		      layer.msg('编辑');
		    break;
		  };
	});

	//监听行工具事件
	table.on('tool(plotting_list_event)', function(obj){
		var rowData = obj.data;
		if(obj.event === 'del'){
			layer.confirm('真的删除行么', function(index){

				$.ajax({
					url:ioc.api.url.domain+"/api/map/del",
					method:"POST",
					dataType:"json",
					data:{
						id:rowData.id
					},
					success:function(data){
						if(data.code==0){
							obj.del();
						}
					}
				});
				layer.close(index);
			});
		}else if(obj.event === 'edit'){
			editMapForm(obj.data);
		}else if(obj.event === 'list'){
			getMapLayerList(obj.data.id);
		}
	});
	
};

/**
 * 
 */
function resiezeMap(){
	$(window).resize(function(){
		var height=$(window).height();
		$("#map").css("height",height+"px");
	});
}