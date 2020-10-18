var map,layernames;
$(document).ready(function(){
	layernames=getQueryString("layers");
	var layernamelist = layernames.split(",");
	var layername = layernamelist[0];
	var crs=L.CRS.EPSG3857;
	var crs2 = L.CRS.EPSG4326;//new L.Proj.CRS("EPSG:4326","");
	if(null!=layername && ""!=layername){
		map=L.map('map',{
			crs: crs2,
		}).setView([41, 123], 5);
		
		L.control.mousePosition({
			position: 'bottomright'
		}).addTo(map);
		var zoomControl = L.control.zoom({
	        position: 'bottomright',
	        zoomInTitle:"放大",
	        zoomOutTitle:"缩小"
		});  
		map.addControl(zoomControl);
		getLayerInfo(layername);
		
		
		
		initMapClick();
	}
});

/**
 * 图层查询
 */
function getLayerInfo(layername){
	$.ajax({
		type:"GET",
		url:baseUrl+"/geoserver/layer/"+layername,
		data:{},
		success:function(data){
			var jsondata=data.data;
			var url=jsondata.url;
			var bbox=jsondata.bbox;
			var wmsLayer3= L.tileLayer.wms(url, {
				VERSION: '1.1.1', 
				layers: layernames,//图层名称
				format: 'image/png',//图片格式
				transparent: true
			}).addTo(map);
			
			map.fitBounds([
				[parseFloat(bbox[1]), parseFloat(bbox[0])],
				[parseFloat(bbox[3]), parseFloat(bbox[2])]
			]);
		
		},
		error:function(){
		
		}
	});

};


var start=true,end=false;

var locationLayer,lineLayer;
function initMapClick(){
	
	locationLayer = L.layerGroup([]).addTo(map);
	lineLayer = L.layerGroup([]).addTo(map);
	
	
	//地图图形开始创建
    map.on("click", function (event) {
    	var latlng=event.latlng;
    	var marker=L.marker(latlng);
    	locationLayer.addLayer(marker);
    	//火星坐标
    	var gcjLonlat=latlng.lat+","+latlng.lng;
    	if(start){
    		start=false;
    		end=true;
    		$("#startxy").val(gcjLonlat);
    	}
    	
    	if(end){
    		start=true;
    		end=false;
    		$("#endxy").val(gcjLonlat);
    	}
    });
};




/**
 * url参数提取
 */
function getQueryString(name){
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
    var r = window.location.search.substr(1).match(reg); 
    if (r != null) return unescape(r[2]); 
    return null; 
};