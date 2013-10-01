package elaborate.util;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Charsets;

public class PasswordUtilTest {

  @Ignore
  @Test
  public void testPassword() throws UnsupportedEncodingException {
    final String password = "aap-noot-mies";
    final byte[] passwordDigest2 = PasswordUtil.encode(password);
    String encodedPassword = new String(Base64.encodeBase64(passwordDigest2), Charsets.UTF_8);
    assertTrue(PasswordUtil.matches(password, encodedPassword));
    assertFalse(PasswordUtil.matches("somethingelse", encodedPassword));
  }

}
