package elaborate.util;

import org.apache.commons.lang.StringUtils;

enum TextCase {
  LOWER {
    @Override
    public String applyTo(String string) {
      return string.toLowerCase();
    }
  },
  UPPER {
    @Override
    public String applyTo(String string) {
      return string.toUpperCase();
    }
  },
  CAPITALIZED {
    @Override
    public String applyTo(String string) {
      return StringUtils.capitalize(string.toLowerCase());
    }
  },
  MIXED {
    @Override
    public String applyTo(String string) {
      return string;
    }
  };

  public abstract String applyTo(String string);

  public static TextCase detectCase(String token) {
    for (TextCase stringCase : TextCase.values()) {
      if (stringCase.applyTo(token).equals(token)) {
        return stringCase;
      }
    }
    throw new RuntimeException("No suitable case detected. Check available cases.");
  }

}
