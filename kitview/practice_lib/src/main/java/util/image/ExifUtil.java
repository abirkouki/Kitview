package util.image;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.dentalcrm.kitview.R;

import android.media.ExifInterface;

public class ExifUtil {
	public static ExifInterface createExifInterface(String picturePath){
		ExifInterface exifInterface = null;
		int dotPosition= picturePath.lastIndexOf(".");

		if(dotPosition != -1){
			String ext = picturePath.substring(dotPosition + 1, picturePath.length());
			if (ext.equalsIgnoreCase("jpg")){
				try {
					exifInterface = new ExifInterface(picturePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return exifInterface;
	}

	//Degree-Minutes-Seconds num1/denom1,num2/denom2,num3,denom3
	private static String convertDecimalCoordsToDegreesMinutesSeconds (double latlong) {
		Double _latlong = latlong;
		int num1 = _latlong.intValue();
		double num2d = ((_latlong - (double)num1) * 60);
		int num2 = (int)num2d;
		double num3d = ((num2d - (double)num2) * 60 * 100000);
		int num3 = (int)num3d;

		return String.format("%d/1,%d/1,%d/100000", num1, num2, num3);
	}


	private static float convertDegreesMinutesSecondsToDecimalDegrees(String rawDegreesMinutesSecondsLatLong){
		StringTokenizer st1 = new StringTokenizer(rawDegreesMinutesSecondsLatLong, ",");

		String rawDegrees = st1.nextToken();
		StringTokenizer stDegrees = new StringTokenizer(rawDegrees, "/");
		int degrees = Integer.parseInt(stDegrees.nextToken());

		String rawMinutes = st1.nextToken();
		StringTokenizer stMinutes = new StringTokenizer(rawMinutes, "/");
		int minutes = Integer.parseInt(stMinutes.nextToken());

		String rawSeconds = st1.nextToken();
		StringTokenizer stSeconds = new StringTokenizer(rawSeconds, "/");

		int secondsNum = Integer.parseInt(stSeconds.nextToken());
		int secondsDenom = Integer.parseInt(stSeconds.nextToken()); 

		return ( degrees + minutes/60.0f + ((secondsNum*1.0f / secondsDenom*1.0f))/3600.0f );
	}

	private static boolean isLatLongWellFormatted(String rawLatLong){
		boolean wellFormatted = false;
		StringTokenizer st = new StringTokenizer(rawLatLong,",");

		if(st.countTokens() == 3){
			StringTokenizer st_1 = new StringTokenizer(st.nextToken(),"/");
			StringTokenizer st_2 = new StringTokenizer(st.nextToken(),"/");
			StringTokenizer st_3 = new StringTokenizer(st.nextToken(),"/");

			if(st_1 != null && st_1.countTokens() == 2 && st_2 != null && st_2.countTokens() == 2 && st_3 != null && st_3.countTokens() == 2) wellFormatted = true;

			st_1 = null;
			st_2 = null;
			st_3 = null;
		}
		st = null;

		return wellFormatted;
	}

	public static void setLocationTags(String picturePath, double latitude, double longitude){
		ExifInterface exifInterface = createExifInterface(picturePath);

		if(exifInterface != null){
			exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE,""+convertDecimalCoordsToDegreesMinutesSeconds(latitude));
			exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
			exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, ""+convertDecimalCoordsToDegreesMinutesSeconds(longitude));
			exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
			try {
				exifInterface.saveAttributes();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<Float> getLocationsTags(String picturePath){
		ArrayList<Float> locationTags = new ArrayList<Float>();
		ExifInterface exifInterface = createExifInterface(picturePath);

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);

		if(exifInterface != null){
			if(exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null && isLatLongWellFormatted(exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE))
					&& exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) != null && isLatLongWellFormatted(exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE))){

				float latitude = convertDegreesMinutesSecondsToDecimalDegrees(exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
				float longitude = convertDegreesMinutesSecondsToDecimalDegrees(exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));

				String tmp1 = "";
				String tmp2 = "";

				try{
					tmp1 = nf.format(latitude);
					tmp2 = nf.format(longitude);

					tmp1 = tmp1.replace(",", ".");
					tmp2 = tmp2.replace(",", ".");

					latitude = Float.parseFloat(tmp1);
					longitude = Float.parseFloat(tmp2);
				}catch(Exception e){}

				locationTags.add(latitude);
				locationTags.add(longitude);
			}
		}
		return locationTags;
	}

	public static ArrayList<ExifData> getTagsFromPicture(String picturePath){
		ArrayList<ExifData> tagsRes = new ArrayList<ExifData>();
		ExifInterface exifInterface = createExifInterface(picturePath);

		if(exifInterface != null){
			ArrayList<ExifUtil.ExifData> tags = new ArrayList<ExifUtil.ExifData>();// ExifUtil.ExifData[]{

			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_APERTURE,R.string.tag_aperture,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_DATETIME,R.string.tag_datetime,false));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_EXPOSURE_TIME,R.string.tag_exposure_time,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_FLASH,R.string.tag_flash,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_FOCAL_LENGTH,R.string.tag_focal_length,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_GPS_ALTITUDE,R.string.tag_gps_altitude,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_GPS_ALTITUDE_REF,R.string.tag_gps_altitude_ref,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_GPS_DATESTAMP,R.string.tag_gps_datestamp,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_GPS_LATITUDE,R.string.tag_gps_latitude,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_GPS_LATITUDE_REF,R.string.tag_gps_latitude_ref,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_GPS_LONGITUDE,R.string.tag_gps_longitude,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_GPS_LONGITUDE_REF,R.string.tag_gps_longitude_ref,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_GPS_PROCESSING_METHOD,R.string.tag_gps_processing_method,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_GPS_TIMESTAMP,R.string.tag_gps_timestamp,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_IMAGE_LENGTH,R.string.tag_image_length,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_IMAGE_WIDTH,R.string.tag_image_width,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_ISO,R.string.tag_iso,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_MAKE,R.string.tag_make,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_MODEL,R.string.tag_model,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_ORIENTATION,R.string.tag_orientation,true));
			tags.add(new ExifUtil.ExifData(ExifInterface.TAG_WHITE_BALANCE,R.string.tag_white_balance,true));

			int nbTags = tags.size();
			String currentAttribute;

			for(int i=0;i<nbTags;i++){
				currentAttribute = exifInterface.getAttribute(tags.get(i).getTag());
				if(currentAttribute != null && currentAttribute != "")tagsRes.add(tags.get(i));
			}
		}
		return tagsRes;
	}

	public static class ExifData{
		private String tag;
		private int resourceIndex;
		private boolean isLocked;

		public ExifData(String tag, int resourceIndex, boolean isLocked){
			this.tag = tag;
			this.resourceIndex = resourceIndex;
			this.isLocked = isLocked;
		}

		public String getTag(){
			return this.tag;
		}

		public int getResourceIndex(){
			return this.resourceIndex;
		}

		public boolean isLocked(){
			return this.isLocked;
		}
	}

}