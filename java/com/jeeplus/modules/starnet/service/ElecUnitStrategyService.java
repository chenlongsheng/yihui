package com.jeeplus.modules.starnet.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.starnet.dao.ElecUnitStrategyDao;
import com.jeeplus.modules.starnet.entity.ElecUnitStrategy;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = false)
public class ElecUnitStrategyService extends CrudService<ElecUnitStrategyDao, ElecUnitStrategy> {
	

	public void add(ElecUnitStrategy eus) {
		dao.insert(eus);
	}

	public void update(ElecUnitStrategy eus) {
		dao.update(eus);
	}

	public void delete(ElecUnitStrategy eus) {
		dao.delete(eus);
		
	}

	public ElecUnitStrategy get(ElecUnitStrategy eus) {
		return dao.get(eus); 
	}

	public String getUnitIdByLoopOrgCode(String orgCode) {
		return dao.getUnitIdByLoopOrgCode(orgCode);
	}

}
