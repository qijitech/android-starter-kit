package starter.kit.rx.app.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.parceler.Parcel;
import starter.kit.model.entity.DefaultEntity;

@Parcel @JsonIgnoreProperties(ignoreUnknown = true) public class Comment extends DefaultEntity {

  public String content;
  @JsonProperty("created_at") public int createdAt;

  @JsonProperty("user_info") public UserInfo userInfo;

  public Comment parent;
}
