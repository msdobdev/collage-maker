package com.ms.dob.collage.skew;

import android.graphics.PointF;
import com.ms.dob.collage.Line;

import static com.ms.dob.collage.skew.SkewUtils.intersectionOfLines;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Divided into two slashes, horizontal lines and vertical lines
 * Horizontal slash --> start is the point on the left, end is the point on the right
 * Vertical slash --> start is the point above, end is the point below
 *
 */
class SkewLine implements Line {
  CrossoverPointF start;
  CrossoverPointF end;

  // point before moving
  private PointF previousStart = new PointF();
  private PointF previousEnd = new PointF();

  public final Line.Direction direction;

  SkewLine attachLineStart;
  SkewLine attachLineEnd;

  Line upperLine;
  Line lowerLine;

  SkewLine(Line.Direction direction) {
    this.direction = direction;
  }

  SkewLine(CrossoverPointF start, CrossoverPointF end, Line.Direction direction) {
    this.start = start;
    this.end = end;
    this.direction = direction;
  }

  public float length() {
    return (float) sqrt(pow(end.x - start.x, 2) + pow(end.y - start.y, 2));
  }

  @Override public PointF startPoint() {
    return start;
  }

  @Override public PointF endPoint() {
    return end;
  }

  @Override public Line lowerLine() {
    return lowerLine;
  }

  @Override public Line upperLine() {
    return upperLine;
  }

  @Override public Line attachStartLine() {
    return attachLineStart;
  }

  @Override public Line attachEndLine() {
    return attachLineEnd;
  }

  @Override public void setLowerLine(Line lowerLine) {
    this.lowerLine = lowerLine;
  }

  @Override public void setUpperLine(Line upperLine) {
    this.upperLine = upperLine;
  }

  @Override public Direction direction() {
    return direction;
  }

  @Override public float slope() {
    return SkewUtils.calculateSlope(this);
  }

  public boolean contains(float x, float y, float extra) {
    return SkewUtils.contains(this, x, y, extra);
  }

  @Override public boolean move(float offset, float extra) {
    if (direction == Line.Direction.HORIZONTAL) {
      if (previousStart.y + offset < lowerLine.maxY() + extra
          || previousStart.y + offset > upperLine.minY() - extra
          || previousEnd.y + offset < lowerLine.maxY() + extra
          || previousEnd.y + offset > upperLine.minY() - extra) {
        return false;
      }

      start.y = previousStart.y + offset;
      end.y = previousEnd.y + offset;
    } else {
      if (previousStart.x + offset < lowerLine.maxX() + extra
          || previousStart.x + offset > upperLine.minX() - extra
          || previousEnd.x + offset < lowerLine.maxX() + extra
          || previousEnd.x + offset > upperLine.minX() - extra) {
        return false;
      }

      start.x = previousStart.x + offset;
      end.x = previousEnd.x + offset;
    }

    return true;
  }

  @Override public void prepareMove() {
    previousStart.set(start);
    previousEnd.set(end);
  }

  @Override public void update(float layoutWidth, float layoutHeight) {
    intersectionOfLines(start, this, attachLineStart);
    intersectionOfLines(end, this, attachLineEnd);
  }

  @Override public float minX() {
    return min(start.x, end.x);
  }

  @Override public float maxX() {
    return max(start.x, end.x);
  }

  @Override public float minY() {
    return min(start.y, end.y);
  }

  @Override public float maxY() {
    return max(start.y, end.y);
  }

  @Override public void offset(float x, float y) {
    start.offset(x, y);
    end.offset(x, y);
  }

  @Override public String toString() {
    return "start --> " + start.toString() + ",end --> " + end.toString();
  }
}
