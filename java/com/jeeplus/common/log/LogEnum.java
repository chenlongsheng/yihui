package com.jeeplus.common.log;

public class LogEnum {
		public static final String LOG_CODE_CLIENT_LOGIN = "1";//客户端登陆")              , // 客户端登陆
		public static final String	LOG_CODE_CLIENT_LOGOUT = "2";//客户端退出")                , // 客户端退出
		public static final String    LOG_CODE_VIDEO_PREVIEW = "1001";//预览视频")          , // 预览视频
		public static final String	LOG_CODE_VIDEO_PREVIEW_STOP = "1002";//停止预览视频")           , // 停止预览视频
		public static final String	LOG_CODE_VIDEO_REAL_SOUND = "1003";//实时监听")             , // 实时监听
		public static final String    LOG_CODE_VIDEO_REAL_SOUND_STOP = "1004";//停止实时监听")        , // 停止实时监听
		public static final String	LOG_CODE_VIDEO_REAL_REC = "1005";//实时录像")               , // 实时录像
		public static final String	LOG_CODE_VIDEO_REAL_REC_STOP = "1006";//停止实时录像")          , // 停止实时录像
		public static final String	LOG_CODE_VIDEO_REAL_PIC = "1007";//实时截图")               , // 实时截图
		public static final String	LOG_CODE_VIDEO_EFFECT = "1008";//视频参数调节")                 , // 视频参数调节
		public static final String	LOG_CODE_VIDEO_PTZ = "1009";//云台控制")                    , // 云台控制
		public static final String	LOG_CODE_VIDEO_PTZ_STOP = "1010";//停止云台控制")                  , // 停止云台控制
		public static final String	LOG_CODE_VIDEO_REPLAY = "1801";//回放")          , // 回放
		public static final String	LOG_CODE_VIDEO_REPLAY_STOP = "1802";//停止回放")            , // 停止回放
		public static final String	LOG_CODE_VIDEO_REPLAY_SOUND = "1803";//回放时打开声音")           , // 回放时打开声音
		public static final String	LOG_CODE_VIDEO_REPLAY_SOUND_CLOSE = "1804";//回放时关闭声音")     , // 回放时关闭声音
		public static final String	LOG_CODE_VIDEO_REPLAY_REC = "1805";//回放录像")             , // 回放录像
		public static final String	LOG_CODE_VIDEO_REPLAY_REC_STOP = "1806";//回放录像")        , // 停止回放录像
		public static final String	LOG_CODE_VIDEO_REPLAY_PIC = "1807";//回放截图")             , // 回放截图
		public static final String	LOG_CODE_VIDEO_REPLAY_DOWN = "1808";//回放下载")            , // 回放下载
		public static final String	LOG_CODE_VIDEO_REPLAY_DOWN_STOP = "1809";//停止回放下载")       , // 停止回放下载
		public static final String	LOG_CODE_ALARM_BF = "2001";//布防")               , // 布防
		public static final String	LOG_CODE_ALARM_CF = "2002";//撤防")                      , // 撤防
		public static final String	LOG_CODE_ALARM_PASS = "2003";//旁路")                   , // 旁路
		public static final String	LOG_CODE_ALARM_DISPASS = "2004";//取消旁路")                , // 取消旁路
		public static final String	LOG_CODE_ALARM_CLEAR = "2005";//清除报警")                  , // 清除报警
		public static final String	LOG_CODE_ALARM_OUT = "2601";//控制输出")              , // 控制输出
		public static final String	LOG_CODE_ALARM_OUT_CLOSE = "2602";//关闭控制输出")              , // 关闭控制输出
		public static final String	LOG_CODE_ALARM_DO = "2701";//报警处理")              , // 报警处理
		public static final String	LOG_CODE_DEV_CFG_TIME = "3001";//设备校时")           , // 设备校时
		public static final String	LOG_CODE_DEV_CFG_NAME = "3002";//设备名称修改")                 , // 设备名称修改
		public static final String	LOG_CODE_DEV_CFG_CH_NAME = "3003";//设备通道名称修改")              , // 设备通道名称修改
		public static final String	LOG_CODE_SYS_CFG_DEV_ADD = "4001";//增加设备到数据库")        , // 增加设备到数据库
		public static final String    LOG_CODE_SYS_CFG_DEV_DEL = "4002";//删除设备到数据库")              , // 删除
		public static final String	LOG_CODE_SYS_CFG_DEV_EDT = "4003";//修改设备到数据库")              , // 修改
		public static final String	LOG_CODE_SYS_CFG_CH_ADD = "4101";//添加通道到数据库")         , //
		public static final String	LOG_CODE_SYS_CFG_CH_DEL = "4102";//删除通道到数据库")               , //
		public static final String	LOG_CODE_SYS_CFG_CH_EDT = "4103";//修改通道到数据库")               , //
		public static final String	LOG_CODE_SYS_CFG_USER_ADD = "4201";//添加用户到数据库")       , //
		public static final String	LOG_CODE_SYS_CFG_USER_DEL = "4202";//删除用户到数据库")             , //
		public static final String	LOG_CODE_SYS_CFG_USER_EDT = "4203";//修改用户到数据库")             , //
		//	-------------------------后台日志--------------------
		public static final String	LOG_CODE_SYS_HTXT_ORG = "10001";//区域管理")             , //
		public static final String	LOG_CODE_SYS_HTXT_SERVICE = "10002";//服务管理")             , //
		public static final String	LOG_CODE_SYS_HTXT_DEV = "10003";//设备管理")             , //
		public static final String	LOG_CODE_SYS_HTXT_CHANNEL = "10004";//通道管理")             , //
		public static final String	LOG_CODE_SYS_HTXT_MAP = "10005";//地图配置")             , //
		public static final String	LOG_CODE_SYS_HTXT_LINK = "10006";//联动配置")             , //
		public static final String	LOG_CODE_SYS_HTXT_ALARM_PARAM = "10007";//报警参数配置")             , //
		public static final String	LOG_CODE_SYS_HTXT_MENU = "10008";//菜单管理")             , //
		public static final String	LOG_CODE_SYS_HTXT_COMPANY = "10009";//组织机构管理")             , //
		public static final String	LOG_CODE_SYS_HTXT_DEPARTMENT = "10010";//部门管理")             , //
		public static final String	LOG_CODE_SYS_HTXT_USER = "10011";//用户管理")             , //
		public static final String	LOG_CODE_SYS_HTXT_ROLE = "10012";//角色管理")             ; //
		
		
		public static final String LOG_CODE_SYS_SEARCH_ALARM_LOG = "10013";//报警日志查询;
		public static final String LOG_CODE_SYS_ADD_ALARM_LOG = "10014";//报警日志新增;
		public static final String LOG_CODE_SYS_DELETE_ALARM_LOG = "10015";//报警日志删除;
		
		
		public static final String LOG_CODE_SYS_SEARCH_DEV_FAULT = "10016";//设备状态查询;
		public static final String LOG_CODE_SYS_ADD_DEV_FAULT = "10017";//设备状态新增;
		public static final String LOG_CODE_SYS_DELETE_DEV_FAULT = "10018";//设备状态删除;
		
		public static final String LOG_CODE_SYS_SEARCH_OPER_LOG = "10019";//操作日志查询;
		public static final String LOG_CODE_SYS_ADD_OPER_LOG = "10020";//操作日志新增;
		public static final String LOG_CODE_SYS_DELETE_OPER_LOG = "10021";//操作日志删除;
		
		public static final String LOG_CODE_SYS_SEARCH_PRE_ALARM = "10022";//预警配置查询;
		public static final String LOG_CODE_SYS_ADD_PRE_ALARM = "10023";//预警配置新增;
		public static final String LOG_CODE_SYS_DELETE_PRE_ALARM = "10024";//预警配置删除;
		
		public static final String LOG_CODE_SET_SEARCH_ALARM_LEV_TYPE = "10025";//通道默认策略查询;
		public static final String LOG_CODE_SET_ADD_ALARM_LEV_TYPE= "10026";//通道默认策略新增;
		public static final String LOG_CODE_SET_DELETE_ALARM_LEV_TYPE = "10027";//通道默认策略删除;
		
		public static final String LOG_CODE_SET_SEARCH_CHANNEL = "10028";//通道管理查询;
		public static final String LOG_CODE_SET_ADD_CHANNEL= "10029";//通道管理新增;
		public static final String LOG_CODE_SET_DELETE_CHANNEL = "10030";//通道管理删除;
		
		public static final String LOG_CODE_SET_SEARCH_CHANNEL_GROUP = "10031";//通道组管理查询;
		public static final String LOG_CODE_SET_ADD_CHANNEL_GROUP= "10032";//通道组管理新增;
		public static final String LOG_CODE_SET_DELETE_CHANNEL_GROUP = "10033";//通道组管理删除;
		
		public static final String LOG_CODE_SET_SEARCH_CHANNEL_POLICY = "10034";//通道策略查询;
		public static final String LOG_CODE_SET_ADD_CHANNEL_POLICY = "10035";//通道策略新增;
		public static final String LOG_CODE_SET_DELETE_CHANNEL_POLICY = "10036";//通道策略删除;
		
		public static final String LOG_CODE_SET_SEARCH_CODE = "10037";//code查询;
		public static final String LOG_CODE_SET_ADD_CODE = "10038";//code新增;
		public static final String LOG_CODE_SET_DELETE_CODE = "10039";//code删除;
		
		public static final String LOG_CODE_SET_SEARCH_CODE_TYPE = "10040";//codetype查询;
		public static final String LOG_CODE_SET_ADD_CODE_TYPE = "10041";//codetype新增;
		public static final String LOG_CODE_SET_DELETE_CODE_TYPE = "10042";//codetype删除;
		
		public static final String LOG_CODE_SET_SEARCH_DEVICE = "10043";//device查询;
		public static final String LOG_CODE_SET_ADD_DEVICE = "10044";//device新增;
		public static final String LOG_CODE_SET_DELETE_DEVICE = "10045";//device删除;
		
		public static final String LOG_CODE_SET_SEARCH_DVR = "10046";//dvr查询;
		public static final String LOG_CODE_SET_ADD_DVR = "10047";//dvr新增;
		public static final String LOG_CODE_SET_DELETE_DVR = "10048";//dvr删除;
		
		public static final String LOG_CODE_SET_SEARCH_ORG = "10049";//区域管理查询;
		public static final String LOG_CODE_SET_ADD_ORG = "10050";//区域管理新增;
		public static final String LOG_CODE_SET_DELETE_ORG = "10051";//区域管理删除;
		
		public static final String LOG_CODE_SET_SEARCH_REAL_DATA = "10052";//实时数据查询;
		public static final String LOG_CODE_SET_ADD_REAL_DATA = "10053";//实时数据新增;
		public static final String LOG_CODE_SET_DELETE_REAL_DATA = "10054";//实时数据删除;
		
		public static final String LOG_CODE_SET_SEARCH_EXPER_MODEL = "10055";//专家模式查询;
		public static final String LOG_CODE_SET_ADD_EXPER_MODEL = "10056";//专家模式新增;
		public static final String LOG_CODE_SET_DELETE_EXPER_MODEL = "10057";//专家模式删除;
		
		public static final String LOG_CODE_SET_SEARCH_MODEL_STEP = "10058";//专家模式步骤查询;
		public static final String LOG_CODE_SET_ADD_MODEL_STEP = "10059";//专家模式步骤新增;
		public static final String LOG_CODE_SET_DELETE_MODEL_STEP = "10060";//专家模式步骤删除;
		
		public static final String LOG_CODE_SET_SEARCH_MODEL_PREALAEM = "10061";//专家系统配置查询;
		public static final String LOG_CODE_SET_ADD_MODEL_PREALAEM = "10062";//专家系统配置新增;
		public static final String LOG_CODE_SET_DELETE_MODEL_PREALAEM = "10063";//专家系统配置删除;
		
		public static final String LOG_CODE_SET_SEARCH_PREALAEM_SETTINGS = "10064";//预警配置查询;
		public static final String LOG_CODE_SET_ADD_PREALAEM_SETTINGS = "10065";//预警配置新增;
		public static final String LOG_CODE_SET_DELETE_PREALAEM_SETTINGS = "10066";//预警配置删除;
		
		public static final String LOG_CODE_SET_SEARCH_BACK_CTRL = "10067";//反馈控制详细查询;
		public static final String LOG_CODE_SET_ADD_BACK_CTRL = "10068";//反馈控制详细新增;
		public static final String LOG_CODE_SET_DELETE_BACK_CTRL = "10069";//反馈控制详细删除;
		
		public static final String LOG_CODE_SET_SEARCH_TIMING_CTRL = "10070";//定时控制查询;
		public static final String LOG_CODE_SET_ADD_TIMING_CTRL = "100671";//定时控制新增;
		public static final String LOG_CODE_SET_DELETE_TIMING_CTRL = "10072";//定时控制删除;
		
		public static final String LOG_CODE_SET_SEARCH_EXPER_CONFIG = "10073";//场景专家模式配置查询;
		public static final String LOG_CODE_SET_ADD_EXPER_CONFIG = "100674";//场景专家模式配置新增;
		public static final String LOG_CODE_SET_DELETE_EXPER_CONFIG = "10075";//场景专家模式配置删除;
		
}
