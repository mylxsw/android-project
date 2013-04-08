package name.orionis.project.givemyphoneback;

import name.orionis.project.givemyphoneback.helper.SmsHelper;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
/**
 * 短消息发送完成后的回馈
 * @author code.404
 *
 */
public class SmsBroadReceiver extends BroadcastReceiver{
	private String identify;
	private Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		
		String action = intent.getAction();
		identify = intent.getStringExtra(SmsHelper.MESSAGE_IDENTIFY);
		if(action.equals(SmsHelper.SENT)){
			_smsSentDeal(context, intent);
		}else{
			_smsDeliveredDeal(context, intent);
		}
	}
	/**
	 * 短信是否已经发送出去的处理
	 * @param context
	 * @param intent
	 */
	private void _smsSentDeal(Context context, Intent intent){
		
		//更换SIM卡
		if(identify.equals(BootstrapReceiver.CHANGE_SIM)){
			_simChanged();
		}
		//发送普通短信
		if(identify.equals(SmsCommandReceiver.SMS_IDENTIFY)){
			
		}
		
	}
	/**
	 * 短信是否接收到的处理
	 * @param context
	 * @param intent
	 */
	private void _smsDeliveredDeal(Context context, Intent intent){
		//更换SIM卡
//		if(identify.equals(BootstrapReceiver.CHANGE_SIM)){
//			_simChanged();
//		}
	}
	/**
	 * SIM卡改变消息处理
	 */
	private void _simChanged(){
		if(getResultCode() == Activity.RESULT_OK){//修改当前的IMSI为当前号码的IMSI
			SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
			Editor edit = preferences.edit();
			
			//获取手机卡的IMSI
			TelephonyManager tm = (TelephonyManager) context.getSystemService(
					Context.TELEPHONY_SERVICE);
			edit.putString("subscriberId", tm.getSubscriberId());
			edit.commit();
		}
	}
}
