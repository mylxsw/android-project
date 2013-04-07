package name.orionis.project.givemyphoneback.fragment;

import name.orionis.project.givemyphoneback.fragment.listener.FragmentActionListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class BaseDialogFragment extends DialogFragment {
	
	FragmentActionListener mListener;

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			mListener = (FragmentActionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " 必须实现FragmentActionListener接口");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
	}

}
