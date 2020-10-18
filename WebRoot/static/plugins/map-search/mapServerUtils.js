/**
 * 地图服务查询
 */
var MapServerUtils={
	utils:{
		
		/**
		 * 米转千米
		 * @param distance
		 * @returns {String}
		 */
		disRound:function(distance){
			if(distance < 1000){
				return distance+"米";
			}
			else if(distance > 1000){
				return (Math.round(distance/100)/10).toFixed(1) + "公里";
			}
		},
		
		/**
		 * 秒转 天、小时、分钟、秒
		 */
		timeStamp:function(mss){
			var time="约";
			var day=parseInt(mss / (60 * 60 * 24));
			var hour=parseInt((mss % (60 * 60 * 24)) / (60 * 60));
			var min= parseInt((mss % (60 * 60)) / (60));
			var StatusMinute="";
			if (day > 0){
				time= day + "天";
			}
			if (hour>0){
				time += hour + "小时";
			}
			if (min>0){
				time += parseFloat(min) + "分钟";
			}
			if(dayda = 0 && hour==0 && min==0){
				return mss;
			}
			return time;
		}
	},
	/**
	 * 高德地图web api
	 */
	amap:{
		/**
		 * 地理编码
		 */
		geo:function(address,callback){
			$.ajax({
				url : "https://restapi.amap.com/v3/geocode/geo",
				dataType : "json",
				data : {
					address : address,
					output : "output",
					key : ioc.map.amap.key//"853ca443b4e7edc49849b8e36da1e70b"
				},
				success : function(data) {
					if(callback)callback(data);
				}
			});
		},
		/**
		 * 逆地理编码
		 * @param location 116.310003,39.991957
		 * @param callback 回调函数
		 */
		regeo:function(location,callback){
			$.ajax({
				url : "https://restapi.amap.com/v3/geocode/regeo",
				dataType : "json",
				data : {
					location : location,
					output : "json",
					extensions : "base",
					key : ioc.map.amap.key //"853ca443b4e7edc49849b8e36da1e70b"
				},
				success : function(data) {
					if(callback)callback(data);
				}
			});
		},
		/**
		 * 步行路径规划
		 * @params origin 出发点
		 * @params destination 目的地
		 */
		walking:function(origin,destination,callback){
			$.ajax({
				url : "https://restapi.amap.com/v3/direction/walking",
				dataType : "json",
				data : {
					origin : origin,
					destination:destination,
					output : "json",
					key : ioc.map.amap.key //"853ca443b4e7edc49849b8e36da1e70b"
				},
				success : function(data) {
					if(callback)callback(data);
				}
			});
		},
		
		/**
		 * 公交路径规划
		 * @params origin 出发点
		 * @params destination 目的地
		 * @params city 城市
		 */
		integrated:function(origin,destination,city,callback){
			$.ajax({
				url : "https://restapi.amap.com/v3/direction/transit/integrated",
				dataType : "json",
				data : {
					origin : origin,
					destination:destination,
					city:city,
					output : "json",
					key : ioc.map.amap.key //"853ca443b4e7edc49849b8e36da1e70b"
				},
				success : function(data) {
					if(callback)callback(data);
				}
			});
		},
		/**
		 * 驾车路径规划
		 * @params origin 出发点
		 * @params destination 目的地
		 */
		driving:function(origin,destination,callback){
			$.ajax({
				url : "https://restapi.amap.com/v3/direction/driving",
				dataType : "json",
				data : {
					origin : origin,
					destination:destination,
					output : "json",
					key : ioc.map.amap.key //"853ca443b4e7edc49849b8e36da1e70b"
				},
				success : function(data) {
					if(callback)callback(data);
				}
			});
		},
		/**
		 * 骑行路径规划
		 */
		bicycling:function(origin,destination,callback){
			$.ajax({
				url : "https://restapi.amap.com/v4/direction/bicycling",
				dataType : "json",
				data : {
					origin : origin,
					destination:destination,
					key : ioc.map.amap.key //"853ca443b4e7edc49849b8e36da1e70b"
				},
				success : function(data) {
					if(callback)callback(data);
				}
			});
		},
		utils:{
			/**
			 * 公交路径规划线路查询
			 */
			integratedLine:function(data){
				lineLayer.clearLayers();
				if(data.status == '1'){
					var transits=data.route.transits;
					$("#route-list").empty();
					$(transits).each(function(index,obj){
						var cost=obj.cost;
						var row=[];
						row.push("<div class=\"layui-row\">");
							row.push("<div class=\"schemeName\">");
								row.push('<span class="schemePrice"><font>票价<span class="yuanStance">¥</span>'+cost+'</font></span>');
							row.push("</div>");
							row.push('<span class="bus_time">'+MapServerUtils.utils.timeStamp(obj.duration)+'</span>');
							row.push('<span class="busitemdelimiter">|</span>');
							row.push('<span id="blDis_0">'+MapServerUtils.utils.disRound(obj.distance)+'</span>');
							row.push('<span class="busitemdelimiter">|</span>');
							row.push('<span>步行'+MapServerUtils.utils.disRound(obj.walking_distance)+'</span>');
						row.push("</div>");
						var rowDom=row.join("");
						$("#route-list").append(rowDom);
					});
				}
			},
			/**
			 * 驾车路径规划
			 */
			drivingLine:function(data){
				lineLayer.clearLayers();
				if(data.infocode=="10000"){
					var paths=data.route.paths;
					$("#route-list").empty();
					var taxi_cost=data.route.taxi_cost;
					taxi_cost = parseFloat(taxi_cost).toFixed(2);
					$(paths).each(function(index,path){
						var steps=path.steps;
						
						
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

						/**
						 * 途径道路
						 */
						var roadLast=[];
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
							
							var road=obj.road;
							if(road && roadLast.length<=1){
								roadLast.push(road);
							}
							
						});
						
						
						
						var roadStr=roadLast.join("-");
						var row=[];
						row.push('<div class="navtrans-navlist-view  navtrans-type-nav">');
							row.push('<div class="navtrans-navlist-title">');
								row.push('<div class="navtrans-navlist-label">');
									row.push(path.strategy);
								row.push('</div>');
								row.push('<span class="navtrans-navlist-arrow"></span> ');
								row.push('<p class="navtrans-navlist-title-p title-info"><span>'+MapServerUtils.utils.timeStamp(path.duration)+'</span><span class="last">'+MapServerUtils.utils.disRound(path.distance)+'</span></p> ');
								row.push('<p class="navtrans-navlist-title-p"> <span class="">打车约'+taxi_cost+'元</span> <span class="last">途经：'+roadStr+'</span> </p>'); 
							row.push('</div>');
						row.push('</div>');
						
						
						var rowDom=$(row.join("")).data(path);
						rowDom.click(function(){
							
						});
						
						$("#route-list").append(rowDom);
						
					});
					
					//map.fitBounds(stepsPoint);
				}else{
					layer.alert("查询失败",{
						skin: 'layui-layer-molv',
						closeBtn: 0
					});
				}
				
			},
			/**
			 * 步行路径规划
			 */
			walkingLine:function(data){
				lineLayer.clearLayers();
				if(data.infocode=="10000"){
					var paths=data.route.paths;
					$("#route-list").empty();
					$(paths).each(function(index,path){
						var steps=path.steps;
						
						/**
						 * 途径道路
						 */
						var roadLast=[];
						var stepsPoint=[];
						$(steps).each(function(i,obj){
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
							
							
							
							var road=obj.road;
							if(road && roadLast.length<=1){
								roadLast.push(road);
							}
							
							
						});
						
						
						
						var roadStr=roadLast.join("-");
						var row=[];
						row.push('<div class="navtrans-navlist-view  navtrans-type-nav">');
							row.push('<div class="navtrans-navlist-title">');
								row.push('<div class="navtrans-navlist-label">');
									row.push(path.strategy);
								row.push('</div>');
								row.push('<span class="navtrans-navlist-arrow"></span> ');
								row.push('<p class="navtrans-navlist-title-p title-info"><span>'+MapServerUtils.utils.timeStamp(path.duration)+'</span><span class="last">'+MapServerUtils.utils.disRound(path.distance)+'</span></p> ');
								row.push('<p class="navtrans-navlist-title-p"><span class="last">途经：'+roadStr+'</span> </p>'); 
							row.push('</div>');
						row.push('</div>');
						
						
						var rowDom=$(row.join("")).data(path);
						rowDom.click(function(){
							
						});
						
						$("#route-list").append(rowDom);
						
						
						
					});
				}else{
					layer.alert("查询失败",{
						skin: 'layui-layer-molv',
						closeBtn: 0
					});
				}
			},
			/**
			 * 骑行路径规划
			 * @param data
			 */
			bicyclingLine:function(data){
				lineLayer.clearLayers();
				if(data.errcode==0){
					var jsonData=data.data;
					var paths=jsonData.paths;
					$(paths).each(function(index,path){
						var steps=path.steps;
						
						
						/**
						 * 途径道路
						 */
						var roadLast=[];
						var stepsPoint=[];
						$(steps).each(function(i,obj){
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
							
							
							var road=obj.road;
							if(road && roadLast.length<=1){
								roadLast.push(road);
							}
							
							
							
						});
						
						
						
						
						var roadStr=roadLast.join("-");
						var row=[];
						row.push('<div class="navtrans-navlist-view  navtrans-type-nav">');
							row.push('<div class="navtrans-navlist-title">');
								row.push('<div class="navtrans-navlist-label">');
									row.push(path.strategy);
								row.push('</div>');
								row.push('<span class="navtrans-navlist-arrow"></span> ');
								row.push('<p class="navtrans-navlist-title-p title-info"><span>'+MapServerUtils.utils.timeStamp(path.duration)+'</span><span class="last">'+MapServerUtils.utils.disRound(path.distance)+'</span></p> ');
								row.push('<p class="navtrans-navlist-title-p"><span class="last">途经：'+roadStr+'</span> </p>'); 
							row.push('</div>');
						row.push('</div>');
						
						
						var rowDom=$(row.join("")).data(path);
						rowDom.click(function(){
							
						});
						
						$("#route-list").append(rowDom);
						
						
						
					});
				}else{
					layer.alert("查询失败",{
						skin: 'layui-layer-molv',
						closeBtn: 0
					});
				}
			}
		}
	}
	
		
	
};

