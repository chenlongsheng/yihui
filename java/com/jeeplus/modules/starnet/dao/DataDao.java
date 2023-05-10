package com.jeeplus.modules.starnet.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;


@MyBatisDao
public interface DataDao extends CrudDao<MapEntity> {

	List<MapEntity> getLoopElecChannelList();

	MapEntity getLoopLatestElecData(@Param("chId")String chId);

	MapEntity getNewData(@Param("chId")String chId);

	List<MapEntity> getNewDatas(@Param("historyTime")String historyTime, @Param("chId")String chId);

	List<MapEntity> getLoopList();

	MapEntity getChannelMaxValueInADay(@Param("chId")String chId, @Param("todayDatetime")String todayDatetime);

	MapEntity getChannelMinValueInADay(@Param("chId")String chId, @Param("todayDatetime")String todayDatetime);
	
	MapEntity getChannelAVGValueInADay(@Param("chId")String chId, @Param("todayDatetime")String todayDatetime);

	MapEntity getChannelByOrgIdAndType(@Param("orgId")String orgId, @Param("type")int type);

	Double getFirstMeterDataOfDay(@Param("chId")String chId, @Param("todayDatetime")String todayDatetime);

	void addMeterData(@Param("orgId")String orgId, @Param("chId")String chId, @Param("todayDatetime")String todayDatetime, @Param("value") Double value);

	MapEntity getMetryVlaueOfDate(@Param("orgId")String orgId, @Param("chId")String chId,@Param("recordDate")String recordDate);

	void addJumpMetry(@Param("orgId")String orgId, @Param("chId")String chId, @Param("normal")int normal);

}
