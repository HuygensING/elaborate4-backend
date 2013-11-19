package elaborate.editor.providers;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import elaborate.editor.model.orm.service.AnnotationService;

@Provider
public class AnnotationServiceProvider extends SingletonTypeInjectableProvider<Context, AnnotationService> {

  public AnnotationServiceProvider() {
    super(AnnotationService.class, AnnotationService.instance());
  }
}
