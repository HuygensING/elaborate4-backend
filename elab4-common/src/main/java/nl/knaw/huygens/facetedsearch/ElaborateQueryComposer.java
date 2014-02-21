package nl.knaw.huygens.facetedsearch;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ElaborateQueryComposer implements QueryComposer {
	private static final String FUZZY = "~0.75";

	@Override
	public String composeQueryString(ElaborateSearchParameters sp) {
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

		return joinedTermQuery;
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

}
