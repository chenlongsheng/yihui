package com.jeeplus.modules.pdfData.service;

import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.OrgUtil;
import com.jeeplus.modules.pdfData.dao.SecurityDao;
import com.jeeplus.modules.settings.entity.TOrg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ZZUSER on 2018/12/26.
 */
@Service
public class SecurityService extends CrudService<SecurityDao,TOrg> {
	
    @Autowired
    SecurityDao securityDao;

    /**
     * 安防页面获取所有数据
     * @param orgId
     * @param type
     * @return
     */
    /*
    public Map getAllData(String orgId,int type,String startDate,String endDate){
        Map resultMap = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
        }
        int saveDays = countSaveDays(orgId,type);//安全天数
        int open = countOpen(orgId);//门开启数
        int newAlarm = countAlarm(orgId,type);//本月新增报警数
        Map HourOpen = countHourOpen(orgId,startDate,endDate,type,5,2);//各时段开门平均数
        Map OpenTop10 = countOpenTop(orgId,startDate,endDate);//开门次数top10
        resultMap.put("saveDay",saveDays);
        resultMap.put("open",open);
        resultMap.put("newAlarm",newAlarm);
        resultMap.put("HourOpen",HourOpen);
        resultMap.put("OpenTop10",OpenTop10);
        return resultMap;
    }
	*/
    public Map changeDate(String orgId,int type,String startDate,String endDate){
        Map resultMap = new HashMap();
        Map HourOpen = countHourOpen(orgId,startDate,endDate,type,5,2);//各时段开门平均数
        Map OpenTop10 = countOpenTop(orgId,startDate,endDate);//开门次数top10
        resultMap.put("HourOpen",HourOpen);
        resultMap.put("OpenTop10",OpenTop10);
        return resultMap;
    }

    /**
     * 计算安全运行天数
     * @return
     */
    public int countSaveDays(String code,int type,int type_id){
        Map map = new HashMap();
        map.put("code",code);
        map.put("type_id",type_id);
        map.put("type",type);
        String time = securityDao.getLatestAlarmTime(map);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int days = 0;
        try {
        	if(time != null) {
                Date maxTime = formatter.parse(time);
                days = (int) ((new Date().getTime() - maxTime.getTime()) / (1000*3600*24));
        	} else {
        		days = 30;
        	}

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 门开启数量
     * @return
     */
    public int countOpen(String orgId){
     
        return securityDao.countOpen(orgId);
    }

    /**
     * 本月新增报警数
     * @param orgId
     * @return
     */
    public int countAlarm(String code,int type){
        Map map = new HashMap();
        map.put("code", code);
        map.put("type",type);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(new Date());
        map.put("time",time);
        return securityDao.countAlarm(map);
    }

    /**
     * 各时段开门平均数
     * @param orgId
     * @param startDate
     * @param endDate
     * @return
     */
    public Map countHourOpen(String code,String startDate,String endDate,int type,int chType,int typeId){
        Map map = new HashMap();
        map.put("code",code);
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
        map.put("startTime",startDate);
        map.put("endTime",endDate);
        map.put("days",days);
        String xData[] = {"0:00","1:00","2:00","3:00","4:00","5:00","6:00","7:00","8:00","9:00","10:00",
                "11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};
        List<Map> list = securityDao.countHourOpen(map);
        double yData[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(int i=0;i<list.size();i++){
            int index = (int) list.get(i).get("Hour");
            Object ob = list.get(i).get("Count");
            yData[index] = Double.parseDouble(ob.toString());
        }
        Map resultMap = new HashMap();
        resultMap.put("xData",xData);
        resultMap.put("yData",yData);
        return resultMap;

    }

    /**
     * 开门次数top10
     * @param orgId
     * @param startDate
     * @param endDate
     * @return
     */
    public Map countOpenTop(String orgId,String startDate,String endDate){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
        }else {
            map.put("orgId",orgId);
        }
        map.put("type",1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        if((startDate==null || startDate.length()==0) &&(endDate==null || endDate.length()==0) ){
            startDate = formatter.format(new Date());
            endDate = formatter.format(new Date());
        }
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        List<Map> list = securityDao.countOpenTop(map);
        List xData = new ArrayList();
        List yData = new ArrayList();
        for(int i=0;i<list.size();i++){
            xData.add(list.get(i).get("name"));
            yData.add(list.get(i).get("count"));
        }
        Map resultMap = new HashMap();
        resultMap.put("xData",xData);
        resultMap.put("yData",yData);
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

//    public static void main(String[] args) throws ParseException {
//        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
//        Date date= formatter1.parse("2019-12-12");
//        date = getStartOfDay(date);
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(formatter.format(date));
//    }
}
