/**
 * Created by YuGang Yang on September 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.model.dto;

import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import java.util.List;

public interface DataArray<E extends Entitiy> extends Data<List<E>> {

  /**
   * 当前页面
   */
  int currentPage();

  /**
   * 每页显示条目
   */
  int perPage();

  /**
   * 总数据条数
   */
  long total();

  int size();
}
