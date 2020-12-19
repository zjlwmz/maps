mapboxgl.accessToken =
  "pk.eyJ1IjoiY2RodWFuZ3lvbmciLCJhIjoiY2tjeTNvOXFxMDY0OTJzbXdwdnQ5eXJkMyJ9.esxleaBUk77Ybqivz4syVQ";

const data = [
  {
    COORDINATES: [118.76218, 32.04158],
    WEIGHT: 2,
  },
  {
    COORDINATES: [118.76218, 32.04258],
    WEIGHT: 4,
  },
  {
    COORDINATES: [118.76218, 32.04358],
    WEIGHT: 6,
  },
  {
    COORDINATES: [118.76218, 32.04458],
    WEIGHT: 8,
  },
];
var modelOrigin = [118.76218, 32.04158]; // 添加模型的地理位置
var modelAltitude = 0;
var modelRotate = [Math.PI / 2, 0, 0];
var modelScale = 5.41843220338983e-8;
// transformation parameters to position, rotate and scale the 3D model onto the map
var modelTransform = {
  translateX: mapboxgl.MercatorCoordinate.fromLngLat(modelOrigin, modelAltitude)
    .x,
  translateY: mapboxgl.MercatorCoordinate.fromLngLat(modelOrigin, modelAltitude)
    .y,
  translateZ: mapboxgl.MercatorCoordinate.fromLngLat(modelOrigin, modelAltitude)
    .z,
  rotateX: modelRotate[0],
  rotateY: modelRotate[1],
  rotateZ: modelRotate[2],
  scale: modelScale,
};
var upFlag = false;
var heatmapInstance;
var material;
var geometry;
var zoom = 10;

var map = new mapboxgl.Map({
  container: "map",
  center: [118.76218, 32.04158],
  zoom: zoom,
  style: "mapbox://styles/mapbox/streets-v11",
});

map.on("load", function () {
  map.addLayer(customLayer);
  // var layers = map.getStyle().layers;
  //     for (var i = 0; i < layers.length; i++) {
  //         console.log(layers[i],"======");
  //     }
});
// map.on("moveend", function (e) {
//   if (e.moveend && e.moveend == "111") {
//     //查询操作
//   }
// });

map.on("wheel", function (e) {
  var newZoom = e.target.getZoom();
  if (e.originalEvent.deltaY > 0) {
    // 缩小
    console.log("缩小", "======");
    upFlag = true;
    zoom = newZoom;
  } else if (e.originalEvent.deltaY < 0) {
    // 放大
    console.log("放大", "======");
    upFlag = true;
    zoom = newZoom;
  }

  console.log(zoom)
});
const customLayer = {
  id: "highlights",
  type: "custom",
  data: data,
  getPosition: (d) => d.COORDINATES,
  getWeight: (d) => d.WEIGHT,
  renderingMode: "3d",
  onAdd: function (map, gl) {
    var that = this;
    this.camera = new THREE.Camera();
    this.scene = new THREE.Scene();
    this.camera.position.set(3, 4, 10);
    this.renderer = new THREE.WebGLRenderer({
      canvas: map.getCanvas(),
      context: gl,
      antialias: true,
    });
    heatmapInstance = h337.create({
      // only container is required, the rest will be defaults
      container: document.querySelector(".heatmap"),
    });

    // now generate some random data
    var points = [];
    var max = 0;
    var width = 256;
    var height = 256;
    var len = 100;

    while (len--) {
      var val = Math.floor(Math.random() * 100);

      max = Math.max(max, val);
      var point = {
        x: Math.floor(Math.random() * width),
        y: Math.floor(Math.random() * height),
        value: val,
      };
      points.push(point);
    }

    // heatmap data format
    var data = {
      max: max,
      data: points,
    };

    // if you have a set of datapoints always use setData instead of addData
    // for data initialization
    console.log(data);
    heatmapInstance.setData(data);

    var light = new THREE.DirectionalLight(0xffffff);
    light.position.set(0, 0, 1);
    this.scene.add(light);

    geometry = new THREE.PlaneGeometry(0, 0, 255, 255);
    geometry.verticesNeedUpdate = true;
    let geolen = geometry.vertices.length;

    var mdata;
    for (let i = 0; i < geolen; i++) {
      geometry.vertices[i].z =
        heatmapInstance.getValueAt({ x: i % 256, y: Math.trunc(i / 256) }) /
        (256 * 2);
    }

    let texture = new THREE.CanvasTexture(heatmapInstance._renderer.canvas);
    texture.needsUpdate = true;
    material = new THREE.MeshBasicMaterial({
      map: texture,
      transparent: true,
      wireframe: false,
      side: THREE.DoubleSide,
      depthTest: true,
    });

    material.map.needsUpdate = true;
    var plane = new THREE.Mesh(geometry, material);
    plane.name = "heatmap";

    plane.rotateX(-Math.PI / 2);
    plane.scale.set(50000, 50000, 50000);
    plane.position.set(0, 10, 10);
    // var v = threeLayer.coordinateToVector3(new maptalks.Coordinate(map.getCenter()));
    // plane.position.x = v.x;
    // plane.position.y = v.y;
    // plane.position.z = v.z;
    // debugger
    this.scene.add(plane);

    this.renderer.autoClear = false;
  },
  render: function (gl, matrix) {
    /////////////////////
    var rotationX = new THREE.Matrix4().makeRotationAxis(
      new THREE.Vector3(1, 0, 0),
      modelTransform.rotateX
    );
    var rotationY = new THREE.Matrix4().makeRotationAxis(
      new THREE.Vector3(0, 1, 0),
      modelTransform.rotateY
    );
    var rotationZ = new THREE.Matrix4().makeRotationAxis(
      new THREE.Vector3(0, 0, 1),
      modelTransform.rotateZ
    );

    var m = new THREE.Matrix4().fromArray(matrix);
    var l = new THREE.Matrix4()
      .makeTranslation(
        modelTransform.translateX,
        modelTransform.translateY,
        modelTransform.translateZ
      )
      .scale(
        new THREE.Vector3(
          modelTransform.scale,
          -modelTransform.scale,
          modelTransform.scale
        )
      )
      .multiply(rotationX)
      .multiply(rotationY)
      .multiply(rotationZ);

    this.camera.projectionMatrix.elements = matrix;
    this.camera.projectionMatrix = m.multiply(l);
    if (upFlag) {
      update(heatmapInstance, this);
    }
    material.map.needsUpdate = true;
    geometry.verticesNeedUpdate = true;

    this.renderer.state.reset();
    this.renderer.render(this.scene, this.camera);
    map.triggerRepaint();
  },
};

document.querySelector("#changeHeatmap").addEventListener("click", function () {
  upFlag = true;
  zoom += 0.1;
  // console.log(that.scene.getObjectByName('heatmap').geometry.vertices,"2222")	;
});
function update(heatmapInstance, that) {
  var oldData = heatmapInstance.getData();
  var newObj = {
    max: oldData.max,
    data: [],
  };

  console.log(zoom/10,"系数")
  oldData.data.forEach(function (item) {
    // 先判断象限，1，2，3，4
    var newX;
    var newY;
    // var newV = Math.abs(Number(item.value) * zoom/10);
    var newV;
    if (Number(item.x) > 128 && Number(item.y) > 128) {
      //1
      newX = Number(item.x) + zoom/10;
      newY = Number(item.y) - zoom/10;
    } else if (Number(item.x) < 128 && Number(item.y) > 128) {
      //2
      newX = Number(item.x) - zoom/10;
      newY = Number(item.y) - zoom/10;
    } else if (Number(item.x) < 128 && Number(item.y) < 128) {
      //3
      newX = Number(item.x) - zoom/10;
      newY = Number(item.y) + zoom/10;
    } else if (Number(item.x) > 128 && Number(item.y) < 128) {
      //4
      newX = Number(item.x) + zoom/10;
      newY = Number(item.y) + zoom/10;
    } else {
      newX = Number(item.x);
      newY = Number(item.y);
    }

    newObj.data.push({
      x: newX,
      y: newY,
      value: item.value,
    });
  });
  console.log(newObj, "newObj");
  heatmapInstance.setData(newObj);

  var posi = that.scene.getObjectByName("heatmap").geometry.vertices;
  var newPlan = that.scene.getObjectByName("heatmap").geometry;
  for (let i = 0; i < posi.length; i++) {
    newPlan.vertices[i].z =
      heatmapInstance.getValueAt({ x: i % 256, y: Math.trunc(i / 256) }) /
      (256 * 2);
  }

  upFlag = false;
  return;
  var points = [];
  var max = 0;
  var width = 500;
  var height = 500;
  var len = 100;

  while (len--) {
    var val = Math.floor(Math.random() * 100);

    max = Math.max(max, val);
    var point = {
      x: Math.floor(Math.random() * width),
      y: Math.floor(Math.random() * height),
      value: val,
    };
    points.push(point);
  }

  // heatmap data format
  var data = {
    max: max,
    data: points,
  };

  // if you have a set of datapoints always use setData instead of addData
  // for data initialization
  heatmapInstance.setData(data);

  var posi = that.scene.getObjectByName("heatmap").geometry.vertices;
  var newPlan = that.scene.getObjectByName("heatmap").geometry;
  for (let i = 0; i < posi.length; i++) {
    newPlan.vertices[i].z =
      heatmapInstance.getValueAt({ x: i % 256, y: Math.trunc(i / 256) }) /
      (256 * 2);
  }
}
