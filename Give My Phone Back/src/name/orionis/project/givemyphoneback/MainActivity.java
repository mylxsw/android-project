package name.orionis.project.givemyphoneback;

import java.util.Map;
import java.util.Set;

import name.orionis.project.givemyphoneback.fragment.LoginDialogFragment;
import name.orionis.project.givemyphoneback.fragment.SetAccountDialogFragment;
import name.orionis.project.givemyphoneback.fragment.listener.FragmentActionListener;
import name.orionis.project.givemyphoneback.helper.EncryptHelper;
import name.orionis.project.givemyphoneback.helper.ToastHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends FragmentActivity implements FragmentActionListener{

	private SharedPreferences sharedPreferences;
	private ToggleButton isStartBtn;
	private EditText guardNumber ;
	private Button changePasswordBtn;
	private EditText safe_password_c ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//��ȡ����
		sharedPreferences = getSharedPreferences("data", Activity.MODE_PRIVATE);
		//�ж��Ƿ��һ�ε�½������ǣ���Ҫ���������룬������е�¼
		boolean isFirstStart = sharedPreferences.getBoolean("isFirstStart", true);
		if(isFirstStart){//��һ�ε�½��Ҫ�����������˺�
			SetAccountDialogFragment account = new SetAccountDialogFragment();
			account.show(getSupportFragmentManager(), "SetAccount");
		}else{//���ǵ�һ�ε�½��Ҫ���¼�˺�����
			LoginDialogFragment login = new LoginDialogFragment();
			login.show(getSupportFragmentManager(), "Login");
		}
	}
	/**
	 * ִ�н������ĳ�ʼ��
	 * Ӧ�ñ�֤�÷���ֱ�ӻ��߼�ӱ�onCreate��������
	 */
	private void init(){
		setContentView(R.layout.activity_main);
		
		isStartBtn = (ToggleButton) findViewById(R.id.isStartBtn);
		guardNumber = (EditText) findViewById(R.id.guardNumber);
		changePasswordBtn = (Button) findViewById(R.id.modifyPasswordBtn);
		safe_password_c = (EditText) findViewById(R.id.safe_password);
		
		guardNumber.setText(sharedPreferences.getString("guardNumber", ""));
		
		
		boolean isTracking = sharedPreferences.getBoolean("tracking", false);
		if(isTracking){//�Ѿ�������׷��
			isStartBtn.setChecked(true);
			guardNumber.setEnabled(false);
			safe_password_c.setEnabled(false);
		}else{//û�п���
			isStartBtn.setChecked(false);
			guardNumber.setEnabled(true);
			safe_password_c.setEnabled(true);
		}
		//��ʼ׷�ٰ�ť�¼�
		isStartBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					_startTracking();
				}else{
					_stopTracking();
				}
			}
		});
		//�޸����밴ť�¼�
		changePasswordBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
				startActivity(intent);
			}
		});
		
		//���ڵ���
		StringBuilder debuginfo = new StringBuilder();
		
		Map<String, ?> map = sharedPreferences.getAll();
		Set<String> keySet = map.keySet();
		for(String key : keySet){
			debuginfo.append("\n" + key + ":" + map.get(key).toString());
		}
		TextView textView = (TextView) findViewById(R.id.configInfo);
		textView.setText(debuginfo.toString());
	}
	/**
	 * ����׷��
	 */
	private void _startTracking(){
		//��������Ϊ�յĻ������������
		if(guardNumber.getText().toString().trim().equals("")){
			ToastHelper.showMessage(this, getResources().getString(R.string.guardNumberNotNull));
			isStartBtn.setChecked(false);
			return;
		}
		//��ȫ���벻��Ϊ��
		String safe_password = safe_password_c.getText().toString().trim();
		boolean flag = true;//��־��true����Ҫ��������
		if(safe_password.equals("")){
			String saved_pwd = sharedPreferences.getString("safe_key", "");
			flag = saved_pwd.equals("");
			if(flag){
				ToastHelper.showMessage(this, getResources().getString(R.string.safePasswordMustSet));
				isStartBtn.setChecked(false);
				return;
			}
		}
		//����������Ϣ
		guardNumber.setEnabled(false);
		safe_password_c.setEnabled(false);
		Editor edit = sharedPreferences.edit();
		edit.putBoolean("tracking", true);
		edit.putString("guardNumber", guardNumber.getText().toString());
		if(flag)
			edit.putString("safe_key", EncryptHelper.md5(safe_password));
		
		TelephonyManager tm = (TelephonyManager) getSystemService(
				Context.TELEPHONY_SERVICE);
		edit.putString("subscriberId", tm.getSubscriberId());
		
		edit.commit();
		
		ToastHelper.showMessage(this, getResources().getString(R.string.guardServiceStarted));
	}
	/**
	 * ֹͣ����
	 */
	private void _stopTracking(){
		guardNumber.setEnabled(true);
		safe_password_c.setEnabled(true);
		Editor edit = sharedPreferences.edit();
		edit.putBoolean("tracking", false);
		edit.commit();
		
		ToastHelper.showMessage(this, getResources().getString(R.string.guardServiceStoped));
	}

	/**
	 * ��ť�¼�������Fragment
	 */
	@Override
	public void onFragmentAction(int action,Bundle data, Fragment fragment) {
		switch(action){
		case LoginDialogFragment.LOGIN_OK://��¼ȷ��
			_loginOk(action, data, fragment);
			break;
		case LoginDialogFragment.LOGIN_CANCEL://ȡ����¼
			_closeFragmentDialog(fragment);
			break;
		case SetAccountDialogFragment.SET_ACCOUNT_OK:
			_setAccountOk(action, data, fragment);
		break;
		case SetAccountDialogFragment.SET_ACCOUNT_CANCEL:
			_closeFragmentDialog(fragment);
			break;
		}
	}
	
	private void _setAccountOk(int action, Bundle data, Fragment fragment) {
		String username = data.getString("username").trim();
		String password = data.getString("password").trim();
		if("".equals(username) || "".equals(password)){
			ToastHelper.showMessage(this, getResources().getString(R.string.tip_usernameOrPaswordWrong));
		}else{
			((DialogFragment) fragment).dismiss();
			
			Editor edit = sharedPreferences.edit();
			edit.putBoolean("isFirstStart", false);
			edit.putString("username", username);
			edit.putString("password", EncryptHelper.md5(password));
			edit.commit();
			
			init();		
		}
	}
	private void _loginOk(int action,Bundle data, Fragment fragment){
		String username = data.getString("username");
		String password = EncryptHelper.md5(data.getString("password"));
		
		String v_username = sharedPreferences.getString("username", "");
		String v_password = sharedPreferences.getString("password", "");
		
		if(v_username.equals(username) && v_password.equals(password)){
			((LoginDialogFragment) fragment).dismiss();
			init();
		}else{
			ToastHelper.showMessage(this, getResources().getString(R.string.tip_usernameOrPaswordWrong));
		}
	}
	private void _closeFragmentDialog( Fragment fragment){
		((DialogFragment) fragment).dismiss();
		finish();
	}

}
