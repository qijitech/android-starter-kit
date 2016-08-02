package support.ui.utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by YuGang Yang on 04 13, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public final class AppInfo {

  public String os;
  public String deviceName;
  public String deviceId;
  public String version;
  public String versionCode;
  public String channel;
  public int screenWidth;
  public int screenHeight;

  private void initOs() {
    this.os = android.os.Build.MODEL + "," + android.os.Build.VERSION.SDK_INT + "," + android.os.Build.VERSION.RELEASE;
  }

  private void initMetrics() {
    this.screenWidth = ScreenUtils.getScreenWidth();
    this.screenHeight = ScreenUtils.getScreenHeight();
  }

  private void initDeviceId(Context context) {
    this.deviceId = DeviceID.getDeviceID(context);
  }

  private void initVersion(Context context) {
    PackageManager packageManager = context.getPackageManager();
    PackageInfo packInfo = null;
    try {
      packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    String version = "";
    String code = "";
    if (packInfo != null) {
      version = packInfo.versionName;
      code = Integer.valueOf(packInfo.versionCode).toString();
    }
    this.version = version;
    this.versionCode = code;
  }

  private void initChannel(Context context) {
    this.channel = AndroidUtilities.getMetaData(context, "UMENG_CHANNEL");
  }

  private void initDeviceName() {
    this.deviceName = android.os.Build.DEVICE;
  }

  public AppInfo(Context context) {
    initDeviceId(context);
    initVersion(context);
    initChannel(context);
    initOs();
    initDeviceName();
    initMetrics();
  }
}
