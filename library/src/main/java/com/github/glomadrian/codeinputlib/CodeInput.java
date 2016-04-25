package com.github.glomadrian.codeinputlib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.github.glomadrian.codeinputlib.data.FixedStack;
import com.github.glomadrian.codeinputlib.model.Underline;
import com.weicong.gotravelling.library.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Adrián García Lomas
 */
public class CodeInput extends View {

  private static final int DEFAULT_CODES = 6;
  private FixedStack<Character> characters;
  private Underline underlines[];
  private Paint underlinePaint;
  private Paint underlineSelectedPaint;
  private Paint textPaint;
  private Paint hintPaint;
  private ValueAnimator reductionAnimator;
  private ValueAnimator hintYAnimator;
  private ValueAnimator hintSizeAnimator;
  private float underlineReduction;
  private float underlineStrokeWidth;
  private float underlineWidth;
  private float reduction;
  private float textSize;
  private float textMarginBottom;
  private float hintX;
  private float hintNormalSize;
  private float hintSmallSize;
  private float hintMarginBottom;
  private float hintActualMarginBottom;
  private float viewHeight;
  private long animationDuration;
  private int height;
  private int underlineAmount;
  private int underlineColor;
  private int underlineSelectedColor;
  private int hintColor;
  private int textColor;
  private boolean underlined = true;
  private String hintText;

  public CodeInput(Context context) {
    super(context);
    init(null);
  }

  public CodeInput(Context context, AttributeSet attributeset) {
    super(context, attributeset);
    init(attributeset);
  }

  public CodeInput(Context context, AttributeSet attributeset, int defStyledAttrs) {
    super(context, attributeset, defStyledAttrs);
    init(attributeset);
  }

  private void init(AttributeSet attributeset) {
    initDefaultAttributes();
    initCustomAttributes(attributeset);
    initDataStructures();
    initPaint();
    initAnimator();
    initViewOptions();
  }

  private void initDefaultAttributes() {
    underlineStrokeWidth = getContext().getResources().getDimension(R.dimen.underline_stroke_width);
    underlineWidth = getContext().getResources().getDimension(R.dimen.underline_width);
    underlineReduction = getContext().getResources().getDimension(R.dimen.section_reduction);
    textSize = getContext().getResources().getDimension(R.dimen.text_size);
    textMarginBottom = getContext().getResources().getDimension(R.dimen.text_margin_bottom);
    underlineColor = getContext().getResources().getColor(R.color.underline_default_color);
    underlineSelectedColor = getContext().getResources().getColor(R.color.underline_selected_color);
    hintColor = getContext().getResources().getColor(R.color.hintColor);
    textColor = getContext().getResources().getColor(R.color.textColor);
    hintMarginBottom = getContext().getResources().getDimension(R.dimen.hint_margin_bottom);
    hintNormalSize = getContext().getResources().getDimension(R.dimen.hint_size);
    hintSmallSize = getContext().getResources().getDimension(R.dimen.hint_small_size);
    animationDuration = getContext().getResources().getInteger(R.integer.animation_duration);
    viewHeight = getContext().getResources().getDimension(R.dimen.view_height);
    hintX = 0;
    hintActualMarginBottom = 0;
    underlineAmount = DEFAULT_CODES;
    reduction = 0.0F;
  }

  private void initCustomAttributes(AttributeSet attributeset) {
    TypedArray attributes =
        getContext().obtainStyledAttributes(attributeset, R.styleable.core_area);

    underlineColor = attributes.getColor(R.styleable.core_area_underline_color, underlineColor);
    underlineSelectedColor =
        attributes.getColor(R.styleable.core_area_underline_selected_color, underlineSelectedColor);
    hintColor = attributes.getColor(R.styleable.core_area_underline_color, hintColor);
    hintText = attributes.getString(R.styleable.core_area_hint_text);
    underlineAmount = attributes.getInt(R.styleable.core_area_codes, underlineAmount);
    textColor = attributes.getInt(R.styleable.core_area_code_text_color, textColor);
  }

  private void initDataStructures() {
    underlines = new Underline[underlineAmount];
    characters = new FixedStack();
    characters.setMaxSize(underlineAmount);
  }

  private void initPaint() {
    underlinePaint = new Paint();
    underlinePaint.setColor(underlineColor);
    underlinePaint.setStrokeWidth(underlineStrokeWidth);
    underlinePaint.setStyle(Paint.Style.STROKE);
    underlineSelectedPaint = new Paint();
    underlineSelectedPaint.setColor(underlineSelectedColor);
    underlineSelectedPaint.setStrokeWidth(underlineStrokeWidth);
    underlineSelectedPaint.setStyle(Paint.Style.STROKE);
    textPaint = new Paint();
    textPaint.setTextSize(textSize);
    textPaint.setColor(textColor);
    textPaint.setAntiAlias(true);
    textPaint.setTextAlign(Paint.Align.CENTER);
    hintPaint = new Paint();
    hintPaint = new Paint();
    hintPaint.setTextSize(hintNormalSize);
    hintPaint.setAntiAlias(true);
    hintPaint.setColor(underlineColor);
  }

  private void initAnimator() {
    reductionAnimator = ValueAnimator.ofFloat(0, underlineReduction);
    reductionAnimator.setDuration(animationDuration);
    reductionAnimator.addUpdateListener(new ReductionAnimatorListener());
    reductionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    hintSizeAnimator = ValueAnimator.ofFloat(hintNormalSize, hintSmallSize);
    hintSizeAnimator.setDuration(animationDuration);
    hintSizeAnimator.addUpdateListener(new HintSizeAnimatorListener());
    hintSizeAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    hintYAnimator = ValueAnimator.ofFloat(0, hintMarginBottom);
    hintYAnimator.setDuration(animationDuration);
    hintYAnimator.addUpdateListener(new HintYAnimatorListener());
    hintYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
  }

  private void initViewOptions() {
    setFocusable(true);
    setFocusableInTouchMode(true);
  }

  @Override
  protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    if (!gainFocus && characters.size() == 0) {
      reverseAnimation();
    }
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, (int) viewHeight, oldw, oldh);
    height = h;
    underlineWidth = w / underlineAmount;
    initUnderline();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = measure(widthMeasureSpec);
    int height = measure(heightMeasureSpec);
    setMeasuredDimension(width, height);
  }

  /**
   * 测量大小
   *
   * @param measureSpec 测量参数
   * @return 最终大小
   */
  private int measure(int measureSpec) {
    int result;
    int mode = MeasureSpec.getMode(measureSpec);
    int size = MeasureSpec.getSize(measureSpec);
    if (mode == MeasureSpec.EXACTLY) {
      result = size;
    } else {
      result = (int) viewHeight;
      if(mode == MeasureSpec.AT_MOST){
        result = Math.min(result, size);
      }
    }
    return result;
  }

  private void initUnderline() {
    for (int i = 0; i < underlineAmount; i++) {
      underlines[i] = createPath(i, underlineWidth);
    }
  }

  private Underline createPath(int position, float sectionWidth) {
    float fromX = sectionWidth * (float) position;
    return new Underline(fromX, height, fromX + sectionWidth, height);
  }

  private void showKeyboard() {
    InputMethodManager inputmethodmanager =
        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    inputmethodmanager.showSoftInput(this, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    inputmethodmanager.viewClicked(this);
  }

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);

    imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
  }

  private void startAnimation() {
    reductionAnimator.start();
    hintSizeAnimator.start();
    hintYAnimator.start();
    underlined = false;
  }

  private void reverseAnimation() {
    reductionAnimator.reverse();
    hintSizeAnimator.reverse();
    hintYAnimator.reverse();
    underlined = true;
  }

  /**
   * Detects the del key and delete the numbers
   */
  @Override public boolean onKeyDown(int keyCode, KeyEvent keyevent) {
    if (keyCode == KeyEvent.KEYCODE_DEL && characters.size() != 0) {
      characters.pop();
    }
    return super.onKeyDown(keyCode, keyevent);
  }

  private static final String MATCH_3 = "[1-9]";
  private static final String MATCH_4 = "[0-5]";
  private static final String MATCH_NUM = "\\d";
  /**
   * Capture the keyboard events but only if are A-Z 0-9
   */
  @Override public boolean onKeyUp(int keyCode, KeyEvent keyevent) {
    if (keyCode >= 7 && keyCode <= 16) {
      char character = Integer.toString(keyCode - 7).charAt(0);
      switch (characters.size()) {
        case 0:
          if (keyCode == 8 || keyCode == 9) {
            characters.push(character);
          }
          break;
        case 1:
          char c = characters.get(0);
          if ((c == '2' && keyCode == 7) || (c == '1' && keyCode == 16)) {
            characters.push(character);
          }
          break;
        case 2:
          Pattern pattern = Pattern.compile(MATCH_3);
          Matcher matcher = pattern.matcher(String.valueOf(character));
          c = characters.get(1);
          if ((c == '9' && matcher.matches()) || (c == '0' && character == '1')
                  || (c == '0' && character == '0')) {
            characters.push(character);
          }
          break;
        case 3:
          Pattern p1 = Pattern.compile(MATCH_4);
          Matcher m1 = p1.matcher(String.valueOf(character));
          Pattern p2 = Pattern.compile(MATCH_NUM);
          Matcher m2 = p2.matcher(String.valueOf(character));
          c = characters.get(2);
          if ((c == '1' && m1.matches()) || (c != '1'&& m2.matches())) {
            characters.push(character);
            hideKeyboard();
            if (mListener != null) {
              mListener.onCompleted();
            }
          }
          break;
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * When a touch is detected the view need to focus and animate if is necessary
   */
  @Override public boolean onTouchEvent(MotionEvent motionevent) {
    if (motionevent.getAction() == 0) {
      requestFocus();
      if (underlined) {
        startAnimation();
      }

      showKeyboard();
    }
    return super.onTouchEvent(motionevent);
  }

  @Override protected void onDraw(Canvas canvas) {
    for (int i = 0; i < underlines.length; i++) {
      Underline sectionpath = underlines[i];
      float fromX = sectionpath.getFromX() + reduction;
      float fromY = sectionpath.getFromY();
      float toX = sectionpath.getToX() - reduction;
      float toY = sectionpath.getToY();
      drawSection(i, fromX, fromY, toX, toY, canvas);
      if (characters.toArray().length > i && characters.size() != 0) {
        drawCharacter(fromX, toX, characters.get(i), canvas);
      }
    }
    if (hintText != null) {
      drawHint(canvas);
    }
    invalidate();
  }

  private void drawSection(int position, float fromX, float fromY, float toX, float toY,
      Canvas canvas) {
    Paint paint = underlinePaint;
    if (position == characters.size() && !underlined) {
      paint = underlineSelectedPaint;
    }
    canvas.drawLine(fromX, fromY, toX, toY, paint);
  }

  private void drawCharacter(float fromX, float toX, Character character, Canvas canvas) {
    float actualWidth = toX - fromX;
    float centerWidth = actualWidth / 2;
    float centerX = fromX + centerWidth;
    canvas.drawText(character.toString(), centerX, height - textMarginBottom, textPaint);
  }

  private void drawHint(Canvas canvas) {
    canvas.drawText(hintText, hintX, height - textMarginBottom - hintActualMarginBottom, hintPaint);
  }

  public Character[] getCode() {
    return characters.toArray(new Character[underlineAmount]);
  }

  /**
   * Listener to update the reduction of the underline bars
   */
  private class ReductionAnimatorListener implements ValueAnimator.AnimatorUpdateListener {

    public void onAnimationUpdate(ValueAnimator valueanimator) {
      float value = ((Float) valueanimator.getAnimatedValue()).floatValue();
      reduction = value;
    }
  }

  /**
   * Listener to update the hint y values
   */
  private class HintYAnimatorListener implements ValueAnimator.AnimatorUpdateListener {

    @Override public void onAnimationUpdate(ValueAnimator animation) {
      hintActualMarginBottom = (Float) animation.getAnimatedValue();
    }
  }

  /**
   * Listener to update the size of the hint text
   */
  private class HintSizeAnimatorListener implements ValueAnimator.AnimatorUpdateListener {

    @Override public void onAnimationUpdate(ValueAnimator animation) {
      float size = (Float) animation.getAnimatedValue();
      hintPaint.setTextSize(size);
    }
  }

  public interface onCompletedListener {
      void onCompleted();
  }

  private onCompletedListener mListener;

  public void setCompletedListener(onCompletedListener listener) {
    mListener = listener;
  }
}
