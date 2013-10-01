package elaborate.editor.resources;

import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

import com.google.common.collect.ImmutableMap;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.orm.service.SearchService;
import elaborate.editor.solr.RemoteSolrServer;
import elaborate.editor.solr.SolrServerWrapper;
import elaborate.util.HibernateUtil;

@Path("status")
public class StatusResource extends AbstractElaborateResource {
  Configuration config = Configuration.instance();

  @GET
  @Produces(UTF8MediaType.APPLICATION_JSON)
  public Object getStatus() {
    return ImmutableMap.<String, Object> builder()//
        .put("solrserver", getSolrStatus())//
        .put("database", getDbStatus())//
        .build();
  }

  private ServerStatus getDbStatus() {
    EntityManager entityManager = HibernateUtil.beginTransaction();
    String url = (String) entityManager.getEntityManagerFactory().getProperties().get("hibernate.connection.url");
    boolean online = entityManager.isOpen();
    ServerStatus status = new ServerStatus(url, online);
    HibernateUtil.rollbackTransaction(entityManager);
    return status;
  }

  private ServerStatus getSolrStatus() {
    SolrServerWrapper solrServer = new SearchService().getSolrServer();
    return new ServerStatus(((RemoteSolrServer) solrServer).getUrl(), solrServer.ping());
  }

  static class ServerStatus {
    String url;
    String status;

    public ServerStatus(String url, boolean online) {
      status = online ? "online" : "offline";
      this.url = url;
    }

    public String getUrl() {
      return url;
    }

    public String getStatus() {
      return status;
    }

  }
}
