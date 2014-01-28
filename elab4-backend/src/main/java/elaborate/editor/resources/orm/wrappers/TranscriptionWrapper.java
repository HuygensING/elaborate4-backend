package elaborate.editor.resources.orm.wrappers;

import java.util.List;

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

	public TranscriptionWrapper() {}

	public TranscriptionWrapper(Transcription transcription) {
		setId(transcription.getId());
		setTextLayer(transcription.getTextLayer());
		convertBodyForOutput(transcription.getBody());
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
		//    LOG.info("body from db={}", bodyIn);
		Document document = Document.createFromXml(bodyIn, true);
		TranscriptionBodyVisitor visitor = new TranscriptionBodyVisitor();
		document.accept(visitor);

		setBody(visitor.getContext().getResult()//
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
		getLOG(TranscriptionWrapper.class).info("body input={}", bodyIn);
		String xml = Transcription.BODY_START + XmlUtil.fixXhtml(bodyIn) + Transcription.BODY_END;
		Document document = Document.createFromXml(xml, true);
		TranscriptionBodyInputVisitor visitor = new TranscriptionBodyInputVisitor();
		document.accept(visitor);

		String bodyOut = visitor.getContext().getResult().trim();
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
