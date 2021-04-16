package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private UserDao dao= SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    @Override
    public User login(String loginAct, String password) throws LoginException {
        Map<String,Object> map=new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",password);
        User user=dao.login(map);
        System.out.println("=================="+map);
        //处理错误信息
        if (user==null){
            throw new LoginException("账号或密码错误");
        }
        //验证失效时间
        String expireTime=user.getExpireTime();
        String time= DateTimeUtil.getSysTime();
        if (expireTime.compareTo(time)<0){
            throw new LoginException("登录时间失效");
        }
        //验证状态
        String lockState=user.getLockState();
        if ("0".equals(lockState)){
            throw new LoginException("账户已锁定");
        }
        return user;
    }

    @Override
    public List<User> getUserList() {
        List<User> userList=dao.getUserList();
        return userList;
    }
}
