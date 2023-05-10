package com.jeeplus.modules.pdfData.service;

import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.OrgUtil;
import com.jeeplus.modules.pdfData.dao.FireFightingDao;
import com.jeeplus.modules.settings.entity.TOrg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ZZUSER on 2018/12/26.
 */
@Service
public class FireFightingService extends CrudService<FireFightingDao,TOrg> {
    @Autowired
    FireFightingDao fireFightingDao;

    @Autowired
    SecurityService securityService;

    /**
     *
     * @param orgId
     * @param type
     * @param startDate
     * @param endDate
     * @return
     */
    /*
    public Map getAllData(String orgId,int type,String startDate,String endDate){
        Map resultMap = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
//            orgId = "233993942926888973";
        }
        int savaDays = securityService.countSaveDays(orgId,type);//安全运行天数
        String countOnline = countOnline(orgId,type);//烟感在线率
        int dayAlarm = countDayAlarm(orgId,type);//当日新增报警数
        int countMonthAlarm = countMonthAlarm(orgId,type);//本月新增报警数
        List<Map> OutlineTop10 = getOutlineTop10(orgId,type);//烟感离线率top10
//        Map countElectric = countElectric(orgId,type);//烟感实时电量占比
        List<Map> alarmTop10 = alarmTop10(orgId,startDate,endDate,type);//报警次数top10
        resultMap.put("savaDays",savaDays);
        resultMap.put("countOnline",countOnline);
        resultMap.put("dayAlarm",dayAlarm);
        resultMap.put("countMonthAlarm",countMonthAlarm);
        resultMap.put("OutlineTop10",OutlineTop10);
//        resultMap.put("countElectric",countElectric);
        resultMap.put("alarmTop10",alarmTop10);
        return resultMap;
    }
    */

    public Map changeDate(String orgId,int type,String startDate,String endDate){
        Map resultMap = new HashMap();
        List<Map> alarmTop10 = alarmTop10(orgId,startDate,endDate,type);//报警次数top10
        resultMap.put("alarmTop10",alarmTop10);
        return resultMap;
    }

    /**
     * 设备在线率
     * @param orgId
     * @param type
     * @return
     */
    public String  countOnline(String orgId,int type){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("type",type);
        map.put("value",1);
        int online = fireFightingDao.countOnline1(map);
        map.put("value",null);
        int total = fireFightingDao.countOnline1(map);
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        if(total ==0){
            return "0";
        }
        String result = numberFormat.format((float) online / (float) total * 100);
        return result;
    }

    /**
     * 当天新增报警数
     * @param orgId
     * @return
     */
    public int countDayAlarm(String orgId,int type){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("type",type);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(new Date());
        map.put("time",time);
        return fireFightingDao.countDayAlarm(map);
    }

    /**
     * 本月新增报警数
     * @param orgId
     * @return
     */
    public int countMonthAlarm(String code,int type){
        Map map = new HashMap();
        map.put("code", code);
        map.put("type",type);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(new Date());
        map.put("time",time);
        return fireFightingDao.countMonthAlarm(map);
    }

    public List<Map> getOutlineTop10(String orgId,int type){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("type",type);
        return fireFightingDao.getOutlineTp101(map);
    }

    public Map countElectric(String orgId,int type,String start1,String end1,String start2,String end2,String start3,String end3){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        if(start1==null){
            start1="0";end1="30";start2="30";end2="80";start3="80";end3="100";
        }
        map.put("type",type);
        map.put("start1",start1);
        map.put("end1",end1);
        map.put("start2",start2);
        map.put("end2",end2);
        map.put("start3",start3);
        map.put("end3",end3);
        Map map1 = fireFightingDao.countElectric(map);
        Long total = (Long) map1.get("count");
        Map resultMap = new HashMap();
        List legend = new ArrayList();
        String arr[] = {start1+"%-"+end1+"%",start2+"%-"+end2+"%",start3+"%-"+end3+"%"};
        legend.add(start1+"%-"+end1+"%");legend.add(start2+"%-"+end2+"%");legend.add(start3+"%-"+end3+"%");
        resultMap.put("legend",legend);
        List series = new ArrayList();
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        for(int i=1;i<4;i++){
            Map map2 = new HashMap();
            Object object = map1.get("count"+i);
//            long val=0;
//            if(object!=null) {
//                val = Long.parseLong(String.valueOf(object ));
//            }
//            String result= "0";
//            if(total !=0){
//                result = numberFormat.format((double) val / (double) total * 100);
//            }
//            map2.put("value",result+"%");
            map2.put("value",object);
            map2.put("name",arr[i-1]);
            series.add(map2);
        }
        resultMap.put("series",series);
        return resultMap;
    }

    public List<Map> alarmTop10(String orgId,String startDate,String endDate,int type){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if(startDate ==null || startDate.length()==0 || endDate == null || endDate.length()==0){
            startDate = formatter.format(new Date());
            endDate = formatter.format(new Date());
        }
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("type",type);
        return fireFightingDao.alarmTop10(map);
    }

    public Map wiretemperature(String orgId,int type,long chType,int typeId){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("type",type);
        map.put("chType",chType);
        map.put("typeId",typeId);
        return fireFightingDao.wiretemperature(map);
    }

    /**
     * 统计各时段平均值
     * @param orgId
     * @param startDate
     * @param endDate
     * @param type
     * @param chType
     * @param typeId
     * @return
     */
    public Map countHour(String orgId,String startDate,String endDate,int type,int chType,int typeId){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("type",type);
        map.put("chType",chType);
        map.put("typeId",typeId);
        long days =1;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        if((startDate==null || startDate.length()==0) &&(endDate==null || endDate.length()==0) ){
            startDate = formatter1.format(getStartOfDay(new Date()));
            endDate = formatter1.format(getEndOfDay(new Date()));
        }else {
            try {
                startDate = formatter1.format(getStartOfDay(formatter.parse(startDate)));
                endDate = formatter1.format(getStartOfDay(formatter.parse(endDate)));
                days = ((formatter.parse(endDate).getTime()-formatter.parse(startDate).getTime())/1000/3600/24)+1;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("days",days);
//        List<String> xData = new ArrayList();
        String xData[] = {"0:00","1:00","2:00","3:00","4:00","5:00","6:00","7:00","8:00","9:00","10:00",
                "11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};
        List<Map> list = fireFightingDao.countHour(map);
        double yData[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
//        List<Double> yData = new ArrayList();
        for(int i=0;i<list.size();i++){
            int index = (int) list.get(i).get("Hour");
            Object ob = list.get(i).get("Count");
            yData[index] = Double.parseDouble(ob.toString());;
//            yData.add(Double.parseDouble(ob.toString()));
        }
        Map resultMap = new HashMap();
        resultMap.put("xData",xData);
        resultMap.put("yData",yData);
        return resultMap;
    }

    /**
     * 剩余电流实时峰值top10
     * @param orgId
     * @param type
     * @param chType
     * @param typeId
     * @return
     */
    public List<Map> residueTop10(String orgId,int type,int chType,int typeId){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("type",type);
        map.put("chType",chType);
        map.put("typeId",typeId);
        return fireFightingDao.residueTop10(map);
    }

    //电气火灾设备页报警类型统计
    public List<Map> countAlarmByType(String orgId){
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
//            orgId = "233993942926888973";
        }
        Map map  = new HashMap();
        map.put("orgId",orgId);
        List<Map> list = fireFightingDao.countAlarmByType(map);
        return list;
    }

    //各温度区间探测器实时数量占比
    public Map countTemperature(String orgId,int type,int chType,int typeId,String start1,String end1,String start2,String end2,String start3,String end3){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        if(start1==null){
            start1="0";end1="20";start2="20";end2="30";start3="30";end3="100";
        }
        map.put("type",type);
        map.put("chType",chType);
        map.put("typeId",typeId);
        map.put("start1",start1);
        map.put("end1",end1);
        map.put("start2",start2);
        map.put("end2",end2);
        map.put("start3",start3);
        map.put("end3",end3);
        Map map1 = fireFightingDao.countTemperature(map);
        Long total = (Long) map1.get("count");
        Map resultMap = new HashMap();
        List legend = new ArrayList();
        String arr[] = {start1+"-"+end1+"℃",start2+"-"+end2+"℃",start3+"-"+end3+"℃"};
        legend.add(start1+"-"+end1+"℃");legend.add(start2+"-"+end2+"℃");legend.add(start3+"-"+end3+"℃");
        resultMap.put("legend",legend);
        List series = new ArrayList();
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        for(int i=1;i<4;i++){
            Map map2 = new HashMap();
            Object object = map1.get("count"+i);
            map2.put("value",object);
            map2.put("name",arr[i-1]);
            series.add(map2);
        }
        resultMap.put("series",series);
        return resultMap;
    }

    //高温配电房Top5
    public List<Map> temperatureTop5(String orgId,int type,int chType,int typeId){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("type",type);
        map.put("chType",chType);
        map.put("typeId",typeId);
        return fireFightingDao.temperatureTop5(map);
    }


    //水压区间
    public Map waterGage(String orgId,int type,int chType,int typeId,int chNo){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("type",type);
        map.put("chType",chType);
        map.put("typeId",typeId);
        map.put("chNo",chNo);
        return fireFightingDao.waterGage(map);
    }

    //管网水压top5
    public List<Map> waterGageTop5(String orgId,int type,int chType,int typeId,int chNo){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("type",type);
        map.put("chType",chType);
        map.put("typeId",typeId);
        map.put("chNo",chNo);
        return fireFightingDao.waterGageTop5(map);
    }

    //设备压力实时占比
    public Map devGage(String orgId,int type,int chType,int typeId,int chNo,String start1,String end1,String start2,String end2,String start3,String end3){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        if(start1==null){
            start1="0";end1="100";start2="100";end2="200";start3="200";end3="300";
        }
        map.put("type",type);
        map.put("chType",chType);
        map.put("typeId",typeId);
        map.put("chNo",chNo);
        map.put("start1",start1);
        map.put("end1",end1);
        map.put("start2",start2);
        map.put("end2",end2);
        map.put("start3",start3);
        map.put("end3",end3);
        Map map1 = fireFightingDao.devGage(map);
        Map resultMap = new HashMap();
        List legend = new ArrayList();
        String arr[] = {start1+"-"+end1+"KPa",start2+"-"+end2+"KPa",start3+"-"+end3+"KPa"};
        legend.add(start1+"-"+end1+"KPa");legend.add(start2+"-"+end2+"KPa");legend.add(start3+"-"+end3+"KPa");
        resultMap.put("legend",legend);
        List series = new ArrayList();
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        for(int i=1;i<4;i++){
            Map map2 = new HashMap();
            Object object = map1.get("count"+i);
            map2.put("value",object);
            map2.put("name",arr[i-1]);
            series.add(map2);
        }
        resultMap.put("series",series);
        return resultMap;
    }

    //水池水位
    public Map waterLine(String orgId,int type,int chType,int typeId,int chNo,String start1,String end1,String start2,String end2,String start3,String end3){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        if(start1==null){
            start1="0";end1="2";start2="2";end2="2.5";start3="2.5";end3="3";
        }
        map.put("type",type);
        map.put("chType",chType);
        map.put("typeId",typeId);
        map.put("chNo",chNo);
        map.put("start1",start1);
        map.put("end1",end1);
        map.put("start2",start2);
        map.put("end2",end2);
        map.put("start3",start3);
        map.put("end3",end3);
        Map map1 = fireFightingDao.devGage(map);
        Map resultMap = new HashMap();
        List legend = new ArrayList();
        String arr[] = {start1+"-"+end1+"M",start2+"-"+end2+"M",start3+"-"+end3+"M"};
        legend.add(start1+"-"+end1+"M");legend.add(start2+"-"+end2+"M");legend.add(start3+"-"+end3+"M");
        resultMap.put("legend",legend);
        List series = new ArrayList();
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        for(int i=1;i<4;i++){
            Map map2 = new HashMap();
            Object object = map1.get("count"+i);
            map2.put("value",object);
            map2.put("name",arr[i-1]);
            series.add(map2);
        }
        resultMap.put("series",series);
        return resultMap;
    }

    // 获得某天最大时间 2018-03-20 23:59:59
    public static Date getEndOfDay(Date date) {
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(date);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
        calendarEnd.set(Calendar.MINUTE, 59);
        calendarEnd.set(Calendar.SECOND, 59);
        //防止mysql自动加一秒,毫秒设为0        　　　　　　
        calendarEnd.set(Calendar.MILLISECOND, 0);
        return calendarEnd.getTime();
    }

    // 获得某天最小时间 2018-03-20 00:00:00
    public static Date getStartOfDay(Date date) {
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(date);
        Calendar c1 = new GregorianCalendar();
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        calendarEnd.set(Calendar.MILLISECOND, 0);
        return calendarEnd.getTime();
    }

	public Map wiretemperature2(String code,int type,long chType,int typeId) {
        Map map = new HashMap();
        map.put("type",type);
        map.put("code",code);
        map.put("chType",chType);
        map.put("typeId",typeId);
        return fireFightingDao.wiretemperature2(map);
	}
}
