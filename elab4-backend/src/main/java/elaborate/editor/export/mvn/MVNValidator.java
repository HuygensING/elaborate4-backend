package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2018 Huygens ING
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


import java.io.File;
import java.io.StringReader;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.google.common.base.MoreObjects;
import com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory;

import nl.knaw.huygens.Log;

public class MVNValidator {
  static final Validator validator;

  static {
    SchemaFactory factory = new XMLSyntaxSchemaFactory();
    URL schemaLocation = MVNValidator.class.getResource("/TEI_MVN.rng");
    Log.info("schemaLocation={}", schemaLocation);
    try {
      Schema schema = factory.newSchema(schemaLocation);
      validator = schema.newValidator();
    } catch (SAXException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static ValidationResult validateTEI(String xml) {
    return validateSource(asSource(xml));
  }

  public static ValidationResult validate(File xmlFile) {
    return validateSource(asSource(xmlFile));
  }

  //  private static DOMSource asSource(File xmlFile) throws ParserConfigurationException, SAXException, IOException, FileNotFoundException {
  //    DocumentBuilderFactory dbf = new DocumentBuilderFactoryImpl();
  //    dbf.setXIncludeAware(true);
  //    DocumentBuilder newDocumentBuilder = dbf.newDocumentBuilder();
  //    Document document = newDocumentBuilder.parse(new FileInputStream(xmlFile));
  //    DOMSource source = new DOMSource(document);// jing hasn't implemented validation of DOMSource yet.
  //    return source;
  //  }

  public static class ValidationResult {
    Boolean valid = false;
    private String message = "";

    public void setValid(boolean valid) {
      this.valid = valid;
    }

    public Boolean isValid() {
      return valid;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)//
          .add("valid", valid)//
          .add("message", message)//
          .toString();
    }

  }

  private static ValidationResult validateSource(Source source) {
    ValidationResult result = new ValidationResult();
    try {
      validator.validate(source);
      result.setValid(true);

    } catch (Exception e) {
      e.printStackTrace();
      String location = "";
      if (e instanceof SAXParseException) {
        SAXParseException spe = (SAXParseException) e;
        int line = spe.getLineNumber();
        int column = spe.getColumnNumber();
        location = "(" + line + "," + column + "): ";
      }
      result.setValid(false);
      result.setMessage(location + e.getMessage());
    }
    return result;
  }

  private static Source asSource(String xml) {
    return new StreamSource(new StringReader(xml));
  }

  private static Source asSource(File xmlFile) {
    return new StreamSource(xmlFile);
  }
}
