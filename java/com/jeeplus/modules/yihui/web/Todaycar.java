package com.jeeplus.modules.yihui.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.jeeplus.modules.yihui.Utils.SignUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Todaycar {
    //今日在场所有车辆
    public static Integer todaycar() {
        try {
            String reqId = System.currentTimeMillis() + "";
            //  reqId = "1682234253663";
            System.out.println(reqId);
            JSONObject json = new JSONObject();
            json.put("parkId", "591010197");
            json.put("reqId", reqId);
            json.put("ts", reqId);
            json.put("appId", "12352");

            json.put("pageIndex", 1);
            json.put("pageSize", 1);
            json.put("serviceCode", "queryCarInfo");

            String key = SignUtils.paramsSign(json, "77461f671b9c4d7f80f4cd909bf94bf6");

            System.out.println("key====" + key);
            //      pageIndex=1&pageSize=1&parkId=591010197&reqId=1683511420263&serviceCode=queryCarInfo&ts=1683511420263&77461f671b9c4d7f80f4cd909bf94bf6
            URL url = new URL("https://kp-open.keytop.cn/unite-api/api/wec/QueryCarInfo");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            con.setRequestProperty("version", "1.0.0");
            con.setDoOutput(true);
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("appId", "12352");
            requestBody.addProperty("key", key);
            requestBody.addProperty("parkId", "591010197");
            requestBody.addProperty("ts", reqId);
            requestBody.addProperty("reqId", reqId);
            requestBody.addProperty("serviceCode", "queryCarInfo");
            requestBody.addProperty("pageIndex", "1");
            requestBody.addProperty("pageSize", "1");
            System.out.println(requestBody.toString());
            OutputStream os = con.getOutputStream();
            os.write(requestBody.toString().getBytes());
            os.flush();
            os.close();


            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            StringBuffer jsonString = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                jsonString.append(inputLine);

            }

            JSONObject jsonObject = JSON.parseObject(jsonString.toString());

            String dataString = jsonObject.getString("data");
            JSONObject dataObject = JSON.parseObject(dataString);

            int totalCount = dataObject.getIntValue("totalCount");
            System.out.println(totalCount);


            in.close();
            return totalCount;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
