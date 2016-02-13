package com.smartydroid.android.starter.kit.app;

import android.graphics.drawable.Drawable;
import com.smartydroid.android.starter.kit.model.ErrorModel;

/**
 * Created by YuGang Yang on February 07, 2016.
 * Copyright 20015-2016 qiji.tech. All rights reserved.
 */
public interface EmptyAndErrorCallback {

  void setupErrorModel(ErrorModel errorModel);

  void setupError(String title, String subtitle);

  void setupError(Drawable drawable, String title, String subtitle);

  void setupEmpty(String title, String subtitle);

  void setupEmpty(Drawable drawable, String title, String subtitle);
}
