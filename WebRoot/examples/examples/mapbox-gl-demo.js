

$(document).ready(function(){
	var height=$(window).height();
	$("#map").css("height",height+"px");
	mapboxgl.accessToken = 'pk.eyJ1IjoiemhhbmdqaWFsdSIsImEiOiJjajRvOW44eDcwOGtqMzNxNnFvemQ2ZTlyIn0.i01rCdfpdvooSqkHQBxPBA';
	var map = new mapboxgl.Map({
	    container: 'map', // container id
	    //style: 'mapbox://styles/mapbox/streets-v9', //stylesheet location
		//style:'mapbox://styles/mapbox/satellite-v9',
		style:{
			version:8,
			sources:{
				"jp.tile":{
					type: "raster",
					tiles: [
						"http://maps.gsi.go.jp/xyz/std/{z}/{x}/{y}.png?_=20200402a"
					],
					tileSize: 256
				}
			},
			layers:[
				{
					id: "jp",
					type: "raster",
					source: "jp.tile"
				}
			]
		},
	    center: [-74.50, 40], // starting position
	    zoom: 9 // starting zoom
	});

	map.on("load", function() {
		map.addSource("google.tile", {
			type: "raster",
			tiles: [
				"https://www.google.cn/maps/vt?lyrs=s@189&gl=cn&x={x}&y={y}&z={z}"
			],
			tileSize: 256
		});
		map.addLayer({
			id: "google",
			type: "raster",
			source: "google.tile",
			layout:{
				visibility:'gone'
			},
		});


	});

});