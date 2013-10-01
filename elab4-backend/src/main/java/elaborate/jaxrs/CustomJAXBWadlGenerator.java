package elaborate.jaxrs;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.wadl.generators.WadlGeneratorJAXBGrammarGenerator;
import com.sun.research.ws.wadl.Param;

public class CustomJAXBWadlGenerator extends WadlGeneratorJAXBGrammarGenerator {
  @Override
  public Param createParam(AbstractResource r, AbstractMethod m, final Parameter p) {
    Param param = super.createParam(r, m, p);
    return param;
  }
}