还我手机
===========
1. 几个配置项：
	isFirstStart    bool			是否是第一次运行
	tracking		bool			是否启用防盗功能
	guardNumber		string			守卫号码
	username		string			登录用户名
	password		string			登录密码
	subscriberId	string			手机卡IMSI
	sms_prefix		string			短信号码前缀（+86）
	
2. 命令格式
	命令需要加前缀"cmd:"+ 命令
	命令以及命令参数之间以;进行分隔，参数为key:value形式字符串
	
	
3. 计划
	完成各个命令的执行动作。
	对控制端发送控制消息进行限制，增加验证密码（cmd:+密码+//)
	增加网络同步功能，建立服务器，通过网络服务进行控制
	增加配置项的配置页面