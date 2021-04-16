package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;
import javafx.stage.Stage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        System.out.println("path========" + path);
        if ("/workbench/transaction/add.do".equals(path)) {
             add(req,resp);
        }else if ("/workbench/transaction/getCustomerName.do".equals(path)){
             getCustomerName(req,resp);
        }else if ("/workbench/transaction/save.do".equals(path)){
            save(req,resp);
        }else if ("/workbench/transaction/detail.do".equals(path)){
            detail(req,resp);
        }else if ("/workbench/transaction/getHistoryListByTranId.do".equals(path)){
            getHistoryListByTranId(req,resp);
        }else if ("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(req,resp);
        }else if ("/workbench/transaction/getCharts.do".equals(path)){
            getCharts(req,resp);
        }
    }
    //交易添加功能
    private void add(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        UserService userService=(UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users=userService.getUserList();
        request.setAttribute("uList",users);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }
    //取得客户名称列表,根据客户名称模糊查询
    private void getCustomerName(HttpServletRequest request,HttpServletResponse response){
        String name=request.getParameter("name");
        CustomerService cs=(CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> sList=cs.getCustomerName(name);
        PrintJson.printJsonObj(response,sList);
    }
    //保存交易记录
    private void save(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String id=UUIDUtil.getUUID();
        String owner=request.getParameter("owner");
        String money=request.getParameter("money");
        String name=request.getParameter("name");
        String expectedDate=request.getParameter("expectedDate");
        String customerName=request.getParameter("customerName");
        String stage=request.getParameter("stage");
        String type=request.getParameter("type");
        String source=request.getParameter("source");
        String activityId=request.getParameter("activityId");
        String contactsId=request.getParameter("contactsId");
        String createBy=((User)request.getSession().getAttribute("user")).getName();
        String createTime=DateTimeUtil.getSysTime();
        String description=request.getParameter("description");
        String contactSummary=request.getParameter("contactSummary");
        String nextContactTime=request.getParameter("nextContactTime");

        Tran t=new Tran();
        t.setId(id);
        t.setOwner(owner);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        t.setActivityId(activityId);
        t.setContactsId(contactsId);
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);

        TranService service=(TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag=service.save(t,customerName);
        if (flag){
            //保存成功，跳转到首页
            response.sendRedirect("workbench/transaction/index.html");
        }
    }
    //交易记录的详细页面
    private void detail(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        String id=request.getParameter("id");
        TranService service=(TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran t=service.detail(id);
        //获取可能性
        String stage=t.getStage();
        Map<String,String> map=(Map<String, String>) this.getServletContext().getAttribute("pMap");
        String possibility=map.get(stage);
        request.setAttribute("possibility",possibility);
        t.setPossibility(possibility);
        request.setAttribute("t",t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }
    //根据id取得相应的交易历史
    private void getHistoryListByTranId(HttpServletRequest request,HttpServletResponse response){
        String tranId=request.getParameter("tranId");
        TranService service=(TranService) ServiceFactory.getService(new TranServiceImpl());
        Map<String,String> pMap=(Map<String, String>) this.getServletContext().getAttribute("pMap");
        List<TranHistory> tranHistories=service.getHistoryListByTranId(tranId);
        for (TranHistory th:tranHistories){
            String stage=th.getStage();
            String possibility=pMap.get(stage);
            th.setPossibility(possibility);
        }
        response.setContentType("text/html;charset=utf-8");
        PrintJson.printJsonObj(response,tranHistories);
    }
    //改变阶段
    private void changeStage(HttpServletRequest request,HttpServletResponse response){
        String id=request.getParameter("id");
        String stage=request.getParameter("stage");
        String money=request.getParameter("money");
        String expectedDate=request.getParameter("expectedDate");
        String editTime=DateTimeUtil.getSysTime();
        String editBy=((User)request.getSession().getAttribute("user")).getName();

        Tran t=new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditTime(editTime);
        t.setEditBy(editBy);

        TranService service=(TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag=service.changeStage(t);
        Map<String,String> pMap=(Map<String, String>) this.getServletContext().getAttribute("pMap");
        t.setPossibility(pMap.get(stage));
        Map<String,Object> map=new HashMap<>();
        map.put("success",flag);
        map.put("t",t);
        response.setContentType("text/html;charset=utf-8");
        PrintJson.printJsonObj(response,map);
    }
    //表涂
    private void getCharts(HttpServletRequest request,HttpServletResponse response){
        TranService service=(TranService) ServiceFactory.getService(new TranServiceImpl());
        Map<String,Object> map=service.getCharts();
        PrintJson.printJsonObj(response,map);
    }
}