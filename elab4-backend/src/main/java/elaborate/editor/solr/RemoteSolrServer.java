package elaborate.editor.solr;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

public class RemoteSolrServer extends AbstractSolrServer {
  private final String solrUrl;

  public RemoteSolrServer(String solrUrl) {
    super();
    this.solrUrl = solrUrl;
    setServer();
  }

  @Override
  void setServer() {
    LOG.info("SOLR URL = {}", solrUrl);
    //    try {
    //      server = new CommonsHttpSolrServer(new URL(solrUrl), null, new XMLResponseParser(), false);
    server = new HttpSolrServer(solrUrl);
    //    } catch (MalformedURLException e) {
    //      throw new RuntimeException(e.getMessage());
    //    }
  }

  public String getUrl() {
    return solrUrl;
  }
}
