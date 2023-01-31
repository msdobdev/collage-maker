package com.ms.dob.collage.skew;

import android.graphics.PointF;
import android.util.Pair;
import com.ms.dob.collage.Line;
import java.util.ArrayList;
import java.util.List;


class SkewUtils {
  private static final PointF A = new PointF();
  private static final PointF B = new PointF();
  private static final PointF C = new PointF();
  private static final PointF D = new PointF();
  private static final PointF AB = new PointF();
  private static final PointF AM = new PointF();
  private static final PointF BC = new PointF();
  private static final PointF BM = new PointF();
  private static final PointF CD = new PointF();
  private static final PointF CM = new PointF();
  private static final PointF DA = new PointF();
  private static final PointF DM = new PointF();

  private SkewUtils() {
    //no instance
  }

  static float distance(PointF one, PointF two) {
    return (float) Math.sqrt(Math.pow(two.x - one.x, 2) + Math.pow(two.y - one.y, 2));
  }

  static List<SkewArea> cutAreaWith(SkewArea area, SkewLine line) {
    List<SkewArea> areas = new ArrayList<>();
    SkewArea area1 = new SkewArea(area);
    SkewArea area2 = new SkewArea(area);

    if (line.direction == Line.Direction.HORIZONTAL) {
      area1.lineBottom = line;
      area1.leftBottom = line.start;
      area1.rightBottom = line.end;

      area2.lineTop = line;
      area2.leftTop = line.start;
      area2.rightTop = line.end;
    } else {
      area1.lineRight = line;
      area1.rightTop = line.start;
      area1.rightBottom = line.end;

      area2.lineLeft = line;
      area2.leftTop = line.start;
      area2.leftBottom = line.end;
    }

    areas.add(area1);
    areas.add(area2);

    return areas;
  }

  static SkewLine createLine(SkewArea area, Line.Direction direction, float startratio,
                             float endratio) {
    SkewLine line = new SkewLine(direction);

    if (direction == Line.Direction.HORIZONTAL) {
      line.start = getPoint(area.leftTop, area.leftBottom, Line.Direction.VERTICAL, startratio);
      line.end = getPoint(area.rightTop, area.rightBottom, Line.Direction.VERTICAL, endratio);

      line.attachLineStart = area.lineLeft;
      line.attachLineEnd = area.lineRight;

      line.upperLine = area.lineBottom;
      line.lowerLine = area.lineTop;
    } else {
      line.start = getPoint(area.leftTop, area.rightTop, Line.Direction.HORIZONTAL, startratio);
      line.end = getPoint(area.leftBottom, area.rightBottom, Line.Direction.HORIZONTAL, endratio);

      line.attachLineStart = area.lineTop;
      line.attachLineEnd = area.lineBottom;

      line.upperLine = area.lineRight;
      line.lowerLine = area.lineLeft;
    }

    return line;
  }

  static Pair<List<SkewLine>, List<SkewArea>> cutAreaWith(final SkewArea area,
                                                          final int horizontalSize, final int verticalSize) {
    List<SkewArea> areaList = new ArrayList<>();
    List<SkewLine> horizontalLines = new ArrayList<>(horizontalSize);

    SkewArea restArea = new SkewArea(area);
    for (int i = horizontalSize + 1; i > 1; i--) {
      SkewLine horizontalLine =
          createLine(restArea, Line.Direction.HORIZONTAL, (float) (i - 1) / i - 0.025f, (float) (i - 1) / i + 0.025f);
      horizontalLines.add(horizontalLine);
      restArea.lineBottom = horizontalLine;
      restArea.leftBottom = horizontalLine.start;
      restArea.rightBottom = horizontalLine.end;
    }
    List<SkewLine> verticalLines = new ArrayList<>();

    restArea = new SkewArea(area);
    for (int i = verticalSize + 1; i > 1; i--) {
      SkewLine verticalLine =
          createLine(restArea, Line.Direction.VERTICAL, (float) (i - 1) / i + 0.025f, (float) (i - 1) / i - 0.025f);
      verticalLines.add(verticalLine);
      SkewArea spiltArea = new SkewArea(restArea);
      spiltArea.lineLeft = verticalLine;
      spiltArea.leftTop = verticalLine.start;
      spiltArea.leftBottom = verticalLine.end;

      for (int j = 0; j <= horizontalLines.size(); j++) {
        SkewArea blockArea = new SkewArea(spiltArea);
        if (j == 0) {
          blockArea.lineTop = horizontalLines.get(j);
        } else if (j == horizontalLines.size()) {
          blockArea.lineBottom = horizontalLines.get(j - 1);

          CrossoverPointF leftBottom =
              new CrossoverPointF(blockArea.lineBottom, blockArea.lineLeft);
          intersectionOfLines(leftBottom, blockArea.lineBottom, blockArea.lineLeft);
          CrossoverPointF rightBottom =
              new CrossoverPointF(blockArea.lineBottom, blockArea.lineRight);
          intersectionOfLines(rightBottom, blockArea.lineBottom, blockArea.lineRight);
          blockArea.leftBottom = leftBottom;
          blockArea.rightBottom = rightBottom;
        } else {
          blockArea.lineTop = horizontalLines.get(j);
          blockArea.lineBottom = horizontalLines.get(j - 1);
        }
        CrossoverPointF leftTop = new CrossoverPointF(blockArea.lineTop, blockArea.lineLeft);
        intersectionOfLines(leftTop, blockArea.lineTop, blockArea.lineLeft);
        CrossoverPointF rightTop = new CrossoverPointF(blockArea.lineTop, blockArea.lineRight);
        intersectionOfLines(rightTop, blockArea.lineTop, blockArea.lineRight);
        blockArea.leftTop = leftTop;
        blockArea.rightTop = rightTop;
        areaList.add(blockArea);
      }
      restArea.lineRight = verticalLine;
      restArea.rightTop = verticalLine.start;
      restArea.rightBottom = verticalLine.end;
    }

    for (int j = 0; j <= horizontalLines.size(); j++) {
      SkewArea blockArea = new SkewArea(restArea);
      if (j == 0) {
        blockArea.lineTop = horizontalLines.get(j);
      } else if (j == horizontalLines.size()) {
        blockArea.lineBottom = horizontalLines.get(j - 1);
        CrossoverPointF leftBottom = new CrossoverPointF(blockArea.lineBottom, blockArea.lineLeft);
        intersectionOfLines(leftBottom, blockArea.lineBottom, blockArea.lineLeft);
        CrossoverPointF rightBottom =
            new CrossoverPointF(blockArea.lineBottom, blockArea.lineRight);
        intersectionOfLines(rightBottom, blockArea.lineBottom, blockArea.lineRight);
        blockArea.leftBottom = leftBottom;
        blockArea.rightBottom = rightBottom;
      } else {
        blockArea.lineTop = horizontalLines.get(j);
        blockArea.lineBottom = horizontalLines.get(j - 1);
      }
      CrossoverPointF leftTop = new CrossoverPointF(blockArea.lineTop, blockArea.lineLeft);
      intersectionOfLines(leftTop, blockArea.lineTop, blockArea.lineLeft);
      CrossoverPointF rightTop = new CrossoverPointF(blockArea.lineTop, blockArea.lineRight);
      intersectionOfLines(rightTop, blockArea.lineTop, blockArea.lineRight);
      blockArea.leftTop = leftTop;
      blockArea.rightTop = rightTop;
      areaList.add(blockArea);
    }

    List<SkewLine> lines = new ArrayList<>();
    lines.addAll(horizontalLines);
    lines.addAll(verticalLines);
    return new Pair<>(lines, areaList);
  }

  static List<SkewArea> cutAreaCross(final SkewArea area, final SkewLine horizontal,
                                     final SkewLine vertical) {
    List<SkewArea> list = new ArrayList<>();

    CrossoverPointF crossoverPoint = new CrossoverPointF(horizontal, vertical);
    intersectionOfLines(crossoverPoint, horizontal, vertical);

    SkewArea one = new SkewArea(area);
    one.lineBottom = horizontal;
    one.lineRight = vertical;

    one.rightTop = vertical.start;
    one.rightBottom = crossoverPoint;
    one.leftBottom = horizontal.start;
    list.add(one);

    SkewArea two = new SkewArea(area);
    two.lineBottom = horizontal;
    two.lineLeft = vertical;

    two.leftTop = vertical.start;
    two.rightBottom = horizontal.end;
    two.leftBottom = crossoverPoint;
    list.add(two);

    SkewArea three = new SkewArea(area);
    three.lineTop = horizontal;
    three.lineRight = vertical;

    three.leftTop = horizontal.start;
    three.rightTop = crossoverPoint;
    three.rightBottom = vertical.end;
    list.add(three);

    SkewArea four = new SkewArea(area);
    four.lineTop = horizontal;
    four.lineLeft = vertical;

    four.leftTop = crossoverPoint;
    four.rightTop = horizontal.end;
    four.leftBottom = vertical.end;
    list.add(four);

    return list;
  }

  private static CrossoverPointF getPoint(final PointF start, final PointF end,
      final Line.Direction direction, float ratio) {
    CrossoverPointF point = new CrossoverPointF();
    getPoint(point, start, end, direction, ratio);
    return point;
  }

  static void getPoint(final PointF dst, final PointF start, final PointF end,
      final Line.Direction direction, float ratio) {
    float deltaY = Math.abs(start.y - end.y);
    float deltaX = Math.abs(start.x - end.x);
    float maxY = Math.max(start.y, end.y);
    float minY = Math.min(start.y, end.y);
    float maxX = Math.max(start.x, end.x);
    float minX = Math.min(start.x, end.x);
    if (direction == Line.Direction.HORIZONTAL) {
      dst.x = minX + deltaX * ratio;
      if (start.y < end.y) {
        dst.y = minY + ratio * deltaY;
      } else {
        dst.y = maxY - ratio * deltaY;
      }
    } else {
      dst.y = minY + deltaY * ratio;
      if (start.x < end.x) {
        dst.x = minX + ratio * deltaX;
      } else {
        dst.x = maxX - ratio * deltaX;
      }
    }
  }

  // 叉乘
  private static float crossProduct(final PointF a, final PointF b) {
    return a.x * b.y - b.x * a.y;
  }

  /**
   * Determine whether a slashed area contains (x,y) points
   *
   * @param area slash area
   * @param x x
   * @param y y
   * whether @return contains
   */
  static boolean contains(SkewArea area, float x, float y) {
    AB.x = area.rightTop.x - area.leftTop.x;
    AB.y = area.rightTop.y - area.leftTop.y;

    AM.x = x - area.leftTop.x;
    AM.y = y - area.leftTop.y;

    BC.x = area.rightBottom.x - area.rightTop.x;
    BC.y = area.rightBottom.y - area.rightTop.y;

    BM.x = x - area.rightTop.x;
    BM.y = y - area.rightTop.y;

    CD.x = area.leftBottom.x - area.rightBottom.x;
    CD.y = area.leftBottom.y - area.rightBottom.y;

    CM.x = x - area.rightBottom.x;
    CM.y = y - area.rightBottom.y;

    DA.x = area.leftTop.x - area.leftBottom.x;
    DA.y = area.leftTop.y - area.leftBottom.y;

    DM.x = x - area.leftBottom.x;
    DM.y = y - area.leftBottom.y;

    return crossProduct(AB, AM) > 0
        && crossProduct(BC, BM) > 0
        && crossProduct(CD, CM) > 0
        && crossProduct(DA, DM) > 0;
  }

  static boolean contains(SkewLine line, float x, float y, float extra) {
    PointF start = line.start;
    PointF end = line.end;
    if (line.direction == Line.Direction.VERTICAL) {
      A.x = start.x - extra;
      A.y = start.y;
      B.x = start.x + extra;
      B.y = start.y;
      C.x = end.x + extra;
      C.y = end.y;
      D.x = end.x - extra;
      D.y = end.y;
    } else {
      A.x = start.x;
      A.y = start.y - extra;
      B.x = end.x;
      B.y = end.y - extra;
      C.x = end.x;
      C.y = end.y + extra;
      D.x = start.x;
      D.y = start.y + extra;
    }

    AB.x = B.x - A.x;
    AB.y = B.y - A.y;

    AM.x = x - A.x;
    AM.y = y - A.y;

    BC.x = C.x - B.x;
    BC.y = C.y - B.y;

    BM.x = x - B.x;
    BM.y = y - B.y;

    CD.x = D.x - C.x;
    CD.y = D.y - C.y;

    CM.x = x - C.x;
    CM.y = y - C.y;

    DA.x = A.x - D.x;
    DA.y = A.y - D.y;

    DM.x = x - D.x;
    DM.y = y - D.y;

    return crossProduct(AB, AM) > 0
        && crossProduct(BC, BM) > 0
        && crossProduct(CD, CM) > 0
        && crossProduct(DA, DM) > 0;
  }

  /**
   * Calculate the intersection of two lines
   *
   * @param dst calculated intersection
   * @param lineOne line one
   * @param lineTwo line two
   */
  static void intersectionOfLines(final CrossoverPointF dst, final SkewLine lineOne,
      final SkewLine lineTwo) {
    dst.horizontal = lineOne;
    dst.vertical = lineTwo;
    if (isParallel(lineOne, lineTwo)) {
      dst.set(0, 0);
      return;
    }

    if (isHorizontalLine(lineOne) && isVerticalLine(lineTwo)) {
      dst.set(lineTwo.start.x, lineOne.start.y);
      return;
    }

    if (isVerticalLine(lineOne) && isHorizontalLine(lineTwo)) {
      dst.set(lineOne.start.x, lineTwo.start.y);
      return;
    }

    if (isHorizontalLine(lineOne) && !isVerticalLine(lineTwo)) {
      float k = calculateSlope(lineTwo);
      float b = calculateVerticalIntercept(lineTwo);

      dst.y = lineOne.start.y;
      dst.x = (dst.y - b) / k;
      return;
    }

    if (isVerticalLine(lineOne) && !isHorizontalLine(lineTwo)) {
      float k = calculateSlope(lineTwo);
      float b = calculateVerticalIntercept(lineTwo);

      dst.x = lineOne.start.x;
      dst.y = k * dst.x + b;
      return;
    }

    if (isHorizontalLine(lineTwo) && !isVerticalLine(lineOne)) {
      float k = calculateSlope(lineOne);
      float b = calculateVerticalIntercept(lineOne);

      dst.y = lineTwo.start.y;
      dst.x = (dst.y - b) / k;
      return;
    }

    if (isVerticalLine(lineTwo) && !isHorizontalLine(lineOne)) {
      float k = calculateSlope(lineOne);
      float b = calculateVerticalIntercept(lineOne);

      dst.x = lineTwo.start.x;
      dst.y = k * dst.x + b;
      return;
    }

    final float k1 = calculateSlope(lineOne);
    final float b1 = calculateVerticalIntercept(lineOne);

    final float k2 = calculateSlope(lineTwo);
    final float b2 = calculateVerticalIntercept(lineTwo);

    dst.x = (b2 - b1) / (k1 - k2);
    dst.y = dst.x * k1 + b1;
  }

  private static boolean isHorizontalLine(SkewLine line) {
    return line.start.y == line.end.y;
  }

  private static boolean isVerticalLine(SkewLine line) {
    return line.start.x == line.end.x;
  }

  /**
   * Determine if two lines are parallel
   *
   * @param lineOne first line
   * @param lineTwo second line
   * @return is parallel
   */
  private static boolean isParallel(final SkewLine lineOne, final SkewLine lineTwo) {
    return calculateSlope(lineOne) == calculateSlope(lineTwo);
  }

  /**
   * Calculate the slope of the line
   *
   * @param line line
   * @return the slope of the line
   */
  static float calculateSlope(final SkewLine line) {
    if (isHorizontalLine(line)) {
      return 0f;
    } else if (isVerticalLine(line)) {
      return Float.POSITIVE_INFINITY;
    } else {
      return (line.start.y - line.end.y) / (line.start.x - line.end.x);
    }
  }

  /**
   * Calculate the vertical intercept
   *
   * @param line line
   * @return vertical intercept
   */
  private static float calculateVerticalIntercept(final SkewLine line) {
    if (isHorizontalLine(line)) {
      return line.start.y;
    } else if (isVerticalLine(line)) {
      return Float.POSITIVE_INFINITY;
    } else {
      float k = calculateSlope(line);
      return line.start.y - k * line.start.x;
    }
  }
}
