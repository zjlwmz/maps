var $=null,form,element,layer;
var map;


var shopMakerLayer,shopMakerLayer_shenyangshi,shopPointLayer = null;
layui.use(['form','element','layer'], function(){
	$ = layui.jquery,form=layui.form,element = layui.element,layer = layui.layer;
	
	
	initMap();
	initDom();
	
	
	//长春市
	findshopList('长春市',shopMakerLayer);
	
	//沈阳市
	findshopList('沈阳市',shopMakerLayer_shenyangshi);
	
	addShopMarker();
});




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
	}).setView([43.8704257,125.3258514], 13);
 
	
	var zoomControl = L.control.zoom({
        position: 'bottomright',
        zoomInTitle:"放大",
        zoomOutTitle:"缩小"
	});  
	map.addControl(zoomControl);
	
	
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
		"捷泰地图":geoqVectorMap,
		"谷歌矢量": googleVectorMap,
		"谷歌影像": googleImageMap,
		"高德矢量":gaoDeiImageMap
	};
	
	shopMakerLayer= L.layerGroup([]).addTo(map);
	shopMakerLayer_shenyangshi= L.layerGroup([]).addTo(map);
	//门店所在位置
	shopPointLayer = L.layerGroup([]).addTo(map);
	
	var overlays = {
			"长春市":shopMakerLayer,
			"沈阳市":shopMakerLayer_shenyangshi
	};
	L.control.layers(baseLayers,overlays,{
		collapsed:false
	}).addTo(map);
	
	
	
	
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
	
	
	$(".city.layui-btn").click(function(){
		if($(this).hasClass("layui-btn-primary")){
			var value=$(this).text();
			if(value=="长春市"){
				map.setView([43.8704257,125.3258514], 13);
			}else if(value=="沈阳市"){
				map.setView([41.7925606, 123.4320831], 13);
			}
			$(this).removeClass("layui-btn-primary");
			$(this).siblings().addClass("layui-btn-primary");
		}
	});
};


function addShopMarker(){
	var vedioIcon = L.icon({
        iconUrl: baseUrl+'/static/images/icon/shop.png',
        //shadowUrl: 'leaf-shadow.png',
        iconSize:     [30, 30], // size of the icon
        //shadowSize:   [50, 64], // size of the shadow
        iconAnchor:   [22, 32], // point of the icon which will correspond to marker's location
        //shadowAnchor: [4, 62],  // the same for the shadow
        popupAnchor:  [-2, -30] // point from which the popup should open relative to the iconAnchor
    });
	var marker=L.marker([43.8700699,125.2975434],{
		icon: vedioIcon
	});
	
	shopPointLayer.addLayer(marker);
}

/**
 * 门店查询
 */
function findshopList(city,markerLayer){
	$.ajax({
		type:'get',
        url: baseUrl+"/shop/list",
        data:{
        	city:city
        },
        success: function (data) {
        	if(data.code==0){
        		var listData=data.data;
        		$(listData).each(function(index,obj){
        			if(obj.x && obj.y){
        				var vedioIcon = L.icon({
                            iconUrl: baseUrl+'/static/images/icon/address1.png',
                            //shadowUrl: 'leaf-shadow.png',
                            iconSize:     [16, 16], // size of the icon
                            //shadowSize:   [50, 64], // size of the shadow
                            iconAnchor:   [22, 32], // point of the icon which will correspond to marker's location
                            //shadowAnchor: [4, 62],  // the same for the shadow
                            popupAnchor:  [-2, -30] // point from which the popup should open relative to the iconAnchor
                        });
        				/*
                    	var marker=L.marker([obj.y, obj.x],{
                    		icon: vedioIcon
                    	});*/
        				
        				
        				var money=obj.money;
        				var moneyint=money;
        				if(!moneyint){
        					moneyint=0;
        				}
        				var color =getColor(moneyint);
                    	var marker=L.circleMarker([obj.y, obj.x],{
                    		radius:5,
                    		weight:2,
                    		fill:true,
                    		fillOpacity:1,
                    		fillColor:color,
                    		color:color
                    	});
                    	
                    	
                    	//标注文本展示
                    	var moneystr;
                    	if(!money){
                    		moneystr="无";
                    	}else{
                    		moneystr=money+"";
                    	}
                    	var pmClass=getColorClass(money);
                    	
                    	/*
                		marker.bindTooltip(moneystr,{
                			permanent : true,
                            offset : [ 0,20 ],// 偏移
                            direction : "center",// 放置位置
                            //sticky:true,//是否标记在点上面
                            className : pmClass,// CSS控制
                		}).openTooltip();
                    	*/
                    	markerLayer.addLayer(marker);
        			}
        		});
        	}
        }
	});
}




/**
 *  pm值分类
 *  
 */

function getPhType(ph){
	if(ph<=50){
		return ph+" 优 ";
	}else if(ph>=51 && ph<=100){
		return ph+" 良 ";
	}else if(ph>=101 && ph<=150){
		return ph+" 轻微 ";
	}else if(ph>=151 && ph<=200){
		return ph+" 轻度  ";
	}else if(ph>=201 && ph<=250){
		return ph+" 中度  ";
	}else if(ph>=251 && ph<=300){
		return ph+" 中度重污染  ";
	}else if(ph>300){
		return ph+" 重度污染  ";
	}else{
		return ph;
	}
}

function getColorClass(ph){
	if(ph<=50){
		return "you-tooltip";
	}else if(ph>=51 && ph<=100){
		return "liang-tooltip";
	}else if(ph>=101 && ph<=150){
		return "qingwei-tooltip";
	}else if(ph>=151 && ph<=200){
		return "qingdu-tooltip";
	}else if(ph>=201 && ph<=250){
		return "zhongdu-tooltip";
	}else if(ph>=251 && ph<=300){
		return "zdzwr-tooltip";
	}else if(ph>300){
		return "zdwr-tooltip";
	}else{
		return "anim-tooltip";
	}
}

function getColor(money){
	if(money<35){
		return "#40c057";
	}else if(money>=35 && money<=80){
		return "#FFB800";
	}else if(money>80){
		return "#FF5722";
	}
}
