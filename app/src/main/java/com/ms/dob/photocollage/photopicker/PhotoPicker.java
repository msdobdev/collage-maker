package com.ms.dob.photocollage.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;


import com.ms.dob.photocollage.photopicker.datatype.Photo;
import com.ms.dob.photocollage.photopicker.ui.PickActivity;
import com.ms.dob.photocollage.photopicker.ui.adapter.PhotoAdapter;

import java.util.List;


public class PhotoPicker {
  private Configure mConfigure;

  private PhotoPicker() {
    mConfigure = new Configure();
  }

  private PhotoPicker(Configure configure) {
    this.mConfigure = configure;
  }

  public static PhotoPicker newInstance() {
    return new PhotoPicker();
  }

  public static PhotoPicker newInstance(Configure configure) {
    return new PhotoPicker(configure);
  }


  public PhotoPicker setToolbarColor(@ColorInt int color) {
    this.mConfigure.setToolbarColor(color);
    return this;
  }

  public PhotoPicker setToolbarTitleColor(@ColorInt int color) {
    this.mConfigure.setToolbarTitleColor(color);
    return this;
  }

  public PhotoPicker setAlbumTitle(String title) {
    this.mConfigure.setAlbumTitle(title);
    return this;
  }

  public PhotoPicker setPhotoTitle(String title) {
    this.mConfigure.setPhotoTitle(title);
    return this;
  }

  public PhotoPicker setNavIcon(@DrawableRes int resId) {
    this.mConfigure.setNavIcon(resId);
    return this;
  }

  public PhotoPicker setStatusBarColor(@ColorInt int color) {
    this.mConfigure.setStatusBarColor(color);
    return this;
  }

  public PhotoPicker setMaxNotice(String message) {
    this.mConfigure.setMaxNotice(message);
    return this;
  }

  public PhotoPicker setMaxCount(int count) {
    this.mConfigure.setMaxCount(count);
    return this;
  }

  public void pick(Context context) {
    Intent intent = new Intent(context, PickActivity.class);
    intent.putExtra(Define.CONFIGURE, mConfigure);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, Define.DEFAULT_REQUEST_CODE);
    } else {
      throw new IllegalStateException("the context need to use activity");
    }
  }

  public void inflate(final RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
    final PhotoAdapter photoAdapter = new PhotoAdapter();
    recyclerView.setAdapter(photoAdapter);
    recyclerView.setLayoutManager(layoutManager);

    PhotoManager photoManager = new PhotoManager(recyclerView.getContext());

    new GetAllPhotoTask() {
      @Override
      protected void onPostExecute(List<Photo> photos) {
        super.onPostExecute(photos);
        photoAdapter.refreshData(photos);
      }
    }.execute(photoManager);
  }
}
