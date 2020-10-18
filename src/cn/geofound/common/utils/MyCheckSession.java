package cn.geofound.common.utils;

import javax.servlet.http.HttpSession;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.ServerRedirectView;

@IocBean
public class MyCheckSession implements ActionFilter{

	private static final Log log = Logs.get();
	
	
	@Override
	public View match(ActionContext actionContext) {
		log.debug("start");
		HttpSession session = Mvcs.getHttpSession();
		if(session.getAttribute("user") == null){
			return new ServerRedirectView("/login");
		}
		log.debug("end");
		return null;
	}

}
