/**
 * 
 */
package com.jeeplus.modules.settings.entity;

import com.jeeplus.common.persistence.DataEntity;

/**
 * @author admin
 *
 */
public class PdfLink extends DataEntity<PdfLink>{

	
	private static final long serialVersionUID = 1L;

	private String name;
	private String nickname;
	private String orgId;
	private String notUse;
	
	
	
	
	
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
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	/**
	 * @return the orgId
	 */
	public String getOrgId() {
		return orgId;
	}
	/**
	 * @param orgId the orgId to set
	 */
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	/**
	 * @return the notUse
	 */
	public String getNotUse() {
		return notUse;
	}
	/**
	 * @param notUse the notUse to set
	 */
	public void setNotUse(String notUse) {
		this.notUse = notUse;
	}

	
	
	
	
	
	
}
