package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    private ActivityDao dao= SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao remarkDao=SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao=SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public boolean save(Activity activity) {
        boolean flag=false;
        int result=dao.save(activity);
        if (result>0){
            flag=true;
        }
        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        //取得total
        int total=dao.getTotalByCondition(map);
        //取得dataList
        List<Activity> activityList=dao.getActivityListByCondition(map);
        //将结果传到vo对象中
        PaginationVO<Activity> vo=new PaginationVO<>();
        vo.setTotal(total);
        vo.setDataList(activityList);
        return vo;
    }
    //删除市场活动
    @Override
    public boolean delete(String[] ids) {
        boolean flag=true;
        //查询出需要删除的行数
        int count=remarkDao.getCountByAids(ids);
        //删除备注返回一个受影响的行数
        int count2=remarkDao.deleteByAids(ids);
        if (count!=count2){
            flag=false;
        }
        //删除市场活动
        int result=dao.delete(ids);
        //判断是否删除干净
        if (result!= ids.length){
            flag=false;
        }
        return flag;
    }
    //修改市场活动的查询功能
    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        //取userList
        List<User> users=userDao.getUserList();
        //根据id取Activity
        Activity activity=dao.getById(id);
        //将数据放入到map集合中最后返回
        Map<String,Object> map=new HashMap<>();
        //和前端的key值保持一致
        map.put("uList",users);
        map.put("a",activity);
        return map;
    }
    //修改市场活动的更新功能
    @Override
    public boolean update(Activity activity) {
        boolean flag=false;
        int result=dao.update(activity);
        if (result>0){
            flag=true;
        }
        return flag;
    }
    //市场活动的detail页面
    @Override
    public Activity detail(String id) {
        Activity a=dao.detail(id);
        return a;
    }
    //市场活动的备注功能
    @Override
    public List<ActivityRemark> getRemarkListByAid(String activityId) {
        List<ActivityRemark> activityRemarkList=remarkDao.getRemarkListByAid(activityId);
        return activityRemarkList;
    }
    //市场活动备注的删除功能
    @Override
    public boolean deleteRemark(String id) {
        boolean flag=true;
        int count=remarkDao.deleteByid(id);
        if (count!=1){
            flag=false;
        }
        return flag;
    }
    //市场活动备注添加功能
    @Override
    public boolean saveRemark(ActivityRemark ar) {
        boolean flag=true;
        int count=remarkDao.saveRemark(ar);
        if (count!=1){
            flag=false;
        }
        return flag;
    }
    //市场活动更新功能
    @Override
    public boolean updateRemark(ActivityRemark ar) {
        boolean flag=true;
        int count=remarkDao.updateRemark(ar);
        if (count!=1){
            flag=false;
        }
        return flag;
    }
    //根据线索id查询关联的市场活动列表
    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> activityList=dao.getActivityListByClueId(clueId);
        return activityList;
    }
    //关联市场活动添加功能中的查询功能
    @Override
    public List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map) {
        List<Activity> activityList=dao.getActivityListByNameAndNotByClueId(map);
        return activityList;
    }
    //线索转换的搜索功能
    @Override
    public List<Activity> getActivityListByName(String aname) {
        List<Activity> activityList=dao.getActivityListByName(aname);
        return activityList;
    }
}
