package elaborate.freemarker;

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
