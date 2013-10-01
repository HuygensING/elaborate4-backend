package elaborate.publication.solr;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;

public class LocalSolrServer extends AbstractSolrServer {

  public static final String CORENAME = "entries";

  private final String solrDir;
  private CoreContainer container;

  public LocalSolrServer(String solrDir) {
    super();
    this.solrDir = solrDir;
    setServer();
  }

  @Override
  public void shutdown() throws IndexException {
    try {
      server.optimize();
    } catch (Exception e) {
      throw new IndexException(e.getMessage());
    } finally {
      if (container != null) {
        container.shutdown();
      }
    }
  }

  @Override
  void setServer() {
    try {
      container = new CoreContainer(solrDir);
      container.load();
      server = new EmbeddedSolrServer(container, CORENAME);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

}
