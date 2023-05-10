package com.jeeplus.modules.homepage.entity;

/**
 * Created by Administrator on 2018-12-20.
 */
public class OrgPDFInfo {
    //安全运行天数
    private String securityDay;
    //配电房总数
    private String PDFTotal;
    //报警配电房总数
    private String AlarmPDFTotal;

    public String getSecurityDay() {
        return securityDay;
    }

    public void setSecurityDay(String securityDay) {
        this.securityDay = securityDay;
    }

    public String getPDFTotal() {
        return PDFTotal;
    }

    public void setPDFTotal(String PDFTotal) {
        this.PDFTotal = PDFTotal;
    }

    public String getAlarmPDFTotal() {
        return AlarmPDFTotal;
    }

    public void setAlarmPDFTotal(String alarmPDFTotal) {
        AlarmPDFTotal = alarmPDFTotal;
    }
}
