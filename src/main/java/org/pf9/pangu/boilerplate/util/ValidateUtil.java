package org.pf9.pangu.boilerplate.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

    private final static String regexEmail = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
    private final static String regexPhone = "^((\\d+[0-9\\-]*\\d+)|(0))$";
    private final static String regexMobile = "((\\d{6,13})|(0))";
    private final static String regexZip = "[1-9]\\d{5}";
    private final static String regexInteger = "\\d+";
    private final static String regexEnglish = "[A-Za-z]+";
    private final static String regexNonChinese = "[^\u0391-\uFFE5]+"; //全部由非汉字的字符组成，就是非汉字



    /**
     * 判断指定的字符串是否符合某个正则表达式
     * @param content 字符串
     * @param regex 正则表达式
     * @param caseSentivite 是否大小写敏感，true区分大小写，false不区分
     * @return 符合返回true，否则返回false
     */
    public static boolean isMatchString(String content,String regex, boolean caseSentivite){
        boolean result = false;
        if(null == content || null == regex){
            throw new NullPointerException("error,content or regex is null");
        }

        Pattern pattern = null;
        if(!caseSentivite){
            pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        }else{
            pattern = Pattern.compile(regex);
        }

        Matcher matcher = pattern.matcher(content);
        result = matcher.matches();

        return result;
    }

    /**
     * 判断指定的字符串是否符合某个正则表达式，大小写敏感
     * @param content 字符串
     * @param regex 正则表达式
     * @return 符合返回true，否则返回false
     */
    public static boolean isMatchString(String content,String regex){
        return isMatchString(content,regex,true);
    }

    /**
     *
     * isEmail： 验证是否为电子邮件
     *
     * @param content: 验证内容
     * @return 验证结果
     *
     */
    public static boolean isEmail(String content){
        return isMatchString(content, regexEmail);
    }

    /**
     *
     * isPhone： 验证是否为固定电话
     *
     * @param content: 验证内容
     * @return 验证结果
     *
     */
    public static boolean isPhone(String content){
        return isMatchString(content, regexPhone);
    }

    public static boolean isYesNo(String content){
        return "0".equals(content) || "1".equals(content);
    }

    /**
     *
     * isMobile： 验证是否为移动电话
     *
     * @param content: 验证内容
     * @return 验证结果
     *
     */
    public static boolean isMobile(String content){
        return isMatchString(content, regexMobile);
    }

    /**
     *
     * isZip： 验证是否为邮编
     *
     * @param content: 验证内容
     * @return 验证结果
     *
     */
    public static boolean isZip(String content){
        return isMatchString(content, regexZip);
    }

    /**
     *
     * isInteger： 验证是否为整数
     *
     * @param content: 验证内容
     * @return 验证结果
     *
     */
    public static boolean isInteger(String content){
        return isMatchString(content, regexInteger);
    }

    /**
     *
     * isEnglish： 验证是否为英文字母
     *
     * @param content: 验证内容
     * @return 验证结果
     *
     */
    public static boolean isEnglish(String content){
        return isMatchString(content, regexEnglish);
    }

    /**
     *
     * containsChinese： 验证是否包含汉字
     *
     * @param content: 验证内容
     * @return 验证结果
     *
     */
    public static boolean containsChinese(String content) {
        for (int i = 0; i < content.length(); i++) {
            int c = (int) content.charAt(i);
            if (c >= 0x0391 && c <= 0xFFE5) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * isDouble： 验证是否为浮点数
     *
     * @param content: 验证内容
     * @return 验证结果
     *
     */
    public static boolean isDouble(String content){
        try {
            Double.parseDouble(content);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //excel 中使用double表示日期的
    public static boolean isExcelDate(String content){
        return isDouble(content);
    }

    //截取电话号码
    public static String filterPhone(String strPhone) {
        String result = strPhone;
        int iIndex = strPhone.indexOf("-");

        // 规则C：判断是否有符号'-'，有的话，则截取后面的数据
        if (iIndex != -1)
            result = strPhone.substring(iIndex + 1);

        int iLength = result.length();

        // 规则A：判断是否长度为7或8，如果是，则不做任何处理返回
        if (iLength == 7 || iLength == 8)
            return result;

        // 规则B：
        if (iLength > 8) {

            if (result.charAt(0) == '0') {
                // 规则B.a 如果为013、015、018开头，截掉第一位
                String strTemp = result.substring(0, 3);
                if (strTemp.equals("013") || strTemp.equals("015")
                        || strTemp.equals("018") || strTemp.equals("014")) {
                    return result.substring(1);
                }
                // 规则B.b 非a且为02，01开头,截掉前3位
                strTemp = result.substring(0, 2);
                if (strTemp.equals("01") || strTemp.equals("02")) {
                    return result.substring(3);
                } else
                    // 规则B.c 非a、b，则截掉前4位
                    return result.substring(4);

            } else
                // 直接获取
                return result;
        }

        return result;
    }






    //验证字符串中是否包含字母
    public static boolean matchLetter(String str){
        boolean isLetter=false;
        for(int i=0;i<str.length();i++){
            if(Character.isLetter(str.charAt(i))){
                isLetter=true;
            }
        }
        return isLetter;
    }
    //验证字符串中是否包含数字
    public static boolean matchDigit(String str){
        boolean isNumber=false;
        for(int i=0;i<str.length();i++){
            if(Character.isDigit(str.charAt(i))){
                isNumber=true;
            }
        }
        return isNumber;
    }

    /**
     * 字符串是否包含汉字  在unix下可用
     * @param str
     * @return
     */
    public static boolean isCN(String str){
        String regex = "[0-9a-zA-Z_-]+$";//只允许数字,字母,下划线和-
        //String regex = "[u4e00-u9fa5]";//不允许输中文
        if(str.matches(regex)){
            return false;
        }else{
            return true;
        }
    }



    /**
     * 过滤HTML标签
     * @param html
     * @return
     */
    public static String HtmlFilter(String html){
        Pattern p_html;
        Matcher m_html;
        String regEx_html = "<.*?>|&nbsp;|&ldquo;|&rdquo;"; //定义HTML标签的正则表达式
        p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        m_html = p_html.matcher(html);
        html = m_html.replaceAll(""); //过滤html标签
        return html.trim();
    }



}