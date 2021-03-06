package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        System.out.println("path========" + path);
        if ("/workbench/clue/getUserList.do".equals(path)) {
             getUserList(req,resp);
        }else if ("/workbench/clue/save.do".equals(path)){
            save(req,resp);
        }else if ("/workbench/clue/detail.do".equals(path)){
            detail(req,resp);
        }else if ("/workbench/clue/getActivityListByClueId.do".equals(path)){
            getActivityListByClueId(req,resp);
        }else if ("/workbench/clue/unbund.do".equals(path)){
            unbund(req,resp);
        }else if ("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(path)){
            getActivityListByNameAndNotByClueId(req,resp);
        }else if ("/workbench/clue/bund.do".equals(path)){
            bund(req,resp);
        }else if ("/workbench/clue/getActivityListByName.do".equals(path)){
            getActivityListByName(req,resp);
        }else if ("/workbench/clue/convert.do".equals(path)){
            convert(req,resp);
        }

    }
    //????????????????????????
    private void getUserList(HttpServletRequest request,HttpServletResponse response){
        UserService service=(UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users=service.getUserList();
        response.setContentType("text/html;charset=utf-8");
        PrintJson.printJsonObj(response,users);
    }
    //??????????????????
    private void save(HttpServletRequest request,HttpServletResponse response){
        String id=UUIDUtil.getUUID();
        String fullname=request.getParameter("fullname");
        String appellation=request.getParameter("appellation");
        String owner=request.getParameter("owner");
        String company=request.getParameter("company");
        String job=request.getParameter("job");
        String email=request.getParameter("email");
        String phone=request.getParameter("phone");
        String website=request.getParameter("website");
        String mphone=request.getParameter("mphone");
        String state=request.getParameter("state");
        String source=request.getParameter("source");
        String createBy=((User)request.getSession().getAttribute("user")).getName();
        String createTime=DateTimeUtil.getSysTime();
        String description=request.getParameter("description");
        String contactSummary=request.getParameter("contactSummary");
        String nextContactTime=request.getParameter("nextContactTime");
        String address=request.getParameter("address");

        //??????????????????????????????
        Clue clue=new Clue();
        clue.setId(id);
        clue.setFullname(fullname);
        clue.setAppellation(appellation);
        clue.setOwner(owner);
        clue.setCompany(company);
        clue.setJob(job);
        clue.setEmail(email);
        clue.setPhone(phone);
        clue.setWebsite(website);
        clue.setMphone(mphone);
        clue.setState(state);
        clue.setSource(source);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setDescription(description);
        clue.setContactSummary(contactSummary);
        clue.setNextContactTime(nextContactTime);
        clue.setAddress(address);

        ClueService service=(ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag=service.save(clue);
        PrintJson.printJsonFlag(response,flag);
    }
    //????????????????????????
    private void detail(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        String id=request.getParameter("id");
        ClueService service=(ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue c=service.detail(id);
        request.setAttribute("c",c);
        request.getRequestDispatcher("detail.jsp").forward(request,response);
    }
    //????????????id?????????????????????????????????
    private void getActivityListByClueId(HttpServletRequest request,HttpServletResponse response){
        String clueId=request.getParameter("clueId");
        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList=service.getActivityListByClueId(clueId);
        response.setContentType("text/html;charset=utf-8");
        PrintJson.printJsonObj(response,activityList);
    }
    //????????????????????????
    private void unbund(HttpServletRequest request,HttpServletResponse response){
        String id=request.getParameter("id");
        ClueService service=(ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag=service.unbund(id);
        PrintJson.printJsonFlag(response,flag);
    }
    //????????????????????????????????????????????????
    private void getActivityListByNameAndNotByClueId(HttpServletRequest request,HttpServletResponse response){
        String aname=request.getParameter("aname");
        String clueId=request.getParameter("clueId");
        Map<String,String> map=new HashMap<>();
        map.put("aname",aname);
        map.put("clueId",clueId);
        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList=service.getActivityListByNameAndNotByClueId(map);
        response.setContentType("text/html;charset=utf-8");
        PrintJson.printJsonObj(response,activityList);
    }
    //?????????????????????????????????
    private void bund(HttpServletRequest request,HttpServletResponse response){
        String cid=request.getParameter("cid");
        String aids[]=request.getParameterValues("aid");
        ClueService service=(ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag=service.bund(cid,aids);
        PrintJson.printJsonFlag(response,flag);
    }
    //???????????????????????????
    private void getActivityListByName(HttpServletRequest request,HttpServletResponse response){
        String aname=request.getParameter("aname");
        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activity=service.getActivityListByName(aname);
        response.setContentType("text/html;charset=utf-8");
        PrintJson.printJsonObj(response,activity);
    }
    //??????????????????
    private void convert(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String clueId=request.getParameter("clueId");
        //???????????????????????????????????????
        String flag=request.getParameter("flag");
        Tran t=null;
        String createBy=((User)request.getSession().getAttribute("user")).getName();
        if ("a".equals(flag)){
            t=new Tran();
            String money=request.getParameter("money");
            String name =request.getParameter("name");
            String expectedDate=request.getParameter("expectedDate");
            String stage=request.getParameter("stage");
            String activity=request.getParameter("activity");
            String id=UUIDUtil.getUUID();
            String createTime=DateTimeUtil.getSysTime();


            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectedDate);
            t.setStage(stage);
            t.setActivityId(activity);
            t.setId(id);
            t.setCreateTime(createTime);
            t.setCreateBy(createBy);
        }
        ClueService service=(ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean result=service.convert(clueId,t,createBy);
        if (result){
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.html");
        }
    }
}