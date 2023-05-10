/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.sys.entity.Role;

/**
 * 角色DAO接口
 * @author jeeplus
 * @version 2013-12-05
 */
@MyBatisDao
public interface RoleDao extends CrudDao<Role> {

	public Role getByName(Role role);
	
	public Role getByEnname(Role role);
	/**
	 * 维护角色与菜单权限关系
	 * @param role
	 * @return
	 */
	public int deleteRoleMenu(Role role);

	public int insertRoleMenu(Role role);	
	/**
	 * 维护角色与公司部门关系
	 * @param role
	 * @return
	 */
	public int deleteRoleOffice(Role role);    
    
	public int insertRoleOffice(Role role);
	
	List<String> roleMenuList(@Param("id")String id);
	
	List<MapEntity> getUserList(MapEntity entity);
	
	Set<MapEntity> roleListByUserId(@Param("userId")String userId);
	
	void updateVisible(@Param("visible")String visible ,@Param("roleId")String roleId);
	
	
	List<String> rolesByUserId(@Param("userId")String userId);
	
	Set<MapEntity> getMenusByPId(@Param("pIds")String pIds);

}
