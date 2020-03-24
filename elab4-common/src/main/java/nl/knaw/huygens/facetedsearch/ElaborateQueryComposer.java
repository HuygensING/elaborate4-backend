package nl.knaw.huygens.facetedsearch;

/*
 * #%L
 * elab4-common
 * =======
 * Copyright (C) 2013 - 2020 Huygens ING
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
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ElaborateQueryComposer implements QueryComposer {
  private static final String FUZZY = "~0.75";
  private String searchQuery;
  private String highlightQuery;
  private boolean mustHighlight = false;

  @Override
  public void compose(ElaborateSearchParameters sp) {
    List<String> textLayers = sp.getTextLayers();
    String joinedTermQuery = "";
    if (textLayers.isEmpty() || sp.getTerm().equals("*")) {
      joinedTermQuery = "*:*";

    } else {
      List<String> terms = getTerms(sp);
      String termQuery = (terms.size() == 1) ? terms.get(0) : MessageFormat.format("({0})", Joiner.on(" ").join(terms));

      List<String> fieldQueries = Lists.newArrayList();
      fieldQueries.add(MessageFormat.format("{0}:{1}", SolrFields.TITLE, termQuery)); // always search in entry title as well
      for (String field : sp.getTextFieldsToSearch().keySet()) {
        fieldQueries.add(MessageFormat.format("{0}:{1}", field, termQuery));
      }
      joinedTermQuery = Joiner.on(" ").join(fieldQueries);
      if (StringUtils.isEmpty(joinedTermQuery)) {
        joinedTermQuery = "*:*";
      }
    }
    // the solr highlighter should only get the fulltextsearch part of the query
    if (joinedTermQuery.equals("*:*")) {
      mustHighlight = false;
    } else {
      mustHighlight = true;
      this.highlightQuery = joinedTermQuery;
    }

    List<String> facetQueries = composeFacetQueries(sp);
    if (!facetQueries.isEmpty()) {
      String joinedFacetQuery = Joiner.on(" ").join(facetQueries);
      joinedTermQuery = MessageFormat.format("+({0}) {1}", joinedTermQuery, joinedFacetQuery);
    }
    this.searchQuery = joinedTermQuery;
  }

  private List<String> composeFacetQueries(ElaborateSearchParameters sp) {
    List<String> facetQueries = Lists.newArrayList();
    for (FacetParameter fp : sp.getFacetValues()) {
      if (!fp.isRangeFacetParameter()) {
        String prefix = fp.combineValuesWithAnd() ? "+" : "";
        String values = prefix + Joiner.on(" " + prefix).join(fp.getEscapedValues());
        String facetQuery = MessageFormat.format("+{0}:({1})", fp.getName(), values);
        facetQueries.add(facetQuery);

      } else {
        long lowerDate = fp.getLowerLimit();
        long upperDate = fp.getUpperLimit();
        String rangeQuery = MessageFormat.format(//
            "+{0}_lower:[{1,number,#} TO {2,number,#}] +{0}_upper:[{1,number,#} TO {2,number,#}]", //
            fp.getName(), lowerDate, upperDate//
        );
        facetQueries.add(rangeQuery);
      }
      //
    }
    return facetQueries;
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

  @Override
  public String getSearchQuery() {
    if (searchQuery == null) {
      throw new RuntimeException("searchQuery not set, call compose() first");
    }
    return searchQuery;
  }

  @Override
  public String getHighlightQuery() {
    if (highlightQuery == null) {
      throw new RuntimeException("highlightQuery not set, call compose() first");
    }
    return highlightQuery;
  }

  @Override
  public boolean mustHighlight() {
    return mustHighlight;
  }

}
