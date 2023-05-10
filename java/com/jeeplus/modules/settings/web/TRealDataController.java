package com.jeeplus.modules.settings.web;

import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.modules.settings.dao.TRealDataDao;
import com.jeeplus.modules.settings.entity.TRealData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Administrator on 2018-08-15.
 */
@Controller
@RequestMapping(value = "/api/realData")
public class TRealDataController {
    @Autowired
    private TRealDataDao realDataDao;

    @ResponseBody
    @RequestMapping(value = "/selectRealById")
    public AjaxJson selectRule(String id){
        AjaxJson json = new AjaxJson();
        try{
            TRealData data = realDataDao.get(id);
            json.setSuccess(true);
            json.put("data",data);
            json.setErrorCode("0");
        }catch (Exception e){
            json.setSuccess(false);
            json.setMsg(e.getMessage());
        }
        return json;
    }
}
