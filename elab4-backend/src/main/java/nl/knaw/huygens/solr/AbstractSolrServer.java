package nl.knaw.huygens.solr;

import java.util.Collection;
import java.util.List;

import nl.knaw.huygens.LoggableObject;
import nl.knaw.huygens.solr.FacetCount.Option;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrInputDocument;

public abstract class AbstractSolrServer extends LoggableObject implements SolrServerWrapper {
  public static final String KEY_NUMFOUND = "numFound";

  protected SolrServer server;

  protected abstract void setServer();

  @Override
  public void initialize() throws IndexException {
    try {
      server.deleteByQuery("*:*");
    } catch (Exception e) {
      throw new IndexException(e.getMessage());
    }
  }

  @Override
  public void shutdown() throws IndexException {
    try {
      server.commit();
      server.optimize();
    } catch (Exception e) {
      throw new IndexException(e.getMessage());
    }
  }

  @Override
  public boolean ping() {
    try {
      return server.ping().getStatus() == 0;
    } catch (Exception e) {
      LOG.error("ping failed with '{}'", e.getMessage());
      return false;
    }
  }

  @Override
  public void add(SolrInputDocument doc) throws IndexException {
    try {
      server.add(doc);
    } catch (Exception e) {
      throw new IndexException(e.getMessage());
    }
  }

  @Override
  public void add(Collection<SolrInputDocument> docs) throws IndexException {
    try {
      server.add(docs);
    } catch (Exception e) {
      throw new IndexException(e.getMessage());
    }
  }

  /**
   * Returns a list of facetinfo with counts.
   * @param field 
   * @param title 
   * @param type 
   */
  protected FacetCount convertFacet(FacetField field, String title, FacetType type) {
    if (field != null) {
      FacetCount facetCount = new FacetCount()//
          .setName(field.getName())//
          .setTitle(title)//
          .setType(type);
      List<Count> counts = field.getValues();
      if (counts != null) {
        for (Count count : counts) {
          Option option = new FacetCount.Option()//
              .setName(count.getName())//
              .setCount(count.getCount());
          facetCount.addOption(option);
        }
      }
      return facetCount;
    }
    return null;
  }
  //  /**
  //   * Returns a map with facet counts, using a (persistent) id as key.
  //   * The map is empty if the field does not exist or has no values.
  //   */
  //  private Map<String, Object> convertFacet(FacetField field) {
  //    Map<String, Object> map = Maps.newTreeMap(/*SORT_CASE_INSENSITIVE*/);
  //    if (field != null) {
  //      List<Count> counts = field.getValues();
  //      if (counts != null) {
  //        for (Count count : counts) {
  //          map.put(SolrUtils.escapeFacetId(count.getName()), count.getCount());
  //        }
  //      }
  //    }
  //    return map;
  //  }

  // -------------------------------------------------------------------

  //  @Override
  //  public String highlight(String pid, String term) throws IndexException {
  //    SolrQuery query = new SolrQuery()//
  //        .setQuery("letter_id:" + pid + " AND text:(" + term + ")")//
  //        .setHighlight(true)//
  //        .setHighlightFragsize(0);
  //    query.set(HighlightParams.MAX_CHARS, -1);
  //    try {
  //      QueryResponse response = server.query(query);
  //      Map<String, Map<String, List<String>>> hl = response.getHighlighting();
  //      for (Entry<String, Map<String, List<String>>> entry : hl.entrySet()) {
  //        //        String key = entry.getKey();
  //        Map<String, List<String>> fragments = entry.getValue();
  //        List<String> texts = fragments.get("text");
  //        if (texts != null) {
  //          for (String text : texts) {
  //            text = text.replaceAll("&lt;", "<");
  //            text = text.replaceAll("&gt;", ">");
  //            text = text.replaceAll("&quot;", "\"");
  //            return text;
  //          }
  //        }
  //      }
  //      return "";
  //    } catch (SolrServerException e) {
  //      LOG.error(e.getMessage());
  //      throw new IndexException(e.getMessage());
  //    }
  //  }
  //
  //  public QueryResponse getQueryResponse(String term, String[] facetFieldNames, String sort, boolean sortAscending, String core) throws SolrServerException, IOException {
  //    SolrQuery query = new SolrQuery()//
  //        .setQuery(term)//
  //        .setFields(SOLR_DEFAULT_FIELD)//
  //        .setRows(ROWS)//
  //        .addFacetField(facetFieldNames)//
  //        .setFacetMinCount(0)//
  //        .setFacetLimit(FACET_LIMIT)//
  //        .setFilterQueries("!cache=false");
  //    if (StringUtils.isNotBlank(sort)) {
  //      query.setSort(sort, sortAscending ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc);
  //    }
  //    LOG.debug("{}", query);
  //    // For some bizarre reason, need to do this, too:
  //    server.commit();
  //    return server.query(query);
  //  }

}
