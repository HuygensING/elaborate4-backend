package elaborate.editor.solr;

import org.apache.commons.lang.StringUtils;

import nl.knaw.huygens.facetedsearch.LocalSolrServer;
import nl.knaw.huygens.facetedsearch.QueryComposer;
import nl.knaw.huygens.facetedsearch.RemoteSolrServer;
import nl.knaw.huygens.facetedsearch.SolrServerWrapper;

import elaborate.editor.config.Configuration;

class SolrServerFactory {
	private static SolrServerWrapper instance;

	public static synchronized SolrServerWrapper getInstance() {
		if (instance == null) {
			QueryComposer queryComposer = new ElaborateEditorQueryComposer();
			String url = Configuration.instance().getSetting(Configuration.SOLR_URL_KEY);
			if (StringUtils.isNotEmpty(url)) {
				instance = new RemoteSolrServer(url, queryComposer);
			} else {
				instance = new LocalSolrServer(null, "entries", queryComposer);
			}
		}
		return instance;
	}

}
