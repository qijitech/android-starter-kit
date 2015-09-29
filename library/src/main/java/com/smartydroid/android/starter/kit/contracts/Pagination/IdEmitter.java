/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.contracts.Pagination;

import com.smartydroid.android.starter.kit.network.Result;
import java.util.List;
import retrofit.Call;

public interface IdEmitter<T> extends Emitter<T> {

  /**
   * maxItem 在 loadMore 的时候需要
   * sinceItem 在获取最新数据需要
   * @param sinceItem
   * @param maxItem
   * @param perPage
   * @return
   */
  Call<Result<List<T>>> paginate(T sinceItem, T maxItem, int perPage);

}
