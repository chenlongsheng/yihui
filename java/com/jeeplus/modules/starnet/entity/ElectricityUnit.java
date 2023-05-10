/**
 * 
 */
package com.jeeplus.modules.starnet.entity;

import java.io.Serializable;

import com.jeeplus.common.persistence.DataEntity;

/**
 * @author admin
 *
 */
public class ElectricityUnit extends DataEntity<ElectricityUnit> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String code;
    private String parentId;
    private String parentIds;
    private String bureauId;
    private String bureauName;

    private String area;
    private String number;
    private String orderNo;

    /**
     * @param name
     * @param code
     * @param parentId
     * @param parentIds
     * @param area
     * @param number
     * @param orderNo
     */
    public ElectricityUnit() {
        super();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    
    
    
    /**
     * @return the bureauId
     */
    public String getBureauId() {
        return bureauId;
    }

    /**
     * @param bureauId the bureauId to set
     */
    public void setBureauId(String bureauId) {
        this.bureauId = bureauId;
    }

    /**
     * @return the bureauName
     */
    public String getBureauName() {
        return bureauName;
    }

    /**
     * @param bureauName the bureauName to set
     */
    public void setBureauName(String bureauName) {
        this.bureauName = bureauName;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the parentIds
     */
    public String getParentIds() {
        return parentIds;
    }

    /**
     * @param parentIds the parentIds to set
     */
    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    /**
     * @return the area
     */
    public String getArea() {
        return area;
    }

    /**
     * @param area the area to set
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * @return the orderNo
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * @param orderNo the orderNo to set
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

}
