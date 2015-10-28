/**
 * Created by YuGang Yang on October 28, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorModel {
  @JsonProperty("status_code") public int mStatusCode;
  @JsonProperty("message") public String mMessage;
}
