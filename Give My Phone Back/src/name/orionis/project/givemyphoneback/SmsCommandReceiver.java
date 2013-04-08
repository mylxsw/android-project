package name.orionis.project.givemyphoneback;

import java.util.HashMap;
import java.util.Map;

import name.orionis.project.givemyphoneback.helper.EncryptHelper;
import name.orionis.project.givemyphoneback.helper.SmsHelper;
import name.orionis.project.givemyphoneback.helper.ToastHelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.PowerManager;
import android.sax.StartElementListener;
import android.telephony.SmsMessage;
import android.util.Log;
/**
 * 守卫端命令接收
 * @author code.404
 *
 */
public class SmsCommandReceiver extends BroadcastReceiver {
	private SharedPreferences sharPreferences;
	private Context context;
	public static final String SMS_IDENTIFY = "name.orionis.givemyphoneback.sendSmsTo";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("DDD","接收到短信命令广播");
		sharPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
		this.context = context;
		//没有启用跟踪
		if(!sharPreferences.getBoolean("tracking", false)){
			return;
		}
		//判断是否是守卫号码的短信
		Log.i("DDD","准备判断是否是守卫号码");
		if(intent != null && intent.getAction() != null){
			Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
			SmsMessage [] messages = new SmsMessage[pduArray.length];
			String sms_message = "";
			
			for(int i = 0; i < pduArray.length; i ++){
				messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
				//判断号码是否是来自守卫号码
				String senderNumber = sharPreferences.getString("sms_prefix", "") + messages[i].getOriginatingAddress().trim();
				if(!senderNumber.equals(sharPreferences.getString("guardNumber", ""))){
					Log.i("DDD","不是守卫号码发送的短信" + senderNumber);
					return;
				}
				
				//拼装短信
				sms_message += messages[i].getMessageBody();
			}
			Log.i("DDD","是守卫号码发送的短信，下一步进行判断命令");
			//如果消息不是以cmd:开头，则不是命令消息，直接返回
			if(!sms_message.startsWith("cmd:")){
				Log.i("DDD","不是命令短信");
				return;
			}
			Log.i("DDD","中断广播，准备执行命令");
			//命令消息处理
			//对命令消息，不应该让用户看到，因此，终止继续广播
			abortBroadcast();
			
			//校验安全Key是否正确
			int last_pos = sms_message.indexOf("//");
			String safe_key = sms_message.substring(4, last_pos);
			Log.i("DDD","校验安全密钥" + safe_key);
			if(!sharPreferences.getString("safe_key", "").equals(EncryptHelper.md5(safe_key))){
				return ;
			}
			Log.i("DDD","准备执行命令");
			Log.i("DDD","短信内容：" + sms_message);
			//执行命令
			try{
				executeCmd(sms_message.substring(last_pos + 2).trim());
			} catch(Exception e){
				Log.i("DDD","命令执行异常" + e.getLocalizedMessage());
				e.printStackTrace();
			}
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
			for(int i = 1; i < cmdParams.length; i ++){
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
			_cmd_reboot();
			break;
		case uploadcontract://上传所有联系人列表
			_cmd_uploadcontract();
			break;
		case getcontract://获取所有联系人
			_cmd_getcontract(params);
			break;
		case uploadmessage://上传所有短消息
			_cmd_uploadmessage(params);
			break;
		case sendsmsto://向指定用户发送短信
			_cmd_sendsmsto(params);
			break;
		case setval://修改配置文件
			_cmd_setval(params);
			break;
		case notify://提示消息
			_cmd_notify(params);
			break;
		default:
			break;
		}
	}

	private void _cmd_sendsmsto(Map<String, String> params) {
		if(!params.containsKey("who")){
			return;
		}
		if(!params.containsKey("msg")){
			return;
		}
		
		SmsHelper smsHelper = SmsHelper.getInstance(context);
		smsHelper.sendMessage(params.get("who"), params.get("msg"),SMS_IDENTIFY );
	}
	private void _cmd_uploadmessage(Map<String, String> params) {
		
	}
	private void _cmd_getcontract(Map<String, String> params) {
		
	}
	private void _cmd_uploadcontract() {
		
	}
	/**
	 * 执行重启命令
	 */
	private void _cmd_reboot() {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		pm.reboot("");
	}
	/**
	 * 执行关机动作
	 */
	private void _cmd_shutdown() {
		Intent intent = new Intent(Intent.ACTION_SHUTDOWN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	/**
	 * 修改配置项
	 * @param params
	 */
	private void _cmd_setval(Map<String, String> params) {
		String key = params.containsKey("key") ? params.get("key"):"";
		String val = params.containsKey("val") ? params.get("val"):"";
		
		if(key.equalsIgnoreCase("guardNumber")){
			int guardNumber = Integer.parseInt(val);
			if(guardNumber > 1000){
				Editor edit = sharPreferences.edit();
				edit.putString("guardNumber", guardNumber + "");
				edit.commit();
			}
		}
		if(key.equalsIgnoreCase("safe_key")){
			if(!val.trim().equals("")){
				Editor edit = sharPreferences.edit();
				edit.putString("guardNumber", EncryptHelper.md5(val));
				edit.commit();
			}
		}
	}
	
	/**
	 * 提示消息
	 * @param params
	 */
	@SuppressWarnings("deprecation")
	private void _cmd_notify(Map<String, String> params) {
//		ToastHelper.showMessage(context, params.get("msg"));
		String msg = params.containsKey("msg") ? params.get("msg")
				: context.getResources().getString(R.string.phone_not_belong_to_you);
		
		Intent i = new Intent();
    	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
    	
    	NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	
    	@SuppressWarnings("deprecation")
		Notification notif = new Notification(R.drawable.ic_launcher, msg,
    			System.currentTimeMillis());
    	
    	CharSequence from = context.getResources().getString(R.string.warning);
    	CharSequence message = msg;
    	
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
