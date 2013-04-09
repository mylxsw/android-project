package name.orionis.accountcalculate.helper;

import android.content.Context;
import android.widget.Toast;
/**
 * Toast÷˙ ÷
 * @author code.404
 *
 */
public class ToastHelper {
	public static void showMessage(Context context , String message, int duration){
		Toast.makeText(context, message, duration).show();
	}
	public static void showMessage(Context context, String message){
		showMessage(context, message, Toast.LENGTH_SHORT);
	}
}
