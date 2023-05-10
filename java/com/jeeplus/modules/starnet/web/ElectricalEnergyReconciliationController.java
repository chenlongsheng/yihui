package com.jeeplus.modules.starnet.web;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.modules.starnet.entity.ElecHistoryData;
import com.jeeplus.modules.starnet.entity.ElectricalEnergyReconciliation;
import com.jeeplus.modules.starnet.service.DataService;
import com.jeeplus.modules.starnet.service.ElecHistoryDataService;
import com.jeeplus.modules.starnet.service.ElectricalEnergyReconciliationService;
import com.jeeplus.modules.starnet.service.LoadUnitAdjustProportionService;
@Controller
@RequestMapping(value = "star/ElectricalEnergyReconciliation")
public class ElectricalEnergyReconciliationController {

	public static void main(String[] args) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		LocalDate local = LocalDate.now();//获取当前时间
		DayOfWeek dayOfWeek = local.getDayOfWeek();//获取今天是周几
		LocalDate lastMonday = local.minusDays(7+dayOfWeek.getValue()-1);//算出上周一
		LocalDate lastToday = local.minusDays(7);//算出上周今天
		
		String lastWeekStart = lastMonday.format(formatter) + " 00:00:00";
		String lastWeekToday = lastToday.format(formatter) + " 23:59:59";
		
		System.out.println("pweek1_start："+lastWeekStart);
		System.out.println("pweek1_end："+lastWeekToday);
		
		LocalDate thisMonday = local.minusDays(dayOfWeek.getValue()+1);//本周一
		
		String thisWeekStart = thisMonday.format(formatter) + " 00:00:00";
		String today = local.format(formatter) + " 23:59:59";
		
		System.out.println("thisWeekStart："+thisWeekStart);
		System.out.println("today："+today);
	}
	
	
	@Autowired
	ElectricalEnergyReconciliationService electricalEnergyReconciliationService;
	
	@Autowired
	ElecHistoryDataService elecHistoryDataService;
	
	@Autowired
	DataService dataService;
	
    @Autowired
    LoadUnitAdjustProportionService loadUnitAdjustProportionService; 
	
	/*
	 *	计算异常电量值 
	 */
	@RequestMapping("/calcExceptionElec")
	@ResponseBody
	public String calcExceptionElec(String loopId,HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		LocalDate local = LocalDate.now();//获取当前时间
		DayOfWeek dayOfWeek = local.getDayOfWeek();//获取今天是周几
		LocalDate lastMonday = local.minusDays(7+dayOfWeek.getValue()-1);//算出上周一
		LocalDate lastToday = local.minusDays(7);//算出上周今天
		
		String lastWeekStart = lastMonday.format(formatter) + " 00:00:00";
		String lastWeekToday = lastToday.format(formatter) + " 23:59:59";
		
		System.out.println("pweek1_start："+lastWeekStart);
		System.out.println("pweek1_end："+lastWeekToday);
		
		LocalDate thisMonday = local.minusDays(dayOfWeek.getValue()+1);//本周一
		
		String thisWeekStart = thisMonday.format(formatter) + " 00:00:00";
		String today = local.format(formatter) + " 23:59:59";
		
		System.out.println("thisWeekStart："+thisWeekStart);
		System.out.println("today："+today);
		
		Double lastWeekSumValue = elecHistoryDataService.getSumValueByTimeBetween(loopId,lastWeekStart,lastWeekToday);
		if(lastWeekSumValue == null)
			lastWeekSumValue = 0.0;
		
		Double thisWeekSumValue = elecHistoryDataService.getSumValueByTimeBetween(loopId,thisWeekStart,today);
		if(thisWeekSumValue == null)
			thisWeekSumValue = 0.0;
		
		
		Double diff = 0.0;
		
		if(lastWeekSumValue > thisWeekSumValue) {
			diff = lastWeekSumValue - thisWeekSumValue;
		}
		
		/*
		//====================================
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		//查找上一周的电量值
		LocalDate pweek1 = today.minusWeeks(1);
		String pweek1_start = pweek1.format(formatter);
		pweek1_start += "00:00:00";
		String pweek1_end = pweek1.format(formatter);
		pweek1_end += "23:59:59";
		
		//查找上上周的电量值
		LocalDate pweek2 =today.minusWeeks(2);
		String pweek2_start = pweek2.format(formatter);
		pweek1_start += "00:00:00";
		String pweek2_end = pweek2.format(formatter);
		pweek1_end += "23:59:59";
		
		//查找上上上周的电量值
		LocalDate pweek3 =today.minusWeeks(3);
		String pweek3_start = pweek3.format(formatter);
		pweek1_start += "00:00:00";
		String pweek3_end = pweek3.format(formatter);
		pweek1_end += "23:59:59";
		
		//查找上月4周的电量值
		LocalDate pweek4 =today.minusWeeks(4);
		String pweek4_start = pweek4.format(formatter);
		pweek1_start += "00:00:00";
		String pweek4_end = pweek4.format(formatter);
		pweek1_end += "23:59:59";
		//====================================
		*/
		
		return ServletUtils.buildRs(true, "", diff);
		
	}
	
	
	@RequestMapping("/getLoopEnergyReconciliationSum")
	@ResponseBody
	public String getLoopEnergyReconciliationSum(ElectricalEnergyReconciliation eer,HttpServletRequest request, HttpServletResponse response) {
		
		Long energySum = electricalEnergyReconciliationService.getLoopEnergyReconciliationSum(eer.getLoopId());
		
		Long moneySum = energySum;
		
		MapEntity entity = new MapEntity();
		if(energySum != null)
			entity.put("energySum", energySum);
		else
			entity.put("energySum", 0);
		if(energySum != null)
			entity.put("moneySum", moneySum);
		else
			entity.put("moneySum", 0);
		
		return ServletUtils.buildRs(true, "电能调账总额", entity);
	}

	/*
	 *	电能调账列表
	 */
	@RequestMapping("/list")
	@ResponseBody
	public String list(ElectricalEnergyReconciliation eer,HttpServletRequest request, HttpServletResponse response) {
		Page<ElectricalEnergyReconciliation> page = 
				electricalEnergyReconciliationService.findPage(new Page<ElectricalEnergyReconciliation>(request,response), eer);
		return ServletUtils.buildRs(true, "电能调账列表", page);
	}
	
	
	/*
	 *	电能调账删除
	 */
	@RequestMapping("/del")
	@ResponseBody
	public String del(ElectricalEnergyReconciliation eer,HttpServletRequest request, HttpServletResponse response) {
		electricalEnergyReconciliationService.delete(eer);
		
		
		return ServletUtils.buildRs(true, "删除电能调账", "");
	}
	
	/*
	 * 电能调账
	 */
	@RequestMapping("/add")
	@ResponseBody
	public String add(ElectricalEnergyReconciliation eer,Integer exceptionValue,HttpServletRequest request, HttpServletResponse response) {

		//电能调账
		eer.setExceptionState(1);
		eer.setMoney((long)eer.getElectricQuantity());
		if(eer.getElectricQuantity() > 0)
			eer.setOprType(1);
		else
			eer.setOprType(2);
		electricalEnergyReconciliationService.save(eer);
		
		
		//电能异常
		eer.setExceptionState(2);
		eer.setMoney((long)eer.getElectricQuantity());
		if(exceptionValue > 0)
			eer.setOprType(1);
		else
			eer.setOprType(2);
		eer.setElectricQuantity(exceptionValue);
		eer.setMoney(exceptionValue.longValue());
		eer.setIsNewRecord(true);
		electricalEnergyReconciliationService.save(eer);
		
		
		
		MapEntity channel = dataService.getEnergyChannelByOrgId(eer.getLoopId());
		Long maxId = elecHistoryDataService.getMaxElecHistoryDataIdLessThan10000000();
		if(maxId == null)
			maxId = 0L;
		
		ElecHistoryData ehd = new ElecHistoryData();
		ehd.setId(String.valueOf(maxId+1));
		ehd.setChId(channel.get("id").toString());
		ehd.setOrgId(eer.getLoopId());
		ehd.setValue((double)eer.getElectricQuantity());
		ehd.setTime(eer.getAdjustTime());
		elecHistoryDataService.add(ehd);
		
		
		return ServletUtils.buildRs(true, "调账成功", "");
	}
	
}
