package com.jeeplus.modules.warm.entity;

import com.jeeplus.common.persistence.DataEntity;

/** openid绑定
 * Created by ZZUSER on 2018/12/12.
 */
public class PdfBind extends DataEntity<PdfBind> {

    private String openId;

    private String userId;
    
    private String email;
    
    private String mobile;
    

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
    
    
    
}
