package nl.knaw.huygens.solr;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

import elaborate.editor.solr.AbstractElaborateSolrServer;

public class RemoteSolrServer extends AbstractElaborateSolrServer {
  private final String solrUrl;

  public RemoteSolrServer(String solrUrl) {
    super();
    this.solrUrl = solrUrl;
    setServer();
  }

  @Override
  protected void setServer() {
    LOG.info("SOLR URL = {}", solrUrl);
    server = new HttpSolrServer(solrUrl);
  }

  public String getUrl() {
    return solrUrl;
  }
}
