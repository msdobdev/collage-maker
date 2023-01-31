package com.ms.dob.photocollage.layout.custom;

import android.util.Log;
import com.ms.dob.collage.straight.StraightCollageLayout;


public abstract class CollageNumberLayout extends StraightCollageLayout {
  static final String TAG = "NumberStraightLayout";
  protected int theme;

  public CollageNumberLayout(int theme) {
    if (theme >= getThemeCount()) {
      Log.e(TAG, "NumberStraightLayout: the most theme count is "
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
