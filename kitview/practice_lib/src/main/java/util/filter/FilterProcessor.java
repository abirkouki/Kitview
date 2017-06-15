package util.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.v8.renderscript.*;

public class FilterProcessor{
	public static Bitmap blurFilter(Context context,Bitmap sendBitmap){
		Bitmap sendBitmap2 = null;
		
		 RenderScript rs = null;
		 Allocation input = null;
		 Allocation output = null;
		 ScriptIntrinsicBlur script = null;
		 int [] pixels = null;
		 
		try{
			int w = sendBitmap.getWidth();
			int h = sendBitmap.getHeight();

			int hOffset = (int) (h*0.27f);
			int heightOffset = (int) (h*0.3f);
			int ySupp = (hOffset+heightOffset);

			pixels = new int[heightOffset*w];

			sendBitmap.getPixels(pixels,0, w, 0, hOffset, w, heightOffset);

			sendBitmap2 = Bitmap.createBitmap(pixels, w, heightOffset, Config.ARGB_8888);

			rs = RenderScript.create( context );
			input = Allocation.createFromBitmap( rs, sendBitmap2, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT );
			output = Allocation.createTyped( rs, input.getType() );
			script = ScriptIntrinsicBlur.create( rs, Element.U8_4( rs ) );

			script.setRadius(25.f);
			script.setInput( input );
			script.forEach( output );
			output.copyTo( sendBitmap2);

			for(int y = hOffset;y<ySupp;y++){
				for(int x = 0;x<w;x++){
					sendBitmap.setPixel(x, y, sendBitmap2.getPixel(x, y-hOffset));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(sendBitmap2 != null){
			sendBitmap2.recycle();
			sendBitmap2 = null;
		}
		
		 if(rs != null)rs.destroy();
		 if(input != null)input.destroy();
		 if(output != null)output.destroy();
		 if(script != null) script.destroy();
		 
		 pixels = null;
		 
	
		
		return sendBitmap;
	}
}