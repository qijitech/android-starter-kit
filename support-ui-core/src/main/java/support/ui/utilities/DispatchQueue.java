/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package support.ui.utilities;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.CountDownLatch;

public class DispatchQueue extends Thread {

  private volatile Handler handler = null;
  private CountDownLatch syncLatch = new CountDownLatch(1);

  public DispatchQueue(final String threadName) {
    setName(threadName);
    start();
  }

  private void sendMessage(Message msg, int delay) {
    try {
      syncLatch.await();
      if (delay <= 0) {
        handler.sendMessage(msg);
      } else {
        handler.sendMessageDelayed(msg, delay);
      }
    } catch (Exception e) {
      FileLog.e("tmessages", e);
    }
  }

  public void cancelRunnable(Runnable runnable) {
    try {
      syncLatch.await();
      handler.removeCallbacks(runnable);
    } catch (Exception e) {
      FileLog.e("tmessages", e);
    }
  }

  public void postRunnable(Runnable runnable) {
    postRunnable(runnable, 0);
  }

  public void postRunnable(Runnable runnable, long delay) {
    try {
      syncLatch.await();
      if (delay <= 0) {
        handler.post(runnable);
      } else {
        handler.postDelayed(runnable, delay);
      }
    } catch (Exception e) {
      FileLog.e("tmessages", e);
    }
  }

  public void cleanupQueue() {
    try {
      syncLatch.await();
      handler.removeCallbacksAndMessages(null);
    } catch (Exception e) {
      FileLog.e("tmessages", e);
    }
  }

  @Override public void run() {
    Looper.prepare();
    handler = new Handler();
    syncLatch.countDown();
    Looper.loop();
  }
}
