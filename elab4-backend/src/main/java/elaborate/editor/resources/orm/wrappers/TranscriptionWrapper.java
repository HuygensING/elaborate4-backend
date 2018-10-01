package elaborate.editor.resources.orm.wrappers;

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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.service.ProjectService.AnnotationData;
import elaborate.util.XmlUtil;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Document;

public class TranscriptionWrapper {
	// TODO: split into input/output object
	private long id = 0l;
	private String textLayer = "";
	private String body = "";
	private static final String NBSP = "\\u00A0";

	@JsonIgnore
	private Map<Integer, AnnotationData> annotationDataMap = null;

	// For input
	public TranscriptionWrapper() {}

	// for output
	public TranscriptionWrapper(Transcription transcription, Map<Integer, AnnotationData> annotationDataMap) {
		this.annotationDataMap = annotationDataMap;
		setId(transcription.getId());
		setTextLayer(transcription.getTextLayer());
		String tBody = transcription.getBody();
		convertBodyForOutput(tBody);
	}

	public long getId() {
		return id;
	}

	public TranscriptionWrapper setId(long id) {
		this.id = id;
		return this;
	}

	public String getTextLayer() {
		return textLayer;
	}

	public TranscriptionWrapper setTextLayer(String textLayer) {
		this.textLayer = textLayer;
		return this;
	}

	public String getBody() {
		return body;
	}

	public TranscriptionWrapper setBody(String body) {
		this.body = body;
		return this;
	}

	@JsonIgnore
	public List<Integer> annotationNumbers = Lists.newArrayList();

	void convertBodyForOutput(String bodyIn) {
		// Log.info("body from db={}", bodyIn);
		String xml = bodyIn;
		if (!XmlUtil.isWellFormed(bodyIn)) {
			Log.error("body not well-formed:\n({})", bodyIn);
			xml = "<body>" + XmlUtil.fixXhtml(bodyIn) + "</body>";
			Log.info("fixed body:\n({})", xml);
		}
		Document document;
		try {
			document = Document.createFromXml(xml, true);
		} catch (Exception e) {
			e.printStackTrace();
			String fixed = xml.replaceAll("</?span[^>]*>", "");
			document = Document.createFromXml(fixed, true);
		}

		TranscriptionBodyVisitor visitor = new TranscriptionBodyVisitor(annotationDataMap);
		document.accept(visitor);

		setBody(visitor.getContext().getResult()//
				.replaceAll("(?s)[" + NBSP + "\\s]+$", "")// remove whitespace at end of body
				.replaceAll("<[a-zA-Z]+/>", "")// remove milestone/empty tags
				.replaceAll("\n", "<br>")//
				.replaceAll("<strong>", "<b>")//
				.replaceAll("</strong>", "</b>")//
				.replaceAll("(<br>)+$", "")//
				.replaceAll(NBSP, "&nbsp;") //
				.trim());
		annotationNumbers = visitor.getAnnotationIds();
	}

	@JsonIgnore
	public Transcription getTranscription() {
		Transcription transcription = new Transcription();
		transcription.setTextLayer(getTextLayer());
		transcription.setBody(convertFromInput(getBody()));
		return transcription;
	}

	@JsonIgnore
	public String getBodyForDb() {
		return convertFromInput(getBody());
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	static String convertFromInput(String bodyIn) {
		bodyIn = bodyIn//
				.replaceAll("<br>", "<br/>")//
				.replaceAll("<[a-zA-Z]+:", "<")// remove xml namespacing (word copy-paste) 
				.replace("\u001A", " ")// 0x1a
				.replace("", " ")//
				.replace("&nbsp;", "&#160;")//
				;
		Log.info("body input={}", bodyIn);
		String xml = Transcription.BODY_START + XmlUtil.fixXhtml(bodyIn) + Transcription.BODY_END;
		Document document = Document.createFromXml(xml, true);
		TranscriptionBodyInputVisitor visitor = new TranscriptionBodyInputVisitor();
		document.accept(visitor);

		String bodyOut = visitor.getContext().getResult().replace("<span class=\"hilite\" data-highlight=\"\"></span>", "").trim();
		return bodyOut;
	}

}
