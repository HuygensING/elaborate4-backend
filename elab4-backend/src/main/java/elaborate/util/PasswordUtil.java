package elaborate.util;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Charsets;

public final class PasswordUtil {

	private static final MessageDigest DIGEST = createDigester();

	private PasswordUtil() {
		throw new AssertionError("Non-instantiable class");
	}

	public static synchronized String digest(String digestThis) {
		return new String(encode(digestThis), Charsets.UTF_8);
	}

	public static synchronized byte[] encode(String digestThis) {
		DIGEST.reset();
		DIGEST.update(digestThis.getBytes(Charsets.UTF_8));
		return DIGEST.digest();
	}

	private static MessageDigest createDigester() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * @param plainPassword
	 *          a plain text password
	 * @param encodedPassword
	 *          Base64 encoded MD5 hash
	 * @return
	 */
	public static synchronized boolean matches(String plainPassword, String encodedPassword) {
		byte[] base64DecodedMd5Password = Base64.decodeBase64(encodedPassword.getBytes());

		String base64DecodedPasswordAsString = new String(base64DecodedMd5Password, Charsets.UTF_8);
		byte[] md5Password = encode(plainPassword);
		String md5PasswordString = new String(md5Password);

		/*
		 * // return MessageDigest.isEqual(md5Password, base64DecodedMd5Password);
		 * Fix authentication by doing a string comparison instead of a byte comparison.
		 * We can't change eLaborate Classic for now.
		 */

		return (StringUtils.equals(md5PasswordString, base64DecodedPasswordAsString));
	}

	public static boolean matches(String password, byte[] encodedpassword) {
		return Arrays.equals(encode(password), encodedpassword);
	}
}
