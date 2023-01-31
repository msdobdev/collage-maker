package com.ms.dob.collage;

import android.content.Context;
import android.util.AttributeSet;


public class SquareCollageView extends CollageView {
  public SquareCollageView(Context context) {
    super(context);
  }

  public SquareCollageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SquareCollageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int width = getMeasuredWidth();
    int height = getMeasuredHeight();
    int length = width > height ? height : width;

    setMeasuredDimension(length, length);
  }
}
