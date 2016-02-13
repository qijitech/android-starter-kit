/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.contracts.Pagination;

import com.smartydroid.android.starter.kit.model.entity.Entity;
import java.util.ArrayList;
import retrofit2.Call;

public interface KeyEmitter<T extends Entity> extends Emitter<T> {

  /**
   * maxItem 在 loadMore 的时候需要
   * sinceItem 在获取最新数据需要
   *
   * @param sinceItem 第一条数据
   * @param maxItem 最后一条数据
   * @param perPage 每页获取多少条数据
   * @return Call
   */
  Call<ArrayList<T>> paginate(T sinceItem, T maxItem, int perPage);
}
