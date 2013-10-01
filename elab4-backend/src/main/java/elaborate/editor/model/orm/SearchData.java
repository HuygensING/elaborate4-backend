package elaborate.editor.model.orm;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import elaborate.editor.model.AbstractStoredEntity;

@Entity
@Table(name = "searchdata")
@XmlRootElement(name = "searchdata")
public class SearchData extends AbstractStoredEntity<SearchData> {
  private static final long serialVersionUID = 1L;

  @Transient
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Temporal(TemporalType.TIMESTAMP)
  private Date created_on;

  String json;

  public Date getCreatedOn() {
    return created_on;
  }

  public SearchData setCreatedOn(Date created_on) {
    this.created_on = created_on;
    return this;
  }

  public String getJson() {
    return json;
  }

  public SearchData setJson(String json) {
    this.json = json;
    return this;
  }

  public SearchData setResults(Map<String, Object> result) {
    StringWriter stringWriter = new StringWriter();
    try {
      objectMapper.writeValue(stringWriter, result);
    } catch (JsonGenerationException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    setJson(stringWriter.toString());
    return this;
  }

  @JsonIgnore
  public Map<String, Object> getResults() {
    try {
      return objectMapper.readValue(json, Map.class);
    } catch (JsonParseException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
