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
 * �������������
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
		//û�����ø���
		if(!sharPreferences.getBoolean("tracking", false)){
			return;
		}
		//�ж��Ƿ�����������Ķ���
		
		if(intent != null && intent.getAction() != null &&
				ACTION.compareToIgnoreCase(intent.getAction()) == 0){
			Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
			SmsMessage [] messages = new SmsMessage[pduArray.length];
			String sms_message = "";
			
			for(int i = 0; i < pduArray.length; i ++){
				messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
				//�жϺ����Ƿ���������������
				String senderNumber = sharPreferences.getString("sms_prefix", "+86") + messages[i].getOriginatingAddress().trim();
				if(!senderNumber.equals(sharPreferences.getString("guardNumber", ""))){
					return;
				}
				
				//ƴװ����
				sms_message += messages[i].getMessageBody();
			}
			//�����Ϣ������cmd:��ͷ������������Ϣ��ֱ�ӷ���
			if(!sms_message.startsWith("cmd:")){
				return;
			}
			//������Ϣ����
			//��������Ϣ����Ӧ�����û���������ˣ���ֹ�����㲥
			abortBroadcast();
			
			executeCmd(sms_message.substring(4).trim());
			
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
			for(int i = 1; i <= cmdParams.length; i ++){
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
			break;
		case uploadcontract://�ϴ�������ϵ���б�
			break;
		case getcontract://��ȡ������ϵ��
			break;
		case uploadmessage://�ϴ����ж���Ϣ
			break;
		case sendsmsto://��ָ���û����Ͷ���
			break;
		case setval://�޸������ļ�
			break;
		case notify://��ʾ��Ϣ
			_cmd_notify(params);
			break;
		default:
			break;
		}
	}
	/**
	 * ִ�йػ�����
	 */
	private void _cmd_shutdown() {
		
	}
	/**
	 * ��ʾ��Ϣ
	 * @param params
	 */
	@SuppressWarnings("deprecation")
	private void _cmd_notify(Map<String, String> params) {
		Intent i = new Intent();
    	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
    	
    	NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	@SuppressWarnings("deprecation")
		Notification notif = new Notification(R.drawable.ic_launcher, "Reminder��Meeting starts in 5 minutes",
    			System.currentTimeMillis());
    	
    	CharSequence from = "ϵͳ��ʾ";
    	CharSequence message = "Meeting with customer at 3pm...";
    	
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
