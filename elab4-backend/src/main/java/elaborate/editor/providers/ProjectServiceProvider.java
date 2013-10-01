package elaborate.editor.providers;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import elaborate.editor.model.orm.service.ProjectService;

@Provider
public class ProjectServiceProvider extends SingletonTypeInjectableProvider<Context, ProjectService> {

  public ProjectServiceProvider() {
    super(ProjectService.class, new ProjectService());
  }
}
