package elaborate.editor.providers;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import elaborate.editor.model.orm.service.ProjectMetadataFieldService;

@Provider
public class ProjectMetadataFieldServiceProvider extends SingletonTypeInjectableProvider<Context, ProjectMetadataFieldService> {

  public ProjectMetadataFieldServiceProvider() {
    super(ProjectMetadataFieldService.class, new ProjectMetadataFieldService());
  }
}
