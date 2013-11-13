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
  public long id;
  public String textLayer;
  public String body;

  @JsonIgnore
  public List<Integer> annotationNumbers = Lists.newArrayList();

  public TranscriptionWrapper(Transcription transcription) {
    id = transcription.getId();
    textLayer = transcription.getTextLayer();
    convertBodyForOutput(transcription.getBody());
  }

  void convertBodyForOutput(String bodyIn) {
    LOG.info("body from db={}", bodyIn);
    Document document = Document.createFromXml(bodyIn, true);
    TranscriptionBodyVisitor visitor = new TranscriptionBodyVisitor();
    document.accept(visitor);

    body = visitor.getContext().getResult()//
        .replaceAll("\n", "<br>")//
        .trim();
    annotationNumbers = visitor.getAnnotationIds();
  }

  @JsonIgnore
  public Transcription getTranscription() {
    Transcription transcription = new Transcription();
    transcription.setTextLayer(textLayer);
    transcription.setBody(convertFromInput(body));
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
    return convertFromInput(body);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
