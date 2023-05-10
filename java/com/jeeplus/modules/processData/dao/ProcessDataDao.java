package com.jeeplus.modules.processData.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.processData.entity.Channels;
import com.jeeplus.modules.processData.entity.Devices;
import com.jeeplus.modules.processData.entity.ImageDevCh;
import com.jeeplus.modules.processData.entity.Images;
import com.jeeplus.modules.settings.entity.TOrg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ZZUSER on 2019/1/4.
 */
@MyBatisDao
public interface ProcessDataDao extends CrudDao<TOrg> {
	                           
	void  truncateDetail();

	void updateOnline(@Param("mac") String mac, @Param("online") String online);

	void insertDevice(Devices device);//

	void inserChannels(Channels channel);

	void inserImage(Images images);// pdf_org_image 表

	void inserImageDevCh(ImageDevCh ImageDevCh);// pdf_image_dev_ch表

	void updatedevicetail(@Param("id") String id, @Param("online") String online);

	void updateRealData(List<MapEntity> list);



}
