package elaborate.editor.model.orm;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import elaborate.editor.model.AbstractStoredEntity;

@Entity
@Table(name = "transcription_types")
@XmlRootElement
public class TranscriptionType extends AbstractStoredEntity<TranscriptionType> {
  private static final long serialVersionUID = 1L;

  public static final String DIPLOMATIC = "Diplomatic";
  public static final String CRITICAL = "Critical";
  public static final String TRANSLATION = "Translation";
  public static final String COMMENTS = "Comments";

  @Column(columnDefinition = "text")
  private String name;

  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "transcriptionType")
  private Set<Transcription> transcriptions;

  public Set<Transcription> getTranscriptions() {
    return transcriptions;
  }

  public TranscriptionType setTranscriptions(Set<Transcription> transcriptions) {
    this.transcriptions = transcriptions;
    return this;
  }

  public String getName() {
    return name;
  }

  public TranscriptionType setName(String name) {
    this.name = name;
    return this;
  }

}
