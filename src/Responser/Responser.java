package Responser;

/**
 * 应答报文发送器 接口
 */
public interface Responser {

    /**
     * 发送应答
     * @return 成功则返回true，否则返回false
     */
    boolean send() throws Exception;

}
