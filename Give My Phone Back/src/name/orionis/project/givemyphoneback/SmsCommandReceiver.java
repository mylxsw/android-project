package name.orionis.project.givemyphoneback;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsMessage;
/**
 * 守卫端命令接收
 * @author code.404
 *
 */
public class SmsCommandReceiver extends BroadcastReceiver {
	private SharedPreferences sharPreferences;
	private Context context;
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	@Override
	public void onReceive(Context context, Intent intent) {
		sharPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
		this.context = context;
		//没有启用跟踪
		if(!sharPreferences.getBoolean("tracking", false)){
			return;
		}
		//判断是否是守卫号码的短信
		
		if(intent != null && intent.getAction() != null &&
				ACTION.compareToIgnoreCase(intent.getAction()) == 0){
			Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
			SmsMessage [] messages = new SmsMessage[pduArray.length];
			String sms_message = "";
			
			for(int i = 0; i < pduArray.length; i ++){
				messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
				//判断号码是否是来自守卫号码
				String senderNumber = sharPreferences.getString("sms_prefix", "+86") + messages[i].getOriginatingAddress().trim();
				if(!senderNumber.equals(sharPreferences.getString("guardNumber", ""))){
					return;
				}
				
				//拼装短信
				sms_message += messages[i].getMessageBody();
			}
			//如果消息不是以cmd:开头，则不是命令消息，直接返回
			if(!sms_message.startsWith("cmd:")){
				return;
			}
			//命令消息处理
			//对命令消息，不应该让用户看到，因此，终止继续广播
			abortBroadcast();
			
			executeCmd(sms_message.substring(4).trim());
			
		}
	}
	/**
	 * 执行命令
	 * @param trim
	 */
	private void executeCmd(String cmd) {
		//第1个值为命令，后面为参数
		String [] cmdParams = cmd.trim().split(";");
		//分隔命令参数，放到params中
		Map<String, String> params = new HashMap<String, String>();
		
		if(cmdParams.length > 1){
			for(int i = 1; i <= cmdParams.length; i ++){
				String[] p = cmdParams[i].split(":");
				if(p.length != 2 || p[0].trim().equals("")){
					continue;
				}
				params.put(p[0].trim(), p[1].trim());
			}
		}
		//对命令进行分发
		switch (Command.getCommand(cmdParams[0])) {
		case shutdown://执行关机命令
			_cmd_shutdown();
			break;
		case reboot://执行重启命令
			break;
		case uploadcontract://上传所有联系人列表
			break;
		case getcontract://获取所有联系人
			break;
		case uploadmessage://上传所有短消息
			break;
		case sendsmsto://向指定用户发送短信
			break;
		case setval://修改配置文件
			break;
		case notify://提示消息
			_cmd_notify(params);
			break;
		default:
			break;
		}
	}
	/**
	 * 执行关机动作
	 */
	private void _cmd_shutdown() {
		
	}
	/**
	 * 提示消息
	 * @param params
	 */
	@SuppressWarnings("deprecation")
	private void _cmd_notify(Map<String, String> params) {
		Intent i = new Intent();
    	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
    	
    	NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	@SuppressWarnings("deprecation")
		Notification notif = new Notification(R.drawable.ic_launcher, "Reminder：Meeting starts in 5 minutes",
    			System.currentTimeMillis());
    	
    	CharSequence from = "系统提示";
    	CharSequence message = "Meeting with customer at 3pm...";
    	
    	notif.setLatestEventInfo(context, from, message, pendingIntent);
    	
    	notif.vibrate = new long [] {100, 250, 100, 500};
    	nm.notify(1, notif);
		
	}
	/**
	 * 可用命令清单
	 * @author code.404
	 *
	 */
	private enum Command{
		shutdown, reboot, uploadcontract, getcontract, uploadmessage, none, setval, sendsmsto, notify;
		public static Command getCommand(String cmd){
			Command c;
			try{
				c = valueOf(cmd.toLowerCase());
			}catch (Exception e) {
				c = none;
			}
			return c;
		}
	}
}
