package com.weaver.teams.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生成密钥对
 * 
 * @author Ricky
 * 
 */
public class TeamsKeyPair {
	private PrivateKey priKey;
	private PublicKey pubKey;

	private static final Logger logger = LoggerFactory.getLogger(TeamsKeyPair.class);

	/**
	 * 初始化key pair
	 */
	public TeamsKeyPair(String seed) {
		this.gengerateKeyPair(seed);
	}

	public void gengerateKeyPair(String seed) {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance(SecurityConstant.KEY_ALGORITHM);
			keygen.initialize(SecurityConstant.KEY_SIZE);
			SecureRandom secrand = SecureRandom.getInstance(SecurityConstant.RNG_ALGORITHM,
					SecurityConstant.RNG_PROVIDER);
			secrand.setSeed(seed.getBytes());
			keygen.initialize(1024, secrand);
			KeyPair keys = keygen.genKeyPair();

			pubKey = keys.getPublic();
			priKey = keys.getPrivate();

		} catch (Exception e) {
			logger.error("生成密钥对失败", e);
		}

	}

	public PrivateKey getPriKey() {
		return priKey;
	}

	public PublicKey getPubKey() {
		return pubKey;
	}

	public static void main(String[] args) {
		Base64 base64 = new Base64();
		String dataEncode = "DGGwF1ikuaLNmkuXsw7pfvsbOwwbUOiP3zwnB054/B4AMP6wg+IYPeaiSEjuqVBu3DRpBHMzs0H6qvsYI6goSxKSySmwupzV29JkXZlNaH6hb6YRYQzShe+sWCVdo5uS15xIETrxO9Oh1Htcz1zqWTgcpb72QY3/mPGauszbh3s=";
		// String dataEncode =
		// "K/B9UjsDeDKnO5DX522K0wa7WgFWVzJw2kRe0wkSe6HHAtSuxpwFOwdUgSloy+tG568onihsgErLeF2LQF34Ug1AAzeFcq45iUckUnqlqTTCy3ssH3hPyv0GuiMJPAfKMVNRumviEova/1oQERmjcNF9nNkRBU6jm6cF4ASiUhc=";
		// String priKey =
		// "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKHOeBaPOojTTRr88phCxrLZS8crbc64B0yurwKX2XfgCjt+oxRBHKkiwvi52B1iBAEOfhDrpdGpjjv/6HB2JBEODtN0lPmXsbbN0DwPwLC//Ce+O8V+5IGXyRVifPqEBsJZ/9im48s6VRQQo+tlpCgXc12tLc/zsVtGkIRwn9svAgMBAAECgYBXZ7sDJLfP9aSvMXiyu0+uiScgHduhUVPQHoS3d+Onl8SvQsdBR7L9wqNjzLBrnuje0T9UUJcwXNFSo/ih14+/Mr/5Fl634EX+f5mzA7YO4Vt+GdMl/rL+8TtCfP/+z/jzwwvJSKD8DsfaH5mFnjp6VD6pZrpUsgvsrG27iioFAQJBANKJZp9sdVUwen/Y3izPushb5oG1dTCdb1o7i7uIpmvFqJvrImsXDb/X8+3/oo0NnqA1nnzz+GJ2xIdAjQHqsO8CQQDEvzuV4uvnsivDLt6FFise/XgkD0KRShxn6XaPUbp5h883q4nYZbgV7pO1c+YZIh/mKT3a4olO5kgvRx2g6PnBAkAIOvraDKFhnS2MxeuZkN1aa81NEMzuA5mQy3cg86yd1fJLdhu4WARflY+hpDa0kO28kZwKgncZUk9IgkuA4a3dAkBPDoCE49/jBAbtsQGc/KFrHHKQGsmm097CKl+Hi1Ggz9+GARmsqILl3S3hNab55mpm8591Op3t5D6RMMRJChSBAkEAh3SOL07tUU320Yn1XQO0rC7bUx3x2DD1s3V7IYCcbEJL367PHE/baZkjB4z2xww5iWT+xgJpkZZHrXqgs2kwIA==";

		String DEFAULT_PRIVATE_KEY =
				"MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKHOeBaPOojTTRr8" + "\r" +
						"8phCxrLZS8crbc64B0yurwKX2XfgCjt+oxRBHKkiwvi52B1iBAEOfhDrpdGpjjv/" + "\r" +
						"6HB2JBEODtN0lPmXsbbN0DwPwLC//Ce+O8V+5IGXyRVifPqEBsJZ/9im48s6VRQQ" + "\r" +
						"o+tlpCgXc12tLc/zsVtGkIRwn9svAgMBAAECgYBXZ7sDJLfP9aSvMXiyu0+uiScg" + "\r" +
						"HduhUVPQHoS3d+Onl8SvQsdBR7L9wqNjzLBrnuje0T9UUJcwXNFSo/ih14+/Mr/5" + "\r" +
						"Fl634EX+f5mzA7YO4Vt+GdMl/rL+8TtCfP/+z/jzwwvJSKD8DsfaH5mFnjp6VD6p" + "\r" +
						"ZrpUsgvsrG27iioFAQJBANKJZp9sdVUwen/Y3izPushb5oG1dTCdb1o7i7uIpmvF" + "\r" +
						"qJvrImsXDb/X8+3/oo0NnqA1nnzz+GJ2xIdAjQHqsO8CQQDEvzuV4uvnsivDLt6F" + "\r" +
						"Fise/XgkD0KRShxn6XaPUbp5h883q4nYZbgV7pO1c+YZIh/mKT3a4olO5kgvRx2g" + "\r" +
						"6PnBAkAIOvraDKFhnS2MxeuZkN1aa81NEMzuA5mQy3cg86yd1fJLdhu4WARflY+h" + "\r" +
						"pDa0kO28kZwKgncZUk9IgkuA4a3dAkBPDoCE49/jBAbtsQGc/KFrHHKQGsmm097C" + "\r" +
						"Kl+Hi1Ggz9+GARmsqILl3S3hNab55mpm8591Op3t5D6RMMRJChSBAkEAh3SOL07t" + "\r" +
						"UU320Yn1XQO0rC7bUx3x2DD1s3V7IYCcbEJL367PHE/baZkjB4z2xww5iWT+xgJp" + "\r" +
						"kZZHrXqgs2kwIA==" + "\r";

		String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChzngWjzqI000a/PKYQsay2UvHK23OuAdMrq8Cl9l34Ao7fqMUQRypIsL4udgdYgQBDn4Q66XRqY47/+hwdiQRDg7TdJT5l7G2zdA8D8Cwv/wnvjvFfuSBl8kVYnz6hAbCWf/YpuPLOlUUEKPrZaQoF3NdrS3P87FbRpCEcJ/bLwIDAQAB";

		String planText = "Password test";
		try {
			byte[] priKeyBytes = base64.decode(DEFAULT_PRIVATE_KEY);
			byte[] pubKeyBytes = base64.decode(pubKey);
			PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(priKeyBytes);
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(SecurityConstant.KEY_ALGORITHM);
			PrivateKey privateKey = keyFactory.generatePrivate(priKeySpec);
			PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);

			Cipher enCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			enCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] planTextData = enCipher.doFinal(planText.getBytes("UTF8"));
			System.out.println(base64.encodeAsString(planTextData));

			Cipher deCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			deCipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] dataDecode = deCipher.doFinal(base64.decode(dataEncode));
			System.out.println(new String(dataDecode, "utf-8"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}