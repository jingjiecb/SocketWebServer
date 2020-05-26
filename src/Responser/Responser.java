package Responser;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 应答报文发送器 接口
 */
abstract public class Responser {

    //资源基地址
    protected static final String BASEURL="http://127.0.0.1:9000";

    //资源根目录
    protected static final String BASEPATH = "/root/web";

    protected OutputStream outputStream;

    /**
     * 发送应答
     * @return 成功则返回true，否则返回false
     */
    abstract public boolean send() throws Exception;

    /**
     * 发送仅包含一个状态码的简单应答
     * @param codeAndText 状态码和描述
     * @throws Exception 一切异常
     */
    protected void sendCodeAndText(String codeAndText) throws Exception{
        PrintStream writer = new PrintStream(outputStream);
        writer.println("HTTP/1.1 "+codeAndText);
        writer.println("Content-Type:text/plain");
        writer.println("Content-Length:"+codeAndText.length());
        writer.println();
        //发送响应体
        writer.print(codeAndText);
        writer.flush();
        writer.close();
    }
}
