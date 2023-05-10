package com.jeeplus.modules.warm.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.warm.entity.PdfBind;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * Created by ZZUSER on 2018/12/12.
 */
@MyBatisDao
public interface PdfBindDao extends CrudDao<PdfBind> {

    void addBind(PdfBind pdfBind);

    void updateBind(PdfBind pdfBind);

    List<PdfBind> findBind(PdfBind pdfBind);

    List<Map> getUserProject(String[] arr);//获取用户所有项目

    List<PdfBind> findBindByIds(@Param(value="userIds")String userIds);//根据用户id获取到绑定数据
}
