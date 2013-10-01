package elaborate.util;

public class ResourceUtil {

  public static int toRange(int value, int minValue, int maxValue) {
    return Math.min(Math.max(value, minValue), maxValue);
  }

  private ResourceUtil() {
    throw new AssertionError("Non-instantiable class");
  }

}
