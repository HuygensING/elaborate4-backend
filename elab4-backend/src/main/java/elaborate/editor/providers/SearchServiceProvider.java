package elaborate.editor.providers;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import elaborate.editor.model.orm.service.SearchService;

@Provider
public class SearchServiceProvider extends SingletonTypeInjectableProvider<Context, SearchService> {

  public SearchServiceProvider() {
    super(SearchService.class, new SearchService());
  }
}
