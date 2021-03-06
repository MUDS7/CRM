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
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path=req.getServletPath();
        System.out.println("path========"+path);
        if ("/workbench/activity/getUserList.do".equals(path)){
            getUserList(req,resp);
        }else if ("/workbench/activity/save.do".equals(path)){
            save(req,resp);
        }else if("/workbench/activity/pageList.do".equals(path)){
            pageList(req,resp);
        }else if("/workbench/activity/delete.do".equals(path)){
            delete(req,resp);
        }else if ("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(req,resp);
        }else if ("/workbench/activity/update.do".equals(path)){
            update(req,resp);
        }else if ("/workbench/activity/detail.do".equals(path)){
            detail(req,resp);
        }else if ("/workbench/activity/getRemarkListByAid.do".equals(path)){
            getRemarkListByAid(req,resp);
        }else if ("/workbench/activity/deleteRemark.do".equals(path)){
            deleteRemark(req,resp);
        }else if ("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(req,resp);
        }else if ("/workbench/activity/updateRemark.do".equals(path)){
            updateRemark(req,resp);
        }
    }


    //????????????????????????
    public void save(HttpServletRequest request,HttpServletResponse response) {
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //?????????????????????????????????
        String createTime = DateTimeUtil.getSysTime();
        //?????????????????????????????????
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        //??????
        Activity activity=new Activity();
        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setCreateBy(createBy);
        activity.setCreateTime(createTime);
        //??????service????????????
        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag=service.save(activity);
        response.setContentType("text/html;charset=utf-8");
        PrintJson.printJsonFlag(response,flag);
    }
    //??????????????????
    public void getUserList(HttpServletRequest request,HttpServletResponse response){
        UserService service=(UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users=service.getUserList();
        PrintJson.printJsonObj(response,users);
        response.setContentType("text/html;charset=utf-8");
    }
    //????????????
    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        String name=request.getParameter("name");
        String owner=request.getParameter("owner");
        String startDate=request.getParameter("startDate");
        String endDate=request.getParameter("endDate");
        String pageNoStr=request.getParameter("pageNo");
        String pageSizeStr=request.getParameter("pageSize");
        //????????????
        Integer pageNo=Integer.parseInt(pageNoStr);
        Integer pageSize=Integer.parseInt(pageSizeStr);
        //???????????????????????????????????????
        int skipCount=(pageNo-1)*pageSize;
        //??????????????????map
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        //???????????????
        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        //???vo??????????????????????????????
        PaginationVO<Activity> vo=service.pageList(map);
        response.setContentType("text/html;charset=utf-8");
        //???vo??????json
        PrintJson.printJsonObj(response,vo);
    }
    //????????????????????????
    private void delete(HttpServletRequest request,HttpServletResponse response){
        String ids[]=request.getParameterValues("id");
        //??????service??????
        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag=service.delete(ids);
        //????????????
        PrintJson.printJsonFlag(response,flag);
    }
    //?????????????????????????????????
    private void getUserListAndActivity(HttpServletRequest request,HttpServletResponse response){
        String id=request.getParameter("id");
        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        //????????????
        Map<String,Object> map=service.getUserListAndActivity(id);
        //????????????
        response.setContentType("text/html;charset=utf-8");
        PrintJson.printJsonObj(response,map);
    }
    //?????????????????????????????????
    private void update(HttpServletRequest request,HttpServletResponse response){
        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //?????????????????????????????????
        String editTime = DateTimeUtil.getSysTime();
        //?????????????????????????????????
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        //??????
        Activity activity=new Activity();
        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setEditTime(editTime);
        activity.setEditBy(editBy);
        //??????service????????????
        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag=service.update(activity);
        response.setContentType("text/html;charset=utf-8");
        PrintJson.printJsonFlag(response,flag);
    }
    //???????????????detail??????
    private void detail(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        String id=request.getParameter("id");
        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity a=service.detail(id);
        request.setAttribute("a",a);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);
    }
    //????????????????????????
    private void getRemarkListByAid(HttpServletRequest request,HttpServletResponse response){
        String activityId=request.getParameter("activityId");
        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<ActivityRemark> activityRemarkList=service.getRemarkListByAid(activityId);
        response.setContentType("text/html;charset=utf-8");
        PrintJson.printJsonObj(response,activityRemarkList);
    }
    //???????????????????????????????????????
    private void deleteRemark(HttpServletRequest request,HttpServletResponse response){
        String id=request.getParameter("id");
        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag=service.deleteRemark(id);
        PrintJson.printJsonFlag(response,flag);
    }
    //?????????????????????????????????
    private void saveRemark(HttpServletRequest request,HttpServletResponse response){
        String noteContent=request.getParameter("noteContent");
        String activityId=request.getParameter("activityId");
        String id=UUIDUtil.getUUID();
        String createTime=DateTimeUtil.getSysTime();
        String createBy=((User)request.getSession().getAttribute("user")).getName();
        String editFlag="0";

        ActivityRemark ar=new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setActivityId(activityId);
        ar.setCreateTime(createTime);
        ar.setCreateBy(createBy);
        ar.setEditFlag(editFlag);

        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag=service.saveRemark(ar);
        Map<String,Object> map=new HashMap<>();
        map.put("success",flag);
        map.put("ar",ar);
        response.setContentType("text/html;charset=utf-8");
        PrintJson.printJsonObj(response,map);
    }
    //???????????????????????????
    private void updateRemark(HttpServletRequest request,HttpServletResponse response){
        String id=request.getParameter("id");
        String noteContent=request.getParameter("noteContent");
        String editTime=DateTimeUtil.getSysTime();
        String editBy=((User)request.getSession().getAttribute("user")).getName();
        String editFlag="1";

        ActivityRemark ar=new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditBy(editBy);
        ar.setEditTime(editTime);
        ar.setEditFlag(editFlag);

        ActivityService service=(ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag=service.updateRemark(ar);
        Map<String,Object> map=new HashMap<>();
        map.put("success",flag);
        map.put("ar",ar);
        response.setContentType("text/html;charset=utf-8");
        PrintJson.printJsonObj(response,map);
    }
}
