package com.jeeplus.modules.homepage.entity;

import java.util.Date;

/**
 * Created by Administrator on 2018-12-24.
 */
public class SandTable {
    //区域id
    private String id;
    //父区域id
    private String parentId;
    //区域名
    private String name;
    //父区域名称
    private String parentName;
    //区域级别
    private String type;
    //设备类型
    private String devType;
    //安防
    private Boolean security = false;
    //消防
    private Boolean fireControl = false;
    //故障
    private Boolean fault = false;
    //高温
    private Boolean hyperthermia = false;
    //报警时间
    private Date occurTime;
    //故障时间
    private Date procTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSecurity() {
        return security;
    }

    public void setSecurity(Boolean security) {
        this.security = security;
    }

    public Boolean getFireControl() {
        return fireControl;
    }

    public void setFireControl(Boolean fireControl) {
        this.fireControl = fireControl;
    }

    public Boolean getFault() {
        return fault;
    }

    public void setFault(Boolean fault) {
        this.fault = fault;
    }

    public Boolean getHyperthermia() {
        return hyperthermia;
    }

    public void setHyperthermia(Boolean hyperthermia) {
        this.hyperthermia = hyperthermia;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    public Date getProcTime() {
        return procTime;
    }

    public void setProcTime(Date procTime) {
        this.procTime = procTime;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
