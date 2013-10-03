package nl.knaw.huygens.elaborate.publication.metadata;

import java.io.IOException;
import java.io.StringWriter;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class AbstractMetadataRecord {
  private static final Configuration FREEMARKER = new Configuration();
  static {
    FREEMARKER.setClassForTemplateLoading(AbstractMetadataRecord.class, "");
    FREEMARKER.setObjectWrapper(new DefaultObjectWrapper());
  }

  abstract String getTemplate();

  abstract Object getDataModel();

  public String asXML() {
    String xml = "";
    try {
      Template template;
      template = FREEMARKER.getTemplate(getTemplate());
      template.setOutputEncoding("UTF-8");
      StringWriter stringWriter = new StringWriter();
      template.process(getDataModel(), stringWriter);
      xml = stringWriter.toString();

    } catch (IOException e1) {
      e1.printStackTrace();

    } catch (TemplateException e) {
      e.printStackTrace();
    }
    return xml;
  }

}
