package com.jeeplus.modules.warm.web;

import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.warm.dao.PdfAlarmDao;
import com.jeeplus.modules.warm.dao.PdfOrderDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ZZUSER on 2018/12/7.
 */
@Controller
@RequestMapping("/warm")
public class PdfOrderDealController extends BaseController {
	
	
	@Autowired
	PdfAlarmDao pdfAlarmDao;
	
	
}
