package com.jeeplus.modules.starnet.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.modules.starnet.entity.ElecUnitStrategy;
import com.jeeplus.modules.starnet.service.ElecUnitStrategyService;
import com.jeeplus.modules.sys.entity.Dict;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "star/elecUnitStrategy")
public class ElecUnitStrategyController {

	@Autowired
	ElecUnitStrategyService elecUnitStrategyService;
	
	//用电单位策略列表
	@RequestMapping("/list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response,ElecUnitStrategy eus) {
		Page<ElecUnitStrategy> page = elecUnitStrategyService.findPage(new Page(request, response), eus);
		return ServletUtils.buildRs(true, "用电单位策略列表", page);
	}
	
	
	//用电单位策略添加
	@RequestMapping("/add")
	@ResponseBody
	public String addElecUnitStrategy(ElecUnitStrategy eus) {
		User currentUser = UserUtils.getUser();
		eus.setOperator(currentUser.getId());
		if(eus.getOprType().equals("1")) {
			eus.setRealValue(eus.getValue());
		} else {
			eus.setRealValue(-eus.getValue());
		}
		
		elecUnitStrategyService.add(eus);
		return ServletUtils.buildRs(true, "用电单位策略添加成功", null);	
	}
	
	//用电单位策略修改
	@RequestMapping("/update")
	@ResponseBody
	public String updateElecUnitStrategy(ElecUnitStrategy eus) {
		User currentUser = UserUtils.getUser();
		eus.setOperator(currentUser.getId());
		if(eus.getOprType().equals("1")) {
			eus.setRealValue(eus.getValue());
		} else {
			eus.setRealValue(-eus.getValue());
		}
		
		elecUnitStrategyService.update(eus);
		return ServletUtils.buildRs(true, "用电单位策略修改成功", null);	
	}
	
	//用电单位策略删除
	@RequestMapping("/del")
	@ResponseBody
	public String delElecUnitStrategy(ElecUnitStrategy eus) {
		elecUnitStrategyService.delete(eus);
		return ServletUtils.buildRs(true, "用电单位策略删除成功", null);	
	}
	
}
