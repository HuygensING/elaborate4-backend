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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.knaw.huygens.facetedsearch.FacetCount;
import nl.knaw.huygens.facetedsearch.FacetInfo;
import nl.knaw.huygens.facetedsearch.FacetParameter;
import nl.knaw.huygens.facetedsearch.IndexException;
import nl.knaw.huygens.facetedsearch.SolrUtils;
import nl.knaw.huygens.facetedsearch.SortParameter;
import nl.knaw.huygens.solr.AbstractSolrServer;
import nl.knaw.huygens.solr.FacetedSearchParameters;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.HighlightParams;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

public abstract class AbstractElaborateSolrServer extends AbstractSolrServer {
	public static final String KEY_NUMFOUND = "numFound";
	private static final int HIGHLIGHT_FRAGSIZE = 100;
	private static final int ROWS = 50000;
	private static final int FACET_LIMIT = 10000;
	private static final String FUZZY = "~0.75";

	@Override
	public Map<String, Object> search(FacetedSearchParameters<?> fsp) throws IndexException {
		ElaborateSearchParameters sp = (ElaborateSearchParameters) fsp;
		LOG.info("searchparameters={}", sp);
		String queryString = composeQuery(sp);
		String[] facetFields = sp.getFacetFields();
		//		LOG.debug("search({},{})", queryString, sp.getSort());
		Map<String, String> textFieldMap = sp.getTextFieldsToSearch();

		SolrQuery query = new SolrQuery();
		String[] fieldsToReturn = getIndexFieldToReturn(sp.getResultFields());
		query.setQuery(queryString)//
				.setFields(fieldsToReturn)//
				.setRows(ROWS)//
				.addFacetField(facetFields)//
				.setFacetMinCount(1)//
				.setFacetLimit(FACET_LIMIT)//
				.setHighlight(true)//
				.setParam("hl.mergeContiguous", false)//
				.setParam("hl.maxAnalyzedChars", String.valueOf(100000))//
				.setHighlightSnippets(500)//
				.setHighlightFragsize(HIGHLIGHT_FRAGSIZE);
		query.set(HighlightParams.MAX_CHARS, -1);
		query.set(HighlightParams.FIELDS, textFieldMap.keySet().toArray(new String[textFieldMap.size()]));
		query.set(HighlightParams.Q, queryString);
		query = setSort(query, sp);

		Map<String, Object> data = getSearchData(sp, facetFields, query, fieldsToReturn);
		return data;
	}

	private Map<String, Object> getSearchData(ElaborateSearchParameters sp, String[] facetFields, SolrQuery query, String[] fieldsToReturn) throws IndexException {
		Map<String, Object> data = Maps.newHashMap();
		data.put("term", query.getQuery());
		try {
			LOG.info("query=\n{}", query);
			QueryResponse response = server.query(query);
			LOG.debug("response='{}'", response);

			SolrDocumentList documents = response.getResults();
			data.put(KEY_NUMFOUND, documents.getNumFound());

			Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

			List<String> ids = Lists.newArrayList();
			List<Map<String, Object>> results = Lists.newArrayList();
			int occurrences = 0;
			for (SolrDocument document : documents) {
				String docId = document.getFieldValue(SolrFields.DOC_ID).toString();
				ids.add(docId);
				Map<String, Object> result = entryView(document, fieldsToReturn, highlighting.get(docId), sp.getTextFieldsToSearch());
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
			LOG.error(e.getMessage());
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

		Multiset<String> terms = HashMultiset.create();
		Map<String, Object> newKwicMap = Maps.newLinkedHashMap();
		for (Entry<String, List<String>> entry : kwicMap.entrySet()) {
			String fieldName = entry.getKey();
			List<String> snippets = entry.getValue();
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

	private String[] getIndexFieldToReturn(Collection<String> orderLevels) {
		List<String> list = Lists.newArrayList(SolrFields.DOC_ID, SolrFields.NAME);
		for (String level : orderLevels) {
			list.add(SolrUtils.facetName(level));
		}
		return list.toArray(new String[list.size()]);
	}

	public String composeQuery(ElaborateSearchParameters sp) {
		List<String> textLayers = sp.getTextLayers();
		String joinedTermQuery = "";
		if (textLayers.isEmpty() || sp.getTerm().equals("*")) {
			joinedTermQuery = "*:*";

		} else {
			List<String> terms = getTerms(sp);
			String termQuery = (terms.size() == 1) ? terms.get(0) : MessageFormat.format("({0})", Joiner.on(" ").join(terms));

			List<String> fieldQueries = Lists.newArrayList();
			for (String field : sp.getTextFieldsToSearch().keySet()) {
				fieldQueries.add(MessageFormat.format("{0}:{1}", field, termQuery));
			}
			joinedTermQuery = Joiner.on(" ").join(fieldQueries);
			if (StringUtils.isEmpty(joinedTermQuery)) {
				joinedTermQuery = "*:*";
			}
		}

		List<String> facetQueries = Lists.newArrayList();
		for (FacetParameter fp : sp.getFacetValues()) {
			String values = Joiner.on(" ").join(fp.getEscapedValues());
			String facetQuery = MessageFormat.format("+{0}:({1})", fp.getName(), values);
			facetQueries.add(facetQuery);
		}

		if (!facetQueries.isEmpty()) {
			joinedTermQuery = MessageFormat.format("+({0}) {1}", joinedTermQuery, Joiner.on(" ").join(facetQueries));
		}

		return StringUtils.isEmpty(joinedTermQuery) ? MessageFormat.format("{0}:{1,number,#}", SolrFields.PROJECT_ID, sp.getProjectId()) //
				: MessageFormat.format("({0}) AND {1}:{2,number,#}", joinedTermQuery, SolrFields.PROJECT_ID, sp.getProjectId());
	}

	private List<String> getTerms(ElaborateSearchParameters sp) {
		List<String> terms = SolrUtils.splitTerms(sp.getTerm());
		if (sp.isFuzzy()) {
			List<String> fuzzyTerms = Lists.newArrayList();
			for (String term : terms) {
				term += FUZZY;
				fuzzyTerms.add(term);
			}
			return fuzzyTerms;
		}

		return terms;
	}

	/**
	 * Sets the sort criteria for the query.
	 * @return 
	 */
	private SolrQuery setSort(SolrQuery query, ElaborateSearchParameters sp) {
		//		boolean ascending = sp.isAscending();
		//		String sortField = sp.getSort();
		//		ORDER sortOrder = ascending ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc;

		//		if (SolrFields.SCORE.equals(sortField)) {
		//			query.addSort(SolrFields.SCORE, ascending ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc);
		//
		//		} else if (sortField != null) {
		//			query.addSort(sortField, sortOrder);
		//		}

		List<SortParameter> sortParameters = sp.getSortParameters();
		for (SortParameter sortParameter : sortParameters) {
			String facetName = SolrUtils.facetName(sortParameter.getFieldname());
			ORDER solrOrder = solrOrder(sortParameter.getDirection());
			query.addSort(facetName, solrOrder);
		}

		query.addSort(sp.getLevel1Field(), SolrQuery.ORDER.asc);
		query.addSort(sp.getLevel2Field(), SolrQuery.ORDER.asc);
		query.addSort(sp.getLevel3Field(), SolrQuery.ORDER.asc);
		query.addSort(SolrFields.NAME, SolrQuery.ORDER.asc);
		return query;
	}

	private ORDER solrOrder(String direction) {
		return "asc".equals(direction) ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc;
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
