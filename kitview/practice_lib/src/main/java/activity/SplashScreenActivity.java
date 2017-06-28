package activity;

import util.system.SystemUtil;
import model.PersistenceManager;

import com.crittercism.app.Crittercism;
import com.dentalcrm.kitview.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SplashScreenActivity extends Activity {
	private static final int SPLASH_TIME = 1000;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splashscreen);

		Crittercism.initialize(getApplicationContext(), "551171e7e0697fa449637b33");

		if(SystemUtil.hasIceCreamSandwich()){
			intent = new Intent(getApplicationContext(), MainActivity.class);

			//Toast.makeText(getApplicationContext(),MainActivity.class.getName(),Toast.LENGTH_LONG).show();

			if(intent != null){
				PersistenceManager pm = PersistenceManager.getInstance();

				if(pm != null){
					pm.initializeSharedPreferences(SplashScreenActivity.this,false);

					if(pm.getSplashScreenEnabled()){
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								startActivity(intent);
								overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
							}
						}, SPLASH_TIME);
					}else startActivity(intent);
				}
			}
		}else{
			SystemUtil.showPopup(SplashScreenActivity.this,SplashScreenActivity.this.getResources().getString(R.string.ice_cream_sandwitch_required));
		}
	}

	public Intent getIntent(){
		return this.intent;
	}

	@Override
	public void onBackPressed() {
		this.finish();
		super.onBackPressed();
	}
}