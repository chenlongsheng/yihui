package com.jeeplus.common.utils;

import com.jeeplus.modules.sys.utils.UserUtils;

/**获取当前登录用户所属orgId
 * Created by ZZUSER on 2019/1/11.
 */
public class OrgUtil {

    public static String getOrgId(){
   	
    	    	
        return UserUtils.getUser().getArea().getId();
    }

    public static String getUserId(){
    
        return UserUtils.getUser().getId();
    }
}
