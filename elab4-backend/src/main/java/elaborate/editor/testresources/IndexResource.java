package elaborate.editor.testresources;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
