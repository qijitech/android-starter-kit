package com.smartydroid.android.starter.kit.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.smartydroid.android.starter.kit.R;
import com.smartydroid.android.starter.kit.model.ErrorModel;
import com.smartydroid.android.starter.kit.network.callback.GenericCallback;
import java.net.UnknownHostException;
import support.ui.content.ContentPresenter;
import support.ui.content.EmptyView;
import support.ui.content.ReflectionContentPresenterFactory;

/**
 * Created by YuGang Yang on February 07, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public abstract class CallbackFragment<E> extends StarterFragment
    implements GenericCallback<E>, EmptyView.OnEmptyClickListener {

  ReflectionContentPresenterFactory factory =
      ReflectionContentPresenterFactory.fromViewClass(getClass());
  ContentPresenter contentPresenter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    contentPresenter = factory.createContentPresenter();
    contentPresenter.setOnEmptyClickListener(this);
    contentPresenter.onCreate(getContext());
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    contentPresenter.onDestroyView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    contentPresenter.onDestroy();
  }

  public ContentPresenter getContentPresenter() {
    return contentPresenter;
  }

  @Override public void errorNotFound(ErrorModel errorModel) {
    buildEmpty(errorModel);
  }

  @Override public void errorUnProcessable(ErrorModel errorModel) {
    buildEmpty(errorModel);
  }

  @Override public void errorUnauthorized(ErrorModel errorModel) {
    buildErrorEmpty(errorModel);
  }

  @Override public void errorForbidden(ErrorModel errorModel) {
    buildErrorEmpty(errorModel);
  }

  @Override public void eNetUnReach(Throwable t, ErrorModel errorModel) {
    buildErrorEmpty(errorModel);
  }

  @Override public void errorSocketTimeout(Throwable t, ErrorModel errorModel) {
    buildErrorEmpty(errorModel);
  }

  @Override public void errorUnknownHost(UnknownHostException e, ErrorModel errorModel) {
    buildErrorEmpty(errorModel);
  }

  @Override public void error(ErrorModel errorModel) {
    buildErrorEmpty(errorModel);
  }

  private void buildEmpty(ErrorModel errorModel) {
    if (contentPresenter != null) {
      contentPresenter.buildImageView(R.drawable.support_ui_empty)
          .buildEmptyTitle(errorModel.getMessage())
          .shouldDisplayEmptySubtitle(false);
    }
  }

  private void buildErrorEmpty(ErrorModel errorModel) {
    if (contentPresenter != null) {
      contentPresenter.buildImageView(R.drawable.support_ui_empty_network_error)
          .buildEmptyTitle(errorModel.getMessage())
          .shouldDisplayEmptySubtitle(false);
    }
  }

  ////////////////////////////////////
  ////////////////////////////////////
  ////////////NetworkCallback/////////
  ////////////////////////////////////
  ////////////////////////////////////
  @Override public void startRequest() {
  }

  @Override public void respondSuccess(E data) {
  }

  @Override public void respondWithError(Throwable t) {
  }

  @Override public void endRequest() {
  }
}
