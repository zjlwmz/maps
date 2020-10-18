package cn.geofound.technology.support;

/**
 * jedis缓存key值对照关系
 * @author zhangjialu
 * @date 2018-9-13上午9:25:17
 */
public class JedisKeyUtils {

	/**
	 * 定位gps数据 list 集合
	 */
	public static final String locationGpsList="location:gps:list";
	
	/**
	 * 接收gps日志数据 list 集合
	 */
	public static final String locationGpsLogList="location:gpslog:list";
	
	
	
	/**
	 * 蓝牙设备 list 集合
	 */
	public static final String locationIbeaconList="location:ibeacon:list";
	/**
	 * 蓝牙设备 map 集合
	 */
	public static final String locationIbeaconMap="location:ibeacon:map";
	
	
	
	/**
	 * 停机坪 区域 list 集合
	 */
	public static final String locationParkingapronList="location:parkingapron:list";
	
	/**
	 * 停机坪 区域 map 集合
	 */
	public static final String locationParkingapronMap="location:parkingapron:map";
	
	
	
	/**
	 * 定位终端设备 setlist 集合
	 */
	public static final String locationTerminalSet="location:terminal:set";
	
	/**
	 * 定位终端设备 map  定位即时数据 集合
	 */
	public static final String locationTerminalRealTimeData="location:terminal:realtimedata";

	
	
	
	
	
	//-----------------------------------------------------------------------------------------
	
	
	
	
	/**
	 * 公司信息 set 集合
	 */
	public static final String locationCompanySet="location:company:set";
	
	/**
	 * 公司信息 map 集合
	 */
	public static final String locationCompanyMap="location:company:map";
	
	
	/**
	 * 车辆set 集合
	 */
	public static final String locationVehicleSet="location:vehicle:set";
	
	/**
	 * 车辆map集合
	 */
	public static final String locationVehicleMap="location:vehicle:map";
	
	
	
	/**
	 * 车辆类型 set 集合
	 */
	public static final String locationVehicltypeSet="location:vehicletype:set";
	
	/**
	 * 车辆类型 map 集合
	 */
	public static final String locationVehicltypeMap="location:vehicletype:map";
	
	
	/**
	 * 超速预警状态 map 集合
	 * 记录超速终端 和区域
	 * location:warning:sw:set 终端编号  \ 区域id s
	 */
	public static final String locationVehicleSwSet="location:warning:sw:set";
	/**
	 * 超速预警状态 map 集合
	 * location:warning:sw:map_终端编编号_区域ID
	 */
	public static final String locationVehicleSwDataMap="location:warning:sw:map";
	
	/**
	 * 电子围栏预警状态 map集合
	 */
	public static final String locationVehicleEfMap="location:warning:ef:map";
	
	
	
	/**
	 * 用户待展示预警信息 list 集合
	 */
	public static final String locationWarningSwUserList="location:warning:sw:user:list";
	
	
	/**
	 * 用户待展示电子围栏预警信息  list 集合
	 */
	public static final String locationWarningEfUserList="location:warning:ef:user:list";
	
	
	/**
	 * 用户未读消息总数
	 */
	public static final String locationWarningUserUmCountString="location:warning:user:umcount:string";
	
}
