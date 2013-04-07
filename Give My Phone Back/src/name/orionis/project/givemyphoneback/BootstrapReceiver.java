package name.orionis.project.givemyphoneback;

import name.orionis.project.givemyphoneback.helper.SmsHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
/**
 * 监听开机事件
 * @author code.404
 *
 */
public class BootstrapReceiver extends BroadcastReceiver {
	private SharedPreferences sharPreferences;
	
	final public static String CHANGE_SIM = "CHANGE_SIM";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		sharPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
		//没有启用跟踪
		if(!sharPreferences.getBoolean("tracking", false)){
			return;
		}
		//启用了跟踪
		//获取手机卡的IMSI
		TelephonyManager tm = (TelephonyManager) context.getSystemService(
				Context.TELEPHONY_SERVICE);
		String subscriberId = tm.getSubscriberId();
		//如果IMSI与存储的一致，则不需要提醒更换号码
		if(subscriberId.trim().equals(sharPreferences.getString("subscriberId", ""))){
			return ;
		}
		//IMSI不一致，说明已经换卡
		
		//发送给安全号码短消息，提醒换号
		SmsHelper smsHelper = SmsHelper.getInstance(context);
		smsHelper.sendMessage(sharPreferences.getString("guardNumber", "10010"), 
				context.getResources().getString(R.string.has_change_number),CHANGE_SIM);
	}
}
