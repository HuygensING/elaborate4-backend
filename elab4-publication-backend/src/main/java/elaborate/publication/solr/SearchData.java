package elaborate.publication.solr;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import elaborate.LoggableObject;

@XmlRootElement(name = "searchdata")
public class SearchData extends LoggableObject {
  private final long id;
  private final Date created_on;
  String json;

  public SearchData() {
    created_on = new Date();
    id = created_on.getTime();
  }

  private final ObjectMapper objectMapper = new ObjectMapper();

  public Date getCreatedOn() {
    return created_on;
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

  public long getId() {
    return id;
  }
}
