package elaborate.editor.resources.orm.wrappers;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2015 Huygens ING
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

import static nl.knaw.huygens.tei.Traversal.NEXT;
import static nl.knaw.huygens.tei.Traversal.STOP;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

import elaborate.editor.model.orm.Transcription;
import nl.knaw.huygens.facetedsearch.SolrUtils;
import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;
import nl.knaw.huygens.tei.handlers.RenderElementHandler;
import nl.knaw.huygens.tei.handlers.XmlTextHandler;

public class TranscriptionBodyVisitor extends DelegatingVisitor<XmlContext> {
	private static int notenum;
	private static List<Integer> annotationIds;
	private static Map<Integer, String> annotationTypes;
	private static Map<Integer, Map<String, String>> annotationParameters;

	public TranscriptionBodyVisitor(Map<Integer, String> _annotationTypes, Map<Integer, Map<String, String>> _annotationParameters) {
		super(new XmlContext());
		annotationTypes = _annotationTypes;
		annotationParameters = _annotationParameters;
		notenum = 1;
		annotationIds = Lists.newArrayList();
		setTextHandler(new XmlTextHandler<XmlContext>());
		setDefaultElementHandler(new RenderElementHandler());
		addElementHandler(new IgnoreHandler(), Transcription.BodyTags.BODY);
		addElementHandler(new AnnotationBeginHandler(), Transcription.BodyTags.ANNOTATION_BEGIN);
		addElementHandler(new AnnotationEndHandler(), Transcription.BodyTags.ANNOTATION_END);
	}

	private static class IgnoreHandler implements ElementHandler<XmlContext> {
		@Override
		public Traversal enterElement(Element e, XmlContext c) {
			return NEXT;
		}

		@Override
		public Traversal leaveElement(Element e, XmlContext c) {
			return NEXT;
		}
	}

	private static class AnnotationBeginHandler implements ElementHandler<XmlContext> {
		private static final String TAG_SPAN = "span";

		@Override
		public Traversal enterElement(Element e, XmlContext c) {
			String idString = e.getAttribute("id");
			if (StringUtils.isNotBlank(idString)) {
				Integer id = Integer.valueOf(idString);
				Element span = new Element(TAG_SPAN);
				span.setAttribute("data-marker", "begin");
				span.setAttribute("data-id", idString);
				if (annotationTypes != null) {
					span.setAttribute("data-type", annotationTypes.get(id));
					if (annotationParameters != null) {
						Map<String, String> parameters = annotationParameters.get(id);
						if (parameters != null) {
							for (Entry<String, String> entry : parameters.entrySet()) {
								String key = SolrUtils.normalize(entry.getKey());
								String value = entry.getValue();
								span.setAttribute("data-param-" + key, value);
							}
						}
					}
				}
				c.addOpenTag(span);
			}
			return STOP;
		}

		@Override
		public Traversal leaveElement(Element e, XmlContext c) {
			String id = e.getAttribute("id");
			if (StringUtils.isNotBlank(id)) {
				c.addCloseTag(TAG_SPAN);
			}
			return NEXT;
		}
	}

	private static class AnnotationEndHandler implements ElementHandler<XmlContext> {
		private static final String TAG_SUP = "sup";

		@Override
		public Traversal enterElement(Element e, XmlContext c) {
			String id = e.getAttribute("id");
			if (StringUtils.isNotBlank(id)) {
				Element sup = new Element(TAG_SUP);
				sup.setAttribute("data-marker", "end");
				sup.setAttribute("data-id", id);
				c.addOpenTag(sup);
			}
			return STOP;
		}

		@Override
		public Traversal leaveElement(Element e, XmlContext c) {
			String id = e.getAttribute("id");
			if (StringUtils.isNotBlank(id)) {
				annotationIds.add(Integer.valueOf(id));
				c.addLiteral(notenum++);
				c.addCloseTag(TAG_SUP);
			}
			return NEXT;
		}
	}

	public List<Integer> getAnnotationIds() {
		return annotationIds;
	}

}
