package com.ms.dob.photocollage.photopicker;

import android.os.AsyncTask;


import com.ms.dob.photocollage.photopicker.datatype.Photo;

import java.util.List;


public class GetAllPhotoTask extends AsyncTask<PhotoManager, Integer, List<Photo>> {
  private static final String TAG = "GetAllPhotoTask";

  @Override
  protected List<Photo> doInBackground(PhotoManager... params) {
    return params[0].getAllPhoto();
  }
}
