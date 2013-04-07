package name.orionis.project.givemyphoneback;

import name.orionis.project.givemyphoneback.helper.SmsHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
/**
 * ���������¼�
 * @author code.404
 *
 */
public class BootstrapReceiver extends BroadcastReceiver {
	private SharedPreferences sharPreferences;
	
	final public static String CHANGE_SIM = "CHANGE_SIM";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		sharPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
		//û�����ø���
		if(!sharPreferences.getBoolean("tracking", false)){
			return;
		}
		//�����˸���
		//��ȡ�ֻ�����IMSI
		TelephonyManager tm = (TelephonyManager) context.getSystemService(
				Context.TELEPHONY_SERVICE);
		String subscriberId = tm.getSubscriberId();
		//���IMSI��洢��һ�£�����Ҫ���Ѹ�������
		if(subscriberId.trim().equals(sharPreferences.getString("subscriberId", ""))){
			return ;
		}
		//IMSI��һ�£�˵���Ѿ�����
		
		//���͸���ȫ�������Ϣ�����ѻ���
		SmsHelper smsHelper = SmsHelper.getInstance(context);
		smsHelper.sendMessage(sharPreferences.getString("guardNumber", "10010"), 
				context.getResources().getString(R.string.has_change_number),CHANGE_SIM);
	}
}
