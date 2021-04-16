package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.settings.dao.DicTypeDao;
import com.bjpowernode.crm.settings.dao.DicValueDao;
import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {
    private DicTypeDao dicTypeDao= SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao= SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);
    //获取所有的dic表信息
    @Override
    public Map<String, List<DicValue>> getAll() {
        Map<String,List<DicValue>> map=new HashMap<>();
        //获取字典类型的所有信息
        List<DicType> dicTypes=dicTypeDao.getTypeList();
        for (DicType dic:dicTypes){
            //获得每个类型的字典类型编码
            String code=dic.getCode();
            //根据字典类型编码来查询字典数据
            List<DicValue> dicValues=dicValueDao.getListByCode(code);
            map.put(code+"List",dicValues);
        }
        return map;
    }
}
