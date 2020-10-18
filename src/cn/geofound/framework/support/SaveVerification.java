package cn.geofound.framework.support;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.geofound.common.utils.StringUtils;

/**
 * 沉降点添加验证
 * @author yangshuaishuai
 * @date 2018-1-19 下午1:27:58
 */
public class SaveVerification {
	/**
	 * 首位不能为零
	 * @param str
	 * @return
	 */
	public static Boolean isFirstNotZero(String str){
		// 编译正则表达式
	    Pattern pattern = Pattern.compile("/^[^0]\\w{0,}$/");
	    // 忽略大小写的写法
	    // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(str);
	    // 字符串是否与正则表达式相匹配
	    boolean rs = matcher.matches();
	    return rs;
	}
	/**
	 * 匹配数值后面可以有几个小数点   默认一个
	 */
	public static Boolean isDecimalPoint(String str,String Number){
		// 编译正则表达式
		StringBuffer expression = new StringBuffer("/^-?[0-9]+(.{0,1}[0-9]{0,");
		if(StringUtils.isNotBlank(Number)){
			expression.append(Number);
		}else{
			expression.append("1");
		}
		expression.append("})$/");
	    Pattern pattern = Pattern.compile(expression.toString());
	    // 忽略大小写的写法
	    // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(str);
	    // 字符串是否与正则表达式相匹配
	    boolean rs = matcher.matches();
	    return rs;
	}
	/**
	 * 非负数
	 */
	public static Boolean isNonNegativeNumber(String str){
		// 编译正则表达式
		Pattern pattern = Pattern.compile("/^[0-9]+(.{0,1}[0-9]{0,})$/");
		// 忽略大小写的写法
		// Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		// 字符串是否与正则表达式相匹配
		boolean rs = matcher.matches();
		return rs;
	}
	/**
	 * 数字
	 * @param str
	 * @return
	 */
	public static Boolean isTwoDigits(String str){
		// 编译正则表达式
		Pattern pattern = Pattern.compile("/^-?[0-9]+(.{0,1}[0-9]{0,1})$/");
		// 忽略大小写的写法
		// Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		// 字符串是否与正则表达式相匹配
		boolean rs = matcher.matches();
		return rs;
	}
	public static Boolean isDouble(String str){
		try{
		Double.parseDouble(str);
		return true;
		}
		catch(Exception ex){
		return false;
		}
	}
	public static Boolean isInteger(String str){
		try{
			Integer.valueOf(str);
		return true;
		}
		catch(Exception ex){
		return false;
		}
	}
	public static Boolean isDate(String str,String pattern){
		try{
			SimpleDateFormat dateFormater1 = new SimpleDateFormat(pattern);
			dateFormater1.parse(str);
			return true;
		}
		catch(Exception ex){
		return false;
		}
	}
	
	//查看字符串是否含有非法字符
	public static Boolean isIllegalCharacters(String str){
		String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
	     Pattern p = Pattern.compile(regEx);
	     Matcher m = p.matcher(str);
	     return m.find();
	}
	
}
