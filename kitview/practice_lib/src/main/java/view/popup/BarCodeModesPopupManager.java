package view.popup;

import java.util.ArrayList;
import java.util.List;
import activity.ScenariosActivity;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.dentalcrm.kitview.R;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class BarCodeModesPopupManager {
	private ScenariosActivity mContext;
	private Dialog mCameraSettingsDialog;	
	private Spinner mShakeSpinner;
	private ArrayAdapter<CharSequence> mShakeAdapter;

	private Button mCancelButton, mConfirmButton;

	public static int BAR_CODE_MODE_SELECTED = 0;

	public BarCodeModesPopupManager(ScenariosActivity mMainMenuActivity){
		this.mContext = mMainMenuActivity;
	}

	public void initializeCameraSettingsPopup(){
		this.mCameraSettingsDialog = new Dialog(mContext);

		if(this.mCameraSettingsDialog != null){
			this.mCameraSettingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.mCameraSettingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			this.mCameraSettingsDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND); 
			this.mCameraSettingsDialog.setContentView(R.layout.popup_barcode_modes);
			this.mCameraSettingsDialog.setCancelable(false);
		}

		this.initializeShakeModes();
		this.initializeButtons();
	}

	private void initializeButtons(){
		this.mCancelButton = (Button) mCameraSettingsDialog.findViewById(R.id.cancel);
		this.mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCameraSettingsDialog.dismiss();
				mContext.finish();
			}
		});

		this.mConfirmButton = (Button) mCameraSettingsDialog.findViewById(R.id.ok);
		this.mConfirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCameraSettingsDialog.dismiss();
			}
		});
	}

	private void initializeShakeModes(){
		this.mShakeSpinner = (Spinner)mCameraSettingsDialog.findViewById(R.id.sensivity_spinner);

		final List<String> mShakeModes = new ArrayList<String>();
		mShakeModes.add(mContext.getString(R.string.barcode_mode1));
		mShakeModes.add(mContext.getString(R.string.barcode_mode2));

		final int nbShakeModes = (mShakeModes != null)?mShakeModes.size():0;
		String [] shakeModesArray = new String[nbShakeModes];

		for(int i=0;i<nbShakeModes;i++){
			shakeModesArray[i] = mShakeModes.get(i).toString();
		}

		mShakeAdapter = new ArrayAdapter<CharSequence>(mContext, android.R.layout.simple_spinner_item, shakeModesArray);

		if(mShakeAdapter != null){
			mShakeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mShakeSpinner.setAdapter(mShakeAdapter);
		}

		mShakeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				BAR_CODE_MODE_SELECTED = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

	public void destroy(){
		this.mContext = null;
	}

	public void showCameraSettingsDialogPopup(){
		if(mCameraSettingsDialog != null && !mContext.isFinishing())mCameraSettingsDialog.show();
	}

	public void hideCameraSettingsDialogPopup(){
		if(mCameraSettingsDialog != null)mCameraSettingsDialog.dismiss();
	}
}