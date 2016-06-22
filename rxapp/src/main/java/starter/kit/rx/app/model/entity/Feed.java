package starter.kit.rx.app.model.entity;

import android.net.Uri;
import android.text.TextUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import org.parceler.Parcel;
import starter.kit.model.Entity;

@Parcel @JsonIgnoreProperties(ignoreUnknown = true) public class Feed extends Entity {

  public String content;
  @JsonProperty("created_at") public int createdAt;

  public User user;
  public ArrayList<Image> images;
}
