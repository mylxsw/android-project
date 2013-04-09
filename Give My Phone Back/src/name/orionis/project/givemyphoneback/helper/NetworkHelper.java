package name.orionis.project.givemyphoneback.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * 网络操作助手
 * @author code.404
 *
 */
public class NetworkHelper {
	
	private static final String NETWROK = "Network";
	/**
	 * 打开HTTP GET连接
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static InputStream openHttpConnection(String urlString) throws IOException{
		InputStream in = null;
		int response = -1;
		
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		
		if(!(conn instanceof HttpURLConnection)){
			throw new IOException("Not an Http connection");
		}
		try{
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if(response == HttpURLConnection.HTTP_OK){
				in = httpConn.getInputStream();
			}
		} catch (Exception e) {
			Log.d(NETWROK, e.getLocalizedMessage());
			throw new IOException("Error connecting");
		}
		return in;
	}
	/**
	 * 获取网络上的一个图片
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmapFromUrl(String url){
		Bitmap bitmap = null;
		InputStream in = null;
		
		try {
			in = openHttpConnection(url);
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
		} catch (IOException e) {
			Log.d(NETWROK, e.getLocalizedMessage());
		}
		return bitmap;
	}
	/**
	 * 从指定网址读取文本内容
	 * @param url
	 * @return
	 */
	public static String getTextFromUrl(String url){
		int BUFFER_SIZE = 2000;
		InputStream in = null;
		try {
			in = openHttpConnection(url);
		} catch (IOException e) {
			Log.d(NETWROK, e.getLocalizedMessage());
			return "";
		}
		
		InputStreamReader isr = new InputStreamReader(in);
		int charRead;
		String str = "";
		char [] inputBuffer = new char[BUFFER_SIZE];
		try{
			while ((charRead = isr.read(inputBuffer)) > 0) {
				String readString = String.copyValueOf(inputBuffer, 0, charRead);
				str += readString;
				inputBuffer = new char[BUFFER_SIZE];
			}
			in.close();
		}catch (IOException e) {
			Log.d(NETWROK, e.getLocalizedMessage());
			return "";
		}
		return str;
	}
}
