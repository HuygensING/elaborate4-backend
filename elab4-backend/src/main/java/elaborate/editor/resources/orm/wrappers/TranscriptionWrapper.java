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

import java.util.List;
import java.util.Map;

import nl.knaw.huygens.LoggableObject;
import nl.knaw.huygens.tei.Document;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import elaborate.editor.model.orm.Transcription;
import elaborate.util.XmlUtil;

public class TranscriptionWrapper extends LoggableObject {
	private long id = 0l;
	private String textLayer = "";
	private String body = "";

	@JsonIgnore
	private final Map<Integer, String> annotationTypes;

	@JsonIgnore
	private final Map<Integer, Map<String, String>> annotationParameters;

	public TranscriptionWrapper() {
		this.annotationTypes = null;
		this.annotationParameters = null;
	}

	public TranscriptionWrapper(Transcription transcription, Map<Integer, String> annotationTypes, Map<Integer, Map<String, String>> annotationParameters) {
		this.annotationTypes = annotationTypes;
		this.annotationParameters = annotationParameters;
		setId(transcription.getId());
		setTextLayer(transcription.getTextLayer());
		String tBody = transcription.getBody();
		convertBodyForOutput(tBody);
	}

	public TranscriptionWrapper(Transcription transcription) {
		this(transcription, null, null);
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
		//		LOG.info("body from db={}", bodyIn);
		String xml = bodyIn;
		if (!XmlUtil.isWellFormed(bodyIn)) {
			LOG.error("body not well-formed:\n({})", bodyIn);
			xml = "<body>" + XmlUtil.fixXhtml(bodyIn) + "</body>";
			LOG.info("fixed body:\n({})", xml);
		}
		Document document;
		try {
			document = Document.createFromXml(xml, true);
		} catch (Exception e) {
			e.printStackTrace();
			String fixed = xml.replaceAll("</?span[^>]*>", "");
			document = Document.createFromXml(fixed, true);
		}

		TranscriptionBodyVisitor visitor = new TranscriptionBodyVisitor(annotationTypes, annotationParameters);
		document.accept(visitor);

		setBody(visitor.getContext().getResult()//
				.replaceAll("<[a-zA-Z]+/>", "")//
				.replaceAll("\n", "<br>")//
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

	static String convertFromInput(String bodyIn) {
		bodyIn = bodyIn//
				.replaceAll("<br>", "<br/>")//
				.replace("\u001A", " ")//		0x1a
				.replace("", " ")//
				.replace("&nbsp;", "&#160;")//
		;
		getLOG(TranscriptionWrapper.class).info("body input={}", bodyIn);
		String xml = Transcription.BODY_START + XmlUtil.fixXhtml(bodyIn) + Transcription.BODY_END;
		Document document = Document.createFromXml(xml, true);
		TranscriptionBodyInputVisitor visitor = new TranscriptionBodyInputVisitor();
		document.accept(visitor);

		String bodyOut = visitor.getContext().getResult().replace("<span class=\"hilite\" data-highlight=\"\"></span>", "").trim();
		return bodyOut;
	}

	@JsonIgnore
	public String getBodyForDb() {
		return convertFromInput(getBody());
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
