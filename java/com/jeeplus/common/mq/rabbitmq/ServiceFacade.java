package com.jeeplus.common.mq.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;

import com.jeeplus.modules.settings.service.TChannelService;
import com.jeeplus.modules.settings.service.TDeviceDetailService;
import com.jeeplus.modules.settings.service.TDeviceService;
import com.jeeplus.modules.settings.service.TOrgService;
import com.jeeplus.modules.sys.dao.UserDao;
import com.jeeplus.modules.sys.service.AreaService;
import com.jeeplus.modules.sys.service.OfficeService;
import com.jeeplus.modules.sys.service.SystemService;
import com.jeeplus.modules.warm.dao.PdfOrderDao;
import com.jeeplus.modules.warm.service.PdfOrderService;

public class ServiceFacade {

	
	public SystemService systemService;
	
	public AreaService areaService;
	
	public OfficeService officeService;
	
	public PdfOrderService pdfOrderService;
	
	public TChannelService tChannelService;
	public TDeviceService tDeviceService;
	public TDeviceDetailService tDeviceDetailService;
	public TOrgService tOrgService;
	public UserDao userDao;
	
}
