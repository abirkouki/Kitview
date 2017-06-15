package view.puzzle;

import java.util.ArrayList;
import java.util.Date;
import util.file.FileUtil;
import util.image.ImageUtil;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class PuzzleGUI extends View {
	private static Rect background, rect1, rect2, rect3, rect4, rect5, rect6;
	private static Paint p = new Paint();
	private static int rectColor = Color.argb(255, 204, 204, 204);
	private final static int DELAY_BEFORE_DRAG_AND_DROP = 300;
	private final static int DONT_MOVE_DRAGANDDROP_DIST_MAX = 70;

	private long start = -1;
	private long now;

	private static boolean dragAndDropStarted;

	private boolean hasClickedOnValidArea = false;

	private int startX,startY;
	private static int x,y;

	private static int indexRectSrc = -1;
	private static int indexRectDest = -1;

	private ArrayList<Bitmap> bitmaps;

	private Context context;
	
	private int side = 5;

	public PuzzleGUI(Context context) {
		super(context);

		initialize(context);
	}

	public PuzzleGUI(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public PuzzleGUI(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}
	private static Bitmap b = null;

	private void initialize(Context context){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		}

		this.context = context;

		//File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "comparateur");
		//String p = mediaStorageDir.getAbsolutePath()+"/intrabucal/face/a.jpg";
		
		ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(context);
		int maxWidth = (int) (screenDimensions.get(0).intValue() );//*0.8);
		int maxHeight = (int) (screenDimensions.get(1).intValue() );//*0.8);
		
		b = FileUtil.loadBitmapFromRawRessource(context, com.dentalcrm.kitview.R.raw.puzzle, maxWidth, maxHeight);
		

		//ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(context);
		//int screenWidth = screenDimensions.get(0).intValue();
		//int screenHeight = screenDimensions.get(1).intValue();
		//b = ImageUtil.decodeFile(new File(p), screenWidth, screenHeight);

		int[]pixels = new int[b.getWidth()/2*b.getHeight()/2];
		b.getPixels(pixels, 0, b.getWidth()/2, 0, 0, b.getWidth()/2, b.getHeight()/2);
		Bitmap b1 = addWhiteBorder(Bitmap.createBitmap(pixels, b.getWidth()/2, b.getHeight()/2, Config.ARGB_8888),1);

		int[]pixels2 = new int[b.getWidth()/2*b.getHeight()/2];
		b.getPixels(pixels2, 0, b.getWidth()/2, b.getWidth()/2, 0, b.getWidth()/2, b.getHeight()/2);
		Bitmap b2 = addWhiteBorder(Bitmap.createBitmap(pixels2, b.getWidth()/2, b.getHeight()/2, Config.ARGB_8888),1);

		int[]pixels3 = new int[b.getWidth()/2*b.getHeight()/2];
		b.getPixels(pixels3, 0, b.getWidth()/2, 0, b.getHeight()/2, b.getWidth()/2, b.getHeight()/2);
		Bitmap b3 = addWhiteBorder(Bitmap.createBitmap(pixels3, b.getWidth()/2, b.getHeight()/2, Config.ARGB_8888),1);

		int[]pixels4 = new int[b.getWidth()/2*b.getHeight()/2];
		b.getPixels(pixels4, 0, b.getWidth()/2, b.getWidth()/2, b.getHeight()/2, b.getWidth()/2, b.getHeight()/2);
		Bitmap b4 = addWhiteBorder(Bitmap.createBitmap(pixels4, b.getWidth()/2, b.getHeight()/2, Config.ARGB_8888),1);

		bitmaps = new ArrayList<Bitmap>();

		int nb = 4;

		while(nb > 0){	
			int rand = (int) (Math.random()*4.0d);
			if(rand == 0 && !bitmaps.contains(b1)){
				bitmaps.add(b1);
				nb--;
			}else if(rand == 1 && !bitmaps.contains(b2)){
				bitmaps.add(b2);
				nb--;
			}else if(rand == 2 && !bitmaps.contains(b3)){
				bitmaps.add(b3);
				nb--;
			}else if(rand == 3 && !bitmaps.contains(b4)){
				bitmaps.add(b4);
				nb--;
			}
		}
	}

	public boolean dontMoveDragAndDrop(int startX, int startY, int nowX, int nowY){
		return Math.sqrt(Math.pow( ( startX- nowX), 2) + Math.pow( ( startY- nowY) , 2)) <= DONT_MOVE_DRAGANDDROP_DIST_MAX;
	}

	private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
		Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
		Canvas canvas = new Canvas(bmpWithBorder);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bmp, borderSize, borderSize, null);
		return bmpWithBorder;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		x = (int) event.getX();
		y = (int) event.getY();

		if(event.getAction() == MotionEvent.ACTION_DOWN){

			if(rect1 != null && rect1.contains(x, y)){
				indexRectSrc = 0;
				hasClickedOnValidArea = true;
			}else if(rect2 != null && rect2.contains(x, y)){
				indexRectSrc = 1;
				hasClickedOnValidArea = true;
			}else if(rect3 != null && rect3.contains(x, y)){
				indexRectSrc = 2;
				hasClickedOnValidArea = true;
			}else if(rect4 != null && rect4.contains(x, y)){
				indexRectSrc = 3;
				hasClickedOnValidArea = true;
			}else if(rect5 != null && rect5.contains(x, y)){
				indexRectSrc = 4;
				hasClickedOnValidArea = true;
			}else if(rect6 != null && rect6.contains(x, y)){
				indexRectSrc = 5;
				hasClickedOnValidArea = true;
			}else indexRectSrc = -1;

			if(hasClickedOnValidArea){
				start = new Date().getTime();
				startX = x;
				startY = y;
			}
		}else if(event.getAction() == MotionEvent.ACTION_MOVE){
			now = new Date().getTime();

			if(!dragAndDropStarted && hasClickedOnValidArea && startX != -1 && startY != -1 && dontMoveDragAndDrop(startX, startY, x, y) 
					&& ( Math.abs(now - start) >=  DELAY_BEFORE_DRAG_AND_DROP ) ){

				dragAndDropStarted = true;

				Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(100);

			}else if(hasClickedOnValidArea && !dragAndDropStarted){
				Rect dirtyRectToInvalidate = null;

				switch (indexRectSrc) {
				case 0:
					dirtyRectToInvalidate = rect1;
					break;
				case 1:
					dirtyRectToInvalidate = rect2;
					break;
				case 2:
					dirtyRectToInvalidate = rect3;
					break;
				case 3:
					dirtyRectToInvalidate = rect4;
					break;
				case 4:
					dirtyRectToInvalidate = rect5;
					break;
				case 5:
					dirtyRectToInvalidate = rect6;
					break;
				}

				startX = x;
				startY = y;

				if(dirtyRectToInvalidate != null){
					invalidate(dirtyRectToInvalidate);
				}else invalidate();
			}

			if(dragAndDropStarted){
				invalidate();
			}
		}else if(event.getAction() == MotionEvent.ACTION_UP){
			if(dragAndDropStarted){
				//Use this var to test if user release cursor located in good picture area (dest)
				hasClickedOnValidArea = false;

				if(rect1 != null && rect1.contains(x, y)){
					indexRectDest = 0;
					hasClickedOnValidArea = true;
				}else if(rect2 != null && rect2.contains(x, y)){
					indexRectDest = 1;
					hasClickedOnValidArea = true;
				}else if(rect3 != null && rect3.contains(x, y)){
					indexRectDest = 2;
					hasClickedOnValidArea = true;
				}else if(rect4 != null && rect4.contains(x, y)){
					indexRectDest = 3;
					hasClickedOnValidArea = true;
				}else if(rect5 != null && rect5.contains(x, y)){
					indexRectDest = 4;
					hasClickedOnValidArea = true;
				}else if(rect6 != null && rect6.contains(x, y)){
					indexRectDest = 5;
					hasClickedOnValidArea = true;
				}else indexRectDest = -1;
				if(hasClickedOnValidArea && indexRectSrc != -1 && indexRectDest != -1){
					Bitmap srcPic = bitmaps.get(indexRectSrc);
					Bitmap destPic = ImageUtil.copyBitmap(bitmaps.get(indexRectDest));

					bitmaps.set(indexRectDest, srcPic);
					bitmaps.set(indexRectSrc, destPic);

					invalidate();
				}

				//Clic standard ==> open menu
			}else{
				hasClickedOnValidArea = false;

				if(rect1 != null && rect1.contains(x, y)){
					hasClickedOnValidArea = true;
				}else if(rect2 != null && rect2.contains(x, y)){
					hasClickedOnValidArea = true;
				}else if(rect3 != null && rect3.contains(x, y)){
					hasClickedOnValidArea = true;
				}else if(rect4 != null && rect4.contains(x, y)){
					hasClickedOnValidArea = true;
				}else if(rect5 != null && rect5.contains(x, y)){
					hasClickedOnValidArea = true;
				}else if(rect6 != null && rect6.contains(x, y)){
					hasClickedOnValidArea = true;
				}

				if(hasClickedOnValidArea)hasClickedOnValidArea = false;
			}

			start = -1;
			now = -1;
			dragAndDropStarted = false;
			hasClickedOnValidArea = false;
			indexRectDest = -1;
			indexRectSrc = -1;
			startX = -1;
			startY = -1;
			invalidate();
		}
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int newWidth = MeasureSpec.makeMeasureSpec((int) (MeasureSpec.getSize(widthMeasureSpec)*1),MeasureSpec.EXACTLY);
		int newHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec),MeasureSpec.EXACTLY);

		super.onMeasure(newWidth, newHeight);
	}

	private static Rect [] tmpRects;

	private static int currentGap  = 0; 

	public static Bitmap processGui(Context context, Canvas canvas, int canvasNewWidth, int canvasNewHeight, ArrayList<Bitmap> bitmaps){//,currentLayout,currentRatio,scaling,currentGap, currentBorder,dx_array, dy_array, color,bgDrawableId,shouldDecodeBitmaps,true,mCustomRatioApplied);
		Bitmap bitmapAssociatedToCanvas = null;

		p.setDither(false);
		p.setAntiAlias(false);
		p.setFilterBitmap(false);

		background =  new Rect(0, 0, canvasNewWidth, canvasNewHeight);

		p.setColor(Color.WHITE);	

		rect1 = rect2 = rect3 = rect4 = rect5 = rect6 = null;

		tmpRects = null;

		int mosaicGuiWidth = canvasNewWidth;
		int mosaicGuiHeight = canvasNewHeight;

		int gapTopBottom = (mosaicGuiHeight - b.getHeight())/2;
		int gapLeftRight = (mosaicGuiWidth - b.getWidth())/2;

		int nbRects = 4;
		int rectWidth = bitmaps.get(0).getWidth();
		int rectHeight = bitmaps.get(0).getHeight();

		rect1 = new Rect(gapLeftRight+currentGap, gapTopBottom+currentGap, gapLeftRight+currentGap+rectWidth, gapTopBottom+currentGap+rectHeight);
		rect2 = new Rect(rect1.right+currentGap, gapTopBottom+currentGap, rect1.right+currentGap+rectWidth, gapTopBottom+currentGap+rectHeight);
		rect3 = new Rect(gapLeftRight+currentGap, rect1.bottom+currentGap, gapLeftRight+currentGap+rectWidth, rect1.bottom+currentGap+rectHeight);
		rect4 = new Rect(rect3.right+currentGap, rect3.top, rect3.right+currentGap+rectWidth, rect3.top+currentGap+rectHeight);

		tmpRects = new Rect[nbRects];	
		tmpRects[0] = rect1;
		tmpRects[1] = rect2;
		tmpRects[2] = rect3;
		tmpRects[3] = rect4;

		background =  new Rect(gapLeftRight, gapTopBottom, gapLeftRight+canvasNewWidth, gapTopBottom+canvasNewHeight);

		canvas.drawRect(background, p);

		p.setColor(rectColor);

		Bitmap bitmap = null;

		int i = 0;

		while(i < 4){
			bitmap = bitmaps.get(i);

			if(bitmap != null){
				int marginLeftRight = 0;
				int marginTopBottom = 0;

				if( (!dragAndDropStarted || (dragAndDropStarted && i != indexRectSrc) )){
					canvas.drawBitmap(bitmap, marginLeftRight+tmpRects[i].left, marginTopBottom+tmpRects[i].top, p);
				}
			}
			i++;
		}

		if(dragAndDropStarted){
			Bitmap picSelected = bitmaps.get(indexRectSrc);

			if(picSelected != null){
				if(tmpRects[indexRectSrc] != null && tmpRects[indexRectSrc] != null){
					picSelected = ImageUtil.resizeBitmap(picSelected, picSelected.getWidth(),picSelected.getHeight());

					if(picSelected != null){
						int left = x - picSelected.getWidth()/2;
						int top = y - picSelected.getHeight()/2;
						canvas.drawBitmap(picSelected, left, top, null);
					}
				}
			}

		}
		return bitmapAssociatedToCanvas;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), 0, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);

		super.onDraw(canvas);

		canvas.restore();

		processGui(this.getContext(),canvas,getWidth(), getHeight(),bitmaps);
	}
}