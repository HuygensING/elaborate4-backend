package elaborate.publication.solr;

/*
 * #%L
 * elab4-publication-backend
 * =======
 * Copyright (C) 2013 - 2014 Huygens ING
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

import nl.knaw.huygens.facetedsearch.IndexException;

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
