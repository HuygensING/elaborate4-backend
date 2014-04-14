package elaborate.editor.export.tei;

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

import static nl.knaw.huygens.tei.Traversal.NEXT;
import static nl.knaw.huygens.tei.Traversal.STOP;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.persistence.EntityManager;

import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;
import nl.knaw.huygens.tei.handlers.XmlTextHandler;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;

import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.AnnotationMetadataItem;
import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.service.AnnotationService;

public class TranscriptionVisitor extends DelegatingVisitor<XmlContext> {
	static final Stack<Element> openElements = new Stack<Element>();

	private static int linenum = 1;
	private static boolean skipNextNewline = false;

	private static final String TAG_LB = "lb";

	public TranscriptionVisitor(TeiConversionConfig config, String transcriptionType, EntityManager entityManager) {
		super(new XmlContext());
		setTextHandler(new XmlTextHandler<XmlContext>());
		setDefaultElementHandler(new DefaultElementHandler());
		addElementHandler(new BrHandler(), "br");
		addElementHandler(new LbHandler(), TAG_LB);
		addElementHandler(new IgnoreHandler(NEXT), "content");
		addElementHandler(new DivHandler(transcriptionType), "div");
		addElementHandler(new XmlHandler(), "xml");
		addElementHandler(new SpanHandler(), "span");
		addElementHandler(new AnnotationHandler(config, entityManager), Transcription.BodyTags.ANNOTATION_BEGIN, Transcription.BodyTags.ANNOTATION_END);
	}

	static class BrHandler implements ElementHandler<XmlContext> {
		@Override
		public Traversal enterElement(Element element, XmlContext context) {
			return NEXT;
		}

		@Override
		public Traversal leaveElement(Element element, XmlContext context) {
			context.addEmptyElementTag(new Element(TAG_LB, "n", String.valueOf(linenum)));
			linenum++;
			return NEXT;
		}
	}

	static class LbHandler implements ElementHandler<XmlContext> {
		@Override
		public Traversal enterElement(Element element, XmlContext context) {
			return NEXT;
		}

		@Override
		public Traversal leaveElement(Element element, XmlContext context) {
			if (skipNextNewline) {
				skipNextNewline = false;
			} else {
				context.addEmptyElementTag(TAG_LB);
			}
			return NEXT;
		}
	}

	static class IgnoreHandler implements ElementHandler<XmlContext> {
		private final Traversal onEnter;

		public IgnoreHandler(Traversal onEnter) {
			this.onEnter = onEnter;
		}

		@Override
		public Traversal enterElement(Element element, XmlContext context) {
			return onEnter;
		}

		@Override
		public Traversal leaveElement(Element element, XmlContext context) {
			return NEXT;
		}
	}

	static class DivHandler implements ElementHandler<XmlContext> {
		private final String transcriptionType;

		public DivHandler(String transcriptionType) {
			this.transcriptionType = transcriptionType;
		}

		@Override
		public Traversal enterElement(Element element, XmlContext context) {
			element.setAttribute("type", transcriptionType);
			context.addOpenTag(element);
			return NEXT;
		}

		@Override
		public Traversal leaveElement(Element element, XmlContext context) {
			return NEXT;
		}
	}

	static class XmlHandler implements ElementHandler<XmlContext> {
		@Override
		public Traversal enterElement(Element element, XmlContext context) {
			context.addOpenTag(element);
			return NEXT;
		}

		@Override
		public Traversal leaveElement(Element element, XmlContext context) {
			context.addCloseTag(element);
			return NEXT;
		}
	}

	static class SpanHandler implements ElementHandler<XmlContext> {
		@Override
		public Traversal enterElement(Element element, XmlContext context) {
			String type = element.getAttribute("type");
			String style = element.getAttribute("style");
			String id = element.getAttribute("id");
			if (StringUtils.isEmpty(id)) {
				Element e = null;
				if ("text-decoration: underline;".equals(style)) {
					e = new Element("hi", "rend", "underline");

				} else if ("text-decoration: line-through;".equals(style)) {
					e = new Element("del", "rend", "strikethrough");

				} else if ("font-style: italic;".equals(style)) {
					e = new Element("ex");
				}
				if (e != null) {
					context.addOpenTag(e);
					openElements.push(element);
				}
			} else {
				if (isStartMilestone(id)) {
					if ("ex".equals(type)) {
						Element e = new Element("ex");
						context.addOpenTag(e);
						openElements.push(element);
					}
				} else {
					context.addCloseTag(openElements.pop());
				}
			}
			return NEXT;
		}

		@Override
		public Traversal leaveElement(Element element, XmlContext context) {
			if (!element.hasAttribute("style")) {
				context.addCloseTag(element);
			}
			return NEXT;
		}

		static boolean isStartMilestone(String id) {
			return id.endsWith("s");
		}

	}

	static class AnnotationHandler implements ElementHandler<XmlContext> {
		private final TeiConversionConfig config;
		private final EntityManager entityManager;

		public AnnotationHandler(TeiConversionConfig config, EntityManager entityManager) {
			this.config = config;
			this.entityManager = entityManager;
		}

		@Override
		public Traversal enterElement(Element element, XmlContext context) {
			String id = element.getAttribute("id");
			Annotation annotation = getAnnotation(id);
			if (annotation != null) {
				AnnotationType annotationType = annotation.getAnnotationType();
				String name = element.getName();
				if (name.equals(Transcription.BodyTags.ANNOTATION_BEGIN)) {
					if (isMappable(annotationType)) {
						TagInfo taginfo = tagInfo(annotation, annotationType);
						context.addOpenTag(new Element(taginfo.getName(), taginfo.getAttributes()));

					} else {
						//            addPtr(context, id, "annotation_begin");
					}

				} else if (name.equals(Transcription.BodyTags.ANNOTATION_END)) {
					if (isMappable(annotationType)) {
						TagInfo taginfo = tagInfo(annotation, annotationType);
						context.addCloseTag(new Element(taginfo.getName()));
						skipNextNewline = taginfo.skipNewlineAfter();

					} else {
						//            addPtr(context, id, "annotation_end");
						addNote(context, annotation);
					}
				}
			}

			return STOP;
		}

		@Override
		public Traversal leaveElement(Element element, XmlContext context) {
			return NEXT;
		}

		private boolean isMappable(AnnotationType annotationType) {
			return config.getAnnotationTypeMapper().containsKey(annotationType);
		}

		private TagInfo tagInfo(Annotation annotation, AnnotationType annotationType) {
			TagInfo taginfo = config.getAnnotationTypeMapper().get(annotationType).apply(annotation);
			return taginfo;
		}

		private void addNote(XmlContext context, Annotation annotation) {
			Element note = new Element("note");
			note.setAttribute("xml:id", "note" + annotation.getAnnotationNo());
			note.setAttribute("type", annotation.getAnnotationType().getName());
			String annotationBody = annotation.getBody();
			context.addOpenTag(note);
			Set<AnnotationMetadataItem> annotationMetadataItems = annotation.getAnnotationMetadataItems();
			if (!annotationMetadataItems.isEmpty()) {
				context.addOpenTag(TeiMaker.INTERP_GRP);
				for (AnnotationMetadataItem annotationMetadataItem : annotationMetadataItems) {
					String type = annotationMetadataItem.getAnnotationTypeMetadataItem().getName();
					String value = annotationMetadataItem.getData();
					context.addEmptyElementTag(interp(type, value));
				}
				context.addCloseTag(TeiMaker.INTERP_GRP);
			}
			context.addLiteral(AnnotationBodyConverter.convert(annotationBody));
			context.addCloseTag(note);
		}

		private Element interp(String key, String value) {
			Map<String, String> attrs = Maps.newHashMap();
			attrs.put("type", key);
			attrs.put("value", StringEscapeUtils.escapeHtml(value));
			Element meta = new Element("interp", attrs);
			return meta;
		}

		private Annotation getAnnotation(String annotationId) {
			return AnnotationService.instance().getAnnotationByAnnotationNo(Integer.valueOf(annotationId), entityManager);
		}

	}

	static class DefaultElementHandler implements ElementHandler<XmlContext> {
		@Override
		public Traversal enterElement(Element element, XmlContext context) {
			String name = element.getName();
			if (TeiMaker.HI_TAGS.containsKey(name)) {
				Element hi = new Element("hi", "rend", TeiMaker.HI_TAGS.get(name));
				context.addOpenTag(hi);
				openElements.push(hi);

			} else {
				context.addOpenTag(element);
				openElements.push(element);
			}
			return NEXT;
		}

		@Override
		public Traversal leaveElement(Element element, XmlContext context) {
			context.addCloseTag(openElements.pop());
			return NEXT;
		}
	}

	static class Handler implements ElementHandler<XmlContext> {
		@Override
		public Traversal enterElement(Element element, XmlContext context) {
			return NEXT;
		}

		@Override
		public Traversal leaveElement(Element element, XmlContext context) {
			return NEXT;
		}
	}

}
