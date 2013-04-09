package name.orionis.accountcalculate.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密助手
 * @author Code.404
 *
 */
public class EncryptHelper {
	public static String MD5 = "MD5";
	public static String SHA_1 = "SHA-1";
	/**
	 * MD5加密
	 * @param str
	 * @return
	 */
	public static String md5(String str){
		return encrypt(str, "MD5");
	}
	/**
	 * SHA1加密
	 * @param str
	 * @return
	 */
	public static String sha1(String str){
		return encrypt(str, "SHA-1");
	}

	public static String encrypt(String str, String alg){
		byte[] byteArray = null;
		try{
			MessageDigest messageDigest = MessageDigest.getInstance(alg);
			messageDigest.update(str.getBytes());
			byteArray = messageDigest.digest();
		}catch(NoSuchAlgorithmException e){
			System.err.println("加密算法不存在!");
			e.printStackTrace();
		}
		return byte2hex(byteArray);
	}

	private static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for(int i = 0; i < b.length; i ++){
			stmp = (java.lang.Integer.toHexString(b[i] & 0XFF));
			if(stmp.length() == 1){
				hs = hs + "0" + stmp;
			}else{
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}
}