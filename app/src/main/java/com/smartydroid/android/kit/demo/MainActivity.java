package com.smartydroid.android.kit.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.smartydroid.android.starter.kit.LoadingLayout;

public class MainActivity extends AppCompatActivity implements LoadingLayout.OnButtonClickListener {

  private LoadingLayout mLoadingLayout;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mLoadingLayout = (LoadingLayout) findViewById(R.id.container_loading_layout);
    mLoadingLayout.setOnButtonClickListener(this);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_show_content) {
      mLoadingLayout.showContentView();
      return true;
    }

    if (id == R.id.action_show_empty) {
      mLoadingLayout.showEmptyView();
      return true;
    }

    if (id == R.id.action_show_loading) {
      mLoadingLayout.showLoadingView();
      return true;
    }

    if (id == R.id.action_show_error) {
      mLoadingLayout.showErrorView(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mLoadingLayout.showLoadingView();
        }
      });
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onEmptyButtonClick(View view) {

  }

  @Override public void onErrorButtonClick(View view) {

  }
}

