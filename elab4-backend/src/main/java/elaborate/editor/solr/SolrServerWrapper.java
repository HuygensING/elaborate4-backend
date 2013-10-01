package elaborate.editor.solr;

import java.util.Collection;
import java.util.Map;

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
   * @param doc the document to add.
   * @throws IndexException if an error occurs.
   */
  void add(Collection<SolrInputDocument> docs) throws IndexException;

  /**
   * @param query
   * @param sort
   * @param ascending
   * @return
   * @throws IndexException
   */
  Map<String, Object> search(ElaborateSearchParameters elaborateSearchParameters) throws IndexException;

  //  /**
  //   * @param pid
  //   * @param queryString
  //   * @return
  //   * @throws IndexException
  //   */
  //  String highlight(String pid, String queryString) throws IndexException;

}
