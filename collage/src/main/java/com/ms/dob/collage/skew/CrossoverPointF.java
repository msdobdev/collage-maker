package com.ms.dob.collage.skew;

import android.graphics.PointF;

/**
 * intersection of two lines
 *
 */
class CrossoverPointF extends PointF {
  SkewLine horizontal;
  SkewLine vertical;

  CrossoverPointF() {

  }

  CrossoverPointF(float x, float y) {
    this.x = x;
    this.y = y;
  }

  CrossoverPointF(SkewLine horizontal, SkewLine vertical) {
    this.horizontal = horizontal;
    this.vertical = vertical;
  }

  void update() {
    if (horizontal == null || vertical == null){
      return;
    }
    SkewUtils.intersectionOfLines(this, horizontal, vertical);
  }
}
