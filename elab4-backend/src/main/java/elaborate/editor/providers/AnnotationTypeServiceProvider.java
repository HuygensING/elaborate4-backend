package elaborate.editor.providers;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import elaborate.editor.model.orm.service.AnnotationTypeService;

@Provider
public class AnnotationTypeServiceProvider extends SingletonTypeInjectableProvider<Context, AnnotationTypeService> {

  public AnnotationTypeServiceProvider() {
    super(AnnotationTypeService.class, AnnotationTypeService.instance());
  }
}
