package com.jeeplus.modules.warm.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeeplus.common.persistence.MapEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.warm.service.PdfPrincipalService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZZUSER on 2018/12/7.
 */
@Controller
@RequestMapping("/testhistory")
public class test extends BaseController {

    @Autowired
    PdfPrincipalService pdfPrincipalService;

    @Autowired
    RedisTemplate<String, Object> redisTempldate;


    @RequestMapping("/selecthis")
    @ResponseBody
    public String selecthis() {


        List<String> list = new ArrayList<>();

        //        Iterator iterator = list.iterator();
//        while (iterator.hasNext()) {
//            Object next = iterator.next();
//        }
//         iter 争强for循环
//         itit 正规迭代器
//        itco   遍历迭代器;
//        itli  正常比例;

        Iterator iterator =  list.iterator();
        while (iterator.hasNext()) {
            Object next =  iterator.next();
        }

        Collection<String> con = new ArrayList<>();


        HashSet<MapEntity> set1 = new HashSet<>();

         //追加的方式添加元素
        con.add("东邪");
        //删除,通过元素名称删除元素
        System.out.println(con.remove("西毒"));
        //判断集合中是否包含指定参数元素
        System.out.println(con.contains("西毒"));  //false
        //判断是否为空
        System.out.println(con.isEmpty());



        
        // redisTempldate.opsForHash().multiGet("rrr");
        Map map = new HashMap();
        map.put("k1", "v1");
        map.put("k2", "v2");

        redisTempldate.opsForHash().putAll("key", map);
        redisTempldate.opsForHash().put("test", "123456", "value123");
        redisTempldate.opsForHash().put("test", "map2", "map2-2");

        Object a = redisTempldate.opsForHash().get("test", "123456");
        return ServletUtils.buildRs(true, "", a);
    }


    public static void main(String[] args) {


//        JedisPool pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1");
//        Jedis jedis = pool.getResource();
//        System.out.println("======");
    }
}
