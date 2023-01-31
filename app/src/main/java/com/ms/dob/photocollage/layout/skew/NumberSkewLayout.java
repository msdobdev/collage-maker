package com.ms.dob.photocollage.layout.skew;

import android.util.Log;

import com.ms.dob.collage.skew.SkewCollageLayout;


public abstract class NumberSkewLayout extends SkewCollageLayout {

  static final String TAG = "NumberSlantLayout";
  protected int theme;

  public NumberSkewLayout(int theme) {
    if (theme >= getThemeCount()) {
      Log.e(TAG, "NumberSlantLayout: the most theme count is "
          + getThemeCount()
          + " ,you should let theme from 0 to "
          + (getThemeCount() - 1)
          + " .");
    }
    this.theme = theme;
  }

  public abstract int getThemeCount();

  public int getTheme() {
    return theme;
  }
}
