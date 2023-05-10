package com.jeeplus.common.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.jeeplus.common.mq.rabbitmq.RabbitMQCustomer;
import com.jeeplus.common.mq.rabbitmq.ServiceFacade;
import com.jeeplus.modules.settings.service.TChannelService;
import com.jeeplus.modules.settings.service.TDeviceDetailService;
import com.jeeplus.modules.settings.service.TDeviceService;
import com.jeeplus.modules.settings.service.TOrgService;
import com.jeeplus.modules.sys.dao.UserDao;

public class CustomInitialization implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	RabbitMQCustomer rabbitMQCustomer;
	
	ServiceFacade serviceFacade = new ServiceFacade();
	
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// TODO Auto-generated method stub
		/**
		 * 系统中存在 root application context 和 spring mvc context
		 * 通过此句判断后，只在 root appliation 执行完成后执行一次 
		 */
		if(event.getApplicationContext().getParent() == null) {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			System.out.println("catalina.base========================"+System.getProperty("catalina.base"));

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			rabbitMQCustomer.setServiceFacade(serviceFacade);
			new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("====================startProcessData====================");
					rabbitMQCustomer.startProccessData();
				}
			}).start();
		}
	}

}
