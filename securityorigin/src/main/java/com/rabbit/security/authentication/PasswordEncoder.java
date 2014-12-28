package com.weaver.teams.security.authentication;

import org.jasypt.digest.StringDigester;
import org.jasypt.exceptions.EncryptionInitializationException;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;

public class PasswordEncoder implements org.springframework.security.crypto.password.PasswordEncoder {

	// The password encryptor or string digester to be internally used
	private PasswordEncryptor passwordEncryptor = null;
	private StringDigester stringDigester = null;
	private Boolean useEncryptor = null;

	/**
	 * Creates a new instance of <tt>PasswordEncoder</tt>
	 */
	public PasswordEncoder() {
		super();
	}

	/**
	 * Encodes a password
	 */
	@Override
	public String encode(CharSequence rawPassword) {
		checkInitialization();
		if (this.useEncryptor.booleanValue()) {
			return this.passwordEncryptor.encryptPassword(rawPassword.toString());
		}
		return this.stringDigester.digest(rawPassword.toString());
	}

	/**
	 * Checks a password's validity.
	 */
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		checkInitialization();
		if (this.useEncryptor.booleanValue()) {
			return this.passwordEncryptor.checkPassword(rawPassword.toString(), encodedPassword);
		}
		return this.stringDigester.matches(rawPassword.toString(), encodedPassword);
	}

	/**
	 * Sets a password encryptor to be used. Only one of 
	 * <tt>setPasswordEncryptor</tt> or <tt>setStringDigester</tt> should be
	 * called. If both are, the last call will define which method will be
	 * used.
	 * 
	 * @param passwordEncryptor the password encryptor instance to be used.
	 */
	public void setPasswordEncryptor(final PasswordEncryptor passwordEncryptor) {
		this.passwordEncryptor = passwordEncryptor;
		this.useEncryptor = Boolean.TRUE;
	}

	/**
	 * Sets a string digester to be used. Only one of 
	 * <tt>setPasswordEncryptor</tt> or <tt>setStringDigester</tt> should be
	 * called. If both are, the last call will define which method will be
	 * used.
	 * 
	 * @param stringDigester the string digester instance to be used.
	 */
	public void setStringDigester(final StringDigester stringDigester) {
		this.stringDigester = stringDigester;
		this.useEncryptor = Boolean.FALSE;
	}

	/*
	 * Checks that the PasswordEncoder has been correctly initialized
	 * (either a password encryptor or a string digester has been set).
	 */
	private synchronized void checkInitialization() {
		if (this.useEncryptor == null) {
			this.passwordEncryptor = new BasicPasswordEncryptor();
			this.useEncryptor = Boolean.TRUE;
		} else {
			if (this.useEncryptor.booleanValue()) {
				if (this.passwordEncryptor == null) {
					throw new EncryptionInitializationException("Password encoder not initialized: password " + "encryptor is null");
				}
			} else {
				if (this.stringDigester == null) {
					throw new EncryptionInitializationException("Password encoder not initialized: string " + "digester is null");
				}
			}
		}
	}

}
