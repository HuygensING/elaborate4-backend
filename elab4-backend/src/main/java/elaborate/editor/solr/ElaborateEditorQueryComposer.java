package elaborate.editor.solr;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2018 Huygens ING
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

import org.apache.commons.lang.StringUtils;

import nl.knaw.huygens.facetedsearch.ElaborateQueryComposer;
import nl.knaw.huygens.facetedsearch.ElaborateSearchParameters;
import nl.knaw.huygens.facetedsearch.SolrFields;

public class ElaborateEditorQueryComposer extends ElaborateQueryComposer {
	private String searchQuery;

	@Override
	public void compose(ElaborateSearchParameters sp) {
		super.compose(sp);
		String joinedTermQuery = super.getSearchQuery();

		boolean joinedTermQueryIsEmpty = StringUtils.isEmpty(joinedTermQuery);
		ElaborateEditorSearchParameters esp = (ElaborateEditorSearchParameters) sp;
		searchQuery = joinedTermQueryIsEmpty ? MessageFormat.format("{0}:{1,number,#}", SolrFields.PROJECT_ID, esp.getProjectId()) //
				: MessageFormat.format("({0}) AND {1}:{2,number,#}", joinedTermQuery, SolrFields.PROJECT_ID, esp.getProjectId());
	}

	@Override
	public String getSearchQuery() {
		if (searchQuery == null) {
			throw new RuntimeException("searchQuery not set, call compose() first");
		}
		return searchQuery;
	}

}
