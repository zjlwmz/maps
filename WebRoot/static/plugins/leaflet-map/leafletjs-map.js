var map;
$(document).ready(function(){
	var height=$(window).height();
	$("#map").css("height",height+"px");
	map = L.map('map',{
		inertia:true,
		zoomAnimation:true
	}).setView([39, 111], 4);
 
	//公开地图
	var osmMap=L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
	    attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
	});
	
	
	//google 矢量地图
	var googleVectorUrl="http://localhost:8080/TileService/MapService/getTile.nut?x={x}&y={y}&z={z}";
	googleVectorUrl="http://mt3.google.cn/vt/lyrs=m@207000000&hl=zh-CN&gl=CN&src=app&s=Galile&x={x}&y={y}&z={z}";
	var googleVectorMap= L.tileLayer(googleVectorUrl, {id: 'mapbox.light', attribution: "Google 矢量地图"});
	googleVectorMap.addTo(map);
	
	//google 影像地图
	var googleImageUrl="http://localhost:8080/TileService/ImageryService/getTile.nut?x={x}&y={y}&z={z}";
	googleImageUrl="http://mt2.google.cn/maps/vt?lyrs=s@804&gl=cn&x={x}&y={y}&z={z}";
	var googleImageMap= L.tileLayer(googleImageUrl, {id: 'mapbox.light', attribution: "Google 矢量地图"});
	
	
	//高德地图
	var gaoDeiImageUrl="http://wprd03.is.autonavi.com/appmaptile?lang=zh_cn&size=1&style=7&x={x}&y={y}&z={z}&scl=1&ltype=3";
	var gaoDeiImageMap= L.tileLayer(gaoDeiImageUrl, {id: 'mapbox.light', attribution: "高德矢量地图"});
	
	
	var baseLayers = {
		"公开地图":osmMap,
		"谷歌矢量": googleVectorMap,
		"谷歌影像": googleImageMap,
		"高德矢量":gaoDeiImageMap
	};
	L.control.layers(baseLayers).addTo(map);
	
});

