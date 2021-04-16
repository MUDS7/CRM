package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {
    private TranDao tranDao= SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao=SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao=SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    //建议添加任务
    @Override
    public boolean save(Tran t, String customerName) {
        boolean flag=true;
        //查询客户存不存在，若不存在执行添加操作
        Customer cus=customerDao.getCustomerByName(customerName);
        if (cus==null){
           cus=new Customer();
           cus.setId(UUIDUtil.getUUID());
           cus.setName(customerName);
           cus.setCreateBy(t.getCreateBy());
           cus.setCreateTime(DateTimeUtil.getSysTime());
           cus.setContactSummary(t.getContactSummary());
           cus.setNextContactTime(t.getNextContactTime());
           cus.setOwner(t.getOwner());
           int count1=customerDao.save(cus);
           if (count1!=1){
               flag=false;
           }
        }
        //添加交易
        t.setCustomerId(cus.getId());
        int count2=tranDao.save(t);
        if (count2!=1){
            flag=false;
        }
        //添加交易历史
        TranHistory th=new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setCreateBy(t.getCreateBy());
        int count3=tranHistoryDao.save(th);
        if (count3!=1){
            flag=false;
        }
        return flag;
    }
    //交易记录的详细页面
    @Override
    public Tran detail(String id) {
        Tran t=tranDao.detail(id);
        return t;
    }
    //根据id取得相应的交易历史
    @Override
    public List<TranHistory> getHistoryListByTranId(String tranId) {
        List<TranHistory> tranHistories=tranHistoryDao.getHistoryListByTranId(tranId);
        return tranHistories;
    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag=true;
        //改变交易阶段
        int count1=tranDao.changeStage(t);
        if (count1!=1){
            flag=false;
        }
        //改变交易历史
        TranHistory th=new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setTranId(t.getId());
        int count2=tranHistoryDao.save(th);
        if (count2!=1){
            flag=false;
        }
        return flag;
    }
    //获取图标信息
    @Override
    public Map<String, Object> getCharts() {
        int total=tranDao.getTotal();
        List<Map<String,Object>> mapList=tranDao.getCharts();
        Map<String,Object> map=new HashMap<>();
        map.put("total",total);
        map.put("mapList",mapList);
        return map;
    }
}
