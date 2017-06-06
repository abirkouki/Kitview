package util.location;

import java.util.ArrayList;
import util.image.ExifUtil;
import util.network.NetworkUtil;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationUtil {
	static String bestProvider;
	static String _photoPath;

	public static void savePhotoLocation(Context context, String photoPath,boolean saveGpsCoord){
		if(saveGpsCoord){
			try{
				_photoPath = photoPath;
				LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
				ArrayList<String> providers = (ArrayList<String>) lm.getAllProviders();

				if(providers.contains(LocationManager.NETWORK_PROVIDER) && NetworkUtil.isNetworkAvailable(context)){
					bestProvider = LocationManager.NETWORK_PROVIDER;
				}else if(providers.contains(LocationManager.GPS_PROVIDER)){
					bestProvider = LocationManager.GPS_PROVIDER;
				}else if(providers.contains(LocationManager.PASSIVE_PROVIDER)){
					bestProvider = LocationManager.PASSIVE_PROVIDER;
				}

				if(lm != null){
					Location lastKnowLocation = lm.getLastKnownLocation(bestProvider);

					if(lastKnowLocation != null){	
						ExifUtil.setLocationTags(_photoPath, lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
					}

					lm.requestLocationUpdates(bestProvider, 1000, 0, new locationListener());
				}

				if(providers != null){
					providers.clear();
					providers = null;
				}

				lm = null;

			}catch(Exception e){
			}
		}
	}

	private static class locationListener implements LocationListener{
		@Override
		public void onLocationChanged(Location location) {
			ExifUtil.setLocationTags(_photoPath, location.getLatitude(), location.getLongitude());
		}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}
}