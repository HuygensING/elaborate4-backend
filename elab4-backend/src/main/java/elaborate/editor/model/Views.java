package elaborate.editor.model;

public class Views {
  // only show ids
  public static class IdOnly {};

  // show some more fields
  public static class Minimal extends IdOnly {};

  // show even more fields
  public static class Extended extends Minimal {};

}
