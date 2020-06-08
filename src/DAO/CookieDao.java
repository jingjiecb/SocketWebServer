package DAO;

/**
 * Cookie 浏览器储存的有效cookie信息对象
 * 这是一个Cookie对象数据访问接口
 *
 */
public interface CookieDao {

    /**
     * 判断Cookie是否有效
     * @param cookieStr 服务端接受到的请求报文头中的Cookie字段。格式形如： "username=admin"
     * @return 如果此Cookie字段存在且时效未过（暂定1分钟），就返回true。否则返回false
     * 注意！如果是有效的Cookie，应该刷新最后一次使用时间。不再向外部提供刷新时间接口。
     */
    boolean isValid(String cookieStr);

    /**
     * 添加一个用户认证Cookie
     * @param userName 用户名。
     * @return 返回Cookie字段的字符串，格式形如 "username=admin"
     * 新Cookie时效暂定1分钟
     */
    String getNewCookie(String userName);

    /**
     * 使一个Cookie失效
     * @param cookieStr 服务端接受到的请求报文头中的Cookie字段。格式形如： "username=admin"
     * @return 如果找到对应cookie并成功删除则返回true，没找到cookie则返回false
     */
    boolean disableCookie(String cookieStr);

}
