package elaborate.util;

public class DelimiterDetector {
  StringBuilder pre = new StringBuilder();
  StringBuilder center = new StringBuilder();
  StringBuilder post = new StringBuilder();

  public DelimiterDetector(String string) {
    boolean atStart = true;
    for (char c : string.toCharArray()) {
      if (StringUtil.DELIM.contains(String.valueOf(c))) {
        if (atStart) {
          pre.append(c);
        } else {
          post.append(c);
        }
      } else {
        center.append(c);
        atStart = false;
      }
    }
  }

  String getPreDelimiters() {
    return pre.toString();
  }

  String getPostDelimiters() {
    return post.toString();
  }

  String getStripped() {
    return center.toString();
  }
}
