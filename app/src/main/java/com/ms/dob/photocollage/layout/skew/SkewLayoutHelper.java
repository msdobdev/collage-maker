package com.ms.dob.photocollage.layout.skew;

import com.ms.dob.collage.CollageLayout;

import java.util.ArrayList;
import java.util.List;


public class SkewLayoutHelper {
  private SkewLayoutHelper() {

  }

  public static List<CollageLayout> getAllThemeLayout(int pieceCount) {
    List<CollageLayout> collageLayouts = new ArrayList<>();
    switch (pieceCount) {
      case 1:
        for (int i = 0; i < 4; i++) {
          collageLayouts.add(new OneSkewLayout(i));
        }
        break;
      case 2:
        for (int i = 0; i < 2; i++) {
          collageLayouts.add(new TwoSkewLayout(i));
        }
        break;
      case 3:
        for (int i = 0; i < 6; i++) {
          collageLayouts.add(new ThreeSkewLayout(i));
        }
        break;
      //case 4:
      //  for (int i = 0; i < 8; i++) {
      //    puzzleLayouts.add(new FourStraightLayout(i));
      //  }
      //  break;
      //case 5:
      //  for (int i = 0; i < 17; i++) {
      //    puzzleLayouts.add(new FiveStraightLayout(i));
      //  }
      //  break;
      //case 6:
      //  for (int i = 0; i < 12; i++) {
      //    puzzleLayouts.add(new SixStraightLayout(i));
      //  }
      //  break;
      //case 7:
      //  for (int i = 0; i < 9; i++) {
      //    puzzleLayouts.add(new SevenStraightLayout(i));
      //  }
      //  break;
      //case 8:
      //  for (int i = 0; i < 11; i++) {
      //    puzzleLayouts.add(new EightStraightLayout(i));
      //  }
      //  break;
      //case 9:
      //  for (int i = 0; i < 8; i++) {
      //    puzzleLayouts.add(new NineStraightLayout(i));
      //  }
      //  break;
    }

    return collageLayouts;
  }
}
