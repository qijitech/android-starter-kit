/**
 * Created by YuGang Yang on October 28, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) public class ErrorModel {
  @JsonProperty("status_code") public int mStatusCode;
  @JsonProperty("message") public String mMessage;

  public ErrorModel() {

  }

  public ErrorModel(int statusCode, String msg) {
    this.mStatusCode = statusCode;
    this.mMessage = msg;
  }

  public String getMessage() {
    return mMessage == null ? "" : mMessage;
  }

  public int getStatusCode() {
    return mStatusCode;
  }
}
