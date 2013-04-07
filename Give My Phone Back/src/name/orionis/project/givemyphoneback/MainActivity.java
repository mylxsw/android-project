package name.orionis.project.givemyphoneback;

import name.orionis.project.givemyphoneback.fragment.LoginDialogFragment;
import name.orionis.project.givemyphoneback.fragment.SetAccountDialogFragment;
import name.orionis.project.givemyphoneback.fragment.listener.FragmentActionListener;
import name.orionis.project.givemyphoneback.helper.ToastHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;


public class MainActivity extends FragmentActivity implements FragmentActionListener{

	private SharedPreferences sharedPreferences;
	private ToggleButton isStartBtn;
	private EditText guardNumber ;
	private Button changePasswordBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//读取配置
		sharedPreferences = getSharedPreferences("data", Activity.MODE_PRIVATE);
		//判断是否第一次登陆，如果是，则要求输入密码，否则进行登录
		boolean isFirstStart = sharedPreferences.getBoolean("isFirstStart", true);
		if(isFirstStart){//第一次登陆，要求设置密码账号
			SetAccountDialogFragment account = new SetAccountDialogFragment();
			account.show(getSupportFragmentManager(), "SetAccount");
		}else{//不是第一次登陆，要求登录账号密码
			LoginDialogFragment login = new LoginDialogFragment();
			login.show(getSupportFragmentManager(), "Login");
		}
	}
	/**
	 * 执行进入界面的初始化
	 * 应该保证该方法直接或者间接被onCreate方法调用
	 */
	private void init(){
		setContentView(R.layout.activity_main);
		
		isStartBtn = (ToggleButton) findViewById(R.id.isStartBtn);
		guardNumber = (EditText) findViewById(R.id.guardNumber);
		changePasswordBtn = (Button) findViewById(R.id.modifyPasswordBtn);
		
		guardNumber.setText(sharedPreferences.getString("guardNumber", ""));
		
		boolean isTracking = sharedPreferences.getBoolean("tracking", false);
		if(isTracking){//已经开启了追踪
			isStartBtn.setChecked(true);
			guardNumber.setEnabled(false);
		}else{//没有开启
			isStartBtn.setChecked(false);
			guardNumber.setEnabled(true);
		}
		
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
		
		changePasswordBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
				startActivity(intent);
			}
		});
	}
	private void _startTracking(){
		if(guardNumber.getText().toString().trim().equals("")){
			ToastHelper.showMessage(this, getResources().getString(R.string.guardNumberNotNull));
			isStartBtn.setChecked(false);
			return;
		}
		guardNumber.setEnabled(false);
		Editor edit = sharedPreferences.edit();
		edit.putBoolean("tracking", true);
		edit.putString("guardNumber", guardNumber.getText().toString());
		edit.commit();
		
		ToastHelper.showMessage(this, getResources().getString(R.string.guardServiceStarted));
	}
	private void _stopTracking(){
		guardNumber.setEnabled(true);
		Editor edit = sharedPreferences.edit();
		edit.putBoolean("tracking", false);
		edit.commit();
		
		ToastHelper.showMessage(this, getResources().getString(R.string.guardServiceStoped));
	}

	/**
	 * 按钮事件，来自Fragment
	 */
	@Override
	public void onFragmentAction(int action,Bundle data, Fragment fragment) {
		switch(action){
		case LoginDialogFragment.LOGIN_OK://登录确认
			_loginOk(action, data, fragment);
			break;
		case LoginDialogFragment.LOGIN_CANCEL://取消登录
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
			edit.putString("password", password);
			edit.commit();
			
			init();		
		}
	}
	private void _loginOk(int action,Bundle data, Fragment fragment){
		String username = data.getString("username");
		String password = data.getString("password");
		
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
