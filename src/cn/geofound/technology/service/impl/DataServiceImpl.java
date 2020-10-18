package cn.geofound.technology.service.impl;

import cn.geofound.common.utils.GPSUtil;
import cn.geofound.technology.dao.BasicDao;
import cn.geofound.technology.entity.UserData;
import cn.geofound.technology.entity.UserDataConfig;
import cn.geofound.technology.entity.UserDataFields;
import cn.geofound.technology.service.DataService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangjialu
 * @date 2020/7/12 9:14 下午
 */
@IocBean
public class DataServiceImpl implements DataService {

    @Inject
    private BasicDao basicDao;



    public UserDataConfig getDataConfig(String uid){
        UserDataConfig userDataConfig =  basicDao.findByCondition(UserDataConfig.class, Cnd.NEW().and("uid","=",uid));
        if(null == userDataConfig){
            return  basicDao.find("0",UserDataConfig.class);
        }
        return userDataConfig;
    }


    public List<List<Object>>getRows(String uid){
        List<UserDataFields> userDataFieldsList = this.getfields(uid);
        String fieldsRow = this.getUserDataFieldsJoin(userDataFieldsList);

        String sqlstr = "select "+ fieldsRow +",st_asgeojson(the_geom_webmercator) from data.t_"+uid;
        Sql sql= Sqls.create(sqlstr);
        sql.setCallback(Sqls.callback.maps());
        sql.setEntity(basicDao.getDao().getEntity(NutMap.class));
        basicDao.getDao().execute(sql);
        List<NutMap>list= sql.getList(NutMap.class);
        List<List<Object>>listRows = new ArrayList<List<Object>>();
        for(NutMap nutMap:list){
            List<Object> info = new ArrayList<Object>();
            for(UserDataFields userDataField:userDataFieldsList){
                String fieldName = userDataField.getName();
                String filedType = userDataField.getType();
                if(filedType == "double") {
                    info.add(nutMap.getDouble(fieldName));
                }else if(filedType == "real"){
                    info.add(nutMap.getFloat(fieldName));
                }else if(filedType == "long"){
                    info.add(nutMap.getLong(fieldName));
                }else if(filedType == "integer" || filedType == "smallint"){
                    info.add(nutMap.getInt(fieldName));
                }else if(filedType == "boolean"){
                    info.add(nutMap.getBoolean(fieldName));
                }else if(filedType == "array" || filedType == "jsonb"){
                    info.add(nutMap.get(fieldName));
                }else if(filedType == "character" || filedType == "text"){
                    info.add(nutMap.getString(fieldName));
                }else if(filedType == "timestamp" || filedType.contains("time")){
                    info.add(nutMap.getTime(fieldName));
                }else{
                    info.add(nutMap.get(fieldName));
                }
            }

            String st_asgeojson = nutMap.getString("st_asgeojson");
            JSONObject asgeojsonObject = JSON.parseObject(st_asgeojson);
            if("Point".equals(asgeojsonObject.getString("type"))){
               JSONArray coordinatesList = asgeojsonObject.getJSONArray("coordinates");
               double x = coordinatesList.getDoubleValue(0);
               double y = coordinatesList.getDoubleValue(1);
               double xy[] = GPSUtil.mercator2lonLat(x,y);
                info.add(xy[0]);
                info.add(xy[1]);
            }

            listRows.add(info);
        }
        return listRows;
    }

    public List<UserDataFields> getfields(String uid){
        List<UserDataFields> userDataFieldsList = basicDao.search(UserDataFields.class,Cnd.NEW().and("uid","=",uid));
        return userDataFieldsList;
    }

    public String getUserDataFieldsJoin(List<UserDataFields> userDataFields){
        StringBuffer fieldsBuffere = new StringBuffer();
        for(UserDataFields userDataField:userDataFields){
            fieldsBuffere.append("\""+userDataField.getName()+"\"").append(",");
        }
        String fieldsRow = fieldsBuffere.toString();
        if(userDataFields.size()>0){
            fieldsRow = fieldsRow.substring(0,fieldsRow.length()-1);
        }
        return fieldsRow;
    }


    public List<Double> getfitBounds(String uid){
        List<Double> fitBounds = new ArrayList<Double>();
        UserData userData = basicDao.findByCondition(UserData.class,Cnd.NEW().and("uid","=",uid));
        String extent = userData.getExtent();
        JSONArray extentList = JSON.parseArray(extent);



        double minx = extentList.getDoubleValue(0);
        double miny = extentList.getDoubleValue(1);
        double minxy []= GPSUtil.mercator2lonLat(minx,miny);
        fitBounds.add(minxy[0]);
        fitBounds.add(minxy[1]);

        double maxx = extentList.getDoubleValue(2);
        double maxy = extentList.getDoubleValue(3);
        double maxxy []= GPSUtil.mercator2lonLat(maxx,maxy);
        fitBounds.add(maxxy[0]);
        fitBounds.add(maxxy[1]);
        return fitBounds;
    }


    @Override
    public NutMap getDataInfo(String uid) {
        NutMap nutMap = new NutMap();

        List<Double> fitBounds = this.getfitBounds(uid);
        nutMap.put("fitBounds",fitBounds);

        NutMap datasets = new NutMap();
        NutMap dataNutMap = new NutMap();
        List<List<Object>> rowsList = this.getRows(uid);
        dataNutMap.put("rows",rowsList);

        List<UserDataFields> fieldsList = this.getfields(uid);
        fieldsList.add(new UserDataFields("x","double"));
        fieldsList.add(new UserDataFields("y","double"));
        dataNutMap.put("fields",fieldsList);


        NutMap infoNutMap = new NutMap();
        infoNutMap.put("id",uid);
        datasets.put("data",dataNutMap);
        datasets.put("info",infoNutMap);
        nutMap.put("datasets",datasets);


        UserDataConfig  userDataConfig = this.getDataConfig(uid);
        String config = userDataConfig.getConfig().toString();
        nutMap.put("config",JSON.parseObject(config));

        return nutMap;
    }

    @Override
    public boolean saveDatavizConfig(String uid, String config) {
        basicDao.getDao().clear(UserDataConfig.class,Cnd.NEW().and("uid","=",uid));
        UserDataConfig userDataConfig  = new  UserDataConfig();
        userDataConfig.setUid(uid);
        userDataConfig.setConfig(config);
        basicDao.save(userDataConfig);
        return true;
    }

}
