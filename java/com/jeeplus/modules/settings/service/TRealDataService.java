package com.jeeplus.modules.settings.service;

import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.settings.dao.TRealDataDao;
import com.jeeplus.modules.settings.entity.TRealData;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2018-08-15.
 */
@Service
@Transactional(readOnly = true)
public class TRealDataService extends CrudService<TRealDataDao,TRealData> {
	
	
	
	
	
}
