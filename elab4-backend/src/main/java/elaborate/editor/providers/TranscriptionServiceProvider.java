package elaborate.editor.providers;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import elaborate.editor.model.orm.service.TranscriptionService;

@Provider
public class TranscriptionServiceProvider extends SingletonTypeInjectableProvider<Context, TranscriptionService> {

  public TranscriptionServiceProvider() {
    super(TranscriptionService.class, new TranscriptionService());
  }
}
