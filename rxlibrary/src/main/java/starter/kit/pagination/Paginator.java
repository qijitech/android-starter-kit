package starter.kit.pagination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import starter.kit.model.entity.Entity;
import support.ui.collect.Lists;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class Paginator<T extends Entity>
    extends AbstractPaginator<T> {

  @JsonProperty("total") private int total;

  @JsonProperty("per_page") private Integer perPage;
  @JsonProperty("current_page") private Integer currentPage;
  @JsonProperty("last_page") private Integer lastPage;

  @JsonProperty("data") private ArrayList<T> items;

  @Override public ArrayList<T> items() {
    return this.items == null ? Lists.newArrayList() : this.items;
  }

  @Override public Integer currentPage() {
    return currentPage;
  }

  @Override public Integer perPage() {
    return perPage;
  }

  @Override public int size() {
    return items != null ? items.size() : 0;
  }

  @Override public int total() {
    return total;
  }

  @Override public boolean hasMorePages() {
    return currentPage < lastPage && size() >= perPage();
  }

  @Override public boolean isEmpty() {
    return size() == 0;
  }
}
