package com.jeeplus.modules.warm.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.warm.entity.PdfOrder;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * Created by ZZUSER on 2018/12/6.
 */
@MyBatisDao
public interface PdfOrderDao extends CrudDao<TOrg> {
	
	List<Map> findRealOrder(@Param("userId")String userId,@Param("alarmType")String alarmType);
	



     List<Map> findOrder(PdfOrder pdfOrder);//报警列表查询
     
     List<Map> findSendOrder(PdfOrder pdfOrder);//工单列表查询
     //报警详情
     List<MapEntity> alarmCountDetail(@Param("chId")String chId);

     List<TOrg> getOrgByName(TOrg tOrg);

     List<TOrg> getPdfByOrg(TOrg tOrg);

     long addOrder(PdfOrder pdfOrder);//新增工单

     List<User> getUserByIds(String[] arr);

     void updateOrder(PdfOrder pdfOrder);

     Map findOrderById(String id);

     TDevice getDevById(String devId);

     void deleteOrderByIds(String[] ids);

     List<Map> getFirstData(PdfOrder pdfOrder);//已接单或处理中

     List<Map> getUnRecieve(PdfOrder pdfOrder);//未接工单

     List<Map> getHistoryOrder(PdfOrder pdfOrder);//历史工单

     List<Map> getDevByOrg(TOrg tOrg);//根据区域获取设备

     List<Map> getSendUserList(Map map);//根据区域获取报警人集合
     
     int alarmCount(@Param("devId")Long devId);
     
     
     //维保人员
     List<MapEntity> maintenUserList();
     
     List<MapEntity> maintenanceList(String id);     
     
     void updateMainType(@Param("id")String id,@Param("principal")String principal);
     
     void updateAlarmLog(@Param("chId")String chId,@Param("status")String status);
     
     List<MapEntity>  getVedioLogByChId(@Param("chId")String chId);
     
     List<MapEntity> getVediorecorderByAlarmLogId(@Param("alarmLogId")String alarmLogId);




}
