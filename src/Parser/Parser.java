package Parser;

/**
 * parser实例接收char[]形式的http报文的内容，可以对其进行解析，得到有价值的信息。
 */
public class Parser {

    //请求的每一行
    private String[] lines;

    /**
     * 构造方法
     * @param request 接受请求内容，构造解析器
     */
    public Parser(char[] request){
        String requestStr=String.valueOf(request).replaceAll("[\r\u0000]","");
        // System.out.println("total length: "+requestStr.length());
        this.lines=requestStr.split("\n");
    }

    /**
     * 获得访问资源地址，位于头部第一行
     * @return 资源地址
     */
    public String getPath() {
        return lines[0].split(" ")[1];
    }

    /**
     * 获得Cookie，位于头部某行内
     * @return Cookie内容
     */
    public String getCookie() {
        for (String line : lines) {
            if (line.startsWith("Cookie:")) return line.substring(8);
        }
        return null;
    }

    /**
     * 获得请求体
     * @return 请求体内容
     */
    public String getContent() {
        int i;
        for (i=0;i<lines.length;i++){
            if (lines[i].length()<=1) break;
        }
        i++;

        StringBuilder res= new StringBuilder();

        for (;i<lines.length;i++){
            res.append(lines[i]);
            res.append('\n');
        }
        res.deleteCharAt(res.length()-1);

        return res.toString();
    }


    //测试方法
    public void print(){
        for (int i=0;i<lines.length;i++){
            System.out.println(""+(i+1)+"  "+lines[i]);
            // System.out.println("  "+lines[i].length());
        }
    }
}
