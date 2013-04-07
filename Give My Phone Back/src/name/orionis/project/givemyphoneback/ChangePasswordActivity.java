package name.orionis.project.givemyphoneback;

import name.orionis.project.givemyphoneback.helper.ToastHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.EditText;

public class ChangePasswordActivity extends Activity {
	
	private SharedPreferences sharedPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		
		sharedPreferences = getSharedPreferences("data", Activity.MODE_PRIVATE);
	}
	
	
	public void save(View view){
		EditText old_password = (EditText) findViewById(R.id.old_password);
		EditText new_password = (EditText) findViewById(R.id.new_password);
		EditText new_password_confirm = (EditText) findViewById(R.id.new_password_confirm);
	
		if(!new_password.getText().toString().equals(new_password_confirm.getText().toString())){
			ToastHelper.showMessage(this, getResources().getString(R.string.passwordNotSame));
			return;
		}
		if(new_password.getText().toString().trim().equals("")){
			ToastHelper.showMessage(this, getResources().getString(R.string.newPasswordNotNull));
			return;
		}
		
		if(sharedPreferences.getString("password", "").equals(old_password.getText().toString())){
			Editor edit = sharedPreferences.edit();
			edit.putString("password", new_password.getText().toString().trim());
			edit.commit();
			
			ToastHelper.showMessage(this, getResources().getString(R.string.optSuccess));
			finish();
		}else{
			ToastHelper.showMessage(this, getResources().getString(R.string.old_password_error));
		}
	}
	public void back(View view){
		finish();
	}
}
