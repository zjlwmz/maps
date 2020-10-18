var $=null,form,element,layer;
var map;


var cityZoneNewLayer,titleMakerLayer = null;
layui.use(['form','element','layer'], function(){
	$ = layui.jquery,form=layui.form,element = layui.element,layer = layui.layer;
	
	//选择城市
	form.on("select(city)", function(data){
	    var value=data.value;
	    var valueList=value.split("_");
	    
	    titleMakerLayer.clearLayers();
	    cityZoneNewLayer.clearLayers();
	    if(valueList.length>=3){
	    	var lon=valueList[1]; 
		    var lat=valueList[2];
		    var zoom  =valueList[3];
		    
		    map.setView([lat,lon],zoom)
		    findCityZoneNewList(valueList[0]);
	    }else{
	    	$("#cityzonenew").empty();
	    	form.render();
	    }
	    
	});
	
	
	form.render();
	initMap();
	initDom();
});




function selectCityZoneNewClick(){
	//选择城市
	form.on('checkbox(select_zone_new)', function(data){
		var cityZonedata = $(data.elem).data();
	    if(data.elem.checked){
	    	var path = cityZonedata.path;
	    	//#94E8AE \#5FB878
			var polygon2 = L.polygon(path, {
				    color: '#1E9FFF',
				    fillColor: '#94E8AE',
				    fillOpacity: 0.5,
				    weight:1,
				    id:cityZonedata.id
			});
			
			cityZoneNewLayer.addLayer(polygon2);
			map.fitBounds(polygon2.getBounds());
	    }else{
	    	var layers = cityZoneNewLayer.getLayers();
	    	$(layers).each(function(index,obj){
	    		if(obj.options.id ==cityZonedata.id ){
	    			cityZoneNewLayer.removeLayer(obj);
	    			return true;
	    		}
	    	});
	    }
	 });
};


function findCityZoneNewList(cityCode){
	$.ajax({
		type:"get",
		url:baseUrl+"/cityzone/findCityZoneNewTypeList",
		method:"POST",
		data:{
			"cityCode":cityCode
		},
		success:function(data){
			$("#city_zone_new_list").empty();
			var cityZoneList = [];
			if(data.code==0){
				var jsonList = data.data;
				$(jsonList).each(function(index,obj){
					var type = obj.type;
					var list = obj.list;
					var letters_block_list = [];
					letters_block_list.push('<div class="letters_block">');
					letters_block_list.push('<span class="letters">'+type+'</span>');
					$(list).each(function(index2,obj2){
						cityZoneList.push(obj2);
						var path = obj2.path;
						letters_block_list.push("<input type=\"checkbox\" data-id='"+obj2.id+"' data-path='"+path+"' name=\"like1[write]\" lay-filter=\"select_zone_new\" lay-skin=\"primary\" title='"+obj2.name+"'>");
					});
					letters_block_list.push('</div>');
					
					$("#city_zone_new_list").append(letters_block_list.join(""));
				});
				
				addMarker(cityZoneList);
				
				selectCityZoneNewClick();
				form.render();
			}
		}
	});
}



function addMarker(jsonList){
	$(jsonList).each(function(index,obj){
		var path = eval(obj.path);
		var lng =obj.lng;
		var lat =obj.lat;
		var vedioIcon = L.icon({
            iconUrl: baseUrl+'/static/images/icon/point1.png',
            iconSize:     [24, 32], // size of the icon
            iconAnchor:   [24, 32], // point of the icon which will correspond to marker's location
            popupAnchor:  [-2, -30] // point from which the popup should open relative to the iconAnchor
        });
		var marker=L.marker([lat,lng],{
			icon: vedioIcon
    	});
		
		//标注文本展示
		marker.bindTooltip(obj.name,{
			permanent : true,
            offset : [ 0,48 ],// 偏移
            direction : "center"// 放置位置
            //sticky:true,//是否标记在点上面
            //className : pmClass,// CSS控制
		}).openTooltip();
		
		//气泡
		var popupContent = [];
		popupContent.push("<div class='popup-container'>");
			popupContent.push("<div>"+obj.describe+"</div>");
		popupContent.push("</div>");
		marker.bindPopup(popupContent.join(""));
		
		titleMakerLayer.addLayer(marker);
		
		//#94E8AE \#5FB878
		var polygon2 = L.polygon(path, {
			    color: '#1E9FFF',
			    fillColor: '#94E8AE',
			    fillOpacity: 0.5,
			    weight:1,
			    id:obj.id
		});
		
		//cityZoneNewLayer.addLayer(polygon2);
	
	
		$("#cityzonenew").append("<option value='"+obj.id+"'>"+obj.name+"</option>");
	});
}


function findCityZoneNewList2(cityCode){
	$.ajax({
		type:"get",
		//url:baseUrl+"/cityzone/findCityZoneNewList",
		url:baseUrl+"/cityzone/findCityZoneNewTypeList",
		method:"POST",
		data:{
			"cityCode":cityCode
		},
		success:function(data){
			$("#cityzonenew").empty();
			if(data.code==0){
				var jsonList = data.data;
				$(jsonList).each(function(index,obj){
					var path = eval(obj.path);
					var lng =obj.lng;
					var lat =obj.lat;
					var vedioIcon = L.icon({
                        iconUrl: baseUrl+'/static/images/icon/point1.png',
                        iconSize:     [24, 32], // size of the icon
                        iconAnchor:   [24, 32], // point of the icon which will correspond to marker's location
                        popupAnchor:  [-2, -30] // point from which the popup should open relative to the iconAnchor
                    });
					var marker=L.marker([lat,lng],{
						icon: vedioIcon
                	});
					
					//标注文本展示
					marker.bindTooltip(obj.name,{
            			permanent : true,
                        offset : [ 0,48 ],// 偏移
                        direction : "center"// 放置位置
                        //sticky:true,//是否标记在点上面
                        //className : pmClass,// CSS控制
            		}).openTooltip();
					
					//气泡
					var popupContent = [];
					popupContent.push("<div class='popup-container'>");
						popupContent.push("<div>"+obj.desc+"</div>");
					popupContent.push("</div>");
					marker.bindPopup(popupContent.join(""));
					
					titleMakerLayer.addLayer(marker);
					
					//#94E8AE \#5FB878
					var polygon2 = L.polygon(path, {
						    color: '#1E9FFF',
						    fillColor: '#94E8AE',
						    fillOpacity: 0.5,
						    weight:1,
						    id:obj.id
					});
					
					cityZoneNewLayer.addLayer(polygon2);
				
				
					$("#cityzonenew").append("<option value='"+obj.id+"'>"+obj.name+"</option>");
				});
			}
			
			form.render();
		}
	});
}



/**
 * 初始化地图
 */
function initMap(){
	var height=$(window).height();
	$("#map").css("height",height+"px");
	map = L.map('map',{
		inertia:true,
		zoomAnimation:true,
		zoomControl:false,
	}).setView([39, 111], 4);
 
	
	var zoomControl = L.control.zoom({
        position: 'bottomright',
        zoomInTitle:"放大",
        zoomOutTitle:"缩小"
	});  
	map.addControl(zoomControl);
	
	
	//公开地图
	var osmMap=L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
	    attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
	});
	
	//geoq
	//http://map.geoq.cn/arcgis/rest/services/ChinaOnlineStreetPurplishBlue/MapServer
	//http://map.geoq.cn/arcgis/rest/services/ChinaOnlineStreetPurplishBlue/MapServer/tile/3/4/7
	var geoqUrl="http://map.geoq.cn/arcgis/rest/services/ChinaOnlineStreetPurplishBlue/MapServer/tile/{z}/{y}/{x}";
	var geoqVectorMap= L.tileLayer(geoqUrl, {id: 'mapbox.light', attribution: "Geo 矢量地图",maxZoom: 16,
        minZoom: 0,});
	geoqVectorMap.addTo(map);
	
		
	//google 矢量地图
	var googleVectorUrl="http://mt3.google.cn/vt/lyrs=m@207000000&hl=zh-CN&gl=CN&src=app&s=Galile&x={x}&y={y}&z={z}";
	var googleVectorMap= L.tileLayer(googleVectorUrl, {id: 'mapbox.light', attribution: "Google 矢量地图"});
	//googleVectorMap.addTo(map);
	
	//google 影像地图
	var googleImageUrl="http://mt2.google.cn/maps/vt?lyrs=s@804&gl=cn&x={x}&y={y}&z={z}";
	var googleImageMap= L.tileLayer(googleImageUrl, {id: 'mapbox.light', attribution: "Google 矢量地图"});
	
	
	//高德地图
	var gaoDeiImageUrl="http://wprd03.is.autonavi.com/appmaptile?lang=zh_cn&size=1&style=7&x={x}&y={y}&z={z}&scl=1&ltype=3";
	var gaoDeiImageMap= L.tileLayer(gaoDeiImageUrl, {id: 'mapbox.light', attribution: "高德矢量地图"});
	
	
	var baseLayers = {
		"公开地图":osmMap,
		"捷泰地图":geoqVectorMap,
		"谷歌矢量": googleVectorMap,
		"谷歌影像": googleImageMap,
		"高德矢量":gaoDeiImageMap
	};
	L.control.layers(baseLayers).addTo(map);
	
	
	
	cityZoneNewLayer = L.layerGroup([]).addTo(map);
	
	
	
	//设备覆盖物图层
	titleMakerLayer = L.featureGroup().addTo(map);
	
};




function initDom(){
	var height=$(window).height()-40;
	$("#city_zone_new_list").css({
		"height":height+"px"
	});
	
	$(window).resize(function(){
		height=$(window).height()-40;
		$("#city_zone_new_list").css({
			"height":height+"px"
		});
	});
}