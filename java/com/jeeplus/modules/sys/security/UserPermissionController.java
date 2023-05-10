package com.jeeplus.modules.sys.security;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.service.ServiceResult;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.modules.sys.service.UserPermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by LJR on 2019/1/23.
 */
@Controller
public class UserPermissionController {

    @Autowired
    private UserPermissionService userPermissionService;

    /**
     * 在登录完成后 返回前端token 前端再用token 请求一次该接口 返回该用户的所有权限
     * @param token
     * @return
     */
    @RequestMapping("permission")
    @ResponseBody
    public JSONObject getUserPermission(String token){
        ServiceResult result = userPermissionService.getUserPermission(token);
        return ServletUtils.buildJsonRs(result.isSuccess(),result.getMessage(),result.getData());
    }
}
