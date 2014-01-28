package elaborate.editor.providers;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import elaborate.editor.model.orm.service.ProjectEntryService;

@Provider
public class ProjectEntryServiceProvider extends SingletonTypeInjectableProvider<Context, ProjectEntryService> {

  public ProjectEntryServiceProvider() {
    super(ProjectEntryService.class, ProjectEntryService.instance());
  }
}
