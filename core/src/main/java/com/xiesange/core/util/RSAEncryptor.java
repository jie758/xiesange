package com.xiesange.core.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RSAEncryptor {
	/**
	 * 字节数据转字符串专用集合
	 */
	private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 私钥
	 */
	private RSAPrivateKey privateKey;

	/**
	 * 公钥
	 */
	private RSAPublicKey publicKey;

	public static void main(String[] args) throws Exception {
		
		String publicKeyPath = "C:\\Users\\Think\\Desktop\\test\\rsa_public_key.pem"; // replace
		String privateKeyPath = "C:\\Users\\Think\\Desktop\\test\\pkcs8_private_key.pem"; // replace
		
		EncryptUtil.RSA.init(publicKeyPath, privateKeyPath);
		String str = EncryptUtil.RSA.decode("aa93b48d58524ef90e75c8c7c9698336dc0272e7ab0fe3bebdd55bad72c3c860f828e3c419868d8ef2f4fd1493142bd7ee6212dad9d05245e56df140ef5b2ed36584516eb0dbd1474da6d835a290d21e171434eaba6480c43bf0210489c5901f890f61248de25b403a6d1c88ea878ba4d7016d8dfe89e42de7fdde216d686a0b");
		System.out.println(str);
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("code", "03194d7b920a8d2f57463e021749125T");
		map.put("token", "7W8IZVfGCm7iHjDZO83UISfYsFh7jThQXqYPwBX2isN2fDkxRjUd4sSR0gEbOi79");
		
		//RSAEncryptor encryptor = new RSAEncryptor(publicKeyPath, privateKeyPath);

		/*String modulus = encryptor.publicKey.getModulus().toString(16);
		String exponent = encryptor.publicKey.getPublicExponent().toString(16);
		System.out.println("modulus=" + modulus + " , exponent=" + exponent);
*/
		// System.out.println(byteArrayToString(encryptor.publicKey.getEncoded()));
		/*EncryptUtil.RSA.init(publicKeyPath, privateKeyPath);
		String encodeStr = "21c31a9d1b1ce5a49a3b3ea19c722757198662306c6a807f3b99dddd0085e7d548bdb1a689331819c88fa95ed59fd111b202866ed6cc1c72f48c8e24a566e32c47a0d162f5645559877366350880fc59718be8a5a77723f23be8704b6dc85cfb8df0077c6d37bf2706ecf1d0556ac5a91b8c05e9bf1d081d7e632f726648a078";
		String str = EncryptUtil.RSA.decode(encodeStr);
		System.out.println(str);*/

		/*
		 * String str = "wuyujie101%";
		 * 
		 * 
		 * 
		 * str = EncryptUtil.RSA.encode(str); System.out.println("encode: " +
		 * str);
		 * 
		 * str = EncryptUtil.RSA.decode(str); System.out.println("decode : " +
		 * str);
		 */

		/*
		 * String privateKeyPath =
		 * "C:\\Users\\Think\\Desktop\\test\\rsa_public_key.pem"; // replace
		 * your public key path here String publicKeyPath =
		 * "C:\\Users\\Think\\Desktop\\test\\pkcs8_private_key.pem"; // replace
		 * your private path here RSAEncryptor rsaEncryptor = new
		 * RSAEncryptor(privateKeyPath, publicKeyPath);
		 * 
		 * try { String testRSADeWith64 =
		 * rsaEncryptor.decryptWithBase64(testRSAEnWith64);
		 * //System.out.println("\nEncrypt: \n" + testRSAEnWith64);
		 * 
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 */
	}

	/**
	 * @param publicKeyFilePath
	 * @param privateKeyFilePath
	 */
	public RSAEncryptor(String publicKeyFilePath, String privateKeyFilePath)
			throws Exception {
		String public_key = getKeyFromFile(publicKeyFilePath);
		String private_key = getKeyFromFile(privateKeyFilePath);
		loadPublicKey(public_key);
		loadPrivateKey(private_key);
	}

	public String decryptWithBase64(String base64String) throws Exception {
		byte[] binaryData = decrypt(new BASE64Decoder().decodeBuffer(base64String));
		String string = new String(binaryData);
		return string;
	}

	public String encryptWithBase64(String string) throws Exception {
		byte[] binaryData = encrypt(string);
		String base64String = new BASE64Encoder().encodeBuffer(binaryData) ;
		return base64String;
	}

	/**
	 * 加密过程
	 * 
	 * @param publicKey
	 *            公钥
	 * @param plainTextData
	 *            明文数据
	 * @return
	 * @throws Exception
	 *             加密过程中的异常信息
	 */
	public byte[] encrypt(String plainTextData) throws Exception {
		return encrypt(hexStringToBytes(plainTextData));
	}
	public byte[] encrypt(byte[] plainTextData) throws Exception {
		if (publicKey == null) {
			throw new Exception("加密公钥为空, 请设置");
		}
		
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA");// , new BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(plainTextData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}

	/**
	 * 解密过程
	 * 
	 * @param privateKey
	 *            私钥
	 * @param cipherData
	 *            密文数据
	 * @return 明文
	 * @throws Exception
	 *             解密过程中的异常信息
	 */
	public byte[] decrypt(String cipherData) throws Exception {
		return decrypt(hexStringToBytes(cipherData));
	}
	public byte[] decrypt(byte[] cipherData) throws Exception {
		if (privateKey == null) {
			throw new Exception("解密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA");// , new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(cipherData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}

	private static String getKeyFromFile(String filePath) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				filePath));

		String line = null;
		List<String> list = new ArrayList<String>();
		while ((line = bufferedReader.readLine()) != null) {
			list.add(line);
		}

		// remove the firt line and last line
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 1; i < list.size() - 1; i++) {
			stringBuilder.append(list.get(i)).append("\r");
		}
		bufferedReader.close();
		String key = stringBuilder.toString();
		return key;
	}

	/**
	 * 随机生成密钥对
	 */
	public void genKeyPair() {
		KeyPairGenerator keyPairGen = null;
		try {
			keyPairGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyPairGen.initialize(1024, new SecureRandom());
		KeyPair keyPair = keyPairGen.generateKeyPair();
		this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
		this.publicKey = (RSAPublicKey) keyPair.getPublic();
	}

	/**
	 * 从文件中输入流中加载公钥
	 * 
	 * @param in
	 *            公钥输入流
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	/*private void loadPublicKey(InputStream in) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			loadPublicKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("公钥数据流读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥输入流为空");
		}
	}*/

	/**
	 * 从字符串中加载公钥
	 * 
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	private void loadPublicKey(String publicKeyStr) throws Exception {
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (IOException e) {
			throw new Exception("公钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	/**
	 * 从文件中加载私钥
	 * 
	 * @param keyFileName
	 *            私钥文件名
	 * @return 是否成功
	 * @throws Exception
	 */
	/*private void loadPrivateKey(InputStream in) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			loadPrivateKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥输入流为空");
		}
	}*/

	private void loadPrivateKey(String privateKeyStr) throws Exception {
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			this.privateKey = (RSAPrivateKey) keyFactory
					.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			throw new Exception("私钥非法");
		} catch (IOException e) {
			throw new Exception("私钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	/**
	 * 字节数据转十六进制字符串
	 * 
	 * @param data
	 *            输入数据
	 * @return 十六进制内容
	 */
	private static String byteArrayToString(byte[] data) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			// 取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移
			stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);
			// 取出字节的低四位 作为索引得到相应的十六进制标识符
			stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
			if (i < data.length - 1) {
				stringBuilder.append(' ');
			}
		}
		return stringBuilder.toString();
	}

	private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
	private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


}