package Parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * parser实例接收char[]形式的http报文的内容，可以对其进行解析，得到有价值的信息。
 */
public class Parser {

    //请求的每一行
    private String[] lines;


    /**
     * 构造方法
     *
     * @param request 接受请求内容，构造解析器
     */
    public Parser(char[] request) {
        String requestStr = String.valueOf(request).replaceAll("[\r\u0000]", "");
        // System.out.println("total length: "+requestStr.length());
        this.lines = requestStr.split("\n");
        // Why Not Directly .split("\r\n")????
    }

    /**
     * 得到请求的方法
     *
     * @return 请求的方法。注意是大写字母
     */
    public String getMethod() {
        return lines[0].split(" ")[0];
    }

    /**
     * 获得访问资源地址，位于头部第一行
     *
     * @return 资源地址
     */
    public String getPath() {
        return lines[0].split(" ")[1];
    }

    /**
     * 获得Cookie，位于头部某行内
     *
     * @return Cookie所有内容
     */
    public String getCookie() {
        for (String line : lines) {
            if (line.startsWith("Cookie:")) return line.substring(8);
        }
        return null;
    }

    /**
     * 获得一个指定的Cookie字段
     *
     * @param key 字段的key
     * @return 字段内容
     */
    public String getCookieByKey(String key) {
        String cookie = "";
        for (String line : lines) {
            if (line.startsWith("Cookie:")) {
                cookie = line.substring(8);
                break;
            }
        }

        String[] sessions = cookie.split(" ");
        for (String session : sessions) {
            if (session.split("=")[0].equals(key)) {
                return session;
            }
        }

        return null;
    }

    /**
     * 获得请求体
     *
     * @return 请求体内容
     */
    public String getContent() {
        int i;
        for (i = 0; i < lines.length; i++) {
            if (lines[i].length() <= 1) break;
        }
        i++;

        StringBuilder res = new StringBuilder();

        for (; i < lines.length; i++) {
            res.append(lines[i]);
            res.append('\n');
        }
        res.deleteCharAt(res.length() - 1);

        return res.toString();
    }

    public boolean hasCheckModified(){
        // check if possibly response HTTP 304
        String rawDateStr = "";
        for(String line: lines){
            if(line.startsWith("if-modified-since: ")){
                return true;
            }
        }
        return false;
    }

    public Date getModifiedDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
        Date date = null;
        try {
//            String dateString = "Thu, 25 Feb 2016 12:18:19 GMT";
            String dateString = "";
            for(String line: lines){
                if(line.startsWith("if-modified-since: ")){
                    dateString = line.substring(19);
                }
            }
            String[] dateStringSplitArr = dateString.split(" ");
            String newDateString = dateStringSplitArr[3] + parseToMM(dateStringSplitArr[2]) + dateStringSplitArr[1] + dateStringSplitArr[4];
//          Date String Example:  date = sdf.parse("Thu, 25 Feb 2016 12:18:19 GMT");
            date = sdf.parse(newDateString);
            System.out.println(date.toString());
        }catch (ParseException pe){
            System.out.println("Oops! Modified Date Parse Exception!");
        }
        return date;
    }

    private static String parseToMM(String month){
        month = month.toLowerCase();
        String MM = "-1";
        Map<String, String> monthMap = new HashMap<>();
        monthMap.put("jan", "01");
        monthMap.put("feb", "02");
        monthMap.put("mar", "03");
        monthMap.put("apr", "04");
        monthMap.put("may", "05");
        monthMap.put("jun", "06");
        monthMap.put("jul", "07");
        monthMap.put("aug", "08");
        monthMap.put("sep", "09");
        monthMap.put("oct", "10");
        monthMap.put("nov", "11");
        monthMap.put("dec", "12");
        MM = monthMap.get(month);
        return MM;
    }

    //测试方法
    public void print() {
        for (int i = 0; i < lines.length; i++) {
            System.out.println("" + (i + 1) + "  " + lines[i]);
            // System.out.println("  "+lines[i].length());
        }
        System.out.println("****************");
    }
}
