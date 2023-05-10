package com.jeeplus.modules.pdfData.service;

import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.OrgUtil;
import com.jeeplus.modules.pdfData.dao.EnergySaveDao;
import com.jeeplus.modules.settings.entity.TOrg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ZZUSER on 2019/1/4.
 */
@Service
public class EnergySaveService extends CrudService<EnergySaveDao,TOrg> {
    @Autowired
    EnergySaveDao energySaveDao;

    public Map getAllData(String orgId,String startDate,String endDate) throws ParseException {
        Map resultMap = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
//            orgId = "233993942926888973";
        }
        Map map = historyMoniroring(orgId,startDate,endDate);
        if(startDate==null || startDate.length()==0){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            startDate = formatter.format(new Date());
            endDate = formatter.format(new Date());
        }
        double light = countMonitoring(orgId,7,startDate,endDate);
        double aircondition = countMonitoring(orgId,8,startDate,endDate);
        double socket = countMonitoring(orgId,9,startDate,endDate);
        Map map1 = new HashMap();
        map1.put("name","照明能耗");
        map1.put("value",light);
        Map map2 = new HashMap();
        map2.put("name","空调能耗");
        map2.put("value",aircondition);
        Map map3 = new HashMap();
        map3.put("name","插座能耗");
        map3.put("value",socket);
        List<Map> typeDataList = new ArrayList();
        typeDataList.add(map1);
        typeDataList.add(map2);
        typeDataList.add(map3);
        Map countYearMonByHour = countYearMonByHour(orgId);
        Map countYearMonByMonth = countYearMonByMonth(orgId);
        resultMap.put("light",light);
        resultMap.put("aircondition",aircondition);
        resultMap.put("socket",socket);
        resultMap.put("history",map);
        resultMap.put("typeData",typeDataList);
        resultMap.put("countYearMonByHour",countYearMonByHour);
        resultMap.put("countYearMonByMonth",countYearMonByMonth);
        return resultMap;
    }

    /**
     * 各种类型能耗
     * @param orgId
     * @param type
     * @param startDate
     * @param endDate
     * @return
     */
    public double countMonitoring(String orgId,int type,String startDate,String endDate){
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//          map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(startDate ==null || startDate.length()==0){
            startDate = formatter.format(new Date());
            endDate = formatter.format(new Date());
        }
        map.put("type",type);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        return energySaveDao.countMonitoring(map);
    }

    /**
     * 历史能耗
     * @param orgId
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     */
    public Map historyMoniroring(String orgId,String startDate,String endDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//          map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        if(startDate==null || startDate.length()==0){
            endDate = formatter.format(new Date());
            Calendar c = Calendar.getInstance();
            //过去七天
            c.setTime(new Date());
            c.add(Calendar.DATE, - 7);
            Date d = c.getTime();
            startDate = formatter.format(d);
        }
//        map.put("type",type);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        Date dBegin = formatter.parse(startDate);
        Date dEnd = formatter.parse(endDate);
        List xAxis = findDates(dBegin,dEnd);
        map.put("type",7);
        List<Map> list = energySaveDao.historyMonitoring(map);
        List dataList = changeList(list,startDate,endDate);
        Map map1 = new HashMap();
        map1.put("name","照明能耗");
        map1.put("type","line");
        map1.put("stack","总量");
        map1.put("data",dataList);
        map.put("type",8);
        List<Map> list1 = energySaveDao.historyMonitoring(map);
        List dataList1 = changeList(list1,startDate,endDate);
        Map map2 = new HashMap();
        map2.put("name","空调能耗");
        map2.put("type","line");
        map2.put("stack","总量");
        map2.put("data",dataList1);
        map.put("type",9);
        List<Map> list2 = energySaveDao.historyMonitoring(map);
        List dataList2 = changeList(list2,startDate,endDate);
        Map map3 = new HashMap();
        map3.put("name","插座能耗");
        map3.put("type","line");
        map3.put("stack","总量");
        map3.put("data",dataList2);
        List<Map> series = new ArrayList<>();
        series.add(map1);
        series.add(map2);
        series.add(map3);
        Map resultMap = new HashMap();
        resultMap.put("xAxis",xAxis);
        resultMap.put("series",series);
        return resultMap;
    }

    public List<Map> typeMonitoring(String orgId,String startDate,String endDate){
        double light = countMonitoring(orgId,7,startDate,endDate);
        Map map1 = new HashMap();
        map1.put("name","照明能耗");
        map1.put("value",light);
        double aircondition = countMonitoring(orgId,8,startDate,endDate);
        Map map2 = new HashMap();
        map2.put("name","照明能耗");
        map2.put("value",aircondition);
        double socket = countMonitoring(orgId,9,startDate,endDate);
        Map map3 = new HashMap();
        map3.put("name","照明能耗");
        map3.put("value",socket);
        List<Map> resultList = new ArrayList<>();
        resultList.add(map1);
        resultList.add(map2);
        resultList.add(map3);
        return resultList;
    }

    public List<Map> getYesHour(String orgId){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("date",formatter.format(new Date()));
        map.put("type",7);
        double light = energySaveDao.getYesHour(map);
        map.put("type",8);
        double aircondition = energySaveDao.getYesHour(map);
        map.put("type",9);
        double socket = energySaveDao.getYesHour(map);
        List tList = new ArrayList();
        tList.add(light);tList.add(aircondition);tList.add(socket);
        Map tMap = new HashMap();
        tMap.put("name","今日");
        tMap.put("data",tList);
        Calendar c = Calendar.getInstance();
        //昨天
        c.setTime(new Date());
        c.add(Calendar.DATE, - 1);
        Date d = c.getTime();
        map.put("date",formatter.format(d));
        map.put("type",7);
        double light1 = energySaveDao.getYesHour(map);
        map.put("type",8);
        double aircondition1 = energySaveDao.getYesHour(map);
        map.put("type",9);
        double socket1 = energySaveDao.getYesHour(map);
        List yList = new ArrayList();
        yList.add(light1);yList.add(aircondition1);yList.add(socket1);
        Map yMap = new HashMap();
        yMap.put("name","昨日");
        yMap.put("data",yList);
        List<Map> resultList = new ArrayList<>();
        resultList.add(tMap);
        resultList.add(yMap);
        return resultList;
    }

    public List<Map> getYesDay(String orgId){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("date",formatter.format(new Date()));
        map.put("type",7);
        double light = energySaveDao.getYesDay(map);
        map.put("type",8);
        double aircondition = energySaveDao.getYesDay(map);
        map.put("type",9);
        double socket = energySaveDao.getYesDay(map);
        List tList = new ArrayList();
        tList.add(light);tList.add(aircondition);tList.add(socket);
        Map tMap = new HashMap();
        tMap.put("name","本月");
        tMap.put("data",tList);
        Calendar c = Calendar.getInstance();
        //昨天
        c.setTime(new Date());
        c.add(Calendar.MONTH, - 1);
        Date d = c.getTime();
        map.put("date",formatter.format(d));
        map.put("type",7);
        double light1 = energySaveDao.getYesDay(map);
        map.put("type",8);
        double aircondition1 = energySaveDao.getYesDay(map);
        map.put("type",9);
        double socket1 = energySaveDao.getYesDay(map);
        List yList = new ArrayList();
        yList.add(light1);yList.add(aircondition1);yList.add(socket1);
        Map yMap = new HashMap();
        yMap.put("name","上月");
        yMap.put("data",yList);
        List<Map> resultList = new ArrayList<>();
        resultList.add(tMap);
        resultList.add(yMap);
        return resultList;
    }

    public List<Map> getYesYear(String orgId){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("date",formatter.format(new Date()));
        map.put("type",7);
        double light = energySaveDao.getYesYear(map);
        map.put("type",8);
        double aircondition = energySaveDao.getYesYear(map);
        map.put("type",9);
        double socket = energySaveDao.getYesYear(map);
        List tList = new ArrayList();
        tList.add(light);tList.add(aircondition);tList.add(socket);
        Map tMap = new HashMap();
        tMap.put("name","今年");
        tMap.put("data",tList);
        Calendar c = Calendar.getInstance();
        //昨天
        c.setTime(new Date());
        c.add(Calendar.YEAR, - 1);
        Date d = c.getTime();
        map.put("date",formatter.format(d));
        map.put("type",7);
        double light1 = energySaveDao.getYesYear(map);
        map.put("type",8);
        double aircondition1 = energySaveDao.getYesYear(map);
        map.put("type",9);
        double socket1 = energySaveDao.getYesYear(map);
        List yList = new ArrayList();
        yList.add(light1);yList.add(aircondition1);yList.add(socket1);
        Map yMap = new HashMap();
        yMap.put("name","去年");
        yMap.put("data",yList);
        List<Map> resultList = new ArrayList<>();
        resultList.add(tMap);
        resultList.add(yMap);
        return resultList;
    }

    public List<Map> countYearTop10(String orgId,int type){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("date",formatter.format(new Date()));
        map.put("type",type);
        return energySaveDao.countYearTop10(map);
    }

    public Map countYearMonByHour(String orgId){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("date",formatter.format(new Date()));
        map.put("type",7);
        List<Map> lightList = energySaveDao.countYearMonByHour(map);
        map.put("type",8);
        List<Map> airList = energySaveDao.countYearMonByHour(map);
        map.put("type",9);
        List<Map> socketList = energySaveDao.countYearMonByHour(map);

        Date date=new Date();
        Calendar ca=Calendar.getInstance();
        ca.setTime(date);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");

        String st=sdf.format(date);
        int d=ca.get(Calendar.DAY_OF_YEAR);
        int a=ca.get(Calendar.DAY_OF_MONTH);
        System.out.println("当前时间是："+st+";一年中的"+d+"一个月中的"+a);

        String xData[] = {"0:00","1:00","2:00","3:00","4:00","5:00","6:00","7:00","8:00","9:00","10:00",
                "11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};
        double yData[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(int i=0;i<lightList.size();i++){
            int index = (int) lightList.get(i).get("historyTime");
            Object ob = lightList.get(i).get("historyValue");
            yData[index] = Double.parseDouble(ob.toString())/d;;
        }
        Map lightMap = new HashMap();
        lightMap.put("name","照明能耗");
        lightMap.put("type","line");
        lightMap.put("stack","总量");
        lightMap.put("areaStyle",new HashMap());
        lightMap.put("data",yData);
        double yData1[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(int i=0;i<airList.size();i++){
            int index = (int) airList.get(i).get("historyTime");
            Object ob = airList.get(i).get("historyValue");
            yData1[index] = Double.parseDouble(ob.toString())/d;;
        }
        Map airMap = new HashMap();
        airMap.put("name","空调能耗");
        airMap.put("type","line");
        airMap.put("stack","总量");
        airMap.put("areaStyle",new HashMap());
        airMap.put("data",yData1);

        double yData2[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(int i=0;i<socketList.size();i++){
            int index = (int) socketList.get(i).get("historyTime");
            Object ob = socketList.get(i).get("historyValue");
            yData2[index] = Double.parseDouble(ob.toString())/d;;
        }
        Map socketMap = new HashMap();
        socketMap.put("name","插座能耗");
        socketMap.put("type","line");
        socketMap.put("stack","总量");
        socketMap.put("areaStyle",new HashMap());
        socketMap.put("data",yData2);
        List series = new ArrayList();
        series.add(lightMap);
        series.add(airMap);
        series.add(socketMap);
        Map resultMap = new HashMap();
        resultMap.put("xData",xData);
        resultMap.put("series",series);
        return resultMap;
    }


    public Map countYearMonByMonth(String orgId){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
            map.put("orgId",orgId);
//            map.put("orgId","233993942926888973");
        }else {
            map.put("orgId",orgId);
        }
        map.put("date",formatter.format(new Date()));
        map.put("type",7);
        List<Map> lightList = energySaveDao.countYearMonByMonth(map);
        map.put("type",8);
        List<Map> airList = energySaveDao.countYearMonByMonth(map);
        map.put("type",9);
        List<Map> socketList = energySaveDao.countYearMonByMonth(map);

        String xData[] = {"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};
        double yData[] = {0,0,0,0,0,0,0,0,0,0,0,0};
        for(int i=0;i<lightList.size();i++){
            int index = (int) lightList.get(i).get("historyTime")-1;
            Object ob = lightList.get(i).get("historyValue");
            yData[index] = Double.parseDouble(ob.toString());
        }
        Map lightMap = new HashMap();
        lightMap.put("name","照明能耗");
        lightMap.put("type","line");
        lightMap.put("stack","总量");
        lightMap.put("areaStyle",new HashMap());
        lightMap.put("data",yData);
        double yData1[] = {0,0,0,0,0,0,0,0,0,0,0,0};
        for(int i=0;i<airList.size();i++){
            int index = (int) airList.get(i).get("historyTime")-1;
            Object ob = airList.get(i).get("historyValue");
            yData1[index] = Double.parseDouble(ob.toString());
        }
        Map airMap = new HashMap();
        airMap.put("name","空调能耗");
        airMap.put("type","line");
        airMap.put("stack","总量");
        airMap.put("areaStyle",new HashMap());
        airMap.put("data",yData1);

        double yData2[] = {0,0,0,0,0,0,0,0,0,0,0,0};
        for(int i=0;i<socketList.size();i++){
            int index = (int) socketList.get(i).get("historyTime")-1;
            Object ob = socketList.get(i).get("historyValue");
            yData2[index] = Double.parseDouble(ob.toString());
        }
        Map socketMap = new HashMap();
        socketMap.put("name","插座能耗");
        socketMap.put("type","line");
        socketMap.put("stack","总量");
        socketMap.put("areaStyle",new HashMap());
        socketMap.put("data",yData2);
        List series = new ArrayList();
        series.add(lightMap);
        series.add(airMap);
        series.add(socketMap);
        Map resultMap = new HashMap();
        resultMap.put("xData",xData);
        resultMap.put("series",series);
        return resultMap;
    }



    public static List changeList(List<Map> list,String startDate,String endDate){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List dataList = new ArrayList();
        try {
            Date dBegin = formatter.parse(startDate);
            Date dEnd = formatter.parse(endDate);
            List<String> timeList = findDates(dBegin,dEnd);
            for(int i=0;i<timeList.size();i++){
                double data = 0;
                for(int j=0;j<list.size();j++){
                    if(list.get(j).get("historyTime") .equals(timeList.get(i))){
                        Map map = list.get(j);
                        data = (double) list.get(j).get("historyValue");
                    }
                }
                dataList.add(data);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public static List<String> findDates(Date dBegin, Date dEnd) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List<String> lDate = new ArrayList<String>();
        lDate.add(formatter.format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime()))  {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(formatter.format(calBegin.getTime()));
        }
        return lDate;
    }

}
