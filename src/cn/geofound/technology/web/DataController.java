package cn.geofound.technology.web;

import cn.geofound.common.utils.StringUtils;
import cn.geofound.framework.base.Result;
import cn.geofound.technology.service.DataService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import java.util.concurrent.ExecutionException;

/**
 * @author zhangjialu
 * @date 2020/7/12 9:08 下午
 */
@IocBean
@At("/s/")
public class DataController {

    private static final Log log = Logs.get();

    @Inject
    private DataService dataService;

    /**
     * 获取数据
     * @param uid
     * @return
     */
    @At("data/query")
    @Ok("json")
    public Object getDataInfo(String uid){
        try{
            if(StringUtils.isBlank(uid)){
                return Result.error("uid不能为空");
            }
            NutMap nutMap = dataService.getDataInfo(uid);
            return Result.success("ok",nutMap);
        }catch (Exception e){
            log.error("查询异常", e);
            return Result.error();
        }
    }


    /**
     * 可视化配置保存
     * @param uid
     * @param config
     * @return
     */
    @At("dataviz/config/save")
    @Ok("json")
    public Object saveDatavizConfig(String uid,String config){
        try{
            if(StringUtils.isBlank(uid)){
                return Result.error("uid 不能为空");
            }
            if(StringUtils.isBlank(config)){
                return Result.error("config 不能为空");
            }
            boolean flag = dataService.saveDatavizConfig(uid,config);
            if(flag){
                return Result.success("ok");
            }
            return Result.error("error");
        }catch (Exception e){
            log.error("查询异常", e);
            return Result.error();
        }
    }

}
