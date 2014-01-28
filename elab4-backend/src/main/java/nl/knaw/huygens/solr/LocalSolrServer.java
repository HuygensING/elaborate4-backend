package nl.knaw.huygens.solr;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;

import elaborate.editor.solr.AbstractElaborateSolrServer;

public class LocalSolrServer extends AbstractElaborateSolrServer {

  public static final String SOLR_DIRECTORY = "solr";
  public static final String SOLR_CONFIG_FILE = "solrconfig.xml";

  private final String solrDir;
  private final String coreName;
  private CoreContainer container;

  public LocalSolrServer(String solrDir, String coreName) {
    super();
    this.solrDir = StringUtils.defaultIfBlank(solrDir, SOLR_DIRECTORY);
    this.coreName = StringUtils.defaultIfBlank(coreName, "core1");
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
  protected void setServer() {
    try {
      container = new CoreContainer(solrDir);
      container.load();
      server = new EmbeddedSolrServer(container, coreName);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

}
