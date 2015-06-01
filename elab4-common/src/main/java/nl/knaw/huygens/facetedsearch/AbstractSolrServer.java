package nl.knaw.huygens.facetedsearch;

/*
 * #%L
 * elab4-common
 * =======
 * Copyright (C) 2013 - 2015 Huygens ING
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.knaw.huygens.Log;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.HighlightParams;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

public abstract class AbstractSolrServer implements SolrServerWrapper {
	public static final String KEY_NUMFOUND = "numFound";
	private static final int HIGHLIGHT_FRAGSIZE = 100;
	private static final int ROWS = 50000;
	private static final int FACET_LIMIT = 10000;

	protected SolrServer server;
	private final QueryComposer queryComposer;

	public AbstractSolrServer(QueryComposer queryComposer) {
		this.queryComposer = queryComposer;
	}

	public abstract void setServer();

	@Override
	public void initialize() throws IndexException {
		try {
			server.deleteByQuery("*:*");
		} catch (Exception e) {
			throw new IndexException(e.getMessage());
		}
	}

	@Override
	public void optimize() throws IndexException {
		try {
			server.optimize();
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
			Log.error("ping failed with '{}'", e.getMessage());
			return false;
		}
	}

	@Override
	public void delete(String id) throws SolrServerException, IOException {
		server.deleteById(id);
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

	@Override
	public Map<String, Object> search(FacetedSearchParameters<?> fsp) throws IndexException {
		ElaborateSearchParameters sp = (ElaborateSearchParameters) fsp;
		Log.info("searchparameters={}", sp);
		queryComposer.compose(sp);
		String queryString = queryComposer.getSearchQuery();
		String[] facetFields = sp.getFacetFields();
		//		Log.debug("search({},{})", queryString, sp.getSort());
		Map<String, String> textFieldMap = sp.getTextFieldsToSearch();

		SolrQuery query = new SolrQuery();
		String[] fieldsToReturn = getIndexFieldToReturn(sp.getResultFields());
		query.setQuery(queryString)//
				.setFields(fieldsToReturn)//
				.setRows(ROWS)//
				.addFacetField(facetFields)//
				.setFacetMinCount(1)//
				.setFacetLimit(FACET_LIMIT);
		if (queryComposer.mustHighlight()) {
			query//
					.setHighlight(true)//
					.setHighlightSnippets(500)//
					.setHighlightFragsize(HIGHLIGHT_FRAGSIZE);

			query.set(HighlightParams.MERGE_CONTIGUOUS_FRAGMENTS, false);
			query.set(HighlightParams.MAX_CHARS, -1);
			query.set(HighlightParams.FIELDS, textFieldMap.keySet().toArray(new String[textFieldMap.size()]));
			query.set(HighlightParams.Q, queryComposer.getHighlightQuery());
		}
		query = setSort(query, sp);

		Map<String, Object> data = getSearchData(sp, facetFields, query, fieldsToReturn);
		return data;
	}

	private Map<String, Object> getSearchData(ElaborateSearchParameters sp, String[] facetFields, SolrQuery query, String[] fieldsToReturn) throws IndexException {
		Map<String, Object> data = Maps.newHashMap();
		data.put("term", query.getQuery());
		try {
			Log.info("query=\n{}", query);
			QueryResponse response = server.query(query);
			Log.debug("response='{}'", response);

			SolrDocumentList documents = response.getResults();
			data.put(KEY_NUMFOUND, documents.getNumFound());

			Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

			List<String> ids = Lists.newArrayList();
			List<Map<String, Object>> results = Lists.newArrayList();
			int occurrences = 0;
			for (SolrDocument document : documents) {
				String docId = document.getFieldValue(SolrFields.DOC_ID).toString();
				ids.add(docId);
				Map<String, List<String>> map = (Map<String, List<String>>) ((highlighting == null) ? ImmutableMap.of() : highlighting.get(docId));
				Map<String, Object> result = entryView(document, fieldsToReturn, map, sp.getTextFieldsToSearch());
				results.add(result);
				for (Integer integer : ((Map<String, Integer>) result.get("terms")).values()) {
					occurrences += integer;
				}
			}
			data.put("ids", ids);
			data.put("results", results);
			data.put("occurrences", occurrences);
			data.put("facets", getFacetCountList(sp, facetFields, response));

		} catch (SolrServerException e) {
			Log.error(e.getMessage());
			throw new IndexException(e.getMessage());
		}
		data.put("solrquery", query.toString());
		return data;
	}

	private List<FacetCount> getFacetCountList(ElaborateSearchParameters sp, String[] facetFields, QueryResponse response) {
		List<FacetCount> facets = Lists.newArrayList();
		for (String facetField : facetFields) {
			FacetInfo facetInfo = sp.getFacetInfoMap().get(facetField);
			if (facetInfo != null) {
				FacetCount facetCount = convertFacet(response.getFacetField(facetField), facetInfo.getTitle(), facetInfo.getType());
				if (!facetCount.getOptions().isEmpty()) {
					facets.add(facetCount);
				}
			}
		}
		return facets;
	}

	private Map<String, Object> entryView(SolrDocument document, String[] fieldsToReturn, Map<String, List<String>> kwicMap, Map<String, String> fieldMap) {
		Map<String, Object> view = Maps.newHashMap();
		for (String field : fieldsToReturn) {
			if (field.startsWith(SolrUtils.METADATAFIELD_PREFIX)) {
				view.put(field, document.getFieldValues(field));
			} else {
				view.put(field, document.getFieldValue(field));
			}
		}

		//		Map<String, List<String>> newKwicMap = Maps.newLinkedHashMap();
		//		Set<Entry<String, List<String>>> entrySet = kwicMap.entrySet();
		//		for (Entry<String, List<String>> entry : entrySet) {
		//			String fieldName = entry.getKey();
		//			String fieldTitle = fieldMap.get(fieldName);
		//			newKwicMap.put(fieldTitle, entry.getValue());
		//		}

		Multiset<String> terms = HashMultiset.create();
		Map<String, Object> newKwicMap = Maps.newLinkedHashMap();
		for (Entry<String, List<String>> entry : kwicMap.entrySet()) {
			String fieldName = entry.getKey();
			List<String> raw = entry.getValue();
			List<String> snippets = Lists.newArrayListWithCapacity(raw.size());
			for (String snippet : raw) {
				snippets.add(snippet.trim());
			}
			terms.addAll(extractTerms(snippets));
			String fieldTitle = fieldMap.get(fieldName);
			newKwicMap.put(fieldTitle, snippets);
		}

		Map<String, Integer> termCountMap = getTermCountMap(terms);
		view.put("terms", termCountMap);
		view.put("_kwic", newKwicMap);
		return view;
	}

	private Map<String, Integer> getTermCountMap(Multiset<String> terms) {
		Map<String, Integer> termCountMap = Maps.newHashMap();
		for (String term : terms.elementSet()) {
			termCountMap.put(term, terms.count(term));
		}
		return termCountMap;
	}

	private String[] getIndexFieldToReturn(Collection<String> collection) {
		List<String> list = Lists.newArrayList(SolrFields.DOC_ID, SolrFields.NAME);
		for (String level : collection) {
			list.add(SolrUtils.facetName(level));
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Sets the sort criteria for the query.
	 * @return query the SolrQuery 
	 */
	private SolrQuery setSort(SolrQuery query, ElaborateSearchParameters sp) {
		LinkedHashSet<SortParameter> sortParameters = sp.getSortParameters();
		for (SortParameter sortParameter : sortParameters) {
			if (StringUtils.isNotBlank(sortParameter.getFieldname())) {
				String facetName = SolrUtils.facetName(sortParameter.getFieldname());
				ORDER solrOrder = solrOrder(sortParameter.getDirection());
				query.addSort(facetName, solrOrder);
			}
		}

		LinkedHashSet<String> levelFields = Sets.newLinkedHashSet(ImmutableList.of(sp.getLevel1Field(), sp.getLevel2Field(), sp.getLevel3Field(), SolrFields.NAME));
		for (String sortField : levelFields) {
			query.addSort(sortField, SolrQuery.ORDER.asc);
		}
		return query;
	}

	private ORDER solrOrder(String direction) {
		return "asc".equals(direction) ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc;
	}

	//	/**
	//	 * Sets the sort criteria for the query.
	//	 */
	//	private SolrQuery setSort1(SolrQuery query, ElaborateSearchParameters sp) {
	//		boolean ascending = sp.isAscending();
	//		String sortField = sp.getSort();
	//		ORDER sortOrder = ascending ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc;
	//		if (SolrFields.SCORE.equals(sortField)) {
	//			query.addSort(SolrFields.SCORE, ascending ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc);
	//
	//		} else if (sortField != null) {
	//			query.addSort(sortField, sortOrder);
	//		}
	//
	//		query.addSort(sp.getLevel1Field(), SolrQuery.ORDER.asc);
	//		query.addSort(sp.getLevel2Field(), SolrQuery.ORDER.asc);
	//		query.addSort(sp.getLevel3Field(), SolrQuery.ORDER.asc);
	//		query.addSort(SolrFields.NAME, SolrQuery.ORDER.asc);
	//		return query;
	//	}

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
					FacetCount.Option option = new FacetCount.Option()//
							.setName(count.getName())//
							.setCount(count.getCount());
					facetCount.addOption(option);
				}
			}
			return facetCount;
		}
		return null;
	}

	public static final String HL_PRE = "<em>";
	public static final String HL_POST = "</em>";
	private static final Pattern HL_REGEX = Pattern.compile(HL_PRE + "(.+?)" + HL_POST);

	public static Collection<String> extractTerms(List<String> snippets) {
		Collection<String> terms = Lists.newArrayList();
		for (String snippet : snippets) {
			final Matcher matcher = HL_REGEX.matcher(snippet);
			while (matcher.find()) {
				terms.add(matcher.group(1).toLowerCase());
			}
		}
		return terms;
	}

}
