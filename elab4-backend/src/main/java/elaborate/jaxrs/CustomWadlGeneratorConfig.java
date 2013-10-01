package elaborate.jaxrs;

import java.util.List;

import com.sun.jersey.api.wadl.config.WadlGeneratorConfig;
import com.sun.jersey.api.wadl.config.WadlGeneratorDescription;

public class CustomWadlGeneratorConfig extends WadlGeneratorConfig {
  @Override
  public List<WadlGeneratorDescription> configure() {
    return generator(CustomJAXBWadlGenerator.class)//
        //    .generator(WadlGeneratorApplicationDoc.class)//
        //    .prop("applicationDocsFile", "classpath:/application-doc.xml")//
        //    .generator(WadlGeneratorGrammarsSupport.class)//
        //    .prop("grammarsFile", "classpath:/application-grammars.xml")//
        //    .generator(WadlGeneratorResourceDocSupport.class)//
        //    .prop("resourceDocFile", "classpath:/resourcedoc.xml")//
        .descriptions();
  }
}