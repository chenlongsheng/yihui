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
import java.text.SimpleDateFormat;
import java.util.Date;

public class Intocar {

//今天进场车次
    public static Integer intocar() {
        try {
            String reqId = System.currentTimeMillis() + "";
            //  reqId = "1682234253663";
            System.out.println(reqId);
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            Date date = new Date(System.currentTimeMillis());
            String dat = formatter.format(date);

            System.out.println(dat);

            JSONObject json = new JSONObject();
            json.put("parkId", "591010197");
            json.put("reqId", reqId);
            json.put("ts", reqId);
            json.put("appId", "12352");
            json.put("startTime", dat);
            json.put("pageIndex",1);
            json.put("pageSize", 1);
            json.put("serviceCode", "getCarInoutInfo");

            String key = SignUtils.paramsSign(json, "77461f671b9c4d7f80f4cd909bf94bf6");

            System.out.println("key====" +key);
            //      pageIndex=1&pageSize=1&parkId=591010197&reqId=1683511420263&serviceCode=queryCarInfo&ts=1683511420263&77461f671b9c4d7f80f4cd909bf94bf6
            URL url = new URL("https://kp-open.keytop.cn/unite-api/api/wec/GetCarInoutInfo");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            con.setRequestProperty("version", "1.0.0");
            con.setDoOutput(true);
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("appId", "12352");

            requestBody.addProperty("parkId", "591010197");
            requestBody.addProperty("ts", reqId);
            requestBody.addProperty("reqId", reqId);
            requestBody.addProperty("serviceCode", "getCarInoutInfo");
            requestBody.addProperty("pageIndex", "1");
            requestBody.addProperty("pageSize", "1");
            requestBody.addProperty("startTime",dat);

            requestBody.addProperty("key", key);
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

//今天进场车次

            in.close();
            return totalCount;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
