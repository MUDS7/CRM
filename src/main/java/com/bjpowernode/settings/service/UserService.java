package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String loginAct, String password) throws LoginException;

    List<User> getUserList();
}
