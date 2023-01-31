package com.ms.dob.photocollage.photopicker.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ms.dob.photocollage.photopicker.Configure;
import com.ms.dob.photocollage.photopicker.Define;
import com.ms.dob.photocollage.R;


public class PickActivity extends AppCompatActivity {

  private Configure mConfigure;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.poiphoto_activity_pick);

    Intent intent = getIntent();
    mConfigure = intent.getParcelableExtra(Define.CONFIGURE);

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
      changeStatusBar(mConfigure.getStatusBarColor());
    }

    getSupportFragmentManager().beginTransaction().replace(R.id.container, new AlbumFragment()).commit();

  }

  @TargetApi(21)
  private void changeStatusBar(int statusBarColor) {
    getWindow().setStatusBarColor(statusBarColor);
  }


  public void setConfigure(Configure configure) {
    mConfigure = configure;
  }

  public Configure getConfigure() {
    return mConfigure;
  }


}
