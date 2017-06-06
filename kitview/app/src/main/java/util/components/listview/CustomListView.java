package util.components.listview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ListView;

public class CustomListView extends ListView {
	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {




	//	canvas.save();
		
		//	canvas.rotate(10);



		super.onDraw(canvas);

		//	canvas.restore();


	}
	
	private float degrees,toDegrees;

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		// TODO Auto-generated method stub

		//canvas.save();

		//canvas.rotate(90);
		
		//child.setRotation(toDegrees);
		
		
		/*RotateAnimation ra = new RotateAnimation(0, toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//degrees, degrees+90);//toDegrees);

		ra.setDuration(5000);
		ra.setFillAfter(true);

		child.startAnimation(ra);*/
		
		
		boolean res = super.drawChild(canvas, child, drawingTime);

		
		return res;

	}


	/*public void rotateChilds(float degrees, float toDegrees){
		this.degrees = degrees;
		this.toDegrees = toDegrees;
		requestLayout();
		
		
	}*/
	
}
