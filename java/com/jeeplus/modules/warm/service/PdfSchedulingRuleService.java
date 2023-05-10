package com.jeeplus.modules.warm.service;

import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.OrgUtil;
import com.jeeplus.modules.settings.dao.TOrgDao;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.sys.dao.UserDao;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.warm.dao.PdfOrderDao;
import com.jeeplus.modules.warm.dao.PdfSchedulingDao;
import com.jeeplus.modules.warm.dao.PdfSchedulingRuleDao;
import com.jeeplus.modules.warm.entity.PdfScheduling;
import com.jeeplus.modules.warm.entity.PdfSchedulingDetail;
import com.jeeplus.modules.warm.entity.PdfSchedulingRule;
import com.jeeplus.modules.warm.entity.PdfSchedulings;
import com.jeeplus.modules.warm.util.AccountDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2018/12/12.
 */
@Service
public class PdfSchedulingRuleService extends CrudService<PdfSchedulingRuleDao,PdfSchedulingRule> {

    @Autowired
    PdfSchedulingRuleDao pdfSchedulingRuleDao;

    @Autowired
    PdfSchedulingDao pdfSchedulingDao;

    @Autowired
    UserDao userDao;

    @Autowired
    PdfOrderDao pdfOrderDao;

    @Autowired
    TOrgDao tOrgDao;

    public List<Map> getFirstOrgList(){
        User user =  userDao.get(OrgUtil.getUserId());
        TOrg tOrg = new TOrg();
        tOrg.setType(6);
        tOrg.setId(user.getArea().getId());
        List<Map> list = pdfSchedulingRuleDao.getFirstOrgList(tOrg);
        int type = (int) list.get(0).get("type");
        tOrg.setType(type+1);
        return pdfSchedulingRuleDao.getFirstOrgList(tOrg);
    }

    public List<Map> getOrgList(){
        User user =  userDao.get(OrgUtil.getUserId());
        return pdfSchedulingRuleDao.getOrgList(user.getArea());
    }

    public List<Map> getUseList(String orgId){
        TOrg tOrg1 = new TOrg();
        if(orgId ==null || orgId.length()==0){
            User user =  userDao.get(OrgUtil.getUserId());
            tOrg1.setId(user.getArea().getId());
        }else {
            tOrg1.setId(orgId);
        }
        return pdfSchedulingRuleDao.getUserList1(tOrg1);
    }

    @Transactional(readOnly = false)
    public void addSchedulingRule(PdfSchedulings pdfSchedulings) throws ParseException {
        PdfSchedulingRule pdfSchedulingRule = pdfSchedulings.getPdfSchedulingRule();
        pdfSchedulingRule.setCreateDate(new Date());
        pdfSchedulingRuleDao.addSchedulingRule(pdfSchedulingRule);
        String id = pdfSchedulingRule.getId();
        List<PdfScheduling> list = pdfSchedulings.getList();
        String watchkeeper = "";
        PdfSchedulingDetail pdfSchedulingDetail = new PdfSchedulingDetail();
        for(int i=0;i<list.size();i++){
            PdfScheduling pdfScheduling = list.get(i);
            pdfScheduling.setRuleId(id);
            pdfSchedulingDao.addScheduling(pdfScheduling);
            List<String> list1 = AccountDate.getEveryday(pdfSchedulingRule.getEffectiveDateStart(), pdfSchedulingRule.getEffectiveDateEnd());
            for (String result : list1) {
                int week = AccountDate.getWeekOfDate(result);
                if(pdfScheduling.getPeriodStart()<=week && week<=pdfScheduling.getPeriodEnd()){
                    pdfSchedulingDetail.setSchedulingId(pdfScheduling.getId());
                    pdfSchedulingDetail.setSchedulingDate(result);
                    pdfSchedulingDao.addSchedulingDetail(pdfSchedulingDetail);
                }
            }
            if(i==0){
                watchkeeper = list.get(i).getWatchkeeper();
            }else {
                watchkeeper = watchkeeper+","+list.get(i).getWatchkeeper();
            }
        }
        pdfSchedulingRule.setWatchkeepers(watchkeeper);
        pdfSchedulingRuleDao.updateRule(pdfSchedulingRule);
    }

    public List<Map> findRuleList(PdfSchedulingRule pdfSchedulingRule){
        List<Map> list  = pdfSchedulingRuleDao.findRuleList(pdfSchedulingRule);
        for(int i = 0;i<list.size();i++){
            String ids = String.valueOf(list.get(i).get("watchkeepers"));
            String[] arr = ids.split(",");
            List<User> list1 = pdfOrderDao.getUserByIds(arr);
            String watchkeepers = "";
            for(int j=0;j<list1.size();j++){
                if(j==0){
                    watchkeepers = list1.get(j).getName();
                }else {
                    watchkeepers = watchkeepers +","+list1.get(j).getName();
                }
            }
            list.get(i).put("watchkeepers",watchkeepers);
            list.get(i).put("userList",list1);
        }
        return list;
    }

    public void deleteRules(String ids){
        String[] arr = ids.split(",");
        pdfSchedulingRuleDao.deleteRules(arr);
        pdfSchedulingDao.deleteSchedulings(arr);
    }


}
