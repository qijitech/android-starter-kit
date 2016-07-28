package support.ui.utilities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by YuGang Yang on 04 13, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public final class DeviceID {

  /**
   * @return Combined Device ID
   */
  @SuppressLint("DefaultLocale") public static String getDeviceID(Context context) {

    String m_szLongID = getIMEI(context)
        + getBTMACAddress()
        + getAndroidId(context)
        + getLocalMacAddress(context)
        + getPseudoUniqueID();

    // compute md5
    MessageDigest m = null;
    try {
      m = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
    // get md5 bytes
    byte p_md5Data[] = m.digest();
    // create a hex string
    String m_szUniqueID = new String();
    for (int i = 0; i < p_md5Data.length; i++) {
      int b = (0xFF & p_md5Data[i]);
      // if it is a single digit, make sure it have 0 in front (proper padding)
      if (b <= 0xF) m_szUniqueID += "0";
      // add number to string
      m_szUniqueID += Integer.toHexString(b);
    } // hex string to uppercase
    m_szUniqueID = m_szUniqueID.toUpperCase();
    return m_szUniqueID;
  }

  public static String getBTMACAddress() {
    try {
      BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
      m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      String m_szBTMAC = m_BluetoothAdapter.getAddress();
      return m_szBTMAC;
    } catch (Exception e) {
      // Nothing to do
    }
    return "";
  }

  public static String getAndroidId(Context context) {
    return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
  }

  public static String getPseudoUniqueID() {
    try {
      String m_szDevIDShort = "35" + //we make this look like a valid IMEI
          Build.BOARD.length() % 10 +
          Build.BRAND.length() % 10 +
          Build.CPU_ABI.length() % 10 +
          Build.DEVICE.length() % 10 +
          Build.DISPLAY.length() % 10 +
          Build.HOST.length() % 10 +
          Build.ID.length() % 10 +
          Build.MANUFACTURER.length() % 10 +
          Build.MODEL.length() % 10 +
          Build.PRODUCT.length() % 10 +
          Build.TAGS.length() % 10 +
          Build.TYPE.length() % 10 +
          Build.USER.length() % 10; //13 digits

      return m_szDevIDShort;
    } catch (Exception e) {
      // Nothing to do
    }
    return "";
  }

  /**
   *
   * @return
   */
  public static String getIMEI(Context context) {
    try {
      TelephonyManager TelephonyMgr =
          (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      return TelephonyMgr.getDeviceId();
    } catch (Exception e) {
      // Nothing to do
    }
    return "";
  }

  /**
   *
   * @return
   */
  public static String getLocalMacAddress(Context context) {
    try {
      WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      WifiInfo info = wifi.getConnectionInfo();
      return info.getMacAddress();
    } catch (Exception e) {
      // Nothing to do
    }
    return "";
  }

  private DeviceID() {
  }
}
