package com.ms.dob.collage;

import android.graphics.RectF;
import com.ms.dob.collage.skew.SkewCollageLayout;
import com.ms.dob.collage.straight.StraightCollageLayout;


class CollageLayoutParser {

  private CollageLayoutParser() {
  }

  public static CollageLayout parse(final CollageLayout.Info info) {
    CollageLayout layout;
    if (info.type == CollageLayout.Info.TYPE_STRAIGHT) {
      layout = new StraightCollageLayout() {
        @Override public void layout() {
          final int size = info.steps.size();
          for (int i = 0; i < size; i++) {
            Step step = info.steps.get(i);

            switch (step.type) {
              case Step.ADD_LINE:
                addLine(step.position, step.lineDirection(), 1f / 2);
                break;
              case Step.ADD_CROSS:
                addCross(step.position, 1f / 2);
                break;
              case Step.CUT_EQUAL_PART_ONE:
                cutAreaEqualPart(step.position, step.hSize, step.vSize);
                break;
              case Step.CUT_EQUAL_PART_TWO:
                cutAreaEqualPart(step.position, step.part, step.lineDirection());
                break;
              case Step.CUT_SPIRAL:
                cutSpiral(step.position);
                break;
            }
          }
        }
      };
    } else {
      layout = new SkewCollageLayout() {
        @Override public void layout() {
          final int size = info.steps.size();
          for (int i = 0; i < size; i++) {
            Step step = info.steps.get(i);

            switch (step.type) {
              case Step.ADD_LINE:
                addLine(step.position, step.lineDirection(), 1f / 2);
                break;
              case Step.ADD_CROSS:
                addCross(step.position, 1f / 2, 1f / 2, 1f / 2, 1f / 2);
                break;
              case Step.CUT_EQUAL_PART_ONE:
                cutArea(step.position, step.hSize, step.vSize);
                break;
            }
          }
        }
      };
    }

    RectF bounds = new RectF(info.left, info.top, info.right, info.bottom);
    layout.setOuterBounds(bounds);
    layout.layout();
    layout.setColor(info.color);
    layout.setRadian(info.radian);
    layout.setPadding(info.padding);

    final int size = info.lineInfos.size();
    for (int i = 0; i < size; i++) {
      CollageLayout.LineInfo lineInfo = info.lineInfos.get(i);
      Line line = layout.getLines().get(i);
      line.startPoint().x = lineInfo.startX;
      line.startPoint().y = lineInfo.startY;
      line.endPoint().x = lineInfo.endX;
      line.endPoint().y = lineInfo.endY;
    }

    layout.sortAreas();
    layout.update();

    return layout;
  }
}
