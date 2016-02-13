/**
 * Created by YuGang Yang on October 28, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.contracts.Pagination;

import com.smartydroid.android.starter.kit.model.entity.Entity;
import java.util.ArrayList;
import retrofit2.Call;

public interface PageEmitter<T extends Entity> extends Emitter<T> {

  /**
   * @param page 当前页面
   * @param perPage 每页显示多少
   * @return Call
   */
  Call<ArrayList<T>> paginate(int page, int perPage);
}