package com.jeeplus.modules.warm.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.warm.entity.PdfOrderDeal;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * Created by ZZUSER on 2018/12/7.
 */
@MyBatisDao
public interface PdfOrderDealDao extends CrudDao<PdfOrderDeal> {

	void addOrderDeal(PdfOrderDeal pdfOrderDeal);

	List<Map> getDealList(String orderId);

	void setReaded(PdfOrderDeal pdfOrderDeal);

	int countUnRead(PdfOrderDeal pdfOrderDeal);

	Map getUnRead(PdfOrderDeal pdfOrderDeal);

	List<Map> getOrderDeal(PdfOrderDeal pdfOrderDeal);

	List<String> getMaxDate(String id);

	List<Map> getDealByReplyId(String id);

	void updateState(@Param(value = "id") String id, @Param(value = "sendUser")String sendUser, @Param(value = "content")String content);

}
