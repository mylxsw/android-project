package name.orionis.project.givemyphoneback.fragment;

import name.orionis.project.givemyphoneback.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetAccountDialogFragment extends BaseDialogFragment {
	
	final public static int SET_ACCOUNT_OK = 10011;
	final public static int SET_ACCOUNT_CANCEL = 10012;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_set_account, null);
		
		Button login_but = (Button) view.findViewById(R.id.account_login);
		Button cancel_but = (Button) view.findViewById(R.id.account_cancel);
		
		login_but.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle data = new Bundle();
				
				EditText username = (EditText) view.findViewById(R.id.account_username);
				EditText password = (EditText) view.findViewById(R.id.account_password);
				data.putString("username", username.getText().toString());
				data.putString("password", password.getText().toString());
				
				mListener.onFragmentAction(SET_ACCOUNT_OK, data, SetAccountDialogFragment.this);
			}
		});
		cancel_but.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onFragmentAction(SET_ACCOUNT_CANCEL, null, SetAccountDialogFragment.this);
			}
		});
		
		builder.setView(view);
		return builder.create();
	}
}
