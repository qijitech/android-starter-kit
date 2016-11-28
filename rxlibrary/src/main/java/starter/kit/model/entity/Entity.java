package starter.kit.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) public abstract class Entity {

  @JsonProperty("id") public String identifier;

  public String getPaginatorKey() {
    return identifier;
  }
}
