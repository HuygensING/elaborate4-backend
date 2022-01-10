package nl.knaw.huygens.facetedsearch;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * #%L
 * elab4-common
 * =======
 * Copyright (C) 2013 - 2022 Huygens ING
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

public class RemoteSolrServer extends AbstractSolrServer {
  private final Logger LOG = LoggerFactory.getLogger(getClass());
  private final String solrUrl;

  public RemoteSolrServer(String solrUrl, QueryComposer queryComposer) {
    super(queryComposer);
    this.solrUrl = solrUrl;
    setServer();
  }

  @Override
  public void setServer() {
    LOG.info("SOLR URL = {}", solrUrl);
    server = new HttpSolrServer(solrUrl);
  }

  public String getUrl() {
    return solrUrl;
  }
}
