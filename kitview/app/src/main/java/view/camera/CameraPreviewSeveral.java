package view.camera;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.dentalcrm.kitview.R;

import model.PersistenceManager;
import model.rest.Scenario;
import model.rest.ScenarioItem;
import util.image.ImageUtil;
import view.popup.GenericPopupManager;
import activity.ScenariosActivity;
import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CameraPreviewSeveral extends SurfaceView implements SurfaceHolder.Callback{
	private final static int LINE_TOLERANCE_THRESHOLD = 40; 
	private final static int CLIC_TOLERANCE_THRESHOLD = 240; 

	private final static int LONG_PRESS_MIN = 1000;//800;
	private boolean outOfArea = false;

	private Date mNow, mLastAnchor,mLastNotAnchor;

	private SurfaceHolder mHolder;
	private Camera mCamera;

	private int nbLineHor,nbLineVer;
	private int mCircleRadius = 15;
	private int mStrokeWidth = 2, mSelectedStrokeWidth = 10;

	private ArrayList<Float> mListHorCoeff,mListVerCoeff;

	private ScenariosActivity mContext;

	private ScenarioItem scenarioItem;

	public CameraPreviewSeveral(ScenariosActivity context, Camera camera) {
		super(context);

		mCamera = camera;

		this.mContext = context;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mLinePaint = new Paint();
		mLinePaint.setStrokeWidth(mStrokeWidth);
		mLinePaint.setColor(context.getResources().getColor(R.color.white_transparent));//Color.WHITE);

		mLineSelectedPaint = new Paint();
		mLineSelectedPaint.setStrokeWidth(mSelectedStrokeWidth);
		mLineSelectedPaint.setColor(context.getResources().getColor(R.color.white_transparent));//Color.WHITE);

		mCirclePaint = new Paint();
		mCirclePaint.setStyle(Style.FILL);
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(Color.argb(128, 255, 255, 255));

		ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(context);

		if(screenDimensions != null && screenDimensions.size() == 2){
			mScreenWidth = screenDimensions.get(0);
			mScreenHeight = screenDimensions.get(1);
		}

		this.pm = new GenericPopupManager(mContext);

		if(this.pm != null){
			this.pm.initializePopup();

			this.pm.initializeLayoutReinitPopup(new GenericPopupManager.IClick() {
				@Override
				public void onValidateClick() {
					scenarios = PersistenceManager.getInstance().getScenarios();
					int nbScenarios = (scenarios != null)?scenarios.size():0;

					if(nbScenarios > 0){
						int scenarioIndex = PersistenceManager.getInstance().getScenarioIndex();

						if(scenarioIndex >= 0 && scenarioIndex < nbScenarios){
							Scenario currentScenario = scenarios.get(scenarioIndex);

							if(currentScenario != null && mContext != null){
								int pictureIndex = mContext.getPictureIndex();

								if(pictureIndex >= 0 && pictureIndex < currentScenario.getNbScenarioItems()){
									scenarioItem = currentScenario.getScenarioItemAt(pictureIndex); 

									if(scenarioItem != null){
										mListHorCoeff = new ArrayList<Float>(scenarioItem.getHorizontalCoeffs());
										mListVerCoeff = new ArrayList<Float>(scenarioItem.getVerticalCoeffs());

										mVisibleHorLines = new ArrayList<Boolean>(scenarioItem.getVisibleHorizontalLines());
										int nbVisibleHorLines = (mVisibleHorLines != null)?mVisibleHorLines.size():0;

										for(int i=0;i<nbVisibleHorLines;i++){
											mVisibleHorLines.set(i, true);
										}

										mVisibleVerLines = new ArrayList<Boolean>(scenarioItem.getVisibleVerticalLines());

										int nbVisibleVerLines = (mVisibleVerLines != null)?mVisibleVerLines.size():0;

										for(int i=0;i<nbVisibleVerLines;i++){
											mVisibleVerLines.set(i, true);
										}

										scenarioItem.setVisibleHorizontalLines(mVisibleHorLines);
										scenarioItem.setVisibleVerticalLines(mVisibleVerLines);

										if(PersistenceManager.getInstance() != null)PersistenceManager.getInstance().setScenarios(scenarios);

										invalidate();
									}
								}
							}
						}
					}
				}
				@Override
				public void onCancelClick() {}
			});
		}
	}

	private ArrayList<Boolean> mVisibleHorLines,mVisibleVerLines;

	private List<Scenario> scenarios;

	public void updateLayout(){
		scenarios = PersistenceManager.getInstance().getScenarios();
		int nbScenarios = (scenarios != null)?scenarios.size():0;
		int scenarioIndex = PersistenceManager.getInstance().getScenarioIndex();

		if(scenarioIndex >= 0 && scenarioIndex < nbScenarios){

			Scenario currentScenario = scenarios.get(scenarioIndex);

			if(currentScenario != null){
				int pictureIndex = mContext.getPictureIndex();

				if(pictureIndex >= 0 && pictureIndex < currentScenario.getNbScenarioItems()){
					scenarioItem = currentScenario.getScenarioItemAt(pictureIndex); 

					if(scenarioItem != null){
						mListHorCoeff = new ArrayList<Float>(scenarioItem.getHorizontalCoeffs());
						mListVerCoeff = new ArrayList<Float>(scenarioItem.getVerticalCoeffs());

						mVisibleHorLines = new ArrayList<Boolean>(scenarioItem.getVisibleHorizontalLines());
						mVisibleVerLines = new ArrayList<Boolean>(scenarioItem.getVisibleVerticalLines());

						invalidate();
					}
				}
			}
		}
	}

	private void deleteSelectedLine(){
		if(mAnchorIndexPressedBackup != -1){
			if(mAnchorTypeSelectedBackup.equals("hor")){
				if(mVisibleHorLines != null && mAnchorIndexPressedBackup >= 0 && mAnchorIndexPressedBackup < mVisibleHorLines.size())mVisibleHorLines.set(mAnchorIndexPressedBackup,false);
				invalidate();
			}else if(mAnchorTypeSelectedBackup.equals("ver")){
				if(mVisibleVerLines != null && mAnchorIndexPressedBackup >= 0 && mAnchorIndexPressedBackup < mVisibleVerLines.size())mVisibleVerLines.set(mAnchorIndexPressedBackup,false);
				invalidate();
			}

			if(scenarioItem != null){
				scenarioItem.setVisibleHorizontalLines(mVisibleHorLines);
				scenarioItem.setVisibleVerticalLines(mVisibleVerLines);
			}

			if(PersistenceManager.getInstance() != null)PersistenceManager.getInstance().setScenarios(scenarios);
		}
	}

	private int mScreenWidth,mScreenHeight;
	private Paint mLinePaint, mLineSelectedPaint, mCirclePaint;

	private int mAnchorIndexPressed = -1, mAnchorIndexPressedBackup = -1;
	private String mAnchorTypeSelected = "", mAnchorTypeSelectedBackup = "";

	private GenericPopupManager pm;

	private boolean hasClickedOnLine(float x, float y, int lineIndex, String lineOrientation){
		boolean hasClicked = false;

		if(mContext != null && mContext.getMode() == ScenariosActivity.MODE_SCENARIO){

			float xLine = -1;

			int nbHorLines = (mListHorCoeff != null)?mListHorCoeff.size():0;
			int nbVerLines = (mListVerCoeff != null)?mListVerCoeff.size():0;

			if(lineIndex >= 0 && lineIndex < nbHorLines){
				xLine = mListHorCoeff.get(lineIndex)*mScreenWidth;
			}else{
				xLine = -1;
			}

			float yLine = -1;

			if(lineIndex >= 0 && lineIndex < nbVerLines){
				yLine = mListVerCoeff.get(lineIndex)*mScreenHeight;
			}else{
				yLine = -1;
			}

			if(lineOrientation == "ver"){
				if(yLine != -1 && mVisibleVerLines != null && lineIndex >= 0 && lineIndex < mVisibleVerLines.size()){
					hasClicked = (Math.abs(y - yLine)<= LINE_TOLERANCE_THRESHOLD) && mVisibleVerLines.get(lineIndex);
				}
			}else if(lineOrientation == "hor"){		
				if(xLine != -1 && mVisibleHorLines != null && lineIndex >= 0 && lineIndex < mVisibleHorLines.size()){
					hasClicked = (Math.abs(x - xLine)<= LINE_TOLERANCE_THRESHOLD) && mVisibleHorLines.get(lineIndex);
				}
			}

		}

		return hasClicked;
	}

	private static  final int FOCUS_AREA_SIZE = 100;

	private void focusOnTouch(MotionEvent event){
		if (mCamera != null ) {
			mCamera.cancelAutoFocus();
			Camera.Parameters parameters = mCamera.getParameters();
			if (parameters != null && parameters.getMaxNumMeteringAreas() > 0){
				Rect rect = calculateFocusArea(event.getX(), event.getY());

				List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
				meteringAreas.add(new Camera.Area(rect, 1000));
				parameters.setFocusAreas(meteringAreas);

				try{
					mCamera.setParameters(parameters);
				}catch(Exception e){
					e.printStackTrace();
				}
				mCamera.autoFocus(null);
			}else{
				mCamera.autoFocus(null);
			}
		}
	}

	private Rect calculateFocusArea(float x, float y) {
		int left = clamp(Float.valueOf((x / getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
		int top = clamp(Float.valueOf((y / getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

		return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
	}

	private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
		int result;
		if (Math.abs(touchCoordinateInCameraReper)+focusAreaSize/2>1000){
			if (touchCoordinateInCameraReper>0){
				result = 1000 - focusAreaSize/2;
			} else {
				result = -1000 + focusAreaSize/2;
			}
		} else{
			result = touchCoordinateInCameraReper - focusAreaSize/2;
		}
		return result;
	}

	private float mDownX,mDownY;
	private Date mDownTime;
	private boolean mLayoutReinitDialogDisplayed = false;

	private boolean mHasClickOnCircle = false;

	@Override
	public boolean onTouchEvent(MotionEvent event){
		mNow = new Date();

		if(event != null){
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				mDownX = event.getX();
				mDownY = event.getY();

				mDownTime = (Date) mNow.clone();

				String anchorSelectedData = pressOnLineIndex(mDownX, mDownY);

				mAnchorIndexPressed = -1;
				mAnchorTypeSelected = "";

				//Click on circle
				if(clickOnCircle(mDownX, cx, mDownY, cy, PARENT_CIRCLE_RADIUS)){
					mHasClickOnCircle = true;
					mContext.takePicture();
				}else if(anchorSelectedData != "" && anchorSelectedData != null){
					StringTokenizer st = new StringTokenizer(anchorSelectedData, ";");

					if(st != null && st.countTokens() == 2){
						try{
							int index = Integer.parseInt(st.nextToken());
							String list = st.nextToken();

							if(index != -1){
								if(list != null){
									if(list.equals("hor")){
										mAnchorIndexPressed = index;
										mAnchorTypeSelected = "hor";
										mLastAnchor = (Date) mNow.clone();
									}else if(list.equals("ver")){
										mAnchorIndexPressed = index;
										mAnchorTypeSelected = "ver";
										mLastAnchor = (Date) mNow.clone();
									}
								}
							}else{
								mLastNotAnchor = (Date) mNow.clone(); 
							}
						}catch(NumberFormatException e){
							if(e != null)e.printStackTrace();
							mLastNotAnchor = (Date) mNow.clone();
						}
					}else{
						mLastNotAnchor = (Date) mNow.clone();
					}
				}else{
					mLastNotAnchor = (Date) mNow.clone();
				}
			}else if(event.getAction() == MotionEvent.ACTION_UP){
				//Clic sur une ligne
				if(mAnchorIndexPressed != -1 && mAnchorTypeSelected != ""){

					//Clic prolonge dans le vide
				}else if(mLayoutReinitDialogDisplayed){
					//Touch to focus
				}else{
					//Clic simple mContext.takePicture();
					if(!mHasClickOnCircle && !mContext.isPictureTaken())focusOnTouch(event);
				}

				mHasClickOnCircle = false;

				mAnchorIndexPressedBackup = mAnchorIndexPressed;
				mAnchorTypeSelectedBackup = mAnchorTypeSelected;

				mAnchorIndexPressed = -1;
				mAnchorTypeSelected = "";

				mLayoutReinitDialogDisplayed = false;

				outOfArea = false;

				invalidate();
			}else if(event.getAction() == MotionEvent.ACTION_MOVE){
				//Clic sur une ligne
				if(mAnchorIndexPressed != -1){
					if(Math.abs(mDownX-event.getX()) > LINE_TOLERANCE_THRESHOLD ||
							Math.abs(mDownY-event.getY()) > LINE_TOLERANCE_THRESHOLD)outOfArea = true;

					if(Math.abs(mNow.getTime()-mDownTime.getTime()) >= LONG_PRESS_MIN &&
							!outOfArea){

						//==> sans trop bouger la ligne
						if(pm != null)pm.showPopup(getResources().getString(R.string.confirm_before_deletion),getResources().getString(R.string.confirm_before_deletion_sure_to_delete),new GenericPopupManager.IClick() {
							@Override
							public void onValidateClick() {
								deleteSelectedLine();
							}

							@Override
							public void onCancelClick() {}
						});
					}else{
						if(mAnchorTypeSelected == "hor"){
							float newHorCoeff = (event.getX()/mScreenWidth*1.0f);
							if(mListHorCoeff != null && mAnchorIndexPressed >= 0 && mAnchorIndexPressed < mListHorCoeff.size()){
								mListHorCoeff.set(mAnchorIndexPressed, new Float(newHorCoeff));
							}

							invalidate();
						}else if(mAnchorTypeSelected == "ver"){
							float newVerCoeff = (event.getY()/mScreenHeight*1.0f);
							if(mListVerCoeff != null && mAnchorIndexPressed >= 0 && mAnchorIndexPressed < mListVerCoeff.size()){
								mListVerCoeff.set(mAnchorIndexPressed, new Float(newVerCoeff));
							}
							invalidate();
						}
					}

					if(scenarioItem != null){
						scenarioItem.setHorizontalCoeffs(mListHorCoeff);
						scenarioItem.setVerticalCoeffs(mListVerCoeff);
					}

					if(PersistenceManager.getInstance() != null)PersistenceManager.getInstance().setScenarios(scenarios);

					//Clic sur du vide
				}else{
					if(Math.abs(mNow.getTime()-mDownTime.getTime()) >= LONG_PRESS_MIN &&
							!outOfArea &&	
							Math.abs(mDownX-event.getX()) <= CLIC_TOLERANCE_THRESHOLD &&
							Math.abs(mDownY-event.getY()) <= CLIC_TOLERANCE_THRESHOLD){

						//==> sans trop bouger la ligne
						if(pm != null && mContext.getMode() == ScenariosActivity.MODE_SCENARIO)pm.showPopup(getResources().getString(R.string.confirm_before_reinit),getResources().getString(R.string.confirm_before_reinit_sure_to_reinit),new GenericPopupManager.IClick() {
							@Override
							public void onValidateClick() {
								scenarios = PersistenceManager.getInstance().getScenarios();
								int nbScenarios = (scenarios != null)?scenarios.size():0;

								if(nbScenarios > 0){
									int scenarioIndex = PersistenceManager.getInstance().getScenarioIndex();

									if(scenarioIndex >= 0 && scenarioIndex < nbScenarios){
										Scenario currentScenario = scenarios.get(scenarioIndex);

										if(currentScenario != null && mContext != null){
											int pictureIndex = mContext.getPictureIndex();

											if(pictureIndex >= 0 && pictureIndex < currentScenario.getNbScenarioItems()){
												scenarioItem = currentScenario.getScenarioItemAt(pictureIndex); 

												if(scenarioItem != null){
													mListHorCoeff = new ArrayList<Float>(scenarioItem.getHorizontalCoeffs());
													mListVerCoeff = new ArrayList<Float>(scenarioItem.getVerticalCoeffs());

													mVisibleHorLines = new ArrayList<Boolean>(scenarioItem.getVisibleHorizontalLines());
													int nbVisibleHorLines = (mVisibleHorLines != null)?mVisibleHorLines.size():0;

													for(int i=0;i<nbVisibleHorLines;i++){
														mVisibleHorLines.set(i, true);
													}

													mVisibleVerLines = new ArrayList<Boolean>(scenarioItem.getVisibleVerticalLines());

													int nbVisibleVerLines = (mVisibleVerLines != null)?mVisibleVerLines.size():0;

													for(int i=0;i<nbVisibleVerLines;i++){
														mVisibleVerLines.set(i, true);
													}

													scenarioItem.setVisibleHorizontalLines(mVisibleHorLines);
													scenarioItem.setVisibleVerticalLines(mVisibleVerLines);

													if(PersistenceManager.getInstance() != null)PersistenceManager.getInstance().setScenarios(scenarios);

													invalidate();
												}
											}
										}
									}
								}
							}

							@Override
							public void onCancelClick() {}
						});

						mLayoutReinitDialogDisplayed = true;
					}
				}
			}
		}

		return true;
	}

	public String pressOnLineIndex(float x, float y){
		int index = -1;
		int nb = (mListHorCoeff != null)?mListHorCoeff.size():0;
		int i = 0;
		String list = "";

		while(i < nb && index == -1){
			if(hasClickedOnLine(x, y, i, "hor")){
				index = i;
				list = "hor";
			}

			i++;
		}

		int nb2 = (mListVerCoeff != null)?mListVerCoeff.size():0;
		int j = 0;

		while(j<nb2 && index == -1){
			if(hasClickedOnLine(x, y, j, "ver")){
				index = j;
				list = "ver";
			}

			j++;
		}

		return index+";"+list;
	}

	private  static int MARGIN_TOP,MARGIN_BOTTOM,MARGIN_RIGHT;

	private int PARENT_CIRCLE_RADIUS;
	private int CHILD_CIRCLE_RADIUS;

	private double distance(float x0, float x1, float y0, float y1){
		return Math.sqrt(Math.pow(x1-x0, 2) + Math.pow(y1-y0, 2));
	}

	private boolean clickOnCircle(float xPoint, float yPoint, float xC, float yC, int radius){
		return ( (distance(xPoint, yPoint, xC, yC) <= radius) 
				&& PersistenceManager.getInstance() != null 
				&& PersistenceManager.getInstance().getShutterReleaseButtonIndex() == 0);
	}

	private int cx = 0, cy;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);

		MARGIN_TOP = (mContext != null && mContext.getTitleTextView() != null)?mContext.getTitleTextView().getHeight():0;
		MARGIN_BOTTOM = (mContext != null && mContext.getTitleTextView() != null)?mContext.getCurrentPatientInfosTextView().getHeight():0;
		MARGIN_RIGHT = (int) ((mContext != null && mContext.isRightVerticalListViewShowing())?mContext.getResources().getDimension(R.dimen.image_thumbnail_size):0);

		if(mContext != null && mContext.getMode() == ScenariosActivity.MODE_SCENARIO){
			int nb = (mListHorCoeff != null)?mListHorCoeff.size():0;

			for(int i=0;i<nb;i++){
				boolean selected = (mAnchorTypeSelected != null && mAnchorTypeSelected.equals("hor") && mAnchorIndexPressed == i);

				if(mVisibleHorLines != null && mVisibleHorLines.get(i) && mListHorCoeff != null){
					//vertical line ...

					float coeff = mListHorCoeff.get(i)*mScreenWidth;
					canvas.drawLine(coeff, MARGIN_TOP, coeff, mScreenHeight-MARGIN_BOTTOM, selected?mLineSelectedPaint:mLinePaint);
				}
			}

			int nb2 = (mListVerCoeff != null)?mListVerCoeff.size():0;

			for(int j=0;j<nb2;j++){
				boolean selected = (mAnchorTypeSelected != null && mAnchorTypeSelected.equals("ver") && mAnchorIndexPressed == j);

				if(mVisibleVerLines != null && mVisibleVerLines.get(j) && mListVerCoeff != null){
					//horizontal line ...

					float coeff = mListVerCoeff.get(j)*mScreenHeight;
					canvas.drawLine(0, coeff, mScreenWidth-MARGIN_RIGHT, coeff, selected?mLineSelectedPaint:mLinePaint);
				}
			}
		}

		cy = getHeight()/2;

		PARENT_CIRCLE_RADIUS = getHeight()/4;
		CHILD_CIRCLE_RADIUS = (int) (0.67f * PARENT_CIRCLE_RADIUS);

		if(PersistenceManager.getInstance() != null && PersistenceManager.getInstance().getShutterReleaseButtonIndex() == 0){
			canvas.drawCircle(cx, cy, PARENT_CIRCLE_RADIUS, mCirclePaint);
			canvas.drawCircle(cx, cy, CHILD_CIRCLE_RADIUS, mCirclePaint);
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the preview.
		try {
			if(mCamera != null && holder != null){
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			}
		} catch (IOException e) {
			if(e != null)e.printStackTrace();
		}catch(Exception e){
			if(e != null)e.printStackTrace();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder != null && mHolder.getSurface() == null){
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			if(mCamera != null)mCamera.stopPreview();
		} catch (Exception e){
			// ignore: tried to stop a non-existent preview
			if(e != null)e.printStackTrace();
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			if(mCamera != null && mHolder != null)mCamera.setPreviewDisplay(mHolder);

			if(mContext != null){
				mContext.initializePreviewCallback();
				mContext.initializeFlashMode();
				mContext.initializeFocusMode();
			}

			if(mCamera != null)mCamera.startPreview();
		} catch (Exception e){
			if(e != null)e.printStackTrace();
		}
	}
}