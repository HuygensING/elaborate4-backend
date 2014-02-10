package nl.knaw.huygens.solr;

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

import nl.knaw.huygens.facetedsearch.IndexException;

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
