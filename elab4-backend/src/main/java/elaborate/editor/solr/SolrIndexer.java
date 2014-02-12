package elaborate.editor.solr;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
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

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrIndexer {
	protected static final Logger LOG = LoggerFactory.getLogger(SolrIndexer.class);
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
