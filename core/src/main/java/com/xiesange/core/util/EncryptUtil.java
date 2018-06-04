package com.xiesange.core.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 加密工具
 * @author wuyujie Feb 4, 2015 1:51:20 PM
 *
 */
public class EncryptUtil {
	public static void main(String[] args) throws Exception {
		/*String aa = "463681";
		String code = EncryptUtil.Base64.encode(aa);
		System.out.println("code : "+code);
		
		String decode = EncryptUtil.Base64.decode("ZW1haWw9ODYxOTQ3MzlAcXEuY29tJnZjb2RlPTkxMTYyMA==");
		System.out.println("decode : "+decode);
		
		String a = decode.substring(1);
		
		code = "elsetravel";
		
		code = EncryptUtil.MD5.encode(code);
		System.out.println(code);*/
		
		
		
		
		/*EncrypDES de1 = new EncryptUtil.EncrypDES();  
        String msg ="wuyujie101阿斯顿";  
        byte[] encontent = de1.Encrytor(msg);  
        byte[] decontent = de1.Decryptor(encontent);  
        System.out.println("明文是:" + msg);  
        System.out.println("加密后:" + new String(encontent,"UTF-8"));  
        System.out.println("解密后:" + new String(decontent,"UTF-8")); */ 
	}
	
	public static class MD5{
		public static String encode(String content){
			return DigestUtils.md5Hex(content);
		}
		public static String encode(String content,String key){
			return DigestUtils.md5Hex(content+key);
		}
	}
	
	public static class Base64{
		//加密  
	    public static String encode(String str) {  
	        byte[] b = null;  
	        String s = null;  
	        try {  
	            b = str.getBytes("utf-8");  
	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	        }  
	        if (b != null) {  
	            s = new BASE64Encoder().encode(b);  
	        }  
	        return s;  
	    }  
	  
	    // 解密  
	    public static String decode(String s) {  
	        byte[] b = null;  
	        String result = null;  
	        if (s != null) {  
	            BASE64Decoder decoder = new BASE64Decoder();  
	            try {  
	                b = decoder.decodeBuffer(s);  
	                result = new String(b, "utf-8");  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }  
	        }  
	        return result;  
	    } 
	}
	
	public static class SHA1{
		public static String encode(String content){
			return DigestUtils.shaHex(content);
		}
	} 
	
	
	public static class RSA{
		private static RSAEncryptor encryptor = null;
		
		public static void init(String publicKeyPath,String privateKeyPath) throws Exception{
			encryptor = new RSAEncryptor(publicKeyPath,privateKeyPath);
			LogUtil.getLogger(RSA.class).debug("init encryptor : "+encryptor);
		}
		
		public static String encodeBase64(String content) throws Exception{
			return encryptor.encryptWithBase64(content);
		}
		public static String decodeBase64(String content) throws Exception{
			return encryptor.decryptWithBase64(content);
		}
		
		public static String encode(String content) throws Exception{
			return new String(encryptor.encrypt(content));
		}
		public static String decode(String content) throws Exception{
			return new String(encryptor.decrypt(content));
		}
	}
	/*public static class EncrypDES {  
	      
	    //KeyGenerator 提供对称密钥生成器的功能，支持各种算法  
	    private KeyGenerator keygen;  
	    //SecretKey 负责保存对称密钥  
	    private SecretKey deskey;  
	    //Cipher负责完成加密或解密工作  
	    private Cipher c;  
	    //该字节数组负责保存加密的结果  
	    private byte[] cipherByte;  
	      
	    public EncrypDES() throws NoSuchAlgorithmException, NoSuchPaddingException{  
	        Security.addProvider(new com.sun.crypto.provider.SunJCE());  
	        //实例化支持DES算法的密钥生成器(算法名称命名需按规定，否则抛出异常)  
	        keygen = KeyGenerator.getInstance("DES");  
	        //生成密钥  
	        deskey = keygen.generateKey();  
	        //生成Cipher对象,指定其支持的DES算法  
	        c = Cipher.getInstance("DES");  
	    }  
	      
	    *//** 
	     * 对字符串加密 
	     *  
	     * @param str 
	     * @return 
	     * @throws InvalidKeyException 
	     * @throws IllegalBlockSizeException 
	     * @throws BadPaddingException 
	     *//*  
	    public byte[] Encrytor(String str) throws InvalidKeyException,  
	            IllegalBlockSizeException, BadPaddingException {  
	        // 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式  
	        c.init(Cipher.ENCRYPT_MODE, deskey);  
	        byte[] src = str.getBytes();  
	        // 加密，结果保存进cipherByte  
	        cipherByte = c.doFinal(src);  
	        return cipherByte;  
	    }  
	  
	    *//** 
	     * 对字符串解密 
	     *  
	     * @param buff 
	     * @return 
	     * @throws InvalidKeyException 
	     * @throws IllegalBlockSizeException 
	     * @throws BadPaddingException 
	     *//*  
	    public byte[] Decryptor(byte[] buff) throws InvalidKeyException,  
	            IllegalBlockSizeException, BadPaddingException {  
	        // 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式  
	        c.init(Cipher.DECRYPT_MODE, deskey);  
	        cipherByte = c.doFinal(buff);  
	        return cipherByte;  
	    }  
	} */
	
}
