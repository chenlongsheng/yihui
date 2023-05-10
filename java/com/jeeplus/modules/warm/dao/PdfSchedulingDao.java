package com.jeeplus.modules.warm.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.warm.entity.PdfScheduling;
import com.jeeplus.modules.warm.entity.PdfSchedulingDetail;
import com.jeeplus.modules.warm.entity.PdfSchedulingRule;

import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2018/12/12.
 */
@MyBatisDao
public interface PdfSchedulingDao extends CrudDao<PdfScheduling> {

    void addScheduling(PdfScheduling pdfScheduling);//新增排班

    List<Map> findSchedulingByRuleId(String ruleId);//根据规则获取排班

    void addSchedulingDetail(PdfSchedulingDetail pdfSchedulingDetail);

    List<Map> findSchedulings(PdfSchedulingRule pdfSchedulingRule);//获取排班集合

    List<String> findSchedulingUser(PdfSchedulingRule pdfSchedulingRule);//获取当天值班人员

    void deleteSchedulings(String[] arr);

}
