package com.jeeplus.modules.bim.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.modules.bim.dao.BimDataDao;

@Service
public class BimDataService {

    @Autowired
    BimDataDao bimDataDao;
	
	public Map<String,Object> getRealDataByChId(String chid) {
		return bimDataDao.getRealDataByChId(chid);
	}

}
