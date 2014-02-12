package elaborate.editor.model.orm;

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
