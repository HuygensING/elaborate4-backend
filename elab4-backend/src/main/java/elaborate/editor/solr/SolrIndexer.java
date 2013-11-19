package elaborate.editor.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrIndexer {
  protected static final Logger LOG = LoggerFactory.getLogger(SolrIndexer.class);
  protected static final String EMPTYVALUE_SYMBOL = ":empty";
  private static final int STATUS_OK = 0;
  private final SolrServer server;
  private final String idField;

  public SolrIndexer(SolrServer server, String idField) {
    this.server = server;
    this.idField = idField;
  }

  // -- public methods
  public void clear() {
    try {
      this.server.deleteByQuery("*:*");
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void commit() {
    try {
      this.server.commit();
      this.server.optimize();
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("boxing")
  public boolean isUp() {
    boolean isUp = false;
    try {
      int status = this.server.ping().getStatus();
      LOG.info("solrserver status = {}", status);
      isUp = (status == STATUS_OK);
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return isUp;
  }

  public void deleteById(String id) {
    try {
      this.server.deleteById(id);
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void index(SolrInputDocument doc, boolean commitNow) {
    try {
      String id = String.valueOf(doc.getField(idField).getValue());
      this.server.deleteById(id);
      //        LOG.info("doc={}", doc);
      this.server.add(doc);
      if (commitNow) {
        this.server.commit();
      }
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
