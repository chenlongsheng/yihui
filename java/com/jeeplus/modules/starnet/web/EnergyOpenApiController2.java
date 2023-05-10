package com.jeeplus.modules.starnet.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.modules.starnet.service.ElectricityUnitService;
import com.jeeplus.modules.starnet.service.EnergyOpenApiService;

@Controller
@RequestMapping(value = "openApi")
public class EnergyOpenApiController2 {

	@Autowired
	EnergyOpenApiService energyOpenApiService;

    @RequestMapping(value = { "getDeviceDetails" })
    @ResponseBody
    public String getDeviceDetails() {
        
        List<MapEntity> list = energyOpenApiService.getDeviceDetails();
        return ServletUtils.buildRs(true, "电表详情", list);
        
    }


	
}
