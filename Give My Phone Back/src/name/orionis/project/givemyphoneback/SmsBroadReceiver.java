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
 * ����Ϣ������ɺ�Ļ���
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
	 * �����Ƿ��Ѿ����ͳ�ȥ�Ĵ���
	 * @param context
	 * @param intent
	 */
	private void _smsSentDeal(Context context, Intent intent){
		
		//����SIM��
		if(identify.equals(BootstrapReceiver.CHANGE_SIM)){
			_simChanged();
		}
		//������ͨ����
		if(identify.equals(SmsCommandReceiver.SMS_IDENTIFY)){
			
		}
		
	}
	/**
	 * �����Ƿ���յ��Ĵ���
	 * @param context
	 * @param intent
	 */
	private void _smsDeliveredDeal(Context context, Intent intent){
		//����SIM��
//		if(identify.equals(BootstrapReceiver.CHANGE_SIM)){
//			_simChanged();
//		}
	}
	/**
	 * SIM���ı���Ϣ����
	 */
	private void _simChanged(){
		if(getResultCode() == Activity.RESULT_OK){//�޸ĵ�ǰ��IMSIΪ��ǰ�����IMSI
			SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
			Editor edit = preferences.edit();
			
			//��ȡ�ֻ�����IMSI
			TelephonyManager tm = (TelephonyManager) context.getSystemService(
					Context.TELEPHONY_SERVICE);
			edit.putString("subscriberId", tm.getSubscriberId());
			edit.commit();
		}
	}
}
