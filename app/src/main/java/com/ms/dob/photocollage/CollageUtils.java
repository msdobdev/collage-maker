package com.ms.dob.photocollage;

import com.ms.dob.photocollage.layout.skew.OneSkewLayout;
import com.ms.dob.photocollage.layout.skew.SkewLayoutHelper;
import com.ms.dob.photocollage.layout.skew.ThreeSkewLayout;
import com.ms.dob.photocollage.layout.skew.TwoSkewLayout;
import com.ms.dob.photocollage.layout.custom.CollageEightLayout;
import com.ms.dob.photocollage.layout.custom.CollageFiveLayout;
import com.ms.dob.photocollage.layout.custom.CollageFourLayout;
import com.ms.dob.photocollage.layout.custom.CollageNineLayout;
import com.ms.dob.photocollage.layout.custom.CollageOneLayout;
import com.ms.dob.photocollage.layout.custom.CollageSevenLayout;
import com.ms.dob.photocollage.layout.custom.CollageSixLayout;
import com.ms.dob.photocollage.layout.custom.StraightLayoutHelper;
import com.ms.dob.photocollage.layout.custom.CollageThreeLayout;
import com.ms.dob.photocollage.layout.custom.CollageTwoLayout;
import com.ms.dob.collage.CollageLayout;

import java.util.ArrayList;
import java.util.List;


public class CollageUtils {
  private static final String TAG = "PuzzleUtils";

  private CollageUtils() {
    //no instance
  }

  public static CollageLayout getPuzzleLayout(int type, int borderSize, int themeId) {
    if (type == 0) {
      switch (borderSize) {
        case 1:
          return new OneSkewLayout(themeId);
        case 2:
          return new TwoSkewLayout(themeId);
        case 3:
          return new ThreeSkewLayout(themeId);
        default:
          return new OneSkewLayout(themeId);
      }
    } else {
      switch (borderSize) {
        case 1:
          return new CollageOneLayout(themeId);
        case 2:
          return new CollageTwoLayout(themeId);
        case 3:
          return new CollageThreeLayout(themeId);
        case 4:
          return new CollageFourLayout(themeId);
        case 5:
          return new CollageFiveLayout(themeId);
        case 6:
          return new CollageSixLayout(themeId);
        case 7:
          return new CollageSevenLayout(themeId);
        case 8:
          return new CollageEightLayout(themeId);
        case 9:
          return new CollageNineLayout(themeId);
        default:
          return new CollageOneLayout(themeId);
      }
    }
  }

  public static List<CollageLayout> getAllPuzzleLayouts() {
    List<CollageLayout> collageLayouts = new ArrayList<>();
    //slant layout
    collageLayouts.addAll(SkewLayoutHelper.getAllThemeLayout(2));
    collageLayouts.addAll(SkewLayoutHelper.getAllThemeLayout(3));

    // straight layout
    collageLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(2));
    collageLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(3));
    collageLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(4));
    collageLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(5));
    collageLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(6));
    collageLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(7));
    collageLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(8));
    collageLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(9));
    return collageLayouts;
  }

  public static List<CollageLayout> getPuzzleLayouts(int pieceCount) {
    List<CollageLayout> collageLayouts = new ArrayList<>();
    collageLayouts.addAll(SkewLayoutHelper.getAllThemeLayout(pieceCount));
    collageLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(pieceCount));
    return collageLayouts;
  }
}
