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

import nl.knaw.huygens.facetedsearch.ElaborateQueryComposer;
import nl.knaw.huygens.facetedsearch.ElaborateSearchParameters;
import nl.knaw.huygens.facetedsearch.SolrFields;

import org.apache.commons.lang.StringUtils;

public class ElaborateEditorQueryComposer extends ElaborateQueryComposer {
	@Override
	public String composeQueryString(ElaborateSearchParameters sp) {
		String joinedTermQuery = super.composeQueryString(sp);
		boolean joinedTermQueryIsEmpty = StringUtils.isEmpty(joinedTermQuery);
		ElaborateEditorSearchParameters esp = (ElaborateEditorSearchParameters) sp;
		return joinedTermQueryIsEmpty ? MessageFormat.format("{0}:{1,number,#}", SolrFields.PROJECT_ID, esp.getProjectId()) //
				: MessageFormat.format("({0}) AND {1}:{2,number,#}", joinedTermQuery, SolrFields.PROJECT_ID, esp.getProjectId());
	}

}
