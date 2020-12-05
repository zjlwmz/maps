/*!
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 * 项目自定义的公共JavaScript，可覆盖jeesite.js里的方法
 */
var Utils = {
    /**
     * form表达转json
     * @param form
     */
    formToJson:function (form) {
        var paramArray = $(form).serializeArray();
        /*请求参数转json对象*/
        var jsonObj={};
        $(paramArray).each(function(){
            jsonObj[this.name]=this.value;
        });
        return jsonObj;
    },
    datetimeFormatUtil:function(longTypeDate){
        /*
         * 时间格式化工具
         * 把Long类型的yyyy-MM-dd 00:00:00日期还原yyyy-MM-dd 00:00:00格式日期
         */
        var dateTypeDate = "";
        var date = new Date();
        date.setTime(longTypeDate);
        dateTypeDate += date.getFullYear();   //年
        dateTypeDate += "-" + Utils.getMonth(date); //月
        dateTypeDate += "-" + Utils.getDay(date);   //日
        dateTypeDate += " " + Utils.getHours(date);   //时
        dateTypeDate += ":" + Utils.getMinutes(date);		//分
        dateTypeDate += ":" + Utils.getSeconds(date);		//分
        return dateTypeDate;
    },
    dateFormatUtil:function(longTypeDate){
        /*
         * 时间格式化工具
         * 把Long类型的yyyy-MM-dd日期还原yyyy-MM-dd格式日期
         */
        var dateTypeDate = "";
        var date = new Date();
        date.setTime(longTypeDate);
        dateTypeDate += date.getFullYear();   //年
        dateTypeDate += "-" + Utils.getMonth(date); //月
        dateTypeDate += "-" + Utils.getDay(date);   //日
        return dateTypeDate;
    },
    getMonth:function(date){
        //返回 01-12 的月份值
        var month = "";
        month = date.getMonth() + 1; //getMonth()得到的月份是0-11
        if(month<10){
            month = "0" + month;
        }
        return month;
    },
    getDay:function(date){
    //返回01-30的日期
        var day = "";
        day = date.getDate();
        if(day<10){
            day = "0" + day;
        }
        return day;
    },

    getHours:function(date){
        //小时
        var hours = "";
        hours = date.getHours();
        if(hours<10){
            hours = "0" + hours;
        }
        return hours;
    },
    getMinutes:function(date){
        //分
        var minute = "";
        minute = date.getMinutes();
        if(minute<10){
            minute = "0" + minute;
        }
        return minute;
    },
    getSeconds:function(date){
        //秒
        var second = "";
        second = date.getSeconds();
        if(second<10){
            second = "0" + second;
        }
        return second;
    },
    /**
     * 唯一id
     * @returns {string}
     */
    genID:function () {
        var length = 32;
        return Number(Math.random().toString().substr(3,length) + Date.now()).toString(36);
    },
    /**
     * list查询
     * @param list
     * @param key 查询的键
     * @param val 查询的值
     */
    findByListKey:function(list,key,val) {
        for (var i = 0; i < list.length; i++) {
            if(val==list[i][key]){
                return list[i];
            }
        }
        return null;
    },
    /**
     * 初始化dom对象值
     * @param list
     */
    initDomData:function(list) {
        for(var i in list) {
            $("#"+i).val(list[i]);
        }
    },/**
     * 初始化dom对象值
     * @param list
     */
    initDomDataByData:function(list,data) {
        for(var i in list) {
            $("#"+i).val(data[list[i]]);
        }
    },/**
     * 初始化dom对象值
     * @param list
     */
    initDomDataNull:function(list) {
        for(var i in list) {
            $("#"+i).val("");
        }
    },
    dataGrid:{
        /**
         * datagrid添加一行
         * @param domid
         * @param data
         */
        addRow:function (domid,data) {
            $("#"+domid).jqGrid("addRowData", data.id, data, "last");
        },
        /**
         * datagrid 加载数据
         * @param domid
         * @param dataList
         */
        reloadData:function (domid,dataList) {
            $("#"+domid).setGridParam({data: dataList}).trigger('reloadGrid');
        },
        /**
         * datagrid删除一行
         * @param domid
         * @param id
         */
        delRow:function (domid,id) {
            $("#"+domid).jqGrid("delRowData", id);
        }
    },
    initLevel:function() {
        LevelOneFunc();
        levelTowFunc();
        function LevelOneFunc() {
            var htmls = '<option value="">请选择</option>';
            for (var i = 0; i < LevelOne.length; i++) {
                htmls = htmls + '<option value="' + LevelOne[i].id + '">' + LevelOne[i].name + '</option>';
            }
            $("#LevelOne").html(htmls);
        }
        $('#LevelOne').change(function(){
            levelTowFunc();
        });
        function levelTowFunc(){
            $('#LevelTwo').html("<option value=\"\">请选择</option>");
            var parentid = $("#LevelOne").val();
            if(parentid){
                //var htmlstwo = '';
                var htmlstwo = '<option value="">请选择</option>';
                for (var i = 0; i < LevelTwo.length; i++) {
                    if(parentid == LevelTwo[i].parent) {
                        htmlstwo = htmlstwo + '<option value="' + LevelTwo[i].id + '">' + LevelTwo[i].name + '</option>';
                    }
                }
                $('#LevelTwo').html(htmlstwo);
                $("#officeId").val(parentid);
            }else{
                $("#officeId").val("");
            }
            $('#LevelTwo').trigger('change');
        }
        $('#LevelTwo').change(function(){
            if($("#LevelTwo").val()){
                $("#officeId").val($("#LevelTwo").val());
            }else{
                var parentid = $("#LevelOne").val();
                $("#officeId").val(parentid);
            }
        });
    }
};

/**
 * 数组位置
 * @param val
 * @returns {number}
 */
Array.prototype.indexOf = function (val) {
    for(var i = 0; i < this.length; i++){
        if(this[i] == val){return i;}
    }
    return -1;
};
/**
 * 数组删除
 * @param val
 */
Array.prototype.remove = function (val) {
    var index = this.indexOf(val);
    if(index > -1){this.splice(index,1);}
};
/**
 * 数组替换
 * @param val
 * @param toVal
 */
Array.prototype.replace = function (val,toVal) {
    var index = this.indexOf(val);
    if(index > -1){this.splice(index,1,toVal);}
};

