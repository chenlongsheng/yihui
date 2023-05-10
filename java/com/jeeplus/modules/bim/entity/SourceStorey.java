package com.jeeplus.modules.bim.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018-12-27.
 */
public class SourceStorey {
    private String id;
    //楼层全局uuid
    private String uuid;
    //楼层名称
    private String storeyName;
    //项目编码
    private String buildingUuid;
    //楼高
    private BigDecimal elevation;
    //类型（1=楼层2=单元）
    private int type;
    //创建时间
    private Date createdDate;
    //是否有效状态（E:有效  D:无效）
    private String status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStoreyName() {
        return storeyName;
    }

    public void setStoreyName(String storeyName) {
        this.storeyName = storeyName;
    }

    public String getBuildingUuid() {
        return buildingUuid;
    }

    public void setBuildingUuid(String buildingUuid) {
        this.buildingUuid = buildingUuid;
    }

    public BigDecimal getElevation() {
        return elevation;
    }

    public void setElevation(BigDecimal elevation) {
        this.elevation = elevation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
