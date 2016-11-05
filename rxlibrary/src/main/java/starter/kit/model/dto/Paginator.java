package starter.kit.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class Paginator<T> {

  @JsonProperty("total") private int total;

  @JsonProperty("per_page") private int perPage;
  @JsonProperty("current_page") private int currentPage;
  @JsonProperty("last_page") private int lastPage;

  @JsonProperty("data") private ArrayList<T> mData;

  public ArrayList<T> data() {
    return mData;
  }

  public int perPage() {
    return perPage;
  }

  public int currentPage() {
    return currentPage;
  }

  public int lastPage() {
    return lastPage;
  }

  public int total() {
    return total;
  }

  /**
   * Determine if there are more items in the data source.
   *
   * @return bool
   */
  public boolean hasMorePages() {
    return currentPage < lastPage && size() >= perPage();
  }

  public int size() {
    return mData != null ? mData.size() : 0;
  }
}
