package elaborate.editor.solr;

import org.apache.commons.lang.StringUtils;

import elaborate.editor.config.Configuration;

public class SolrServerFactory {
  private static SolrServerWrapper instance;

  public static synchronized SolrServerWrapper getInstance() {
    if (instance == null) {
      String url = Configuration.instance().getSetting(Configuration.SOLR_URL_KEY);
      if (StringUtils.isNotEmpty(url)) {
        instance = new RemoteSolrServer(url);
      } else {
        instance = new LocalSolrServer(null, "entries");
      }
    }
    return instance;
  }

}