package cn.geofound.common.utils.title;

public class BoundingBox {
	
	/**
	 * 北方 ymax
	 */
	public double north;
	
	/**
	 * 南方 ymin
	 */
	public double south;
	
	/**
	 * 东方 xmax
	 */
	public double east;
	
	
	/**
	 * 西方 xmin
	 */
	public double west;
	
	public BoundingBox(){
		super();
	}
	
	
	public BoundingBox(double xmin,double ymin,double xmax,double ymax){
		this.west = xmin;
		this.south = ymin;
		this.east = xmax;
		this.north = ymax;
	}
	
	
	
}
