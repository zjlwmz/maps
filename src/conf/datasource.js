var ioc = {
	custom : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : ["custom.properties","geoserver.properties"]                 
		}
	},
	dataSource:{
		type:"com.alibaba.druid.pool.DruidDataSource",
		events:{
			depose:"close"
		},
		fields: {
			maxWait: 15000, // 若不配置此项,如果数据库未启动,druid会一直等可用连接,卡住启动过程
			defaultAutoCommit: false, // 提高fastInsert的性能
			url: "jdbc:postgresql://localhost:5432/dmap",
			username: "postgres",
			password: "post"
			// url: "jdbc:postgresql://39.106.68.85:5432/g-map",
			// username: "postgres",
			// password: "postykbzjl918"
		}
	},
	dao : {
		type : "org.nutz.dao.impl.NutDao",
		args : [{refer:'dataSource'}]
	},
	dataSource2:{
		type:"com.alibaba.druid.pool.DruidDataSource",
		events:{
			depose:"close"
		},
		fields:{
			maxWait: 15000, // 若不配置此项,如果数据库未启动,druid会一直等可用连接,卡住启动过程
			defaultAutoCommit : false, // 提高fastInsert的性能
			url: "jdbc:postgresql://localhost:5432/dmap",
			username: "postgres",
			password: "post"
		}
	},
	dao2 : {
		type : "org.nutz.dao.impl.NutDao",
		args : [{refer:'dataSource2'}]
	},
	utils : {
        type : 'cn.geofound.common.MyUtils',       
        fields : {         
            sc : {app:'$servlet'}
        }  
	},
	tmpFilePool : {   
        type : 'org.nutz.filepool.NutFilePool',     // 临时文件最大个数为 1000 个  ，0表示不限制个数     
        args : [ {java:'$utils.getPath("/upload")'}, 0]      
	},
	uploadPicFileContext : {   
       type : 'org.nutz.mvc.upload.UploadingContext',       
       singleton : false,       
       args : [ { refer : 'tmpFilePool' } ],  
       fields : {
           // 是否忽略空文件, 默认为 false
           ignoreNull : true,
           // 单个文件最大尺寸(大约的值，单位为字节，即 10485760 为 10M)
           maxFileSize : 20485760,
           // 正则表达式匹配可以支持的文件名
           nameFilter : '^(.+[.])(gif|jpg|png|apk|ipa|xlsx|xls)$'
       }
    },  
    myUpload : {
       type : 'org.nutz.mvc.upload.UploadAdaptor',
       singleton : false,
       args : [ { refer : 'uploadPicFileContext' } ]  
    },
    jedisPoolConfig:{
    	type:'redis.clients.jedis.JedisPoolConfig',
    	fields:{
    		maxIdle:300,
    		maxTotal:60000,
    		testOnBorrow:true
    	}
    }
};