package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2022 Huygens ING
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

import org.apache.commons.lang3.StringUtils;

class Context {
  public String currentEntryId;
  public boolean indent = false;
  public boolean countAsTextLine = true;
  public boolean inParagraph = false;
  public boolean inPoetry = false;
  public boolean inOpener = false;
  public boolean inCloser = false;
  public int textLineNumber = 1;
  public String foliumLineNumber = "1";
  public String foliumId = "";
  public String textId = "";
  public String text = "";
  public ParseResult parseresult;
  public MVNConversionResult result;

  public void incrementFoliumLineNumber() {
    int asInt = Integer.parseInt(foliumLineNumber.replaceAll("[^0-9]", ""));
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

  public void assertTextIsInValidScope() {
    if (!(inParagraph || inPoetry || inOpener || inCloser) && StringUtils.isNotBlank(text)) {
      result.addError(
          currentEntryId,
          "De tekst '"
              + text
              + "' bevindt zich niet binnen de scope van een mvn:alinea, mvn:poëzie, mvn:opschrift of mvn:onderschrift.");
    }
  }
}
