package nl.knaw.huygens.facetedsearch;

/*
 * #%L
 * elab4-common
 * =======
 * Copyright (C) 2013 - 2021 Huygens ING
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
import java.util.Collection;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

public interface SolrServerWrapper {

	/**
	 * @throws IndexException if an error occurs.
	 */
	void initialize() throws IndexException;

	/**
	 * @throws IndexException if an error occurs.
	 */
	void shutdown() throws IndexException;

	/**
	 * optimize the database, time and diskspace heavy!
	 */
	void optimize() throws IndexException;

	/**
	 * Checks the running status of the server.
	 * @return the boolean value <code>true</code> if everything is OK,
	 * <code>false</code> otherwise.
	 */
	boolean ping();

	/**
	 * Adds a document to the index, replacing a previously added document
	 * with the same unique id.
	 * @param doc the document to add.
	 * @throws IndexException if an error occurs.
	 */
	void add(SolrInputDocument doc) throws IndexException;

	/**
	 * Adds a document to the index, replacing a previously added document
	 * with the same unique id.
	 * @param docs the document to add.
	 * @throws IndexException if an error occurs.
	 */
	void add(Collection<SolrInputDocument> docs) throws IndexException;

	/**
	 * @param facetedSearchParameters
	 * @return
	 * @throws IndexException
	 */
	Map<String, Object> search(FacetedSearchParameters<?> facetedSearchParameters) throws IndexException;

	/**
	 * @param solrDocumentId
	 * @return
	 * @throws IndexException
	 */
	void delete(String solrDocumentId) throws SolrServerException, IOException;

	//  /**
	//   * @param pid
	//   * @param queryString
	//   * @return
	//   * @throws IndexException
	//   */
	//  String highlight(String pid, String queryString) throws IndexException;

}
