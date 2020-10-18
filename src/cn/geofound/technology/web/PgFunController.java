package cn.geofound.technology.web;

import cn.geofound.technology.entity.PgFun;
import cn.geofound.technology.service.PgFunService;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import java.util.List;

/**
 * @author zhangjialu
 * @date 2020/4/19 10:04 下午
 */
@IocBean
@At("/pg/fun/")
public class PgFunController {

    @Inject
    private PgFunService pgFunService;


    @At("/list")
    @Ok("json")
    public Object findList(){
       List<PgFun> pgFunList = pgFunService.query(Cnd.NEW());
       return pgFunList;
    }
}
