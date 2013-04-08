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
	safe_key		String			安全密码，用于控制
2. 命令格式
	命令需要加前缀"cmd:安全密码+//"+ 命令
	命令以及命令参数之间以;进行分隔，参数为key:value形式字符串
	
	
3. 计划
	完成各个命令的执行动作。
	对控制端发送控制消息进行限制，增加验证密码（cmd:+安全密码+//)
	增加网络同步功能，建立服务器，通过网络服务进行控制
	增加配置项的配置页面
	
4. 命令详解
	注意：命令参数不支持使用系统保留的符号(;:)
	1) notify 提醒消息
		参数:message
		例如:cmd:safe_key//notify;msg:这是我的手机，请归还，谢谢！
	2) setval  修改配置
		参数：key,val
		例如：cmd:safe_key//setval;key:要修改的key;val：修改后的值
		可选项:guardNumber, safe_key
	3) sendsmsto 发送给xx短信
		参数: who,msg 
		