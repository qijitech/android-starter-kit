/**
 * Created by YuGang Yang on September 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network.callback;

import com.smartydroid.android.starter.kit.model.dto.DataArray;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;

public interface PaginationCallback<T extends Entitiy> extends NetworkCallback<DataArray<T>> {
}
