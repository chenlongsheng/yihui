/**
 * 
 */
package com.jeeplus.common.scheduled;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jeeplus.common.persistence.MapEntity;
 

/**
 * @author admin
 *
 */
@Service
@Lazy(false)
@EnableScheduling
public class PayScheduledService {
	
	
//	@Autowired
//	BalanceDao balanceDao;
//	
//	  @Scheduled(cron="0 */1 * * * ?")
//   private void loadLoopData() {
//		  
//		  
//		  
//		  
//		  
//	  List<MapEntity> getElecNumByMinute = balanceDao.getElecNumByMinute();
// 
//		  
//		  
//	  }

}
