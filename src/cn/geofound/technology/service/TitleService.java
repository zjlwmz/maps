package cn.geofound.technology.service;

import cn.geofound.common.utils.title.BoundingBox;

/**
 * 瓦片service接口
 * @author zhangjialu
 * @date 2019年5月25日 上午9:39:55
 */
public interface TitleService {

	/**
	 * 线图层
	 * @param boundingBox
	 * @return
	 */
	Object getST_AsMVT(BoundingBox boundingBox);
	
	
	
	/**
	 * 点图层
	 * @param boundingBox
	 * @return
	 */
	Object getST_AsMVT_forPoints(BoundingBox boundingBox);
	
	
	
	
	/**
	 * 面图层
	 * @param boundingBox
	 * @return
	 */
	Object getST_AsMVT_forPolygon(BoundingBox boundingBox);
	
	
	
	
	Object getST_AsMVT_forPolygon(BoundingBox boundingBox,String layername,String dataid);
	
}
