package name.orionis.project.givemyphoneback.helper;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
/**
 * 短消息助手
 * @author code.404
 *
 */
public class SmsHelper {
	
	final public static String SENT = "name.orionis.project.helper.SMS_SENT";
	final public static String DELIVERED = "name.orionis.project.helper.SMS_DELIVERED";
	final public static String MESSAGE_IDENTIFY = "identify";
	
	private static SmsHelper smsHelper = null;
	private SmsManager smsManager;
	
	private Context context;
	
	private SmsHelper(){}
	private SmsHelper(Context context){
		smsManager = SmsManager.getDefault();
		this.context = context;
	}
	/**
	 * 获取短消息助手实例
	 * @param context
	 * @return
	 */
	public static SmsHelper getInstance(Context context){
		if(smsHelper == null){
			smsHelper = new SmsHelper(context);
		}
		return smsHelper;
	}
	/**
	 * 发送短消息
	 * @param destination
	 * @param message
	 */
	public void sendMessage(String destination, String message, String identify){
		Intent sent_intent = new Intent();
		sent_intent.setAction(SENT);
		sent_intent.putExtra(MESSAGE_IDENTIFY, identify );
		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sent_intent, 0);
		
		
		Intent delivered_intent = new Intent();
		delivered_intent.setAction(DELIVERED);
		delivered_intent.putExtra(MESSAGE_IDENTIFY, identify );
		PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, delivered_intent, 0);
		
		ArrayList<String> messages = smsManager.divideMessage(message);
		for(String msg : messages){
			smsManager.sendTextMessage(destination, null, msg, sentPI, deliveredPI);
		}
	}
}
