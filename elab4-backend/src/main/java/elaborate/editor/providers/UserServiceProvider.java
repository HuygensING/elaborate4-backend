package elaborate.editor.providers;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import elaborate.editor.model.orm.service.UserService;

@Provider
public class UserServiceProvider extends SingletonTypeInjectableProvider<Context, UserService> {

  public UserServiceProvider() {
    super(UserService.class, new UserService());
  }
}
