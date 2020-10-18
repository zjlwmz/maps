/*
 *    Copyright 2012-2013 The Haohui Network Corporation
 */
package cn.geofound.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @project baidamei
 * @author cevencheng <cevencheng@gmail.com> www.zuidaima.com
 * @create 2012-11-15 下午4:54:42
 */
public class RegexUtils {

	/**
	 * 匹配图象
	 * 
	 * 
	 * 格式: /相对路径/文件名.后缀 (后缀为gif,dmp,png)
	 * 
	 * 匹配 : /forum/head_icon/admini2005111_ff.gif 或 admini2005111.dmp
	 * 
	 * 
	 * 不匹配: c:/admins4512.gif
	 * 
	 */
	public static final String ICON_REGEXP = "^(/{0,1}//w){1,}//.(gif|dmp|png|jpg)$|^//w{1,}//.(gif|dmp|png|jpg)$";

	/**
	 * 匹配email地址
	 * 
	 * 
	 * 格式: XXX@XXX.XXX.XX
	 * 
	 * 匹配 : foo@bar.com 或 foobar@foobar.com.au
	 * 
	 * 不匹配: foo@bar 或 $$$@bar.com
	 * 
	 */
	public static final String EMAIL_REGEXP = "(?://w[-._//w]*//w@//w[-._//w]*//w//.//w{2,3}$)";

	/**
	 * 匹配匹配并提取url
	 * 
	 * 
	 * 格式: XXXX://XXX.XXX.XXX.XX/XXX.XXX?XXX=XXX
	 * 
	 * 匹配 : http://www.suncer.com 或news://www
	 * 
	 * 不匹配: c:/window
	 * 
	 */
	public static final String URL_REGEXP = "(//w+)://([^/:]+)(://d*)?([^#//s]*)";

	/**
	 * 匹配并提取http
	 * 
	 * 格式: http://XXX.XXX.XXX.XX/XXX.XXX?XXX=XXX 或 ftp://XXX.XXX.XXX 或
	 * https://XXX
	 * 
	 * 匹配 : http://www.suncer.com:8080/index.html?login=true
	 * 
	 * 不匹配: news://www
	 * 
	 */
	public static final String HTTP_REGEXP = "(http|https|ftp)://([^/:]+)(://d*)?([^#//s]*)";

	/**
	 * 匹配日期
	 * 
	 * 
	 * 格式(首位不为0): XXXX-XX-XX或 XXXX-X-X
	 * 
	 * 
	 * 范围:1900--2099
	 * 
	 * 
	 * 匹配 : 2005-04-04
	 * 
	 * 
	 * 不匹配: 01-01-01
	 * 
	 */
	public static final String DATE_BARS_REGEXP = "^((((19){1}|(20){1})\\d{2})|\\d{2})-[0,1]?\\d{1}-[0-3]?\\d{1}$";

	/**
	 * 匹配日期
	 * 
	 * 
	 * 格式: XXXX/XX/XX
	 * 
	 * 
	 * 范围:
	 * 
	 * 
	 * 匹配 : 2005/04/04
	 * 
	 * 
	 * 不匹配: 01/01/01
	 * 
	 */
	public static final String DATE_SLASH_REGEXP = "^[0-9]{4}/(((0[13578]|(10|12))/(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)/(0[1-9]|[1-2][0-9]|30)))$";

	/**
	 * 匹配电话
	 * 
	 * 
	 * 格式为: 0XXX-XXXXXX(10-13位首位必须为0) 或0XXX XXXXXXX(10-13位首位必须为0) 或
	 * 
	 * (0XXX)XXXXXXXX(11-14位首位必须为0) 或 XXXXXXXX(6-8位首位不为0) 或
	 * XXXXXXXXXXX(11位首位不为0)
	 * 
	 * 
	 * 匹配 : 0371-123456 或 (0371)1234567 或 (0371)12345678 或 010-123456 或
	 * 010-12345678 或 12345678912
	 * 
	 * 
	 * 不匹配: 1111-134355 或 0123456789
	 * 
	 */
	public static final String PHONE_REGEXP = "^(?:0[0-9]{2,3}[-//s]{1}|//(0[0-9]{2,4}//))[0-9]{6,8}$|^[1-9]{1}[0-9]{5,7}$|^[1-9]{1}[0-9]{10}$";

	/**
	 * 匹配身份证
	 * 
	 * 格式为: XXXXXXXXXX(10位) 或 XXXXXXXXXXXXX(13位) 或 XXXXXXXXXXXXXXX(15位) 或
	 * XXXXXXXXXXXXXXXXXX(18位)
	 * 
	 * 匹配 : 0123456789123
	 * 
	 * 不匹配: 0123456
	 * 
	 */
	public static final String ID_CARD_REGEXP = "^//d{10}|//d{13}|//d{15}|//d{18}$";

	/**
	 * 匹配邮编代码
	 * 
	 * 格式为: XXXXXX(6位)
	 * 
	 * 匹配 : 012345
	 * 
	 * 不匹配: 0123456
	 * 
	 */
	public static final String ZIP_REGEXP = "^[0-9]{6}$";// 匹配邮编代码

	/**
	 * 不包括特殊字符的匹配 (字符串中不包括符号 数学次方号^ 单引号' 双引号" 分号; 逗号, 帽号: 数学减号- 右尖括号> 左尖括号< 反斜杠/
	 * 即空格,制表符,回车符等 )
	 * 
	 * 格式为: x 或 一个一上的字符
	 * 
	 * 匹配 : 012345
	 * 
	 * 不匹配: 0123456 // ;,:-<>//s].+$";//
	 */
	public static final String NON_SPECIAL_CHAR_REGEXP = "^[^'/";
	// 匹配邮编代码

	/**
	 * 匹配非负整数（正整数 + 0)
	 */
	public static final String NON_NEGATIVE_INTEGERS_REGEXP = "^//d+$";

	/**
	 * 匹配不包括零的非负整数（正整数 > 0)
	 */
	public static final String NON_ZERO_NEGATIVE_INTEGERS_REGEXP = "^[1-9]+//d*$";

	/**
	 * 
	 * 匹配正整数
	 * 
	 */
	public static final String POSITIVE_INTEGER_REGEXP = "^[0-9]*[1-9][0-9]*$";

	/**
	 * 
	 * 匹配非正整数（负整数 + 0）
	 * 
	 */
	public static final String NON_POSITIVE_INTEGERS_REGEXP = "^((-//d+)|(0+))$";

	/**
	 * 
	 * 匹配负整数
	 * 
	 */
	public static final String NEGATIVE_INTEGERS_REGEXP = "^-[0-9]*[1-9][0-9]*$";

	/**
	 * 
	 * 匹配整数
	 * 
	 */
	public static final String INTEGER_REGEXP = "^-?//d+$";

	/**
	 * 
	 * 匹配非负浮点数（正浮点数 + 0）
	 * 
	 */
	public static final String NON_NEGATIVE_RATIONAL_NUMBERS_REGEXP = "^//d+(//.//d+)?$";

	/**
	 * 
	 * 匹配正浮点数
	 * 
	 */
	public static final String POSITIVE_RATIONAL_NUMBERS_REGEXP = "^(([0-9]+//.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*//.[0-9]+)|([0-9]*[1-9][0-9]*))$";

	/**
	 * 
	 * 匹配非正浮点数（负浮点数 + 0）
	 * 
	 */
	public static final String NON_POSITIVE_RATIONAL_NUMBERS_REGEXP = "^((-//d+(//.//d+)?)|(0+(//.0+)?))$";

	/**
	 * 
	 * 匹配负浮点数
	 * 
	 */
	public static final String NEGATIVE_RATIONAL_NUMBERS_REGEXP = "^(-(([0-9]+//.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*//.[0-9]+)|([0-9]*[1-9][0-9]*)))$";

	/**
	 * 
	 * 匹配浮点数
	 * 
	 */
	public static final String RATIONAL_NUMBERS_REGEXP = "^(-?//d+)(//.//d+)?$";

	/**
	 * 
	 * 匹配由26个英文字母组成的字符串
	 * 
	 */
	public static final String LETTER_REGEXP = "^[A-Za-z]+$";

	/**
	 * 
	 * 匹配由26个英文字母的大写组成的字符串
	 * 
	 */
	public static final String UPWARD_LETTER_REGEXP = "^[A-Z]+$";

	/**
	 * 
	 * 匹配由26个英文字母的小写组成的字符串
	 * 
	 */
	public static final String LOWER_LETTER_REGEXP = "^[a-z]+$";

	/**
	 * 
	 * 匹配由数字和26个英文字母组成的字符串
	 * 
	 */
	public static final String LETTER_NUMBER_REGEXP = "^[A-Za-z0-9]+$";

	/**
	 * 
	 * 匹配由数字、26个英文字母或者下划线组成的字符串
	 * 
	 */
	public static final String LETTER_NUMBER_UNDERLINE_REGEXP = "^//w+$";

	
	
	
	/**
	 * 匹配金额
	 * 1.非负整数输入，如0、100等
	 * 2.两位小数的非负浮点数输入
	 * 
	 */
	public static final String money = "^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$";
	
	/**
	 * 验证Email
	 * 
	 * @param email
	 *            email地址，格式：zhangsan@zuidaima.com，zhangsan@xxx.com.cn，
	 *            xxx代表邮件服务商
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkEmail(String email) {
		String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
		return Pattern.matches(regex, email);
	}

	/**
	 * 验证身份证号码
	 * 
	 * @param idCard
	 *            居民身份证号码15位或18位，最后一位可能是数字或字母
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkIdCard(String idCard) {
		String regex = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";
		return Pattern.matches(regex, idCard);
	}

	/**
	 * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
	 * 
	 * @param mobile
	 *            移动、联通、电信运营商的号码段
	 *            <p>
	 *            移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
	 *            、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）
	 *            </p>
	 *            <p>
	 *            联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）
	 *            </p>
	 *            <p>
	 *            电信的号段：133、153、180（未启用）、189
	 *            </p>
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkMobile(String mobile) {
		String regex = "(\\+\\d+)?1[3458]\\d{9}$";
		return Pattern.matches(regex, mobile);
	}

	/**
	 * 验证固定电话号码
	 * 
	 * @param phone
	 *            电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
	 *            <p>
	 *            <b>国家（地区） 代码 ：</b>标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9
	 *            的一位或多位数字， 数字之后是空格分隔的国家（地区）代码。
	 *            </p>
	 *            <p>
	 *            <b>区号（城市代码）：</b>这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号——
	 *            对不使用地区或城市代码的国家（地区），则省略该组件。
	 *            </p>
	 *            <p>
	 *            <b>电话号码：</b>这包含从 0 到 9 的一个或多个数字
	 *            </p>
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkPhone(String phone) {
		String regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
		return Pattern.matches(regex, phone);
	}

	/**
	 * 验证整数（正整数和负整数）
	 * 
	 * @param digit
	 *            一位或多位0-9之间的整数
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkDigit(String digit) {
		String regex = "\\-?[1-9]\\d+";
		return Pattern.matches(regex, digit);
	}

	/**
	 * 验证整数和浮点数（正负整数和正负浮点数）
	 * 
	 * @param decimals
	 *            一位或多位0-9之间的浮点数，如：1.23，233.30
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkDecimals(String decimals) {
		String regex = "\\-?[1-9]\\d+(\\.\\d+)?";
		return Pattern.matches(regex, decimals);
	}

	/**
	 * 验证空白字符
	 * 
	 * @param blankSpace
	 *            空白字符，包括：空格、\t、\n、\r、\f、\x0B
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkBlankSpace(String blankSpace) {
		String regex = "\\s+";
		return Pattern.matches(regex, blankSpace);
	}

	/**
	 * 验证中文
	 * 
	 * @param chinese
	 *            中文字符
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkChinese(String chinese) {
		String regex = "^[\u4E00-\u9FA5]+$";
		return Pattern.matches(regex, chinese);
	}

	/**
	 * 验证日期（年月日）
	 * 
	 * @param birthday
	 *            日期，格式：1992-09-03，或1992.09.03
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkBirthday(String birthday) {
		String regex = "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
		return Pattern.matches(regex, birthday);
	}

	/**
	 * 验证URL地址
	 * 
	 * @param url
	 *            格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或
	 *            http://www.csdn.net:80
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkURL(String url) {
		String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
		return Pattern.matches(regex, url);
	}

	/**
	 * <pre>
	 * 获取网址 URL 的一级域名
	 * http://www.zuidaima.com/share/1550463379442688.htm ->> zuidaima.com
	 * </pre>
	 * 
	 * @param url
	 * @return
	 */
	public static String getDomain(String url) {
		Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
		// 获取完整的域名
		// Pattern
		// p=Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)",
		// Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(url);
		matcher.find();
		return matcher.group();
	}

	/**
	 * 匹配中国邮政编码
	 * 
	 * @param postcode
	 *            邮政编码
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkPostcode(String postcode) {
		String regex = "[1-9]\\d{5}";
		return Pattern.matches(regex, postcode);
	}

	/**
	 * 匹配IP地址(简单匹配，格式，如：192.168.1.1，127.0.0.1，没有匹配IP段的大小)
	 * 
	 * @param ipAddress
	 *            IPv4标准地址
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkIpAddress(String ipAddress) {
		String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
		return Pattern.matches(regex, ipAddress);
	}

	/**
	 * 大小写敏感的正规表达式批配
	 * 
	 * @param source
	 *            批配的源字符串
	 * @param regexp
	 *            批配的正规表达式
	 * @return 如果源字符串符合要求返回真,否则返回假
	 */
	public static boolean isHardRegexpValidate(String source, String regexp) {

		return Pattern.matches(regexp, source);
	}
	
	public static void main(String[] args) {
		boolean result=isHardRegexpValidate("100", money);
		System.out.println(result);
		
		boolean result2=isHardRegexpValidate("11111111111", "^1\\d{10}$");
		System.out.println(result2);
		
	}

}
