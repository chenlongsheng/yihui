package com.jeeplus.modules.bim.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.utils.HttpUtils;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.bim.entity.SourceElement;
import com.jeeplus.modules.bim.entity.SourceSpace;
import com.jeeplus.modules.bim.entity.SourceStorey;
import com.jeeplus.modules.bim.service.BimDataService;
import com.jeeplus.modules.bim.service.SourceElementService;
import com.jeeplus.modules.bim.service.SourceSpaceService;
import com.jeeplus.modules.bim.service.SourceStoreyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-12-27.
 */
@Controller
@RequestMapping(value = "/bim")
public class bimController {
    @Autowired
    SourceElementService elementService;
    @Autowired
    SourceSpaceService spaceService;
    @Autowired
    SourceStoreyService storeyService;
    @Autowired
    BimDataService bimDataService;
    
    //同步楼层
    @ResponseBody
    @RequestMapping(value = "/synLevel")
    public JSONObject synLevel(String project){
        String value = HttpUtils.sendGet("http://222.77.181.112:1810/api/level/getList/" + project,null,null);
        System.out.println(value);
        JSONObject levelJsons= JSONObject.parseObject(value);
        System.out.println(levelJsons.getInteger("status"));
        if(levelJsons.getInteger("status")==200){
            String msg = levelJsons.getString("data");
            JSONArray levelList = JSONObject.parseArray(msg);
//            JSONArray levelList=levelJsons.getJSONArray("data");
            if(levelList!=null&&levelList.size()>0) {
                //删除该项目的所有楼层
                storeyService.delByBuildingUuid(project);
                for(Object levelObj:levelList){
                    JSONObject levelJson=JSONObject.parseObject(JSONObject.toJSONString(levelObj));
                    String levelId=levelJson.getString("LevelId");//楼层id
                    String levelName=levelJson.getString("LevelName");//楼层名称
                    String elevation=levelJson.getString("Elevation");//楼层层高
                    SourceStorey sourceStorey = new SourceStorey();
                    sourceStorey.setBuildingUuid(project);
                    sourceStorey.setUuid(levelId);
                    sourceStorey.setStoreyName(levelName);
                    sourceStorey.setElevation(new BigDecimal(elevation));
                    sourceStorey.setCreatedDate(new Date());
                    sourceStorey.setStatus("E");
                    storeyService.insert(sourceStorey);
                }
            }else{
                return ServletUtils.buildJsonRs(false, "返回的楼层集合为空", null);
            }
            return ServletUtils.buildJsonRs(true, "同步完成", null);
        }else{
            return ServletUtils.buildJsonRs(false, levelJsons.getString("message"), null);
        }
    }

    //同步空间
    @ResponseBody
    @RequestMapping(value = "/synSpace")
    public JSONObject synSpace(String project){
        String value = HttpUtils.sendGet("http://222.77.181.112:1810/api/space/getList/" + project,null,null);
        System.out.println(value);
        JSONObject spaceJsons= JSONObject.parseObject(value);
        if(spaceJsons.getInteger("status")==200){
            String msg = spaceJsons.getString("data");
            JSONArray spaceList = JSONObject.parseArray(msg);
//            JSONArray spaceList=spaceJsons.getJSONArray("data");
            if(spaceList!=null&&spaceList.size()>0){
                //删除该项目的所有空间
                spaceService.delByBuildingUuid(project);

                for(Object spaceObj:spaceList){
                    JSONObject spaceJson=JSONObject.parseObject(JSONObject.toJSONString(spaceObj));
                    String levelId=spaceJson.getString("LevelId");//楼层id
                    String levelName=spaceJson.getString("LevelName");//楼层名称
                    String spaceName=spaceJson.getString("SpaceName");//空间名称
                    String spaceId=spaceJson.getString("SpaceId");//空间id
                    String spaceProperty=null;

                    String spacePropertystr = HttpUtils.sendGet("http://222.77.181.112:1810/api/space/getProperty/" + project+"?SpaceId="+spaceId,null,null);
                    JSONObject spacePropertJsons=JSONObject.parseObject(spacePropertystr);
                    if(spacePropertJsons.getInteger("status")==200) {
                        JSONArray spacePropertList = spacePropertJsons.getJSONArray("data");
                        spaceProperty=spacePropertList.toString();
                    }
                    SourceSpace sourceSpace=new SourceSpace();
                    sourceSpace.setUuid(spaceId);
                    sourceSpace.setName(spaceName);
                    sourceSpace.setStoreyUuid(levelId);
                    sourceSpace.setStoreyName(levelName);
                    sourceSpace.setBuildingUuid(project);
                    sourceSpace.setUserData(spaceProperty);
                    sourceSpace.setCreatedDate(new Date());
                    spaceService.insert(sourceSpace);
                }

            }else{
                return ServletUtils.buildJsonRs(false, "返回数组为空", null);
            }
            return ServletUtils.buildJsonRs(true, "同步完成", null);
        }else {
            return ServletUtils.buildJsonRs(false, spaceJsons.getString("message"), null);
        }
    }

    //同步构件
    @ResponseBody
    @RequestMapping(value = "/synElement")
    public JSONObject synElement(String project){
        int pageNum=0;//当前页码
        int pageSize=500;//一页多少条
        while(true){
            pageNum++;
            String paramStr="?project="+project+"&index="+pageNum+"&size="+pageSize;
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("project",project);
            map.put("index",pageNum);
            map.put("size",pageSize);
            String elementStr=HttpUtils.sendPost("http://222.77.181.112:1810/api/element/getList/"+ project,map,null);
            System.out.println(elementStr);
            JSONObject elementJsons=JSONObject.parseObject(elementStr);
            if(elementJsons.getInteger("status")==200){
                String msg = elementJsons.getString("data");
                JSONArray elementList = JSONObject.parseArray(msg);
//                JSONArray elementList=elementJsons.getJSONArray("data");
                if(elementList!=null&&elementList.size()>0){
                    if(pageNum==1){
                       elementService.delByBuildingUuid(project);
                    }
                    for(Object elementObj:elementList){
                        JSONObject elementJson=JSONObject.parseObject(JSONObject.toJSONString(elementObj));
                        String levelId=elementJson.getString("LevelId");//楼层id
                        String elementName=elementJson.getString("ElementName");//构件名称
                        String levelName=elementJson.getString("LevelName");//楼层名称
                        String elemType=elementJson.getString("ElemType");//ifc类别
                        String originalType=elementJson.getString("OriginalType");//系统中文名称标注
                        String globalId=elementJson.getString("GlobalId");//构件ID

                        SourceElement sourceElement=new SourceElement();
                        sourceElement.setUuid(globalId);
                        sourceElement.setBuildingUuid(project);
                        sourceElement.setStoreyUuid(levelId);
                        sourceElement.setStoreyName(levelName);
                        sourceElement.setElemenName(elementName);
                        sourceElement.setElemType(elemType);
                        sourceElement.setElemTypeName(originalType);
                        sourceElement.setCreatedDate(new Date());
                        elementService.insert(sourceElement);
                    }
                }else {
                    return ServletUtils.buildJsonRs(true, "同步成功", null);
                }
            }else{
                return ServletUtils.buildJsonRs(false, elementJsons.getString("message"), null);
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/findElementList")
    public JSONObject findElementList(SourceElement element){
        if(StringUtils.isBlank(element.getBuildingUuid())){
            return ServletUtils.buildJsonRs(false, "楼层编码不能为空", null);
        }
        try{
            List<SourceElement> list = elementService.findList(element);
            return ServletUtils.buildJsonRs(true, null, list);
        }catch (Exception e){
            return ServletUtils.buildJsonRs(false, e.getMessage(), null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/findSpaceList")
    public JSONObject findSpaceList(SourceSpace space){
        if(StringUtils.isBlank(space.getBuildingUuid())){
            return ServletUtils.buildJsonRs(false, "楼层编码不能为空", null);
        }
        try{
            List<SourceSpace> list = spaceService.findList(space);
            return ServletUtils.buildJsonRs(true, null, list);
        }catch (Exception e){
            return ServletUtils.buildJsonRs(false, e.getMessage(), null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/findStoreyList")
    public JSONObject findStoreyList(SourceStorey storey){
        if(StringUtils.isBlank(storey.getBuildingUuid())){
            return ServletUtils.buildJsonRs(false, "楼层编码不能为空", null);
        }
        try{
            List<SourceStorey> list = storeyService.findList(storey);
            return ServletUtils.buildJsonRs(true, null, list);
        }catch (Exception e){
            return ServletUtils.buildJsonRs(false, e.getMessage(), null);
        }
    }
    
    
    @ResponseBody
    @RequestMapping(value = "/getRealData")
    public JSONObject getRealData(String chid){
    	Map<String,Object> real_data = bimDataService.getRealDataByChId(chid);
    	return ServletUtils.buildJsonRs(true, null, real_data);
    }
    
    
}
