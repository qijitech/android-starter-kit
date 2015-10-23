/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.contracts.Pagination;

import com.smartydroid.android.starter.kit.model.dto.DataArray;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import retrofit.Call;

public interface PagesEmitter<T extends Entitiy> extends Emitter<T> {

  /**
   * @param page 当前页面
   * @param perPage 每页显示多少
   * @return Call
   */
  Call<? extends DataArray<T>> paginate(int page, int perPage);
}
