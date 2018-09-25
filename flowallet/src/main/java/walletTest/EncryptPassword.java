package walletTest;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.floj.core.Address;

public class EncryptPassword {

	static char[] pass;
	static byte[] salt;
	static SecretKeyFactory factory;
	static KeySpec spec;
	static SecretKey tmp, secret;
	static byte[] ciphertext, iv;
	
	public static byte[] encrypt(String password, Address address) throws NoSuchAlgorithmException, NoSuchPaddingException, Exception
	{
		pass = password.toCharArray();
		salt = getSalt();
	            
		factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		spec = new PBEKeySpec(pass, salt, 65536, 256);
		tmp = factory.generateSecret(spec);
		secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();
		iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		ciphertext = cipher.doFinal(address.toString().getBytes("UTF-8"));
		System.out.println("Encrypted Data: " + ciphertext.toString());
		return ciphertext;
	}
	
	public static String decrypt() throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException
	{
		byte[] cipherDecryptText = ciphertext;
		System.out.println("CipherText in decrypt" + cipherDecryptText.toString());
		Cipher cipher1 = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher1.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
		String decryptedValue = new String(cipher1.doFinal(ciphertext), "UTF-8");
		System.out.println("Decrypted Value: " + decryptedValue);
		return decryptedValue;
	}
	
	private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        for(int i = 0; i<16; i++) {
            System.out.print(salt[i] & 0x00FF);
            System.out.print(" ");
        }
        return salt;
    }
}
