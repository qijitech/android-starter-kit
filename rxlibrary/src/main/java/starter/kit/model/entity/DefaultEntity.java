package starter.kit.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class DefaultEntity extends Entity {

  @JsonProperty("id") public String identifier;

  @Override public String identifier() {
    return identifier;
  }

  @Override public String paginatorKey() {
    return identifier;
  }
}
