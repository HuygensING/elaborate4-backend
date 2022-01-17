package elaborate.editor.solr;

/*-
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2022 Huygens ING
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
