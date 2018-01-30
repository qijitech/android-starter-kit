package starter.kit.app;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */

public class LogAdapter implements com.orhanobut.logger.LogAdapter {

  private final FormatStrategy formatStrategy;
  private boolean isLoggable;

  public LogAdapter(String tag, boolean isLoggable) {
    this.formatStrategy = PrettyFormatStrategy.newBuilder().tag(tag).build();
    this.isLoggable = isLoggable;
  }

  public LogAdapter(FormatStrategy formatStrategy) {
    this.formatStrategy = formatStrategy;
  }

  @Override public boolean isLoggable(int priority, String tag) {
    return isLoggable;
  }

  @Override public void log(int priority, String tag, String message) {
    formatStrategy.log(priority, tag, message);
  }
}