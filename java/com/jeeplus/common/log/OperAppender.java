package com.jeeplus.common.log;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.log4j.jdbc.JDBCAppender;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import com.jeeplus.common.utils.SpringContextHolder;
import com.jeeplus.modules.enterprise.dao.TOperLogDao;
import com.jeeplus.modules.enterprise.entity.TAlarmLog;
import com.jeeplus.modules.enterprise.entity.TOperLog;
import com.jeeplus.modules.sys.utils.UserUtils;


@SuppressWarnings("all")
public class OperAppender extends JDBCAppender {
	
	@Autowired
	TOperLogDao tOperLogDao;
	
	private Pattern p = Pattern.compile("\\$args\\[[0-9]\\]{1}");
	
	public OperAppender(LoggingEvent logEvent){
		buffer.add(logEvent);
	}

	/**
	 * override the flushbuffer() to do the exactly db log insert.
	 * 
	 * @author chenhu
	 */
	public void flushBuffer() {
		// Do the actual logging
		String sql;
		removes.ensureCapacity(buffer.size());
		for (Iterator i = buffer.iterator(); i.hasNext();) {
			try {
				LoggingEvent logEvent = (LoggingEvent) i.next();
				if (logEvent instanceof LogEvent) {
					LogEvent logevent = (LogEvent) logEvent;
					sql = getLogStatement(logevent);
					
					removes.add(logEvent);
					//buffer.remove(logEvent);
					
					execute(sql, logevent);
				} else {
					// do nothing;
				}
				
			} catch (SQLException e) {
				errorHandler.error("Failed to excute sql", e,
						ErrorCode.FLUSH_FAILURE);
			}
		}

		// remove from the buffer any events that were reported
		buffer.removeAll(removes);
		// clear the buffer of reported events
		removes.clear();
	}

//	protected String getLogStatement(LogEvent event) {
//		return "insert into t_oper_log (id, add_time, ip, opr_content, log_code, user_id, dev_id, ch_id,creator,cr_date,cr_unit) " +
//				"values (seq_t_oper_log.nextval, now(),?,?,?,?,0,0,?,now(),?)";
//	}

	protected void execute(String sql, final LogEvent event) throws SQLException {
		
		TOperLog ol = new TOperLog();
		ol.setAddTime(new Date());
		if (null == event.getMsgObj().getIp()) {
			ol.setIp(event.getMsgObj().getIp());
		}
		ol.setOprContent(event.getMsgObj().getLogcontent());
		if (event.getMsgObj().getModelCode()==null) {
			ol.setLogCode(Long.parseLong(event.getMsgObj().getModelCode()));
		}
		ol.setUser(UserUtils.getUser());
		ol.setDevId(0L);
		ol.setChId(0L);
		ol.setCreator(UserUtils.getUser().getId());
		ol.setCrDate(new Date());
		ol.setCrUnit("");
		tOperLogDao.insert(ol);
		/*
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		jdbcTemplate.execute(sql, new  PreparedStatementCallback<Object>(){
			
			public Object doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				if (null == event.getMsgObj().getTime()) {
					event.getMsgObj().setTime(new Date());
				}
				
				if (null == event.getMsgObj().getIp()) {
					ps.setNull(1, java.sql.Types.VARCHAR);
				} else {
					ps.setString(1, event.getMsgObj().getIp());
				}
				//2012-11-01 
				String content = event.getMsgObj().getLogcontent();
				//替换空的args[0]
				content = p.matcher(content).replaceAll("");
				ps.setString(2, content);
				
				if (event.getMsgObj().getModelCode()==null) {
					ps.setNull(3, java.sql.Types.NUMERIC);
				} else {
					ps.setLong(3, Long.parseLong(event.getMsgObj().getModelCode()));
				}
				
				
				if ("" == event.getMsgObj().getUserid()) {
					ps.setNull(4, java.sql.Types.VARCHAR);
				} else {
					ps.setString(4, event.getMsgObj().getUserid());
				}
				ps.setString(5, UserUtils.getUser()!=null?UserUtils.getUser().getId().toString():"");
				ps.setString(6, UserUtils.getUser()!=null?UserUtils.getUser().getOffice().getId():"");
				ps.execute();
				
				return null;
			}
			
		});
		*/
//
//		Connection con = null;
//		PreparedStatement stmt = null;
//		try{
//			JdbcTemplate jdbcTemplate = (JdbcTemplate)SpringContextHolder.getApplicationContext().getBean("jdbcTemplate");
//		
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		try {
//			con = getConnection();
//			stmt = con.prepareStatement(sql);
//			if (null == event.getMsgObj().getTime()) {
//				event.getMsgObj().setTime(new Date());
//			}
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//			String timeString = sdf.format(event.getMsgObj().getTime());
//			stmt.setString(1, timeString);
//			if (null == event.getMsgObj().getClazz()) {
//				stmt.setNull(2, java.sql.Types.VARCHAR);
//			} else {
//				stmt.setString(2, event.getMsgObj().getClazz());
//			}
//			if (null == event.getMsgObj().getJavaFun()) {
//				stmt.setNull(3, java.sql.Types.VARCHAR);
//			} else {
//				stmt.setString(3, event.getMsgObj().getJavaFun());
//			}
//			if (null == event.getMsgObj().getIp()) {
//				stmt.setNull(4, java.sql.Types.VARCHAR);
//			} else {
//				stmt.setString(4, event.getMsgObj().getIp());
//			}
//			stmt.setString(5, event.getMsgObj().getLogcontent());
//			if (null == event.getMsgObj().getModelCode()) {
//				stmt.setNull(6, java.sql.Types.VARCHAR);
//			} else {
//				stmt.setString(6, event.getMsgObj().getModelCode());
//			}
//			if (0 == event.getMsgObj().getUserid()) {
//				stmt.setNull(7, java.sql.Types.NUMERIC);
//			} else {
//				stmt.setLong(7, event.getMsgObj().getUserid());
//			}
//			stmt.executeUpdate();
//			con.commit();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			if (stmt != null)
//				stmt.close();
//			con.rollback();
//			throw e;
//		}
//		stmt.close();
//		closeConnection(con);
	}
}
