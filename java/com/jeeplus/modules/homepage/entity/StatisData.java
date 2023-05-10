package com.jeeplus.modules.homepage.entity;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-12-29.
 */
public class StatisData {
    private String name;
    private List<JSONObject> data = new ArrayList<JSONObject>();

    public StatisData(){

    }

    public StatisData(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JSONObject> getData() {
        return data;
    }

    public void setData(List<JSONObject> data) {
        this.data = data;
    }

    public void dataAdd(JSONObject object){
        this.data.add(object);
    }
}
