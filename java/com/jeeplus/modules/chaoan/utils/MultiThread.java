/**
 * 
 */
package com.jeeplus.modules.chaoan.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.modules.chaoan.dao.EnvironmentalDao;

/**
 * @author admin
 *
 */
@Service
@Transactional(readOnly = true)
public class MultiThread implements Runnable {

	@Autowired
	private EnvironmentalDao environmentalDao;
	
	@Override
	public void run() {
			    	
		while (true) {
        
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
