package com.jeeplus.modules.homepage.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-12-24.
 */
public class PDFOrg {
    private String id;
    private String name;
    private List<SandTable> sandTable = new ArrayList<SandTable>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SandTable> getSandTable() {
        return sandTable;
    }

    public void setSandTable(List<SandTable> sandTable) {
        this.sandTable = sandTable;
    }

    public void listAdd(SandTable sandTable){
        this.sandTable.add(sandTable);
    }
}
