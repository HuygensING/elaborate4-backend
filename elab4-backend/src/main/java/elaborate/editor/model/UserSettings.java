package elaborate.editor.model;

public class UserSettings {
  public static final String MAGIC_NUMBER = "magic_number";
  public static final String ENTRY_ORDER = "entryorder";
  public static final String ENTRY_NAMES = "entrynames";
  public static final String ONLINE_STATUS = "onlinestatus";
  public static final String LOGOUT_TIME = "logging_out_at";
  public static final String LOGIN_TIME = "logging_in_at";

  public static String projectLevel(String project_id, int i) {
    return "project_" + project_id + "_level_" + i;
  }
}
