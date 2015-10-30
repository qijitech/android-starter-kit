/**
 * Created by YuGang Yang on October 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network.callback;

import com.smartydroid.android.starter.kit.model.ErrorModel;

public interface ErrorCallback {

  /**
   * 没有数据或者请求的路径找不到
   *
   * @param errorModel ErrorModel
   */
  void errorNotFound(ErrorModel errorModel);

  /**
   * 因为参数不对，导致无法继续处理后面逻辑
   *
   * @param errorModel ErrorModel
   */
  void errorUnprocessable(ErrorModel errorModel);

  /**
   * 当前请求需要用户验证
   *
   * @param errorModel ErrorModel
   */
  void errorUnauthorized(ErrorModel errorModel);

  /**
   * 权限校验不通过
   *
   * @param errorModel ErrorModel
   */
  void errorForbidden(ErrorModel errorModel);

  /**
   * 无网络错误
   *
   * @param t Throwable
   */
  void eNetUnreach(Throwable t);

  /**
   * 链接超时错误
   *
   * @param t Throwable
   */
  void errorSocketTimeout(Throwable t);

  /**
   * 此方法不能同时和其他 errorXXX() 方法使用
   *
   * @param errorModel ErrorModel
   */
  void error(ErrorModel errorModel);
}
