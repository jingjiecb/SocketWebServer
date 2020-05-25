package DAO;

import java.util.HashMap;
import java.util.Map;

public class UserDaoImpl implements UserDao {
    private Map<String, String> users;

    private static UserDaoImpl userDaoInstance=new UserDaoImpl();

    public static UserDaoImpl getUserDaoInstance() {
        return userDaoInstance;
    }

    private UserDaoImpl() {
        users = new HashMap<>();
        users.put("user1", "123456");
        users.put("user2", "hahaha");
    }

    @Override
    public boolean addUser(String userName, String passwd) {
        if (users.containsKey(userName)) {
            return false;//用户名已存在
        } else {
            users.put(userName, passwd);
            return true;
        }
    }


    @Override
    public boolean isMatch(String userName, String passwd) {
        if (users.containsKey(userName)) {
            return users.get(userName).equals(passwd);//用户存在且密码匹配
        }
        return false;//用户不存在或存在但密码不匹配
    }
}
