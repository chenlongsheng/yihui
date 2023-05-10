package com.jeeplus.modules.warm.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.warm.entity.PdfOrderRecorder;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * Created by ZZUSER on 2018/12/7.
 */
@MyBatisDao
public interface PdfOrderRecorderDao extends CrudDao<PdfOrderRecorder> {

    void addOrderRecorder(PdfOrderRecorder pdfOrderRecorder);

    List<Map> getRecorderList(PdfOrderRecorder pdfOrderRecorder);
    
    
    //添加模板
    void insertTemplateDetail(@Param(value="userId")String userId,@Param(value="templateDetail")String templateDetail);
    
    List<MapEntity> getTemplateList(MapEntity entity);
    
    void deleteTemplate(@Param(value="id")String id);
    
    void updateTemplate(@Param(value="templateDetail")String templateDetail,@Param(value="id")String id);
    
}
