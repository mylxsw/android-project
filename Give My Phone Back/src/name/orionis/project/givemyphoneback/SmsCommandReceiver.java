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
 * �������������
 * @author code.404
 *
 */
public class SmsCommandReceiver extends BroadcastReceiver {
	private SharedPreferences sharPreferences;
	private Context context;
	public static final String SMS_IDENTIFY = "name.orionis.givemyphoneback.sendSmsTo";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("DDD","���յ���������㲥");
		sharPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
		this.context = context;
		//û�����ø���
		if(!sharPreferences.getBoolean("tracking", false)){
			return;
		}
		//�ж��Ƿ�����������Ķ���
		Log.i("DDD","׼���ж��Ƿ�����������");
		if(intent != null && intent.getAction() != null){
			Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
			SmsMessage [] messages = new SmsMessage[pduArray.length];
			String sms_message = "";
			
			for(int i = 0; i < pduArray.length; i ++){
				messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
				//�жϺ����Ƿ���������������
				String senderNumber = sharPreferences.getString("sms_prefix", "") + messages[i].getOriginatingAddress().trim();
				if(!senderNumber.equals(sharPreferences.getString("guardNumber", ""))){
					Log.i("DDD","�����������뷢�͵Ķ���" + senderNumber);
					return;
				}
				
				//ƴװ����
				sms_message += messages[i].getMessageBody();
			}
			Log.i("DDD","���������뷢�͵Ķ��ţ���һ�������ж�����");
			//�����Ϣ������cmd:��ͷ������������Ϣ��ֱ�ӷ���
			if(!sms_message.startsWith("cmd:")){
				Log.i("DDD","�����������");
				return;
			}
			Log.i("DDD","�жϹ㲥��׼��ִ������");
			//������Ϣ����
			//��������Ϣ����Ӧ�����û���������ˣ���ֹ�����㲥
			abortBroadcast();
			
			//У�鰲ȫKey�Ƿ���ȷ
			int last_pos = sms_message.indexOf("//");
			String safe_key = sms_message.substring(4, last_pos);
			Log.i("DDD","У�鰲ȫ��Կ" + safe_key);
			if(!sharPreferences.getString("safe_key", "").equals(EncryptHelper.md5(safe_key))){
				return ;
			}
			Log.i("DDD","׼��ִ������");
			Log.i("DDD","�������ݣ�" + sms_message);
			//ִ������
			try{
				executeCmd(sms_message.substring(last_pos + 2).trim());
			} catch(Exception e){
				Log.i("DDD","����ִ���쳣" + e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}
	/**
	 * ִ������
	 * @param trim
	 */
	private void executeCmd(String cmd) {
		//��1��ֵΪ�������Ϊ����
		String [] cmdParams = cmd.trim().split(";");
		//�ָ�����������ŵ�params��
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
		//��������зַ�
		switch (Command.getCommand(cmdParams[0])) {
		case shutdown://ִ�йػ�����
			_cmd_shutdown();
			break;
		case reboot://ִ����������
			_cmd_reboot();
			break;
		case uploadcontract://�ϴ�������ϵ���б�
			_cmd_uploadcontract();
			break;
		case getcontract://��ȡ������ϵ��
			_cmd_getcontract(params);
			break;
		case uploadmessage://�ϴ����ж���Ϣ
			_cmd_uploadmessage(params);
			break;
		case sendsmsto://��ָ���û����Ͷ���
			_cmd_sendsmsto(params);
			break;
		case setval://�޸������ļ�
			_cmd_setval(params);
			break;
		case notify://��ʾ��Ϣ
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
	 * ִ����������
	 */
	private void _cmd_reboot() {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		pm.reboot("");
	}
	/**
	 * ִ�йػ�����
	 */
	private void _cmd_shutdown() {
		Intent intent = new Intent(Intent.ACTION_SHUTDOWN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	/**
	 * �޸�������
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
	 * ��ʾ��Ϣ
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
	 * ���������嵥
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
