package com.ms.dob.photocollage.layout.custom;

import com.ms.dob.collage.CollageLayout;

import java.util.ArrayList;
import java.util.List;


public class StraightLayoutHelper {
  private StraightLayoutHelper() {

  }

  public static List<CollageLayout> getAllThemeLayout(int pieceCount) {
    List<CollageLayout> collageLayouts = new ArrayList<>();
    switch (pieceCount) {
      case 1:
        for (int i = 0; i < 6; i++) {
          collageLayouts.add(new CollageOneLayout(i));
        }
        break;
      case 2:
        for (int i = 0; i < 6; i++) {
          collageLayouts.add(new CollageTwoLayout(i));
        }
        break;
      case 3:
        for (int i = 0; i < 6; i++) {
          collageLayouts.add(new CollageThreeLayout(i));
        }
        break;
      case 4:
        for (int i = 0; i < 8; i++) {
          collageLayouts.add(new CollageFourLayout(i));
        }
        break;
      case 5:
        for (int i = 0; i < 17; i++) {
          collageLayouts.add(new CollageFiveLayout(i));
        }
        break;
      case 6:
        for (int i = 0; i < 12; i++) {
          collageLayouts.add(new CollageSixLayout(i));
        }
        break;
      case 7:
        for (int i = 0; i < 9; i++) {
          collageLayouts.add(new CollageSevenLayout(i));
        }
        break;
      case 8:
        for (int i = 0; i < 11; i++) {
          collageLayouts.add(new CollageEightLayout(i));
        }
        break;
      case 9:
        for (int i = 0; i < 8; i++) {
          collageLayouts.add(new CollageNineLayout(i));
        }
        break;
    }

    return collageLayouts;
  }
}
