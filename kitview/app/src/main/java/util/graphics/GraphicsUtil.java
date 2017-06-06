package util.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class GraphicsUtil {

	public static void drawTextInRect(Paint m_paintText,Canvas paramCanvas, Rect rect, String txt, boolean rotate){
		Rect bounds = new Rect();

		if(m_paintText != null && txt != "" && txt != null)m_paintText.getTextBounds(txt, 0, (txt != null && txt != "")?txt.length():0, bounds);

		int xPos = 0,yPos = 0;

		if(rotate){
			paramCanvas.save();

			xPos = (int) ((rect.left + rect.width()/2.0f) - bounds.width()/2.0f);
			yPos = (int) ((((rect.bottom)-(rect.height())/2.0f))  + Math.abs(m_paintText.ascent() / 2.0f) - Math.abs(m_paintText.descent() / 2.0f) );
			int xC = (int) ((int) (rect.left) + rect.width()/2);
			int yC = (int) ((int) (rect.top) + rect.height()/2);

			paramCanvas.rotate(-90, xC, yC);			

			if(m_paintText != null && txt != "" && txt != null)paramCanvas.drawText(txt, xPos, yPos, m_paintText);

			paramCanvas.restore();
		}else{	
			if(m_paintText != null && rect != null && bounds != null){

				xPos = (int) ((rect.left + rect.width()/2.0f) - bounds.width()/2.0f);
				yPos = (int) ((((rect.bottom)-(rect.height())/2.0f))  + Math.abs(m_paintText.ascent() / 2.0f) - Math.abs(m_paintText.descent() / 2.0f) );

				if(paramCanvas != null && txt != "" && txt != null)paramCanvas.drawText(txt, xPos, yPos, m_paintText);
			}
		}		
	}
}