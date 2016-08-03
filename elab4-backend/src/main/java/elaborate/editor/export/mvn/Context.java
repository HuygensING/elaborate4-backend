package elaborate.editor.export.mvn;

import org.apache.commons.lang3.StringUtils;

public class Context {
  public String currentEntryId;
  public boolean indent = false;
  public boolean countAsTextLine = true;
  public boolean inParagraph = false;
  public boolean inPoetry = false;
  public int textLineNumber = 1;
  public String foliumLineNumber = "1";
  public String foliumId = "";
  public String textId = "";
  public String text = "";
  public ParseResult parseresult;
  public MVNConversionResult result;

  public void incrementFoliumLineNumber() {
    Integer asInt = Integer.valueOf(foliumLineNumber.replaceAll("[^0-9]", ""));
    Integer next = asInt + 1;
    foliumLineNumber = String.valueOf(next);
  }

  public void incrementTextLineNumber() {
    if (countAsTextLine) {
      textLineNumber++;
    }
  }

  public void addError(MVNAnnotationType type, String error) {
    result.addError(currentEntryId, type.getName() + " : " + error);
  }

  public void assertTextIsInPoetryOrParagraph() {
    if (!(inParagraph || inPoetry) && StringUtils.isNotBlank(text)) {
      result.addError(currentEntryId, "De tekst '" + text + "' bevindt zich niet binnen de scope van een mvn:alinea of mvn:poëzie.");
    }
  }

}
