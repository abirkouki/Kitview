package util.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ImageUtil {
	
	public static Point getDefaultDisplaySize(Activity activity) {
		Point size = new Point();
		Display d = activity.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){//ApiHelper.VERSION_CODES.HONEYCOMB_MR2) {
			d.getSize(size);
		} else {
			size.set(d.getWidth(), d.getHeight());
		}
		return size;
	}
	
	
	public static Bitmap copyBitmap(Bitmap bitmap){
		try{
			if(bitmap != null)return bitmap.copy(bitmap.getConfig(), true);
			else return null;
		}catch(OutOfMemoryError e){
			return null;
		}catch(Exception e){
			return null;
		}
	}

	public static Bitmap resizeBitmap(Bitmap bitmap, int outWidth, int outHeight){
		Bitmap b = null;

		try{
			if(bitmap != null){
				b = Bitmap.createScaledBitmap(bitmap,outWidth,outHeight, true);//auto converted to rgb565
				b = b.copy(Config.ARGB_8888, true);//reconvert to rgb 888;
			}else{
				b = null;
			}
		}catch(OutOfMemoryError e){
			b = null;
			System.gc();
		}catch(Exception e){
			b = null;
			System.gc();
		}

		return b;
	}

	public static Bitmap scaleBitmap(Bitmap b,int imageMaxWidth, int imageMaxHeight){
		int IMAGE_MAX_SIZE = Math.min(imageMaxWidth,imageMaxHeight);
		int scale = 1;

		if (b.getHeight() > IMAGE_MAX_SIZE || b.getWidth() > IMAGE_MAX_SIZE) {
			int pow = (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(b.getHeight(), b.getWidth())) / Math.log(0.5) );
			scale = (int)Math.pow(2, pow);
		}

		int dstWidth = (b.getWidth()/scale);
		int dstHeight = (b.getHeight()/scale);
		return Bitmap.createScaledBitmap(b, dstWidth, dstHeight, true);
	}

	public static Bitmap decodeFile(File f,int imageMaxWidth, int imageMaxHeight){
		Bitmap b = null;
		FileInputStream fis = null;
		int IMAGE_MAX_SIZE = Math.min(imageMaxWidth,imageMaxHeight);

		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inPreferredConfig = Config.ARGB_8888;
			o.inJustDecodeBounds = true;
			o.inPurgeable = true;

			fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();

			int scale = 1;

			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				int pow = (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5) );
				scale = (int)Math.pow(2, pow);
			}

			o = new BitmapFactory.Options();
			o.inPreferredConfig = Config.ARGB_8888;
			o.inPurgeable = true;
			o.inSampleSize = scale;
			o.inMutable = true;

			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o);
			o = null;
			fis.close();
			fis = null;
		} catch (OutOfMemoryError e) {
			if(b != null){
				b.recycle();
				b = null;
			}

			if(fis != null){
				try {
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			System.gc();
		}catch(Exception e){
			b = null;

			if(fis != null){
				try {
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			System.gc();
		}

		return b;
	}

	public static ArrayList<Integer> getScreenDimensions(Context context){
		ArrayList<Integer> dimensions = new ArrayList<Integer>();

		if(context != null){
			DisplayMetrics metrics = new DisplayMetrics();
			WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			windowManager.getDefaultDisplay().getMetrics(metrics);

			dimensions.add(Integer.valueOf(metrics.widthPixels));
			dimensions.add(Integer.valueOf(metrics.heightPixels));
		}

		return dimensions;
	}

	public static Bitmap loadBitmapFromRawRessource(Context c, String packageName, int internRawFileId, String externRawFilePath, int imageMaxWidth, int imageMaxHeight, boolean launchFromService){
		Bitmap bitmap = null;
		Context context = null;
		InputStream ins = null;

		try {
			context = (packageName != c.getPackageName() )?c.createPackageContext(packageName,Context.CONTEXT_IGNORE_SECURITY):c;
			ins = (packageName != c.getPackageName() && !launchFromService)?context.getAssets().open(externRawFilePath):context.getResources().openRawResource(internRawFileId);
			int scale = 1;
			BitmapFactory.Options o = new BitmapFactory.Options();

			if(imageMaxWidth != -1 && imageMaxHeight != -1){
				int IMAGE_MAX_SIZE = Math.min(imageMaxWidth,imageMaxHeight);

				o.inPreferredConfig = Config.ARGB_8888;
				o.inJustDecodeBounds = true;
				o.inPurgeable = true;

				BitmapFactory.decodeStream(ins, null, o);

				if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
					scale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / 
							(double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
				}
			}

			o = new BitmapFactory.Options();
			o.inPreferredConfig = Config.ARGB_8888;
			o.inPurgeable = true;
			o.inSampleSize = scale;

			bitmap = BitmapFactory.decodeStream(ins, null, o);
		}catch (OutOfMemoryError e) {
			e.printStackTrace();
			bitmap = null;
		}catch(Exception e){
			e.printStackTrace();
			bitmap = null;
		}
		return bitmap;
	}
}