package com.smartydroid.android.starter.kit.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.smartydroid.android.starter.kit.R;
import com.smartydroid.android.starter.kit.model.ErrorModel;
import com.smartydroid.android.starter.kit.network.callback.GenericCallback;
import java.net.UnknownHostException;
import support.ui.content.ContentPresenter;
import support.ui.content.EmptyView;
import support.ui.content.ErrorView;
import support.ui.content.ReflectionContentPresenterFactory;

/**
 * Created by YuGang Yang on February 07, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public abstract class CallbackFragment<E> extends StarterFragment
    implements GenericCallback<E>,
    EmptyView.OnEmptyViewClickListener,
    ErrorView.OnErrorViewClickListener {

  ReflectionContentPresenterFactory factory =
      ReflectionContentPresenterFactory.fromViewClass(getClass());
  ContentPresenter contentPresenter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    contentPresenter = factory.createContentPresenter();
    contentPresenter.setOnEmptyViewClickListener(this);
    contentPresenter.setOnErrorViewClickListener(this);
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
    buildError(errorModel);
  }

  @Override public void errorUnauthorized(ErrorModel errorModel) {
    buildError(errorModel);
  }

  @Override public void errorForbidden(ErrorModel errorModel) {
    buildError(errorModel);
  }

  @Override public void eNetUnReach(Throwable t, ErrorModel errorModel) {
    buildError(errorModel);
  }

  @Override public void errorSocketTimeout(Throwable t, ErrorModel errorModel) {
    buildError(errorModel);
  }

  @Override public void errorUnknownHost(UnknownHostException e, ErrorModel errorModel) {
    buildError(errorModel);
  }

  @Override public void error(ErrorModel errorModel) {
    buildError(errorModel);
  }

  protected void buildEmpty(ErrorModel errorModel) {
    if (contentPresenter != null) {
      contentPresenter.buildEmptyImageView(R.drawable.support_ui_empty)
          .buildEmptyTitle(errorModel.mTitle)
          .buildEmptySubtitle(errorModel.getMessage());
    }
  }

  protected void buildError(ErrorModel errorModel) {
    if (contentPresenter != null) {
      contentPresenter.buildErrorImageView(R.drawable.support_ui_error_network)
          .buildErrorTitle(errorModel.mTitle)
          .buildErrorSubtitle(errorModel.getMessage())
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
