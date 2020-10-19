package cn.geofound.technology.service;


/**
 * 蓝牙定位service
 * @author zhangjialu
 * @date 2019年4月24日 下午10:15:26
 */
public interface BluetoothLocationService {

	/**
	 * 蓝牙定位
	 * @return
	 */
	String location();
	
	/**
	 * 蓝牙定位 [一个蓝牙设备]
	 * @param beacon1x 蓝牙1经度
	 * @param beacon1y蓝牙2纬度
	 * @param d1 探测人距离蓝牙的距离
	 * @param effectiveArea1 蓝牙1的有效区域geojson
	 * @return
	 */
	String location(Double beacon1x,Double beacon1y,Double d1,String effectiveArea1);
	
	/**
	 * 蓝牙定位 [两个蓝牙设备]
	 * @param beacon1x 蓝牙1经度
	 * @param beacon1y蓝牙2纬度
	 * @param d1 探测人距离蓝牙的距离
	 * @return
	 */
	String location(Double beacon1x,Double beacon1y,Double d1,String effectiveArea1,Double beacon2x,Double beacon2y,Double d2,String effectiveArea2);
	
	
	
	/**
	 * 蓝牙定位 [三个蓝牙设备]
	 * @param beacon1x 蓝牙1经度
	 * @param beacon1y蓝牙2纬度
	 * @param d1 探测人距离蓝牙的距离
	 * @return
	 */
	String location(Double beacon1x,Double beacon1y,Double d1,String effectiveArea1,Double beacon2x,Double beacon2y,Double d2,String effectiveArea2,Double beacon3x,Double beacon3y,Double d3,String effectiveArea3);
}
