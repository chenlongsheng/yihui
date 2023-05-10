package com.jeeplus.modules.pdfData.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface PdfMainpageDao {

	public List<Map<String, Object>> notProcAlarmList(@Param("code")String code);
	
	public Map<String, Object> getLightData(@Param("pdfId")String pdfId);

	public List<Map<String, Object>> getHumTempData(@Param("code")String code);
	public List<Map<String, Object>> getHumData(@Param("code")String code);
	public List<Map<String, Object>> getTempData(@Param("code")String code);

	public List<Map<String, Object>> getWaterData(@Param("code")String code);
	public List<Map<String, Object>> getWaterDeepData(@Param("code")String code);
	public List<Map<String, Object>> getWaterStringData(@Param("code")String code);

	public List<Map<String, Object>> getDoorData(@Param("code")String pdfId);
	public List<Map<String, Object>> getDoorOpenOverTimeData(@Param("code")String pdfId);
	public List<Map<String, Object>> getDoorOpenTimeData(@Param("code")String pdfId);

	
	

	public List<Map<String, Object>> getSmokeData(@Param("code")String code);

	public Map<String, Object> getHighVoltageData(@Param("pdfId")String pdfId);

	public Map<String, Object> getLowVltageData(@Param("boxId")String boxId);

	public Map<String, Object> getSplitSegmentData(@Param("boxId")String boxId);

	public Map<String, Object> getReturnPathData(@Param("boxId")String boxId);

	public List<Map<String, Object>> getEleBoxList(@Param("pdfId")String pdfId);

	public List<Map<String, Object>> getLoraDeviceByTypeAndCode(@Param("devType")int devType,@Param("code")String code);

	public List<Map<String, Object>> getLoraChannelDataByDeviceId(@Param("deviceId")String deviceId);

}
