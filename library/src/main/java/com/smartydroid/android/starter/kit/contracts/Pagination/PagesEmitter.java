/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.contracts.Pagination;

import com.smartydroid.android.starter.kit.network.Result;
import java.util.List;
import retrofit.Call;

public interface PagesEmitter<T> extends Emitter<T> {

  Call<Result<List<T>>> paginate(int page, int perPage);
}
