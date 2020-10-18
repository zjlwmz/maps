package cn.geofound.technology.service;

import org.nutz.lang.util.NutMap;

/**
 * @author zhangjialu
 * @date 2020/7/12 9:10 下午
 */
public interface DataService {

    NutMap getDataInfo(String uid);


    boolean saveDatavizConfig(String uid, String config);
}
