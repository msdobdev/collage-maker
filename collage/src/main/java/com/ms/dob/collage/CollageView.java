package com.ms.dob.collage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xiaopo.flying.collage.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings({"unused", "SameParameterValue"})
public class CollageView extends View {
  private static final String TAG = "F";

  private enum ActionMode {
    NONE, DRAG, ZOOM, MOVE, SWAP
  }

  private ActionMode currentMode = ActionMode.NONE;

  private List<CollagePiece> collagePieces = new ArrayList<>();
  private List<CollagePiece> needChangePieces = new ArrayList<>();
  private Map<Area, CollagePiece> areaPieceMap = new HashMap<>();

  private CollageLayout collageLayout;
  private CollageLayout.Info initialInfo;

  private RectF bounds;
  private int lineSize;

  private int duration;
  private Line handlingLine;

  private CollagePiece handlingPiece;
  private CollagePiece replacePiece;
  private CollagePiece previousHandlingPiece;

  private Paint linePaint;
  private Paint selectedAreaPaint;
  private Paint handleBarPaint;

  private float downX;
  private float downY;
  private float previousDistance;
  private PointF midPoint;
  private boolean needDrawLine;

  private boolean needDrawOuterLine;
  private boolean touchEnable = true;
  private int lineColor;

  private int selectedLineColor;
  private int handleBarColor;
  private float piecePadding;
  private float pieceRadian;

  private boolean needResetPieceMatrix = true;
  private boolean quickMode = false;

  private boolean canDrag = true;
  private boolean canMoveLine = true;
  private boolean canZoom = true;
  private boolean canSwap = true;

  private OnPieceSelectedListener onPieceSelectedListener;

  private Runnable switchToSwapAction = new Runnable() {
    @Override public void run() {
      if (!canSwap) return;
      currentMode = ActionMode.SWAP;
      invalidate();
    }
  };

  public CollageView(Context context) {
    this(context, null);
  }

  public CollageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CollageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CollageView);
    lineSize = ta.getInt(R.styleable.CollageView_line_size, 4);
    lineColor = ta.getColor(R.styleable.CollageView_line_color, Color.WHITE);
    selectedLineColor =
        ta.getColor(R.styleable.CollageView_selected_line_color, Color.parseColor("#99BBFB"));
    handleBarColor =
        ta.getColor(R.styleable.CollageView_handle_bar_color, Color.parseColor("#99BBFB"));
    piecePadding = ta.getDimensionPixelSize(R.styleable.CollageView_piece_padding, 0);
    needDrawLine = ta.getBoolean(R.styleable.CollageView_need_draw_line, false);
    needDrawOuterLine = ta.getBoolean(R.styleable.CollageView_need_draw_outer_line, false);
    duration = ta.getInt(R.styleable.CollageView_animation_duration, 300);
    pieceRadian = ta.getFloat(R.styleable.CollageView_radian, 0f);
    ta.recycle();

    bounds = new RectF();

    // init some paint
    linePaint = new Paint();
    linePaint.setAntiAlias(true);
    linePaint.setColor(lineColor);
    linePaint.setStrokeWidth(lineSize);
    linePaint.setStyle(Paint.Style.STROKE);
    linePaint.setStrokeJoin(Paint.Join.ROUND);
    linePaint.setStrokeCap(Paint.Cap.SQUARE);

    selectedAreaPaint = new Paint();
    selectedAreaPaint.setAntiAlias(true);
    selectedAreaPaint.setStyle(Paint.Style.STROKE);
    selectedAreaPaint.setStrokeJoin(Paint.Join.ROUND);
    selectedAreaPaint.setStrokeCap(Paint.Cap.ROUND);
    selectedAreaPaint.setColor(selectedLineColor);
    selectedAreaPaint.setStrokeWidth(lineSize);

    handleBarPaint = new Paint();
    handleBarPaint.setAntiAlias(true);
    handleBarPaint.setStyle(Paint.Style.FILL);
    handleBarPaint.setColor(handleBarColor);
    handleBarPaint.setStrokeWidth(lineSize * 3);

    midPoint = new PointF();
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    resetPuzzleBounds();

    areaPieceMap.clear();
    if (collagePieces.size() != 0) {
      for (int i = 0; i < collagePieces.size(); i++) {
        CollagePiece piece = collagePieces.get(i);
        Area area = collageLayout.getArea(i);
        piece.setArea(area);
        areaPieceMap.put(area, piece);
        if (needResetPieceMatrix) {
          piece.set(MatrixUtils.generateMatrix(piece, 0f));
        } else {
          piece.fillArea(this, true);
        }
      }
    }
    invalidate();
  }

  private void resetPuzzleBounds() {
    bounds.left = getPaddingLeft();
    bounds.top = getPaddingTop();
    bounds.right = getWidth() - getPaddingRight();
    bounds.bottom = getHeight() - getPaddingBottom();

    if (collageLayout != null) {
      collageLayout.reset();
      collageLayout.setOuterBounds(bounds);
      collageLayout.layout();
      collageLayout.setPadding(piecePadding);
      collageLayout.setRadian(pieceRadian);

      if (initialInfo != null) {
        final int size = initialInfo.lineInfos.size();
        for (int i = 0; i < size; i++) {
          CollageLayout.LineInfo lineInfo = initialInfo.lineInfos.get(i);
          Line line = collageLayout.getLines().get(i);
          line.startPoint().x = lineInfo.startX;
          line.startPoint().y = lineInfo.startY;
          line.endPoint().x = lineInfo.endX;
          line.endPoint().y = lineInfo.endY;
        }
      }

      collageLayout.sortAreas();
      collageLayout.update();
    }
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (collageLayout == null) {
      return;
    }

    linePaint.setStrokeWidth(lineSize);
    selectedAreaPaint.setStrokeWidth(lineSize);
    handleBarPaint.setStrokeWidth(lineSize * 3);

    // draw pieces
    for (int i = 0; i < collageLayout.getAreaCount(); i++) {
      if (i >= collagePieces.size()) {
        break;
      }

      CollagePiece piece = collagePieces.get(i);

      if (piece == handlingPiece && currentMode == ActionMode.SWAP) {
        continue;
      }

      if (collagePieces.size() > i) {
        piece.draw(canvas, quickMode);
      }
    }

    // draw outer bounds
    if (needDrawOuterLine) {
      for (Line outerLine : collageLayout.getOuterLines()) {
        drawLine(canvas, outerLine);
      }
    }

    // draw slant lines
    if (needDrawLine) {
      for (Line line : collageLayout.getLines()) {
        drawLine(canvas, line);
      }
    }

    // draw selected area
    if (handlingPiece != null && currentMode != ActionMode.SWAP) {
      drawSelectedArea(canvas, handlingPiece);
    }

    // draw swap piece
    if (handlingPiece != null && currentMode == ActionMode.SWAP) {
      handlingPiece.draw(canvas, 128, quickMode);
      if (replacePiece != null) {
        drawSelectedArea(canvas, replacePiece);
      }
    }
  }

  private void drawSelectedArea(Canvas canvas, CollagePiece piece) {
    final Area area = piece.getArea();
    // draw select area
    canvas.drawPath(area.getAreaPath(), selectedAreaPaint);

    // draw handle bar
    for (Line line : area.getLines()) {
      if (collageLayout.getLines().contains(line)) {
        PointF[] handleBarPoints = area.getHandleBarPoints(line);
        canvas.drawLine(handleBarPoints[0].x, handleBarPoints[0].y, handleBarPoints[1].x,
            handleBarPoints[1].y, handleBarPaint);
        canvas.drawCircle(handleBarPoints[0].x, handleBarPoints[0].y, lineSize * 3 / 2,
            handleBarPaint);
        canvas.drawCircle(handleBarPoints[1].x, handleBarPoints[1].y, lineSize * 3 / 2,
            handleBarPaint);
      }
    }
  }

  private void drawLine(Canvas canvas, Line line) {
    canvas.drawLine(line.startPoint().x, line.startPoint().y, line.endPoint().x, line.endPoint().y,
        linePaint);
  }

  public void setPuzzleLayout(CollageLayout collageLayout) {
    clearPieces();

    this.collageLayout = collageLayout;

    collageLayout.setOuterBounds(bounds);
    collageLayout.layout();

    invalidate();
  }

  public void setPuzzleLayout(CollageLayout.Info info) {
    this.initialInfo = info;
    clearPieces();

    this.collageLayout = CollageLayoutParser.parse(info);

    this.piecePadding = info.padding;
    this.pieceRadian = info.radian;
    setBackgroundColor(info.color);

    invalidate();
  }

  public CollageLayout getPuzzleLayout() {
    return collageLayout;
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!touchEnable) {
      return super.onTouchEvent(event);
    }
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        downX = event.getX();
        downY = event.getY();

        decideActionMode(event);
        prepareAction(event);
        break;

      case MotionEvent.ACTION_POINTER_DOWN:
        previousDistance = calculateDistance(event);
        calculateMidPoint(event, midPoint);

        decideActionMode(event);
        break;

      case MotionEvent.ACTION_MOVE:
        performAction(event);

        if ((Math.abs(event.getX() - downX) > 10 || Math.abs(event.getY() - downY) > 10)
            && currentMode != ActionMode.SWAP) {
          removeCallbacks(switchToSwapAction);
        }

        break;

      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        finishAction(event);
        currentMode = ActionMode.NONE;
        removeCallbacks(switchToSwapAction);
        break;
    }

    invalidate();
    return true;
  }

  // 决定应该执行什么Action
  private void decideActionMode(MotionEvent event) {
    for (CollagePiece piece : collagePieces) {
      if (piece.isAnimateRunning()) {
        currentMode = ActionMode.NONE;
        return;
      }
    }

    if (event.getPointerCount() == 1) {
      handlingLine = findHandlingLine();
      if (handlingLine != null && canMoveLine) {
        currentMode = ActionMode.MOVE;
      } else {
        handlingPiece = findHandlingPiece();

        if (handlingPiece != null && canDrag) {
          currentMode = ActionMode.DRAG;

          postDelayed(switchToSwapAction, 500);
        }
      }
    } else if (event.getPointerCount() > 1) {
      if (handlingPiece != null
          && handlingPiece.contains(event.getX(1), event.getY(1))
          && currentMode == ActionMode.DRAG
          && canZoom) {
        currentMode = ActionMode.ZOOM;
      }
    }
  }

  // 执行Action前的准备工作
  @SuppressWarnings("unused")
  private void prepareAction(MotionEvent event) {
    switch (currentMode) {
      case NONE:
        break;
      case DRAG:
        handlingPiece.record();
        break;
      case ZOOM:
        handlingPiece.record();
        break;
      case MOVE:
        handlingLine.prepareMove();
        needChangePieces.clear();
        needChangePieces.addAll(findNeedChangedPieces());
        for (CollagePiece piece : needChangePieces) {
          piece.record();
          piece.setPreviousMoveX(downX);
          piece.setPreviousMoveY(downY);
        }
        break;
    }
  }

  // 执行Action
  private void performAction(MotionEvent event) {
    switch (currentMode) {
      case NONE:
        break;
      case DRAG:
        dragPiece(handlingPiece, event);
        break;
      case ZOOM:
        zoomPiece(handlingPiece, event);
        break;
      case SWAP:
        dragPiece(handlingPiece, event);
        replacePiece = findReplacePiece(event);
        break;
      case MOVE:
        moveLine(handlingLine, event);
        break;
    }
  }

  // 结束Action
  private void finishAction(MotionEvent event) {
    switch (currentMode) {
      case NONE:
        break;
      case DRAG:
        if (handlingPiece != null && !handlingPiece.isFilledArea()) {
          handlingPiece.moveToFillArea(this);
        }

        if (previousHandlingPiece == handlingPiece
            && Math.abs(downX - event.getX()) < 3
            && Math.abs(downY - event.getY()) < 3) {
          handlingPiece = null;
        }

        previousHandlingPiece = handlingPiece;
        break;
      case ZOOM:
        if (handlingPiece != null && !handlingPiece.isFilledArea()) {
          if (handlingPiece.canFilledArea()) {
            handlingPiece.moveToFillArea(this);
          } else {
            handlingPiece.fillArea(this, false);
          }
        }
        previousHandlingPiece = handlingPiece;
        break;
      case MOVE:
        break;
      case SWAP:
        if (handlingPiece != null && replacePiece != null) {
          swapPiece();

          handlingPiece = null;
          replacePiece = null;
          previousHandlingPiece = null;
        }
        break;
    }

    // trigger listener
    if (handlingPiece != null && onPieceSelectedListener != null) {
      onPieceSelectedListener.onPieceSelected(handlingPiece,
          collagePieces.indexOf(handlingPiece));
    }

    handlingLine = null;
    needChangePieces.clear();
  }

  private void swapPiece() {
    Drawable temp = handlingPiece.getDrawable();
    String tempPath = handlingPiece.getPath();

    handlingPiece.setDrawable(replacePiece.getDrawable());
    handlingPiece.setPath(replacePiece.getPath());
    replacePiece.setDrawable(temp);
    replacePiece.setPath(tempPath);

    handlingPiece.fillArea(this, true);
    replacePiece.fillArea(this, true);
  }

  private void moveLine(Line line, MotionEvent event) {
    if (line == null || event == null) return;

    boolean needUpdate;
    if (line.direction() == Line.Direction.HORIZONTAL) {
      needUpdate = line.move(event.getY() - downY, 80);
    } else {
      needUpdate = line.move(event.getX() - downX, 80);
    }

    if (needUpdate) {
      collageLayout.update();
      collageLayout.sortAreas();
      updatePiecesInArea(line, event);
    }
  }

  private void updatePiecesInArea(Line line, MotionEvent event) {
    for (int i = 0; i < needChangePieces.size(); i++) {
      needChangePieces.get(i).updateWith(event, line);
    }
  }

  private void zoomPiece(CollagePiece piece, MotionEvent event) {
    if (piece == null || event == null || event.getPointerCount() < 2) return;
    float scale = calculateDistance(event) / previousDistance;
    piece.zoomAndTranslate(scale, scale, midPoint, event.getX() - downX, event.getY() - downY);
  }

  private void dragPiece(CollagePiece piece, MotionEvent event) {
    if (piece == null || event == null) return;
    piece.translate(event.getX() - downX, event.getY() - downY);
  }

  public void replace(Bitmap bitmap, String path) {
    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
    bitmapDrawable.setAntiAlias(true);
    bitmapDrawable.setFilterBitmap(true);

    replace(bitmapDrawable, path);
  }

  public void replace(final Drawable bitmapDrawable, String path) {
    if (handlingPiece == null) {
      return;
    }

    handlingPiece.setPath(path);
    handlingPiece.setDrawable(bitmapDrawable);
    handlingPiece.set(MatrixUtils.generateMatrix(handlingPiece, 0f));

    invalidate();
  }

  public void flipVertically() {
    if (handlingPiece == null) {
      return;
    }

    handlingPiece.postFlipVertically();
    handlingPiece.record();

    invalidate();
  }

  public void flipHorizontally() {
    if (handlingPiece == null) {
      return;
    }

    handlingPiece.postFlipHorizontally();
    handlingPiece.record();

    invalidate();
  }

  public void rotate(float degree) {
    if (handlingPiece == null) {
      return;
    }

    handlingPiece.postRotate(degree);
    handlingPiece.record();

    invalidate();
  }

  private CollagePiece findHandlingPiece() {
    for (CollagePiece piece : collagePieces) {
      if (piece.contains(downX, downY)) {
        return piece;
      }
    }
    return null;
  }

  private Line findHandlingLine() {
    for (Line line : collageLayout.getLines()) {
      if (line.contains(downX, downY, 40)) {
        return line;
      }
    }
    return null;
  }

  private CollagePiece findReplacePiece(MotionEvent event) {
    for (CollagePiece piece : collagePieces) {
      if (piece.contains(event.getX(), event.getY())) {
        return piece;
      }
    }
    return null;
  }

  private List<CollagePiece> findNeedChangedPieces() {
    if (handlingLine == null) return new ArrayList<>();

    List<CollagePiece> needChanged = new ArrayList<>();

    for (CollagePiece piece : collagePieces) {
      if (piece.contains(handlingLine)) {
        needChanged.add(piece);
      }
    }

    return needChanged;
  }

  private float calculateDistance(MotionEvent event) {
    float x = event.getX(0) - event.getX(1);
    float y = event.getY(0) - event.getY(1);

    return (float) Math.sqrt(x * x + y * y);
  }

  private void calculateMidPoint(MotionEvent event, PointF point) {
    point.x = (event.getX(0) + event.getX(1)) / 2;
    point.y = (event.getY(0) + event.getY(1)) / 2;
  }

  public void reset() {
    clearPieces();
    if (collageLayout != null) {
      collageLayout.reset();
    }
  }

  public void clearPieces() {
    clearHandlingPieces();
    collagePieces.clear();

    invalidate();
  }

  public void clearHandlingPieces() {
    handlingLine = null;
    handlingPiece = null;
    replacePiece = null;
    needChangePieces.clear();

    invalidate();
  }

  public void addPieces(List<Bitmap> bitmaps) {
    for (Bitmap bitmap : bitmaps) {
      addPiece(bitmap);
    }

    postInvalidate();
  }

  public void addDrawablePieces(List<Drawable> drawables) {
    for (Drawable drawable : drawables) {
      addPiece(drawable);
    }

    postInvalidate();
  }

  public void addPiece(Bitmap bitmap) {
    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
    bitmapDrawable.setAntiAlias(true);
    bitmapDrawable.setFilterBitmap(true);

    addPiece(bitmapDrawable, null);
  }

  public void addPiece(Bitmap bitmap, Matrix initialMatrix) {
    addPiece(bitmap, initialMatrix, "");
  }

  public void addPiece(Bitmap bitmap, Matrix initialMatrix, String path) {
    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
    bitmapDrawable.setAntiAlias(true);
    bitmapDrawable.setFilterBitmap(true);

    addPiece(bitmapDrawable, initialMatrix, path);
  }


  public void addPiece(Drawable drawable) {
    addPiece(drawable, null);
  }

  public void addPiece(Drawable drawable, Matrix initialMatrix) {
    addPiece(drawable, initialMatrix, "");
  }

  public void addPiece(Drawable drawable, Matrix initialMatrix, String path) {
    int position = collagePieces.size();

    if (position >= collageLayout.getAreaCount()) {
      Log.e(TAG, "addPiece: can not add more. the current puzzle layout can contains "
          + collageLayout.getAreaCount()
          + " puzzle piece.");
      return;
    }

    final Area area = collageLayout.getArea(position);
    area.setPadding(piecePadding);

    CollagePiece piece = new CollagePiece(drawable, area, new Matrix());

    final Matrix matrix = initialMatrix != null
        ? new Matrix(initialMatrix) : MatrixUtils.generateMatrix(area, drawable, 0f);
    piece.set(matrix);

    piece.setAnimateDuration(duration);
    piece.setPath(path);

    collagePieces.add(piece);
    areaPieceMap.put(area, piece);

    setPiecePadding(piecePadding);
    setPieceRadian(pieceRadian);

    invalidate();
  }

  public void setSelected(final int position) {
    post(new Runnable() {
      @Override public void run() {
        if (position >= collagePieces.size()) return;
        previousHandlingPiece = handlingPiece = collagePieces.get(position);

        if (onPieceSelectedListener != null) {
          onPieceSelectedListener.onPieceSelected(handlingPiece, position);
        }
        invalidate();
      }
    });
  }

  public CollagePiece getHandlingPiece() {
    return handlingPiece;
  }

  public int getHandlingPiecePosition() {
    if (handlingPiece == null) {
      return -1;
    }
    return collagePieces.indexOf(handlingPiece);
  }

  // can be null
  public boolean hasPieceSelected() {
    return handlingPiece != null;
  }

  public void setAnimateDuration(int duration) {
    this.duration = duration;
    for (CollagePiece piece : collagePieces) {
      piece.setAnimateDuration(duration);
    }
  }

  public boolean isNeedDrawLine() {
    return needDrawLine;
  }

  public void setNeedDrawLine(boolean needDrawLine) {
    this.needDrawLine = needDrawLine;
    handlingPiece = null;
    previousHandlingPiece = null;
    invalidate();
  }

  public boolean isNeedDrawOuterLine() {
    return needDrawOuterLine;
  }

  public void setNeedDrawOuterLine(boolean needDrawOuterLine) {
    this.needDrawOuterLine = needDrawOuterLine;
    invalidate();
  }

  public int getLineColor() {
    return lineColor;
  }

  public void setLineColor(int lineColor) {
    this.lineColor = lineColor;
    this.linePaint.setColor(lineColor);
    invalidate();
  }

  public int getLineSize() {
    return lineSize;
  }

  public void setLineSize(int lineSize) {
    this.lineSize = lineSize;
    invalidate();
  }

  public int getSelectedLineColor() {
    return selectedLineColor;
  }

  public void setSelectedLineColor(int selectedLineColor) {
    this.selectedLineColor = selectedLineColor;
    this.selectedAreaPaint.setColor(selectedLineColor);
    invalidate();
  }

  public int getHandleBarColor() {
    return handleBarColor;
  }

  public void setHandleBarColor(int handleBarColor) {
    this.handleBarColor = handleBarColor;
    this.handleBarPaint.setColor(handleBarColor);
    invalidate();
  }

  public boolean isTouchEnable() {
    return touchEnable;
  }

  public void setTouchEnable(boolean touchEnable) {
    this.touchEnable = touchEnable;
  }

  public void clearHandling() {
    handlingPiece = null;
    handlingLine = null;
    replacePiece = null;
    previousHandlingPiece = null;
    needChangePieces.clear();
  }

  public void setPiecePadding(float padding) {
    this.piecePadding = padding;
    if (collageLayout != null) {
      collageLayout.setPadding(padding);
      final int size = collagePieces.size();
      for (int i = 0; i < size; i++) {
        CollagePiece collagePiece = collagePieces.get(i);
        if (collagePiece.canFilledArea()) {
          collagePiece.moveToFillArea(null);
        } else {
          collagePiece.fillArea(this, true);
        }
      }
    }

    invalidate();
  }

  public void setPieceRadian(float radian) {
    this.pieceRadian = radian;
    if (collageLayout != null) {
      collageLayout.setRadian(radian);
    }

    invalidate();
  }

  public void setQuickMode(boolean quickMode) {
    this.quickMode = quickMode;
    invalidate();
  }

  @Override public void setBackgroundColor(int color) {
    super.setBackgroundColor(color);
    if (collageLayout != null) {
      collageLayout.setColor(color);
    }
  }

  public void setNeedResetPieceMatrix(boolean needResetPieceMatrix) {
    this.needResetPieceMatrix = needResetPieceMatrix;
  }

  public float getPiecePadding() {
    return piecePadding;
  }

  public float getPieceRadian() {
    return pieceRadian;
  }

  public List<CollagePiece> getPuzzlePieces() {
    final int size = collagePieces.size();
    final List<CollagePiece> pieces = new ArrayList<>(size);
    collageLayout.sortAreas();
    for (int i = 0; i < size; i++) {
      Area area = collageLayout.getArea(i);
      CollagePiece piece = areaPieceMap.get(area);
      pieces.add(piece);
    }

    return pieces;
  }

  public boolean canDrag() {
    return canDrag;
  }

  public void setCanDrag(boolean canDrag) {
    this.canDrag = canDrag;
  }

  public boolean canMoveLine() {
    return canMoveLine;
  }

  public void setCanMoveLine(boolean canMoveLine) {
    this.canMoveLine = canMoveLine;
  }

  public boolean canZoom() {
    return canZoom;
  }

  public void setCanZoom(boolean canZoom) {
    this.canZoom = canZoom;
  }

  public boolean canSwap() {
    return canSwap;
  }

  public void setCanSwap(boolean canSwap) {
    this.canSwap = canSwap;
  }

  public void setOnPieceSelectedListener(OnPieceSelectedListener onPieceSelectedListener) {
    this.onPieceSelectedListener = onPieceSelectedListener;
  }

  public interface OnPieceSelectedListener {
    void onPieceSelected(CollagePiece piece, int position);
  }
}
