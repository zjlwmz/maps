package cn.geofound.common.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Distance {
	 private static final double EARTH_RADIUS = 6378137;
	 
	    private static double rad(double d)
	    {
	       return d * Math.PI / 180.0;
	    }
	    
	    /**
	     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
	     * @param lng1
	     * @param lat1
	     * @param lng2
	     * @param lat2
	     * @return
	     */
	    public static double GetDistance(double lng1, double lat1, double lng2, double lat2)
	    {
	       double radLat1 = rad(lat1);
	       double radLat2 = rad(lat2);
	       double a = radLat1 - radLat2;
	       double b = rad(lng1) - rad(lng2);
	       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + 
	        Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	       s = s * EARTH_RADIUS;
	       s = Math.round(s * 10000) / 10000;
	       return s;
	    }
	    
	    public static List<Date> getBetweenDates(Date begin, Date end,int dayCount) {
            List<Date> result = new ArrayList<Date>();
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(begin);
            /* Calendar tempEnd = Calendar.getInstance();
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
            tempEnd.setTime(end);
            while (tempStart.before(tempEnd)) {
                result.add(tempStart.getTime());
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }*/
	         while(begin.getTime()<=end.getTime()){
	             result.add(tempStart.getTime());
	             tempStart.add(Calendar.DAY_OF_YEAR, dayCount);
	             begin = tempStart.getTime();
	         }
	         if(dayCount != 1){
	        	 tempStart.setTime(end);
	        	 result.add(tempStart.getTime());
	         }
             return result;
	    }
	    
	    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
	        Calendar date = Calendar.getInstance();
	        date.setTime(nowTime);

	        Calendar begin = Calendar.getInstance();
	        begin.setTime(beginTime);

	        Calendar end = Calendar.getInstance();
	        end.setTime(endTime);

	        if (date.after(begin) && date.before(end)) {
	            return true;
	        } else {
	            return false;
	        }
	    }
}
