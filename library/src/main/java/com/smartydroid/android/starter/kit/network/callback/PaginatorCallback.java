/**
 * Created by YuGang Yang on September 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network.callback;

import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import java.util.ArrayList;

public interface PaginatorCallback<T extends Entitiy> extends NetworkCallback<ArrayList<T>> {
}
