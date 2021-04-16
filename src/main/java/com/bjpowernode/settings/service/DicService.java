package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.List;
import java.util.Map;

public interface DicService {
    Map<String, List<DicValue>> getAll();
}
