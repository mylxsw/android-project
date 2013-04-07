package name.orionis.project.givemyphoneback;

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
		
		
		
	}

}
