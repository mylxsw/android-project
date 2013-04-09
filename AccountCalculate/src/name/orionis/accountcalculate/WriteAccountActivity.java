package name.orionis.accountcalculate;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import name.orionis.accountcalculate.helper.NetworkHelper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class WriteAccountActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_account);
		new GetInfoTask().execute("http://blog.orionis.name/android/getUserList.html");
	}
	
	private class GetInfoTask extends AsyncTask<String , Map<String, String>, Void >{

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(String... params) {
			Log.i("NEW",params[0]);
			String response = NetworkHelper.getTextFromUrl(params[0]);
			Log.i("NET", response);
			try {
				JSONObject jsonObject = new JSONObject(response);
				int status = jsonObject.getInt("status");
				if(status == 1){
					JSONArray jsonArray = jsonObject.getJSONArray("data");
					for(int i = 0; i < jsonArray.length(); i++){
						JSONObject object = jsonArray.getJSONObject(i);
						Map<String, String> map = new HashMap<String, String>();
						map.put("id", object.getString("id"));
						map.put("username", object.getString("username"));
						publishProgress(map);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Map<String, String>... values) {
			Map<String, String> map = values[0];
			TextView msg = (TextView) findViewById(R.id.msg);
			msg.setText(msg.getText().toString() + ", " + map.get("username"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.write_account, menu);
		return true;
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	
}
