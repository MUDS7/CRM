package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path=request.getServletPath();
        if ("/settings/user/login.do".equals(path)){
            login(request,response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path=request.getServletPath();
        if ("/settings/user/login.do".equals(path)){
            login(request,response);
        }
    }
    /*protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path=request.getServletPath();
        if ("/settings/user/login.do".equals(path)){
            login(request,response);
        }
    }*/

    //处理登录操作
    private void login(HttpServletRequest request, HttpServletResponse response) {
        //获取参数
        String loginAct=request.getParameter("loginAct");
        String loginPwd=request.getParameter("loginPwd");
        //将密码MD5加密
        String password= MD5Util.getMD5(loginPwd);
        //使用代理类形态的接口对象
//        UserService service=(UserService) ServiceFactory.getService(new UserServiceImpl());0
        UserService service=new UserServiceImpl();
        try{
            User user=service.login(loginAct,password);
            System.out.println(user);
            request.getSession().setAttribute("user",user);
            PrintJson.printJsonFlag(response,true);
        }catch (Exception e){
            e.printStackTrace();
            //自定义错误信息
            String msg=e.getMessage();
            Map<String,Object> map=new HashMap<>();
            map.put("success",false);
            map.put("msg",msg);
            PrintJson.printJsonObj(response,map);
        }
    }
}
