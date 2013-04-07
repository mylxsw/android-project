package name.orionis.project.givemyphoneback.fragment;

import name.orionis.project.givemyphoneback.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
/**
 * µÇÂ¼¶Ô»°¿ò
 * @author code.404
 *
 */
public class LoginDialogFragment extends BaseDialogFragment {
	
	final public static int LOGIN_OK = 10001;
	final public static int LOGIN_CANCEL = 10002;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_login, null);
		
		Button login_but = (Button) view.findViewById(R.id.login_login);
		Button cancel_but = (Button) view.findViewById(R.id.login_cancel);
		
		login_but.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle data = new Bundle();
				EditText username = (EditText) view.findViewById(R.id.username);
				EditText password = (EditText) view.findViewById(R.id.password);
				data.putString("username", username.getText().toString());
				data.putString("password", password.getText().toString());
				mListener.onFragmentAction(LOGIN_OK, data,  LoginDialogFragment.this);
			}
		});
		cancel_but.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onFragmentAction(LOGIN_CANCEL,null, LoginDialogFragment.this);
			}
		});
		
		builder.setView(view);
		return builder.create();
	}
}
