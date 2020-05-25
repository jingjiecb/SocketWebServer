package DAO;

/**
 * 用户数据访问接口
 *
 */
public interface UserDao {

    /**
     * 添加用户方法
     * @param userName 用户名
     * @param passwd 密码
     * @return 如果添加成功返回ture；否则返回false（已有重复的用户名会添加失败）
     */
    boolean addUser(String userName,String passwd);

    /**
     * 查询用户密码是否匹配
     * @param userName 用户名
     * @param passwd 密码
     * @return 如果用户名不存在或者卡密不匹配，返回false；否则返回true
     */
    boolean isMatch(String userName,String passwd);

}
