package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.ArrayList;
import java.util.List;

public class ClueServiceImpl implements ClueService {
    private ClueDao clueDao= SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao=SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao=SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    private CustomerDao customerDao=SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao=SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    private ContactsDao contactsDao=SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao=SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao=SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    private TranDao tranDao=SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao=SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    //线索的保存功能
    @Override
    public boolean save(Clue clue) {
        boolean flag=true;
        int count=clueDao.save(clue);
        if (count!=1){
            flag=false;
        }
        return flag;
    }
    //线索的详细信息页面
    @Override
    public Clue detail(String id) {
        Clue c=clueDao.detail(id);
        return c;
    }
    //接触市场活动关联关系
    @Override
    public boolean unbund(String id) {
        boolean flag=true;
        int count=clueActivityRelationDao.unbund(id);
        if (count!=1){
            flag=false;
        }
        return flag;
    }
    //关联市场活动的添加功能
    @Override
    public boolean bund(String cid, String[] aids) {
        boolean flag=true;
        for (String aid:aids){
            ClueActivityRelation relation=new ClueActivityRelation();
            relation.setId(UUIDUtil.getUUID());
            relation.setActivityId(aid);
            relation.setClueId(cid);
            int count=clueActivityRelationDao.bund(relation);
            if (count!=1){
                flag=false;
            }
        }
        return flag;
    }
    //线索的转换功能

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {
        boolean flag=true;
        String createTime= DateTimeUtil.getSysTime();
        Clue clue=clueDao.getById(clueId);
        //判断公司是否已存在
        String company= clue.getCompany();
        Customer customer=customerDao.getCustomerByName(company);
        if (customer==null){
            customer=new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setAddress(clue.getAddress());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setOwner(clue.getOwner());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setName(company);
            customer.setDescription(clue.getDescription());
            customer.setCreateTime(createTime);
            customer.setCreateBy(createBy);
            customer.setContactSummary(clue.getContactSummary());

            int count=customerDao.save(customer);
            if(count!=1){
                flag=false;
            }
        }
        //提取联系人信息，保存联系人
        Contacts contacts=new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setFullname(clue.getFullname());
        contacts.setEmail(clue.getEmail());
        contacts.setDescription(clue.getDescription());
        contacts.setCustomerId(customer.getId());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAppellation(clue.getAppellation());
        contacts.setAddress(clue.getAddress());
        //添加联系人
        int count2=contactsDao.save(contacts);
        if (count2!=1){
            flag=false;
        }
        //将线索备注转换到相应的表中
        List<ClueRemark> clueRemarks=clueRemarkDao.getListByClueId(clueId);
        for (ClueRemark clueRemark:clueRemarks){
            //取出备注信息
            String noteContent=clueRemark.getNoteContent();
            CustomerRemark customerRemark=new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(noteContent);
            int count3=customerRemarkDao.save(customerRemark);
            if (count3!=1){
                flag=false;
            }
            ContactsRemark contactsRemark=new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setContactsId(contacts.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(noteContent);
            int count4=contactsRemarkDao.save(contactsRemark);
            if (count4!=1){
                flag=false;
            }

        }
        //查询出与该线索关联的市场活动
        List<ClueActivityRelation> clueActivityRelations=clueActivityRelationDao.getListByClueId(clueId);
        for (ClueActivityRelation clueActivityRelation:clueActivityRelations){
            String activityId=clueActivityRelation.getActivityId();

            ContactsActivityRelation contactsActivityRelation=new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(contacts.getId());

            int count5=contactsActivityRelationDao.save(contactsActivityRelation);
            if (count5!=1){
                flag=false;
            }
        }
        //如果有创建交易需求，创建一条交易
        if (t!=null){
            t.setSource(clue.getSource());
            t.setOwner(clue.getOwner());
            t.setNextContactTime(clue.getNextContactTime());
            t.setDescription(clue.getDescription());
            t.setCustomerId(customer.getId());
            t.setContactSummary(clue.getContactSummary());
            t.setContactsId(contacts.getId());
            int count6=tranDao.save(t);
            if (count6!=1){
                flag=false;
            }
            //如果创建了交易，创建一条交易历史
            TranHistory th=new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setCreateBy(createBy);
            th.setCreateTime(createTime);
            th.setExpectedDate(t.getExpectedDate());
            th.setMoney(t.getMoney());
            th.setStage(t.getStage());
            th.setTranId(t.getId());

            int count7=tranHistoryDao.save(th);
            if (count7!=1){
                flag=false;
            }
        }
       //删除线索备注
        for (ClueRemark clueRemark:clueRemarks){
            int count8=clueRemarkDao.delete(clueRemark);
            if (count8!=1){
                flag=false;
            }
        }
        //删除线索和市场活动的关系
        for (ClueActivityRelation clueActivityRelation:clueActivityRelations){
            int count9=clueActivityRelationDao.delete(clueActivityRelation);
            if (count9!=1){
                flag=false;
            }
        }
        //删除线索
        int count10=clueDao.delete(clueId);
        if (count10!=1){
            flag=false;
        }
        return flag;
    }
}
