package elaborate.editor.solr;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2020 Huygens ING
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

import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;

class SolrIndexerVisitor extends DelegatingVisitor<XmlContext> {

	public SolrIndexerVisitor() {
		super(new XmlContext());
		setDefaultElementHandler(new DefaultHandler());
		// addElementHandler(spanHandler(), Element.SPAN_TAG, Element.DIV_TAG);
	}

	static class DefaultHandler implements ElementHandler<XmlContext> {

		@Override
		public Traversal enterElement(Element e, XmlContext c) {
			return Traversal.NEXT;
		}

		@Override
		public Traversal leaveElement(Element e, XmlContext c) {
			return Traversal.NEXT;
		}
	}

}
