package starter.kit.account;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author doublemine
 *         Created  on 2016/11/8.
 *         Summary:
 */

public class AccountStoreHelper {
  private final static String TAG = AccountStoreHelper.class.getSimpleName();

  private static final String JSON_FILE_NAME = "account.json";
  private static final String DIR_ACCOUNT = "Account";
  private Context mContext;
  private JSONObject mJSONObject;

  public AccountStoreHelper(Context context) {
    mContext = context;
    mJSONObject = new JSONObject();
  }

  private boolean mFlagReWrite;//是否写入
  private String mCurrentContent;

  private void close(Flushable flushable) {
    close(null, flushable);
  }

  private void close(Closeable closeable) {
    close(closeable, null);
  }

  private void close(Closeable closeable, Flushable flushable) {
    try {
      if (flushable != null) flushable.flush();
      if (closeable != null) closeable.close();
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(TAG, e.toString());
    }
  }

  public void clear() {
    synchronized (this) {
      File file = new File(mContext.getFilesDir().getParent() + File.separator + DIR_ACCOUNT,
          JSON_FILE_NAME);
      if (file.exists()) {
        if (!file.delete()) file.deleteOnExit();
      }
      mCurrentContent = null;
      setFlagReWrite(true);
    }
  }

  private void setFlagReWrite(boolean flagReWrite) {
    synchronized (this) {
      mFlagReWrite = flagReWrite;
    }
  }

  public void write(String key, String value) {
    try {
      writeToFile(key, value);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void writeToFile(String key, String value) throws JSONException {
    String currentContent = getJsonString();
    JSONObject editJsonObj = null;
    if (TextUtils.isEmpty(currentContent)) {
      editJsonObj = new JSONObject();
    } else {
      editJsonObj = mJSONObject.getJSONObject(currentContent);
    }
    editJsonObj.put(key, value);
    synchronized (this) {
      put(editJsonObj.toString());
    }
  }

  public void put(String content) {
    File file =
        new File(mContext.getFilesDir().getParent() + File.separator + DIR_ACCOUNT, JSON_FILE_NAME);
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new FileWriter(file), 1024);
      out.write(content);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      close(out, out);
      setFlagReWrite(true);
    }
  }

  private String getJsonString() {
    if (!TextUtils.isEmpty(mCurrentContent) && !mFlagReWrite) {
      return mCurrentContent;
    } else {
      FileInputStream fileInputStream = null;
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try {
        fileInputStream = new FileInputStream(
            new File(mContext.getFilesDir().getParent() + File.separator + DIR_ACCOUNT,
                JSON_FILE_NAME));
        FileChannel channel = fileInputStream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1 << 13); // 8K
        int flag;
        while ((flag = channel.read(buffer)) != -1) {
          buffer.flip();
          byteArrayOutputStream.write(buffer.array(), 0, flag);
          buffer.clear();
        }
        setFlagReWrite(false);
        mCurrentContent = byteArrayOutputStream.toString();
        return mCurrentContent;
      } catch (IOException e) {
        Log.e(TAG, e.toString());
        mCurrentContent = null;
        return null;
      } finally {
        close(fileInputStream);
        close(byteArrayOutputStream, byteArrayOutputStream);
      }
    }
  }

  public String getString(String key) {
    try {
      return TextUtils.isEmpty(getJsonString()) ? null
          : mJSONObject.getJSONObject(getJsonString()).getString(key);
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }
}
