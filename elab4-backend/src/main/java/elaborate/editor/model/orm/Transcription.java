package elaborate.editor.model.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import elaborate.editor.model.AbstractProjectEntryElement;

@Entity
@Table(name = "transcriptions")
@XmlRootElement(name = "transcription")
public class Transcription extends AbstractProjectEntryElement<Transcription> {
  private static final long serialVersionUID = 1L;

  public static final String BODY_START = "<body>";
  public static final String BODY_END = "</body>";
  static final String DEFAULT_BODY = BODY_START + BODY_END;

  /* properties to persist */
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "transcription_type_id", columnDefinition = "int4")
  private TranscriptionType transcriptionType;

  @Column(columnDefinition = "text")
  private String title;

  @Column(columnDefinition = "text")
  private String body;

  @Column(columnDefinition = "text")
  private String text_layer;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "transcription")
  private List<Annotation> annotations = Lists.newArrayList();

  // public static Transcription create() {
  // return new Transcription();
  // }

  /* persistent properties getters and setters */
  public TranscriptionType getTranscriptionType() {
    return transcriptionType;
  }

  public Transcription setTranscriptionType(TranscriptionType transcriptionType) {
    this.transcriptionType = transcriptionType;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public Transcription setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getTextLayer() {
    return text_layer;
  }

  public Transcription setTextLayer(String textLayer) {
    this.text_layer = textLayer;
    return this;
  }

  public String getBody() {
    return body;
  }

  public Transcription setBody(String body) {
    this.body = body;
    return this;
  }

  public void setAnnotations(List<Annotation> annotations) {
    this.annotations = annotations;
  }

  @JsonIgnore
  public List<Annotation> getAnnotations() {
    return annotations;
  }

  // public static final String TYPE = "transcription";
  // static final String BODY_START = "<body>";
  // static final String BODY_END = "</body>";
  // static final String DEFAULT_BODY = BODY_START + BODY_END;
  // public static final Comparator<Transcription> TRANSCRIPTION_TITLE_COMPARATOR = new Comparator<Transcription>() {
  // @Override
  // public int compare(Transcription t1, Transcription t2) {
  // return t1.getTranscriptionType().getName().compareTo(t2.getTranscriptionType().getName());
  // }
  // };
  //
  //
  // @OneToMany
  // Annotation[] getAnnotations();
  //
  // @Implemented
  // Annotation addAnnotation(User creator);
  //
  // @Implemented
  // String getLabel();
  //
  // /**
  // * Remove the annotation and the corresponding annotationmarkers in the Transcription Body
  // * @param annotation The Annotation to remove
  // * @param modifier The User credited with the removal
  // */
  // @Implemented
  // void removeAnnotation(Annotation annotation, User modifier);
  //
  //
  // /**
  // * Remove annotation markers in the Transcription Body that have no corresponding annotation
  // * @param modifier The User credited with the removal
  // */
  // @Implemented
  // void removeOrphanedAnnotationReferences(User modifier);
  //
  // @Implemented
  // boolean hasTranscriptionType(TranscriptionType transcriptionType);
  //
  //
  // public Annotation addAnnotation(User creator) {
  // Annotation annotation = ModelFactory.createAnnotation(transcription, creator);
  // return annotation;
  // }
  //
  // public Project getProject() {
  // return transcription.getProjectEntry().getProject();
  // }
  //
  // public String getLabel() {
  // return String.format("transcription '%s' of %s", transcription.getTitle(), transcription.getProjectEntry().getLabel());
  // }
  //
  // public String getSolrId() {
  // return Transcription.TYPE + transcription.getId();
  // }
  //
  // public void index(boolean commitNow) {
  // new SolrIndexer().index(transcription, commitNow);
  // }
  //
  // public void deindex() {
  // new SolrIndexer().deindex(transcription);
  // }
  //
  // public void removeAnnotation(Annotation annotation, User deleter) {
  // int annotationNo = annotation.getAnnotationNo();
  // String begintag = annotationBeginTag(annotationNo);
  // String endtag = annotationEndTag(annotationNo);
  // transcription.setBody(transcription.getBody().replace(begintag, "").replace(endtag, ""));
  // ModelFactory.delete(annotation, deleter);
  // ModelFactory.save(transcription, deleter);
  // }
  //
  // public boolean hasTranscriptionType(TranscriptionType transcriptionType) {
  // return transcription.getTranscriptionType().equals(transcriptionType);
  // }
  public static class BodyTags {
    public static final String ANNOTATION_BEGIN = "ab";
    public static final String ANNOTATION_END = "ae";
    public static final List<String> ANNOTATION_MARKERS = Lists.newArrayList(ANNOTATION_BEGIN, ANNOTATION_END);

    protected static final String ANNOTATION_BEGIN_CLASS = "annotationstart";
    protected static final String ANNOTATION_END_CLASS = "annotationend";

    public static final String BODY = "body";
    protected static final String TAG_SUP = "sup";
    protected static final String TAG_CONTENT = "content";
    protected static final String TAG_SPAN = "span";
    protected static final String TAG_PAGEBREAK = "pb";
  }

}
