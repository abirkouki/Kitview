package util.system;

import com.dentalcrm.kitview.R;
import activity.SplashScreenActivity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SystemUtil {
	public static boolean isStorageFull(Context ctx){
		StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
		long Free  = (((long)statFs.getAvailableBlocks()) * ((long)(statFs.getBlockSize()))) / 1048576L;

		return (Free <= 10);
	}

	public static void showPopup(Activity context,String text){
		if(context != null){
			Toast t = Toast.makeText(context,"", Toast.LENGTH_SHORT);

			LayoutInflater inflater = context.getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast,null);//context.findViewById(R.id.tv_toast_content));

			TextView tv = (TextView) layout.findViewById(R.id.tv_toast_content);
			tv.setText(text);
			t.setView(layout);
			
			if(t != null)t.show();
		}
	}

	public static int dpToPx(Context context, int dp){
		return (int) (dp * context.getResources().getSystem().getDisplayMetrics().density);
	}

	public static int pxToDp(Context context, int px){
		return (int) (px / context.getResources().getSystem().getDisplayMetrics().density);
	}

	public static float pixelsToSp(Context context, float px) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return px/scaledDensity;
	}

	public static float spToPixels(Context context, float sp) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return sp*scaledDensity;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean hasIceCreamSandwich() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static Spanned getString(Context context, int id, String value){
		String rawText = (context != null && id != 0)?context.getResources().getString(id).replace("VALUE", (value!= null)?value:""):"";
		return Html.fromHtml(rawText);
	}

	public static Spanned getString(Context context, int id, String value1, String value2){
		String rawText = (context != null && id != 0)?context.getResources().getString(id).replace("VALUE1", (value1!= null)?value1:""):"";
		rawText = rawText.replace("VALUE2", (value2!= null)?value2:"");
		return Html.fromHtml(rawText);
	}

	public static Spanned getString(Context context, int id, String value1, String value2, String value3){
		String rawText = (context != null && id != 0)?context.getResources().getString(id).replace("VALUE1", (value1!= null)?value1:""):"";
		rawText = rawText.replace("VALUE2", (value2!= null)?value2:"");
		rawText = rawText.replace("VALUE3", (value3!= null)?value3:"");
		return Html.fromHtml(rawText);
	}

	public static String getAppVersion(Context context){
		PackageInfo pInfo = null;
		try {
			pInfo = (context != null)?context.getPackageManager().getPackageInfo(context.getPackageName(), 0):null;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return (pInfo != null)?pInfo.versionName:"";
	}

	public static void restartApplication(Activity context){
		//ApplicationCamera.getInstance().getPersistenceManager().setHasRestartedValue(true);
		//ApplicationCamera.getInstance().getPersistenceManager().setActionAppLoadedValue(Constants.LAUNCH_MAIN_MENU);

		AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent();//);
		intent.setClass(context, SplashScreenActivity.class);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, 
				PendingIntent.getActivity(context.getBaseContext(), 0, intent, Intent.FLAG_ACTIVITY_CLEAR_TASK ));
		System.exit(2);
	}
}
