package elaborate.editor.testresources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;
import elaborate.editor.backend.Indexer;
import elaborate.editor.model.SessionService;
import elaborate.editor.model.orm.service.SearchService;
import elaborate.editor.resources.AbstractElaborateResource;

@Path("z")
public class IndexResource extends AbstractElaborateResource {
  SessionService sessionService = SessionService.instance();
  @Context
  SearchService searchService;

  @GET
  @Produces(UTF8MediaType.TEXT_PLAIN)
  public Object reindex(@QueryParam("c") String c, @Context UriInfo uriInfo) {
    if ("reindex".equals(c)) {
      Indexer.main(new String[0]);
      return "reindexing done";
    }

    if ("cleanup".equals(c)) {
      LOG.info("removing expired Sessions");
      sessionService.removeExpiredSessions();
      LOG.info("removing expired Searches");
      searchService.removeExpiredSearches();
      return "expired searches and sessions have been removed";
    }

    //
    //    if ("hw".equals(c)) {
    //      new Thread(new Job());
    //      return "hello world";
    //    }
    //
    //    if ("s".equals(c)) {
    //      return new StreamingOutput() {
    //
    //        @Override
    //        public void write(OutputStream output) throws IOException, WebApplicationException {
    //          output.write("marco\n".getBytes());
    //          output.write(".....\n".getBytes());
    //          output.flush();
    //          try {
    //            Thread.sleep(6000);
    //          } catch (InterruptedException e) {
    //            e.printStackTrace();
    //          }
    //          output.write("polo!\n".getBytes());
    //        }
    //      };
    //    }
    return c;
  }

  class Job implements Runnable {

    @Override
    public void run() {
      LOG.info("sleeping...");
      try {
        Thread.sleep(60000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      LOG.info("awake!...");
    }
  }
}
