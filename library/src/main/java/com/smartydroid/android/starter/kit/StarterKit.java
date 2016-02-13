package com.smartydroid.android.starter.kit;

/**
 * Created by YuGang Yang on February 13, 2016.
 * Copyright 20015-2016 qiji.tech. All rights reserved.
 */
public final class StarterKit {

  private static int sLoadingTriggerThreshold = 1;
  private static int sItemsPerPage = 20;
  private static int sItemsFirstPage = 1;

  public static int getLoadingTriggerThreshold() {
    return sLoadingTriggerThreshold;
  }

  public static int getItemsPerPage() {
    return sItemsPerPage;
  }

  public static int getItemsFirstPage() {
    return sItemsFirstPage;
  }
}
