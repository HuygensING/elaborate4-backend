package elaborate.freemarker;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarker {
  static Logger LOG = LoggerFactory.getLogger(FreeMarker.class);
  private static final Configuration FREEMARKER = new Configuration();
  static {
    FREEMARKER.setObjectWrapper(new DefaultObjectWrapper());
  }

  public static String templateToString(String fmTemplate, Object fmRootMap, Class<?> clazz) {
    StringWriter out = new StringWriter();
    return processTemplate(fmTemplate, fmRootMap, clazz, out);
  }

  private static String processTemplate(String fmTemplate, Object fmRootMap, Class<?> clazz, Writer out) {
    try {
      FREEMARKER.setClassForTemplateLoading(clazz, "");
      Template template = FREEMARKER.getTemplate(fmTemplate);
      template.setOutputEncoding(Charsets.UTF_8.displayName());
      template.process(fmRootMap, out);
      return out.toString();
    } catch (IOException e1) {
      throw new RuntimeException(e1);
    } catch (TemplateException e) {
      throw new RuntimeException(e);
    }
  }

  public static String templateToFile(String fmTemplate, File file, Object fmRootMap, Class<?> clazz) {
    try {
      FileWriter out = new FileWriter(file);
      return processTemplate(fmTemplate, fmRootMap, clazz, out);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
