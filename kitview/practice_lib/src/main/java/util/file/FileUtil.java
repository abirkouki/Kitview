package util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.components.scanner.MyMediaScannerConnectionClient;
import util.image.ExifUtil;
import util.location.LocationUtil;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class FileUtil {
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	public static boolean createFile(String fileName){
		File f = new File(fileName);
		boolean hasBeenCreated = false;
		try {
			hasBeenCreated = f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hasBeenCreated;
	}
	
	private static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}

	public static void deleteFolderRecursively(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				deleteFolderRecursively(child);

		fileOrDirectory.delete();
	}

	public static Bitmap loadBitmapFromRawRessource(Context context, int rawFileId, int imageMaxWidth, int imageMaxHeight){
		Bitmap bitmap = null;
		InputStream ins = null;
		try {
			ins = context.getResources().openRawResource(rawFileId);
			Bitmap b = null;
			FileInputStream fis = null;
			int IMAGE_MAX_SIZE = Math.min(imageMaxWidth,imageMaxHeight);

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inPreferredConfig = Config.ARGB_8888;
			o.inJustDecodeBounds = true;
			o.inPurgeable = true;

			BitmapFactory.decodeStream(ins, null, o);
			
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

	public static void createFolder(String folderRelativePath){
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
		File output = new File(mediaStorageDir.getAbsoluteFile()+"/"+folderRelativePath);

		if (! output.exists())output.mkdirs();
	}

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".jpg");
		} else if(type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"VID_"+ timeStamp + ".mp4");
		} else {
			return null;
		}
		return mediaFile;
	}

	public static void refreshPhotoInFileSystem(Context context, final String fileName){
		if(fileName != null && fileName != ""){
			try{
				MyMediaScannerConnectionClient client = new MyMediaScannerConnectionClient(new String[]{fileName},context);//"image/jpeg");
				
				client = null;

			}catch(Exception e){}
		}
	}

	public static void savePicture(final Activity context, byte[] data, final String fileName,boolean isFrontCameraEnabled, boolean refreshFileSystem, boolean savePhotoLocation){
		try {
			if(isFrontCameraEnabled){
				ExifInterface exif =  ExifUtil.createExifInterface(fileName);
				exif.setAttribute(ExifInterface.TAG_ORIENTATION, ""+ExifInterface.ORIENTATION_FLIP_VERTICAL);
				exif.saveAttributes();
			}

			FileOutputStream fos = new FileOutputStream(fileName);
			fos.write(data);
			fos.flush();
			fos.close();
			fos = null;

			if(refreshFileSystem)refreshPhotoInFileSystem(context, fileName);

			if(savePhotoLocation){
				new Thread(new Runnable() {
					@Override
					public void run() {
						LocationUtil.savePhotoLocation(context,fileName,true);
					}
				}).start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void savePictureAsJPEG(final Context context,Bitmap bitmap, final String fileName,int jpegQuality,boolean isFrontCameraEnabled,boolean refreshFileSystem, boolean savePhotoLocation){	
		try {
			if(isFrontCameraEnabled){
				ExifInterface exif =  ExifUtil.createExifInterface(fileName);
				exif.setAttribute(ExifInterface.TAG_ORIENTATION, ""+ExifInterface.ORIENTATION_FLIP_VERTICAL);
				exif.saveAttributes();
			}

			FileOutputStream fos = new FileOutputStream(fileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, fos);
			fos.flush();
			fos.close();
			fos = null;

			if(refreshFileSystem)refreshPhotoInFileSystem(context, fileName);

			if(savePhotoLocation){
				new Thread(new Runnable() {
					@Override
					public void run() {
						LocationUtil.savePhotoLocation(context,fileName,true);
					}
				}).start();
			}

		} catch (Exception e) {	
			e.printStackTrace();
		}
	}

	public static void savePictureAsPNG(Context context,Bitmap bitmap, String fileName,int jpegQuality,boolean isFrontCameraEnabled,boolean refreshFileSystem){	
		try {

			FileOutputStream fos = new FileOutputStream(fileName);
			bitmap.compress(Bitmap.CompressFormat.PNG, jpegQuality, fos);

			fos.flush();
			fos.close();
			fos = null;

			if(refreshFileSystem)refreshPhotoInFileSystem(context, fileName);
		} catch (Exception e) {	
			e.printStackTrace();
		}
	}
}