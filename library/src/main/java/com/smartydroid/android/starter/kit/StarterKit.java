package com.smartydroid.android.starter.kit;

/**
 * Created by YuGang Yang on February 13, 2016.
 * Copyright 20015-2016 qiji.tech. All rights reserved.
 */
public final class StarterKit {

  private static int sLoadingTriggerThreshold = 1;
  private static int sItemsPerPage = 20;
  private static int sItemsFirstPage = 1;

  private static boolean sDebug;

  public static boolean isDebug() {
    return sDebug;
  }

  public static int getLoadingTriggerThreshold() {
    return sLoadingTriggerThreshold;
  }

  public static int getItemsPerPage() {
    return sItemsPerPage;
  }

  public static int getItemsFirstPage() {
    return sItemsFirstPage;
  }

  public static class Builder {
    private int loadingTriggerThreshold = 1;
    private int itemsPerPage = 20;
    private int itemsFirstPage = 1;
    private boolean debug;

    public void build() {
      StarterKit.sLoadingTriggerThreshold = loadingTriggerThreshold;
      StarterKit.sItemsPerPage = itemsPerPage;
      StarterKit.sItemsFirstPage = itemsFirstPage;
      StarterKit.sDebug = debug;
    }

    public Builder setLoadingTriggerThreshold(int loadingTriggerThreshold) {
      this.loadingTriggerThreshold = loadingTriggerThreshold;
      return this;
    }

    public Builder setItemsFirstPage(int itemsFirstPage) {
      this.itemsFirstPage = itemsFirstPage;
      return this;
    }

    public Builder setItemsPerPage(int itemsPerPage) {
      this.itemsPerPage = itemsPerPage;
      return this;
    }

    public Builder setDebug(boolean debug) {
      this.debug = debug;
      return this;
    }
  }
}
