package view.popup;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import model.PersistenceManager;
import model.Sound;
import model.rest.ScenarioItem;
import util.camera.CameraUtil;
import util.components.rotate_layout.RotateLayout;
import activity.ScenariosActivity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.dentalcrm.kitview.R;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CameraSettingsPopupManager {
	private ScenariosActivity mContext;
	private PersistenceManager mPersistenceManager;

	private Dialog mCameraSettingsDialog;	
	private ArrayAdapter<CharSequence> mShutterReleaseAdapter, mFlashAdapter,mFocusAdapter,mResolutionAdapter,mSoundAdapter, mPictureTimeDisplayAdapter;

	private Spinner mShutterReleaseButtonSpinner, mFlashSpinner,mFocusSpinner,mResolutionSpinner, mSoundSpinner, mPictureTimeDisplaySpinner;
	private LinearLayout mFlashLinearLayout,mFocusLinearLayout,mResolutionLinearLayout;

	private boolean mFirstTimeShutterReleaseButtonEvent = true,mFirstTimeFlashEvent = true,mFirstTimeFocusEvent = true,mFirstTimeResolutionEvent = true;

	private boolean mFirstTimeSpinnerSoundEventThrown = false;

	private RotateLayout mRotateLayout;

	public CameraSettingsPopupManager(ScenariosActivity mMainMenuActivity){
		this.mContext = mMainMenuActivity;
		this.mPersistenceManager = PersistenceManager.getInstance();
		
		this.mCameraSettingsDialog = new Dialog(mContext);

		if(mCameraSettingsDialog != null){
			this.mCameraSettingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.mCameraSettingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			this.mCameraSettingsDialog.setContentView(R.layout.popup_camera_settings);
		}
	}

	public void initializeCameraSettingsPopup(){
		if(mContext != null){
			this.mCameraSettingsDialog.setContentView(R.layout.popup_camera_settings);

			this.mRotateLayout = (RotateLayout) this.mCameraSettingsDialog.findViewById(R.id.rotateLayout);

			this.initializeSound();
			this.initializeShutterReleaseButton();
			this.initializeFlashModes();
			this.initializeFocusModes();
			this.initializeResolutionModes();
			this.initializePictureTimeDisplay();
		}
	}

	public void updateFlashSpinner(String flash){
		if(((ScenariosActivity)(mContext)).getCamera() != null){
			final List<String> mFlashModes = CameraUtil.getSupportedFlashModes(((ScenariosActivity)(mContext)).getCamera());
			int flashIndex = (mFlashModes != null)?mFlashModes.indexOf(flash):-1;
			if(flashIndex != -1){
				if(mFlashSpinner != null)mFlashSpinner.setSelection(flashIndex);
			}
		}
	}

	private void updateSound(int soundIndex){
		if(mPersistenceManager != null){
			List<Sound> mSoundModes = mPersistenceManager.getSounds();
			final int nbSoundModes = (mSoundModes != null)?mSoundModes.size():0;		

			if(soundIndex >= 0 && soundIndex < nbSoundModes){
				if(mSoundSpinner != null){
					this.mSoundSpinner.setSelection(soundIndex);
					this.mSoundSpinner.invalidate();
				}
			}
		}
	}

	private void initializeSound(){
		if(this.mCameraSettingsDialog != null){
			this.mSoundSpinner = (Spinner) mCameraSettingsDialog.findViewById(R.id.sound_spinner);

			if(mSoundSpinner != null && this.mPersistenceManager != null){
				List<Sound> mSoundModes = mPersistenceManager.getSounds();
				final int nbSoundModes = (mSoundModes != null)?mSoundModes.size():0;

				if(nbSoundModes > 0){
					String [] soundsArray = new String[nbSoundModes];

					for(int i=0;i<nbSoundModes;i++){
						int titleResourceId = mSoundModes.get(i).getTitleId();

						if(titleResourceId != 0 && mContext != null && mContext.getResources() != null)soundsArray[i] = mContext.getResources().getString(titleResourceId);
						else soundsArray[i] = "";
					}

					mSoundAdapter = new ArrayAdapter<CharSequence>(mContext, android.R.layout.simple_spinner_item, soundsArray);

					if(mSoundAdapter != null){
						mSoundAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						mSoundSpinner.setAdapter(mSoundAdapter);
					}

					mSoundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3){
							if(arg2 >= 0 && arg2 < nbSoundModes && !mFirstTimeSpinnerSoundEventThrown){
								mPersistenceManager.setSoundIndex(arg2);
							}
							mFirstTimeSpinnerSoundEventThrown = false;
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {}
					});

					int soundIndex = (mSoundModes != null && mPersistenceManager.getSoundIndex() != PersistenceManager.VALUE_UNDEFINED_INT)?mSoundModes.indexOf(mPersistenceManager.getSoundIndex()):-1;
					if(soundIndex != -1)mSoundSpinner.setSelection(soundIndex);
				}
			}
		}
	}

	private void initializeShutterReleaseButton(){

		if(this.mCameraSettingsDialog != null){
			this.mShutterReleaseButtonSpinner = (Spinner) mCameraSettingsDialog.findViewById(R.id.shutter_release_button_spinner);

			if(mShutterReleaseButtonSpinner != null && this.mPersistenceManager != null){
				ArrayList<Integer> shutterReleaseButtonmodes = new ArrayList<Integer>();
				shutterReleaseButtonmodes.add(new Integer(R.string.shutter_release_button1));
				shutterReleaseButtonmodes.add(new Integer(R.string.shutter_release_button2));

				final int nbShutterReleaseButtonModes = (shutterReleaseButtonmodes != null)?shutterReleaseButtonmodes.size():0;

				if(nbShutterReleaseButtonModes > 0){
					String [] shutterReleaseButtonArray = new String[nbShutterReleaseButtonModes];

					for(int i=0;i<nbShutterReleaseButtonModes;i++){
						int titleResourceId = shutterReleaseButtonmodes.get(i);//.getTitleId();

						if(titleResourceId != 0 && mContext != null && mContext.getResources() != null)shutterReleaseButtonArray[i] = mContext.getResources().getString(titleResourceId);
						else shutterReleaseButtonArray[i] = "";
					}

					mShutterReleaseAdapter = new ArrayAdapter<CharSequence>(mContext, android.R.layout.simple_spinner_item, shutterReleaseButtonArray);

					if(mShutterReleaseAdapter != null){
						mShutterReleaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						mShutterReleaseButtonSpinner.setAdapter(mShutterReleaseAdapter);
					}

					mShutterReleaseButtonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3){
							if(arg2 >= 0 && arg2 < nbShutterReleaseButtonModes && !mFirstTimeShutterReleaseButtonEvent){
								mPersistenceManager.setShutterReleaseIndex(arg2);
							}
							mFirstTimeShutterReleaseButtonEvent = false;
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {}
					});

					int shutterReleaseIndex = (shutterReleaseButtonmodes != null && mPersistenceManager.getShutterReleaseButtonIndex() != -1)?shutterReleaseButtonmodes.indexOf(mPersistenceManager.getShutterReleaseButtonIndex()):-1;
					if(shutterReleaseIndex != -1)mShutterReleaseButtonSpinner.setSelection(shutterReleaseIndex);
				}
			}
		}
	}

	private void initializePictureTimeDisplay(){
		this.mPictureTimeDisplaySpinner = (Spinner)mCameraSettingsDialog.findViewById(R.id.picture_time_display_spinner);

		final List<String> mPictureTimeDisplayModes = new ArrayList<String>();
		mPictureTimeDisplayModes.add(""+PersistenceManager.PICTURE_TIME_DISPLAY_1);
		mPictureTimeDisplayModes.add(""+PersistenceManager.PICTURE_TIME_DISPLAY_2);
		mPictureTimeDisplayModes.add(""+PersistenceManager.PICTURE_TIME_DISPLAY_3);

		final int nbPictureTimeDisplayModes = (mPictureTimeDisplayModes != null)?mPictureTimeDisplayModes.size():0;
		String [] pictureTimeDisplayArray = new String[nbPictureTimeDisplayModes];

		for(int i=0;i<nbPictureTimeDisplayModes;i++){
			pictureTimeDisplayArray[i] = ""+(Integer.parseInt(mPictureTimeDisplayModes.get(i))/1000);
		}

		mPictureTimeDisplayAdapter = new ArrayAdapter<CharSequence>(mContext, android.R.layout.simple_spinner_item, pictureTimeDisplayArray);

		if(mPictureTimeDisplayAdapter != null){
			mPictureTimeDisplayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mPictureTimeDisplaySpinner.setAdapter(mPictureTimeDisplayAdapter);
		}

		mPictureTimeDisplaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(arg2 == 0){
					mPersistenceManager.setPictureTimeDisplay(PersistenceManager.PICTURE_TIME_DISPLAY_1);
				}else if(arg2 == 1){
					mPersistenceManager.setPictureTimeDisplay(PersistenceManager.PICTURE_TIME_DISPLAY_2);
				}else if(arg2 == 2){
					mPersistenceManager.setPictureTimeDisplay(PersistenceManager.PICTURE_TIME_DISPLAY_3);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		int pictureTimeDisplay = mPersistenceManager.getPictureTimeDisplay();

		if(pictureTimeDisplay == PersistenceManager.PICTURE_TIME_DISPLAY_1){
			mPictureTimeDisplaySpinner.setSelection(0);
		}else if(pictureTimeDisplay == PersistenceManager.PICTURE_TIME_DISPLAY_2){
			mPictureTimeDisplaySpinner.setSelection(1);
		}else if(pictureTimeDisplay == PersistenceManager.PICTURE_TIME_DISPLAY_3){
			mPictureTimeDisplaySpinner.setSelection(2);
		}
	}

	private void initializeFlashModes(){
		this.mFlashLinearLayout = (LinearLayout)mCameraSettingsDialog.findViewById(R.id.flash_ll);
		this.mFlashSpinner = (Spinner) mCameraSettingsDialog.findViewById(R.id.flash_spinner);

		if(((ScenariosActivity)(mContext)).getCamera() != null && mFlashSpinner != null){
			final List<String> mFlashModes = CameraUtil.getSupportedFlashModes(((ScenariosActivity)(mContext)).getCamera());
			final int nbFlashModes = (mFlashModes != null)?mFlashModes.size():0;

			if(nbFlashModes > 0){
				String [] flashModesArray = new String[nbFlashModes];

				for(int i=0;i<nbFlashModes;i++){
					flashModesArray[i] = mFlashModes.get(i).toString();
				}

				mFlashAdapter = new ArrayAdapter<CharSequence>(mContext, android.R.layout.simple_spinner_item, flashModesArray);

				if(mFlashAdapter != null){
					mFlashAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					mFlashSpinner.setAdapter(mFlashAdapter);
				}

				mFlashSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						if(arg2 >= 0 && arg2 < nbFlashModes && !mFirstTimeFlashEvent){
							if(mContext instanceof ScenariosActivity){
								((ScenariosActivity)(mContext)).changeFlashMode(mFlashModes.get(arg2));
							}
						}
						mFirstTimeFlashEvent = false;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {}
				});

				int mode = ((ScenariosActivity)(mContext)).getMode();
				String flash = null;

				if(mode == ScenariosActivity.MODE_SCENARIO || mode == ScenariosActivity.MODE_EMERGENCY){
					ScenarioItem scenarioItem = ((ScenariosActivity)(mContext)).getCurrentScenarioItem();
					flash = (scenarioItem != null)?scenarioItem.getFlash():null;
				}else if(mode == ScenariosActivity.MODE_PHOTO || mode == ScenariosActivity.MODE_BARCODE_READER){
					flash = mPersistenceManager.getFlash();
				}

				if(flash != null && flash != ""){
					int flashIndex = (mFlashModes != null)?mFlashModes.indexOf(flash):-1;
					if(flashIndex != -1)mFlashSpinner.setSelection(flashIndex);
				}
			}
			if(mFlashLinearLayout != null)mFlashLinearLayout.setVisibility((nbFlashModes > 0)?View.VISIBLE:View.GONE);
		}
	}

	public void updateFocusSpinner(String focus){
		if(((ScenariosActivity)(mContext)).getCamera() != null){
			final List<String> mFocusModes = CameraUtil.getSupportedFocusModes(((ScenariosActivity)(mContext)).getCamera());
			int focusIndex = (mFocusModes != null)?mFocusModes.indexOf(focus):-1;
			if(focusIndex != -1){
				if(mFocusSpinner != null)mFocusSpinner.setSelection(focusIndex);
			}
		}
	}

	private void initializeFocusModes(){
		this.mFocusLinearLayout = (LinearLayout)mCameraSettingsDialog.findViewById(R.id.focus_ll);
		this.mFocusSpinner = (Spinner) mCameraSettingsDialog.findViewById(R.id.focus_spinner);

		if(((ScenariosActivity)(mContext)).getCamera() != null && mFocusSpinner != null){
			final List<String> mFocusModes = CameraUtil.getSupportedFocusModes(((ScenariosActivity)(mContext)).getCamera());
			final int nbFocusModes = (mFocusModes != null)?mFocusModes.size():0;

			if(nbFocusModes > 0){
				String [] focusModesArray = new String[nbFocusModes];

				for(int i=0;i<nbFocusModes;i++){
					focusModesArray[i] = mFocusModes.get(i).toString();
				}

				mFocusAdapter = new ArrayAdapter<CharSequence>(mContext, android.R.layout.simple_spinner_item, focusModesArray);

				if(mFocusAdapter != null){
					mFocusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					mFocusSpinner.setAdapter(mFocusAdapter);
				}

				mFocusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						if(arg2 >= 0 && arg2 < nbFocusModes && !mFirstTimeFocusEvent){
							if(mContext != null && mContext instanceof ScenariosActivity){
								((ScenariosActivity)(mContext)).changeFocusMode(mFocusModes.get(arg2));
							}
						}
						mFirstTimeFocusEvent = false;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {}
				});

				int mode = ((ScenariosActivity)(mContext)).getMode();
				String focus = null;

				if(mode == ScenariosActivity.MODE_SCENARIO || mode == ScenariosActivity.MODE_EMERGENCY){
					ScenarioItem scenarioItem = ((ScenariosActivity)(mContext)).getCurrentScenarioItem();
					focus = (scenarioItem != null)?scenarioItem.getFocus():null;
				}else if(mode == ScenariosActivity.MODE_PHOTO || mode == ScenariosActivity.MODE_BARCODE_READER){
					focus = mPersistenceManager.getFocus();
				}

				if(focus != null && focus != ""){
					int focusIndex = (mFocusModes != null)?mFocusModes.indexOf(focus):-1;
					if(focusIndex != -1)mFocusSpinner.setSelection(focusIndex);
				}
			}

			if(mFocusLinearLayout != null)mFocusLinearLayout.setVisibility((nbFocusModes > 0)?View.VISIBLE:View.GONE);
		}
	}

	private void initializeResolutionModes(){
		this.mResolutionLinearLayout = (LinearLayout)mCameraSettingsDialog.findViewById(R.id.resolution_ll);
		this.mResolutionSpinner = (Spinner) mCameraSettingsDialog.findViewById(R.id.resolution_spinner);

		if(((ScenariosActivity)(mContext)).getCamera() != null && mResolutionSpinner != null){
			final Camera.Size [] mResolutionModes = ((ScenariosActivity)(mContext)).getSupportedPictureSizes();
			final int nbResolutionModes = (mResolutionModes != null)?mResolutionModes.length:0;

			if(nbResolutionModes > 0){
				String [] resolutionModesArray = new String[nbResolutionModes];

				for(int i=0;i<nbResolutionModes;i++){
					DecimalFormat df = new DecimalFormat();
					df.setMaximumFractionDigits(1);
					df.setMaximumIntegerDigits(2);
					String mp = df.format((mResolutionModes[i].width*mResolutionModes[i].height*1.0d)/1000000d);
					resolutionModesArray[i] = mResolutionModes[i].width+" x "+mResolutionModes[i].height+" ("+mp+"M)";
				}

				mResolutionAdapter = new ArrayAdapter<CharSequence>(mContext, android.R.layout.simple_spinner_item, resolutionModesArray);

				if(mResolutionAdapter != null){
					mResolutionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					mResolutionSpinner.setAdapter(mResolutionAdapter);
				}

				mResolutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						if(arg2 >= 0 && arg2 < nbResolutionModes && !mFirstTimeResolutionEvent){
							if(mContext != null && mContext instanceof ScenariosActivity){
								Camera.Size s = mResolutionModes[arg2];	
								((ScenariosActivity)(mContext)).setResolution(s.width, s.height);
								mPersistenceManager.setResolutionIndex(arg2);
							}
						}
						mFirstTimeResolutionEvent = false;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {}
				});

				int resoIndex = mPersistenceManager.getResolutionIndex();
				if(resoIndex != -1)mResolutionSpinner.setSelection(resoIndex);
			}

			if(mResolutionLinearLayout != null)mResolutionLinearLayout.setVisibility((nbResolutionModes > 0)?View.VISIBLE:View.GONE);
		}
	}

	public void destroy(){
		this.mContext = null;

		try{
			if(mFlashAdapter != null){
				mFlashAdapter = null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			if(mFocusAdapter != null){
				mFocusAdapter = null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			if(mPictureTimeDisplayAdapter != null){
				mPictureTimeDisplayAdapter = null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			if(mSoundAdapter != null){
				mSoundAdapter = null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void showCameraSettingsDialogPopup(Activity context, String flash, String focus, int soundIndex){
		if(!context.isFinishing()){
			if(mCameraSettingsDialog != null)mCameraSettingsDialog.show();
		}

		updateFlashSpinner(flash);
		updateFocusSpinner(focus);
		updateSound(soundIndex);
	}

	public boolean isShowing(){
		return (mCameraSettingsDialog != null)?this.mCameraSettingsDialog.isShowing():false;
	}

	public void hideCameraSettingsDialogPopup(){
		if(mCameraSettingsDialog != null)mCameraSettingsDialog.dismiss();
	}

	public Dialog getCameraSettingsDialogPopup(){
		return mCameraSettingsDialog;
	}

	private ScrollView mScrollView;

	public void invalidate(int angleRot) {	
		if(angleRot == 270) angleRot += 180;
		else if(angleRot == 90) angleRot += 180;

		if(mRotateLayout != null && mRotateLayout.getView() != null && (RotateLayout.LayoutParams) mRotateLayout.getView().getLayoutParams() != null){
			((RotateLayout.LayoutParams) mRotateLayout.getView().getLayoutParams()).angle = angleRot;

			mRotateLayout.requestLayout();
			mRotateLayout.invalidate();
		}
	}
}