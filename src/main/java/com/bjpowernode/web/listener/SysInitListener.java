package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.workbench.service.ActivityService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext application=servletContextEvent.getServletContext();
        DicService dicService= (DicService) ServiceFactory.getService(new DicServiceImpl());
        //获取所有的线索信息
        Map<String, List<DicValue>> map=dicService.getAll();
        //将map存为attribute
        Set<String> set= map.keySet();
        for (String key:set){
            application.setAttribute(key,map.get(key));
        }
        Map<String,String> pMap=new HashMap<>();
        ResourceBundle rb=ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> e=rb.getKeys();
        while (e.hasMoreElements()){
            String key=e.nextElement();
            String value=rb.getString(key);
            pMap.put(key,value);
        }
        //将pMap放到服务器缓存中
        application.setAttribute("pMap",pMap);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
