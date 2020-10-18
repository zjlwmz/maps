package cn.geofound.common.utils.title;

public class TitleUtils {

	
	/**
	 * 瓦片转换经度
	 * @param x
	 * @param z
	 * @return
	 */
	public static double tile2lon(int x, int z) {
		return x / Math.pow(2.0, z) * 360.0 - 180;
	}

	
	/**
	 * 瓦片转换纬度
	 * @param y
	 * @param z
	 * @return
	 */
	public static double tile2lat(int y, int z) {
		double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
		return Math.toDegrees(Math.atan(Math.sinh(n)));
	}

	
	/**
	 * 瓦片获得范围
	 * @param x
	 * @param y
	 * @param zoom
	 * @return
	 */
	public static BoundingBox tile2boundingBox(final int x, final int y, final int zoom) {
		BoundingBox bb = new BoundingBox();
		bb.north = tile2lat(y, zoom);// 北 ymax
		bb.south = tile2lat(y + 1, zoom);//南 ymin
		bb.west = tile2lon(x, zoom);//西  xmin
		bb.east = tile2lon(x + 1, zoom);//东 xmax
		return bb;
	}

}
