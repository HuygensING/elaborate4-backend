package elaborate.publication.providers;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import elaborate.publication.solr.SearchService;

@Provider
public class SearchServiceProvider extends SingletonTypeInjectableProvider<Context, SearchService> {

  public SearchServiceProvider() {
    super(SearchService.class, SearchService.instance());
  }
}
