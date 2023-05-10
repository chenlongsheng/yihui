package com.jeeplus.modules.qxz.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.qxz.entity.GzhUser;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.sys.entity.Role;

import java.util.List;
import java.util.Map;

/**
 * Created by 63446 on 2018/11/6.
 */
@MyBatisDao
public interface GzhUserDao extends CrudDao<GzhUser> {

    List<GzhUser> findGzhUser(GzhUser gzhUser);//获取公众号用户

    void addGzhUser(GzhUser gzhUser);

    void updateGzhUser(GzhUser gzhUser);

    void deleteUser(String[] arr);

    List<Map> findOrgTree(TOrg tOrg);

    List<Map> findListByPage(GzhUser gzhUser);

    int count(GzhUser gzhUser);

    List<Map> roleTree(List list);

    List<Map> plotsTree(TOrg tOrg);

    List<Map> orgRoleList(String[] arr);

    List<Map> roleList(String[] arr);

    List<TOrg> findOrgByArr(String[] arr);

    List<GzhUser> getAllGzhUser();

    List<Role> findRoleByArr(String[] arr);
}
