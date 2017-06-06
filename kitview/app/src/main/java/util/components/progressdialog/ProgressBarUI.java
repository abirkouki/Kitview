package util.components.progressdialog;

import java.util.ArrayList;
import util.components.progressdialog.FRProgressDialog.FRDialog;
import util.image.ImageUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

public class ProgressBarUI extends View {
	//Constants
	private final static int NB_RECTS = 4;

	//Views
	private Paint mBgPaint;
	private Paint mFgSelectedPaint;
	private Paint mFgUnselectedPaint;
	private Paint m_paintText;

	private RectF mBgRect;
	private RectF mFgTopLeftRect;
	private RectF mFgTopRightRect;
	private RectF mFgBottomLeftRect;
	private RectF mFgBottomRightRect;
	//private Rect mTextRect;

	//Model
	private int mCurrentRectIndex;
	//private String mText;
	private float mBgRectBorder;

	public ProgressBarUI(Context context) {
		super(context);
		initialize();
	}

	public ProgressBarUI(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public ProgressBarUI(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	/*public void setText(String mText){
		this.mText = mText;
	}*/

	private void initialize(){
		this.mCurrentRectIndex = 0;

		this.m_paintText = new Paint();
		this.m_paintText.setColor(Color.WHITE);
		this.m_paintText.setAntiAlias(true);
		this.m_paintText.setTextSize(16);
		this.m_paintText.setStrokeWidth(2);

		this.mBgPaint = new Paint();
		this.mBgPaint.setColor(Color.argb(170,85,85,85));//170, 170, 170));
		this.mBgPaint.setAntiAlias(true);

		this.mFgUnselectedPaint = new Paint();
		this.mFgUnselectedPaint.setColor(Color.argb(255, 255, 255, 255));
		this.mFgUnselectedPaint.setAntiAlias(true);
		this.mFgUnselectedPaint.setAlpha(255);

		this.mFgSelectedPaint = new Paint();
		this.mFgSelectedPaint.setColor(Color.argb(255, 55, 181, 229));
		this.mFgSelectedPaint.setAntiAlias(true);
		this.mFgSelectedPaint.setAlpha(255);

		this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(getContext());

				int screenWidth = screenDimensions.get(0).intValue();
				int screenHeight = screenDimensions.get(1).intValue();

				mBgRectBorder = 5.0f;

				int totalHorSpacing = (int) (screenWidth*2.0f/3.0f);
				int rectWidth = (screenWidth-totalHorSpacing);
				int totalHorSpacingInnerRect = (int) (rectWidth / 3.0f);
				int horSpacingBetweenRects = (int) (totalHorSpacingInnerRect/3.0f);

				int rectHeight = rectWidth;
				int totalVerSpacingInnerRect = totalHorSpacingInnerRect;
				int verSpacingBetweenRects = horSpacingBetweenRects;


				int xCenter = (int) (screenWidth/2.0f);
				int yCenter = (int) (screenHeight/2.0f);

				int rectLeft = (int)(xCenter-rectWidth/2.0f);
				int rectRight = (int)(xCenter+rectWidth/2.0f);
				int rectTop = (int)(yCenter-rectWidth/2.0f);
				int rectBottom = (int)(yCenter+rectWidth/2.0f);

				mBgRect = new RectF(new Rect(rectLeft, rectTop, rectRight,rectBottom));


				int subRectWidth = (int) ((rectWidth - totalHorSpacingInnerRect)/2.0f) ;
				int subRectHeight = (int) ((rectHeight - totalVerSpacingInnerRect)/2.0f) ;

				//Top Left Rect
				int xTopLeftCenter = (int) ((xCenter - rectWidth/2.0f) + (horSpacingBetweenRects + subRectWidth/2.0f));
				int yTopLeftCenter = (int) ((yCenter - rectHeight/2.0f) +  (verSpacingBetweenRects + subRectHeight/2.0f));

				int xTopLeft = (int) (xTopLeftCenter - subRectWidth/2.0f);
				int yTopLeft = (int) (yTopLeftCenter - subRectHeight/2.0f);

				mFgTopLeftRect = new RectF(new Rect(xTopLeft, yTopLeft, xTopLeft+subRectWidth, yTopLeft+subRectHeight));

				//Top Right Rect
				int xTopRightCenter = (int) ((xCenter + horSpacingBetweenRects/2.0f + subRectWidth/2.0f));
				int yTopRightCenter = (int) ((yCenter - rectHeight/2.0f) +  (verSpacingBetweenRects + subRectHeight/2.0f));

				int xTopRight = (int) (xTopRightCenter - subRectWidth/2.0f);
				int yTopRight = (int) (yTopRightCenter - subRectHeight/2.0f);

				mFgTopRightRect = new RectF(new Rect(xTopRight, yTopRight, xTopRight+subRectWidth, yTopRight+subRectHeight));


				//Bottom Left Bottom
				int xBottomLeftCenter = (int) (rectLeft + horSpacingBetweenRects + subRectWidth/2.0f);
				int yBottomLeftCenter = (int) (rectBottom - verSpacingBetweenRects - subRectHeight/2.0f);

				int xBottomLeft = (int) (xBottomLeftCenter - subRectWidth/2.0f);
				int yBottomLeft = (int) (yBottomLeftCenter - subRectHeight/2.0f);

				mFgBottomLeftRect = new RectF(new Rect(xBottomLeft, yBottomLeft, xBottomLeft+subRectWidth, yBottomLeft+subRectHeight));

				//Bottom Right Bottom
				int xBottomRightCenter = (int) (rectRight - horSpacingBetweenRects - subRectWidth/2.0f);
				int yBottomRightCenter = (int) (rectBottom - verSpacingBetweenRects - subRectHeight/2.0f);

				int xBottomRight = (int) (xBottomRightCenter - subRectWidth/2.0f);
				int yBottomRight = (int) (yBottomRightCenter - subRectHeight/2.0f);

				mFgBottomRightRect = new RectF(new Rect(xBottomRight, yBottomRight, xBottomRight+subRectWidth, yBottomRight+subRectHeight));

				if(twoSquareMode){
					mFgTopLeftRect = new RectF(new Rect(xTopLeft, yTopLeftCenter, xTopLeft+subRectWidth, yTopLeftCenter+subRectHeight));

					mFgTopRightRect = new RectF(new Rect(xTopRight, yTopRightCenter, xTopRight+subRectWidth, yTopRightCenter+subRectHeight));
				}
				
				//int margin = 5;
				//mTextRect = new Rect(rectLeft, rectBottom + margin, rectRight, rectBottom + margin + verSpacingBetweenRects);
			}
		});
	}

	public void incCurrentRectIndex(){
		mCurrentRectIndex = (mCurrentRectIndex + 1)%NB_RECTS;	
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		//canvas.drawRoundRect(mBgRect, mBgRectBorder, mBgRectBorder, mBgPaint);


		if(!twoSquareMode){
			canvas.drawRoundRect(mFgTopLeftRect, mBgRectBorder, mBgRectBorder, (mCurrentRectIndex == 0)?mFgSelectedPaint:mFgUnselectedPaint);
			canvas.drawRoundRect(mFgTopRightRect, mBgRectBorder, mBgRectBorder, (mCurrentRectIndex == 1)?mFgSelectedPaint:mFgUnselectedPaint);
			canvas.drawRoundRect(mFgBottomLeftRect, mBgRectBorder, mBgRectBorder, (mCurrentRectIndex == 3)?mFgSelectedPaint:mFgUnselectedPaint);
			canvas.drawRoundRect(mFgBottomRightRect, mBgRectBorder, mBgRectBorder, (mCurrentRectIndex == 2)?mFgSelectedPaint:mFgUnselectedPaint);
		}else{
			canvas.drawRoundRect(mFgTopLeftRect, mBgRectBorder, mBgRectBorder, (mCurrentRectIndex %2 == 0)?mFgSelectedPaint:mFgUnselectedPaint);
			canvas.drawRoundRect(mFgTopRightRect, mBgRectBorder, mBgRectBorder, (mCurrentRectIndex %2 == 1)?mFgSelectedPaint:mFgUnselectedPaint);
		}


	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mDialog != null)mDialog.dismiss();

		return super.onTouchEvent(event);
	}

	public int getCurrentRectIndex() {
		return mCurrentRectIndex;
	}

	public void setCurrentRectIndex(int mCurrentRectIndex) {
		this.mCurrentRectIndex = mCurrentRectIndex;
	}

	private FRDialog mDialog;

	public void setDialog(FRDialog mDialog) {
		this.mDialog = mDialog; 
	}

	private boolean twoSquareMode;

	public void setTwoSquareMode(boolean twoSquareMode){
		this.twoSquareMode = twoSquareMode;
	}
}