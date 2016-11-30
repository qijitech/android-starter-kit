package starter.kit.rx.app.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.parceler.Parcel;
import starter.kit.model.entity.DefaultEntity;

@Parcel @JsonIgnoreProperties(ignoreUnknown = true) public class Category extends DefaultEntity {

  public String name;
}
