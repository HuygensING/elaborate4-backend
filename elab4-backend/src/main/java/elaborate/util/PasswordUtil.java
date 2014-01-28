package elaborate.util;

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
   * @param plainPassword a plain text password
   * @param encodedPassword Base64 encoded MD5 hash
   * @return
   */
  public static synchronized boolean matches(String plainPassword, String encodedPassword) {
    byte[] base64DecodedMd5Password = Base64.decodeBase64(encodedPassword.getBytes());

    String base64DecodedPasswordAsString = new String(base64DecodedMd5Password, Charsets.UTF_8);
    byte[] md5Password = encode(plainPassword);
    String md5PasswordString = new String(md5Password);

    /*
     *  // return MessageDigest.isEqual(md5Password, base64DecodedMd5Password);
     *  Fix authentication by doing a string comparison instead of a byte comparison.
     *  We can't change eLaborate Classic for now.
     *
     */

    return (StringUtils.equals(md5PasswordString, base64DecodedPasswordAsString));
  }

  public static boolean matches(String password, byte[] encodedpassword) {
    return Arrays.equals(encode(password), encodedpassword);
  }
}
