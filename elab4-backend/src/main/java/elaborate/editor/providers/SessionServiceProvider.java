package elaborate.editor.providers;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import elaborate.editor.model.SessionService;

@Provider
public class SessionServiceProvider extends SingletonTypeInjectableProvider<Inject, SessionService> {

  public SessionServiceProvider() {
    super(SessionService.class, SessionService.instance());
  }
}
