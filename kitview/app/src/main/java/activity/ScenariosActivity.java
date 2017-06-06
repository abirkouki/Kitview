package activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import javax.net.ServerSocketFactory;
import model.PersistenceManager;
import model.Sound;
import model.rest.Personne;
import model.rest.Scenario;
import model.rest.ScenarioItem;
import util.components.progressdialog.FRProgressDialog;
import util.components.shake.ShakeDetector;
import util.file.FileUtil;
import util.image.ImageUtil;
import util.network.KitviewUtil;
import util.network.NetworkUtil;
import util.sound.SoundUtil;
import util.system.SystemUtil;
import view.LevelView2;
import view.camera.CameraPreviewSeveral;
import view.level.orientation.Orientation;
import view.level.orientation.OrientationListener;
import view.level.orientation.OrientationProvider;
import view.popup.BarCodeModesPopupManager;
import view.popup.CameraSettingsPopupManager;
import view.popup.GenericPopupManager;
import view.adapter.VerticalListViewAdapter;
import view.arrowpopup.ChromeHelpPopup;
import com.embarcadero.javaandroid.TJSONObject;
import com.dentalcrm.kitview.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewAnimator;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ScenariosActivity extends FragmentActivity implements OrientationListener{
	//Constants
	public final static String KEY_MODE = "KEY_MODE";
	public final static int MODE_PHOTO = 0;
	public final static int MODE_SCENARIO = 1;
	public final static int MODE_EMERGENCY = 2;
	public final static int MODE_BARCODE_READER = 3;

	//View
	private CameraPreviewSeveral mPreview;
	private FrameLayout mVerticalListViewPanel;
	private ListView mVerticalListView;
	private VerticalListViewAdapter mVerticalListViewAdapter;
	private FrameLayout preview;
	private CameraSettingsPopupManager mCameraSettingsPopupManager;
	private ViewAnimator mViewAnimator;
	private ImageView mImageView;
	private TextView mTitleTextView, mCurrentPatientInfosTextView;
	private int mViewAnimatorIndex = 0;
	private FRProgressDialog mDialog;

	private FrameLayout mPinchToZoomFrameLayout;
	private ImageView mTopImageView;

	//View : Level
	private static ScenariosActivity CONTEXT;
	private OrientationProvider provider;
	private LevelView2 view2;

	//Model
	private PersistenceManager mPersistenceManager;
	private int cameraId = 0;
	private Camera mCamera;
	private boolean mIsPictureTaken;
	private boolean mAppLoaded = false;
	private boolean firstTime = true;
	private boolean calledRelease = false;
	private int mIndexScenario;
	private int mPictureIndex = 0;
	private boolean mStreamingEnabled;
	private String mStreamingIP;
	private int mStreamingPort;
	private boolean mActivityKilled = false;
	private ArrayList<String> mPicturesList = new ArrayList<String>();
	private ArrayList<Integer> mPicturesListClic;
	private ServerSocket serverSocket;
	private boolean mIsSendingPicture = false;
	private File mPictureFile = null;
	private String patientInfos;
	private int mMode;
	private AudioManager mgr;
	private int mPatientId;

	//Callbacks
	private PictureCallback mPicture;

	//Sensors
	private int mAngleRot;
	private ShakeDetector mShakeDetector;

	//Listeners
	private OrientationEventListener myOrientationEventListener;

	private boolean mScenariosLoaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setContentView(R.layout.activity_scenarios);

		MainActivity.getImageFetcher(null).clearCache();
		MainActivity.getImageFetcher(null).setUseCache(true);
		MainActivity.setUseAttachedViewModified(false);

		this.mPinchToZoomFrameLayout = (FrameLayout) findViewById(R.id.fl_pinch_to_zoom);
		this.mPinchToZoomFrameLayout.setVisibility(View.GONE);

		this.mTopImageView = (ImageView)findViewById(R.id.ivtop);
		this.mMode = getIntent().getExtras().getInt(KEY_MODE);
		this.mDialog = new FRProgressDialog(this, "",false);

		if(mMode != MODE_EMERGENCY) 
			this.mDialog.showFRProgressDialog();

		if(mMode == MODE_BARCODE_READER){
			BarCodeModesPopupManager pm = new BarCodeModesPopupManager(this);
			pm.initializeCameraSettingsPopup();
			pm.showCameraSettingsDialogPopup();
		}

		if(mMode != MODE_EMERGENCY){
			KitviewUtil.GetCurrentIdPatient(ScenariosActivity.this,new KitviewUtil.IIntResponse() {
				@Override
				public void onResponse(final int currentIdPatient) {

					mPatientId = currentIdPatient;

					final Personne personne = KitviewUtil.getPatientInfos(ScenariosActivity.this, currentIdPatient);

					if(personne != null){
						runOnUiThread(new Runnable(){		
							@Override
							public void run(){
								if(personne.getFirstName() != null && personne.getFirstName() != "" && personne.getLastName() != null && personne.getLastName() != ""){
									patientInfos = personne.getLastName().trim() +" "+personne.getFirstName().trim();
								}else patientInfos = "";

								updateCurrentPatientTextView(patientInfos);
							}
						});
					}
				}
			});
		}

		this.mPersistenceManager = PersistenceManager.getInstance();

		this.mStreamingEnabled = (mPersistenceManager != null)?mPersistenceManager.getStreamingEnabled():false;
		this.mStreamingIP = (mPersistenceManager != null)?mPersistenceManager.getStreamingIP(false):"";
		this.mStreamingPort = (mPersistenceManager != null)?mPersistenceManager.getStreamingPort():-1;

		this.mCamera = getCameraInstance();

		this.initializeResolutionMaxCamera();

		this.updatePreviewSize();

		this.mPreview = new CameraPreviewSeveral(ScenariosActivity.this, this.mCamera);
		this.mPreview.setWillNotDraw(false);

		this.preview = (FrameLayout) findViewById(R.id.camera_preview);
		this.preview.addView(this.mPreview);

		this.mVerticalListViewPanel = (FrameLayout) findViewById(R.id.panel);
		this.mVerticalListView = (ListView) findViewById(R.id.album_list2);

		this.mViewAnimator = (ViewAnimator) findViewById(R.id.va);
		this.mTitleTextView = (TextView) findViewById(R.id.title_textview);
		this.mCurrentPatientInfosTextView = (TextView) findViewById(R.id.patient_infos_textview);
		this.mImageView = (ImageView) findViewById(R.id.iv);

		//Level
		CONTEXT = this;
		this.view2 = (LevelView2) findViewById(R.id.level2);
		//End Level

		this.initializeCallbacks();

		ScenariosActivity.setCameraDisplayOrientation(this, cameraId, mCamera);	

		mCameraSettingsPopupManager = new CameraSettingsPopupManager(ScenariosActivity.this);

		if(mMode == MODE_SCENARIO){
			mVerticalListViewPanel.setVisibility(View.VISIBLE);
		}else{
			mVerticalListViewPanel.setVisibility(View.GONE);
		}

		if(mMode != MODE_EMERGENCY){
			KitviewUtil.getScenarios(this,new KitviewUtil.IScenarioArrayListResponse() {
				@Override
				public void onResponse(List<Scenario> scenarios){
					runOnUiThread(new Runnable(){
						public void run(){
							mCameraSettingsPopupManager.initializeCameraSettingsPopup();

							initializeList();

							if(mMode != MODE_PHOTO && mMode != MODE_BARCODE_READER && mMode != MODE_EMERGENCY){
								changeScenario(0);
								initializeScenarios();
							}

							mScenariosLoaded = true;

							if(mDialog != null)mDialog.cancelFRProgressDialog();
						}
					});
				}
			});
		}else{
			mScenariosLoaded = true;//no scenarios need to be loaded ...
			mCameraSettingsPopupManager.initializeCameraSettingsPopup();
		}

		initializeListeners();

		this.mAppLoaded = true;

		try {
			this.serverSocket = ServerSocketFactory.getDefault().createServerSocket((mPersistenceManager != null)?mPersistenceManager.getAndroidPort():-1);

			new Thread(new Runnable() {
				@Override
				public void run() {
					while(!mActivityKilled){
						Socket socket = null;
						try {
							socket = serverSocket.accept();

							takePicture();

							socket.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		} catch (IOException e){
			e.printStackTrace();
			//Port out of range
		}catch(Exception e){
			e.printStackTrace();
		}

		initializeSendPicturesThread();
	}

	private static GenericPopupManager mGenericPopupManager;

	private static void launchCameraInaccessible(Activity context){
		String title = context.getResources().getString(R.string.camera_unavailable_title);
		String content = context.getResources().getString(R.string.camera_unavailable_content);

		launchGenericPopup(context, title, content, false);
	}

	private static void launchOOM(Activity context){
		String title = context.getResources().getString(R.string.oom_title);
		String content = context.getResources().getString(R.string.oom_content);

		launchGenericPopup(context, title, content, false);
	}

	private static void launchWriteToDiskImpossible(Activity context){
		String title = context.getResources().getString(R.string.io_error_title);
		String content = context.getResources().getString(R.string.io_error_content);

		launchGenericPopup(context, title, content, false);
	}

	private static void launchDiskFull(Activity context){
		String title = context.getResources().getString(R.string.disk_full_title);
		String content = context.getResources().getString(R.string.disk_full_content);

		launchGenericPopup(context, title, content, false);
	}

	private static void launchGenericPopup(Activity context, final String title, final String content, final boolean exitOnClose){
		mGenericPopupManager = new GenericPopupManager(context);

		if(mGenericPopupManager != null){
			mGenericPopupManager.initializePopup();
		}

		mGenericPopupManager.showPopup(title,content,new GenericPopupManager.IClick() {
			@Override
			public void onValidateClick(){
				if(exitOnClose)System.exit(0);
			}

			@Override
			public void onCancelClick() {
				mGenericPopupManager.hideLineDeleteDialogPopup();
			}
		});
	}

	// TODO voir pour les deprecated
	public void setResolution(int width, int height){
		try{
			Parameters params = mCamera.getParameters();
			params.setPictureSize(width, height);
			mCamera.setParameters(params);

			updatePreviewSize();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public Size [] getSupportedPictureSizes(){
		Parameters params = mCamera.getParameters();
		ArrayList<Camera.Size> pictureSizes = (ArrayList<Size>) params.getSupportedPictureSizes();
		int nbPictureSizes = (pictureSizes != null)?pictureSizes.size():0;
		Camera.Size [] mSupportedPictureSizes = null;

		if(nbPictureSizes > 0){
			Camera.Size[] mSupportedPictureSizesTmp = new Camera.Size[nbPictureSizes];
			pictureSizes.toArray(mSupportedPictureSizesTmp);

			//Sorting all resolutions highest to lowest ...
			Comparator<Camera.Size> c = new Comparator<Camera.Size>(){
				@Override
				public int compare(Size lhs, Size rhs){
					int lhs_area_mp = lhs.width * lhs.height;
					int rhs_area_mp = rhs.width * rhs.height;

					if(lhs_area_mp < rhs_area_mp)return 1;
					else if(lhs_area_mp > rhs_area_mp)return -1;
					else return 0;
				}
			};

			Arrays.sort(mSupportedPictureSizesTmp, c);

			//Ratio highest native resolution of camera
			double ratioHighestNativeResolution = mSupportedPictureSizesTmp[0].width*1.0d/mSupportedPictureSizesTmp[0].height*1.0d;

			//Is it safe ? Don't know !!
			double tolerance = 0.01d;

			//Keep only resolutions with the same aspect ratio (tolerance +/- 0.01)
			//as the highest native resolution ==> avoid artifact caused when 
			//applying two resolution with different aspect ratio ...

			ArrayList<Camera.Size> mSupportedPicturesSizeToKeep = new ArrayList<Camera.Size>();

			for(int i=0;i<nbPictureSizes;i++){
				double currentRatio = mSupportedPictureSizesTmp[i].width*1.0d/mSupportedPictureSizesTmp[i].height*1.0d;

				if(Math.abs(currentRatio-ratioHighestNativeResolution) < tolerance){
					mSupportedPicturesSizeToKeep.add(mCamera.new Size(mSupportedPictureSizesTmp[i].width, mSupportedPictureSizesTmp[i].height));
				}
			}

			nbPictureSizes = (mSupportedPicturesSizeToKeep != null)?mSupportedPicturesSizeToKeep.size():0;
			mSupportedPictureSizes = new Camera.Size[nbPictureSizes];
			mSupportedPicturesSizeToKeep.toArray(mSupportedPictureSizes);
		}
		return mSupportedPictureSizes;
	}

	public void initializeResolutionMaxCamera(){		
		Parameters params = mCamera.getParameters();

		Camera.Size [] mSupportedPictureSizes = getSupportedPictureSizes();
		int nbPictureSizes = (mSupportedPictureSizes != null)?mSupportedPictureSizes.length:0;

		if(nbPictureSizes > 0){
			try{
				int indexReso = mPersistenceManager.getResolutionIndex();
				Size pictureSize =  mSupportedPictureSizes[indexReso];//nbPictureSizes-1];
				params.setPictureSize(pictureSize.width, pictureSize.height);
				mCamera.setParameters(params);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void updatePreviewSize(){
		if(mCamera != null){
			Parameters params = mCamera.getParameters();

			if(params != null){
				List<Size> supportedPreviewSizes = params.getSupportedPreviewSizes();
				int nbSupportedPreviewSizes = (supportedPreviewSizes != null)?supportedPreviewSizes.size():0;
				int w = -1,h = -1;

				if(supportedPreviewSizes != null && nbSupportedPreviewSizes>0){
					double targetRatio = (params.getPictureSize().width*1.0d/params.getPictureSize().height*1.0d);
					Size previewSize = getOptimalPreviewSize(ScenariosActivity.this, params.getSupportedPreviewSizes(), targetRatio);

					w = previewSize.width;
					h = previewSize.height;

					if(w != -1 && h != -1){
						try{
							params.setPreviewSize(w,h);

							mCamera.setParameters(params);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public Size getOptimalPreviewSize(Activity currentActivity,
			List<Size> sizes, double targetRatio) {
		// Use a very small tolerance because we want an exact match.
		final double ASPECT_TOLERANCE = 0.001;
		if (sizes == null) return null;
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		// Because of bugs of overlay and layout, we sometimes will try to
		// layout the viewfinder in the portrait orientation and thus get the
		// wrong size of preview surface. When we change the preview size, the
		// new overlay will be created before the old one closed, which causes
		// an exception. For now, just get the screen size.
		Point point = ImageUtil.getDefaultDisplaySize(currentActivity);//, new Point());
		int targetHeight = Math.min(point.x, point.y);
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		// Cannot find the one match the aspect ratio. This should not happen.
		// Ignore the requirement.
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public void changeViewAnimatorDisplayChild(int index){
		this.mViewAnimatorIndex = index;

		if(this.mViewAnimator != null){
			this.mViewAnimator.setDisplayedChild(this.mViewAnimatorIndex);
			this.mViewAnimator.invalidate();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK ){
			if(mViewAnimator != null){
				if(mViewAnimator.getDisplayedChild() == 1){
					mViewAnimator.setDisplayedChild(0);
					return true;
				}else{
					int nbPicturesToSend = 0;

					final int nbPictures = (mPicturesList != null)?mPicturesList.size():0;

					for(int i=0;i<nbPictures;i++){
						if(mPicturesList.get(i) != "" )nbPicturesToSend++;
					}

					final int nbPicturesToSendCop = nbPicturesToSend;

					if((mMode == MODE_SCENARIO || mMode == MODE_EMERGENCY) && nbPicturesToSend > 0){
						new Thread(new Runnable(){
							@Override
							public void run() {
								if(mDialog != null)mDialog.cancelFRProgressDialog();

								runOnUiThread(new Runnable() {
									public void run() {
										changeViewAnimatorDisplayChild(0);
										mIsPictureTaken = false;

										final String content = (mMode == MODE_SCENARIO)?(((nbPicturesToSendCop!=nbPictures)?getResources().getString(R.string.pictures_missing)+" ":"")+""+getResources().getString(R.string.confirm_before_sending_sure_to_send_pictures_to_kitview)):getResources().getString(R.string.confirm_before_sending_sure_to_send_pictures_to_medical);
										final GenericPopupManager gpm = new GenericPopupManager(ScenariosActivity.this);

										if(gpm != null){
											gpm.initializePopup();
										}

										gpm.showPopup(getResources().getString(R.string.confirm_before_sending)+" ("+nbPicturesToSendCop+"/"+nbPictures+")",content,new GenericPopupManager.IClick() {
											@Override
											public void onValidateClick(){
												final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);

												if(emailIntent != null){
													emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{mPersistenceManager.getDoctorEmail()});
													emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
													emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
													emailIntent.setType("application/image");
												}

												final ArrayList<Uri> uris = new ArrayList<Uri>();

												new Thread(new Runnable() {
													@Override
													public void run() {
														for(int i=0;i<nbPictures;i++){
															if(mPicturesList != null && mPicturesList.get(i) != null && mPicturesList.get(i) != ""){
																if(mMode == MODE_SCENARIO){
																	sendPictureToKitview(mPicturesList.get(i),i);//current, nbPictures,i);
																}else if(mMode == MODE_EMERGENCY){
																	File fileIn = new File(mPicturesList.get(i));

																	if(fileIn != null)uris.add(Uri.fromFile(fileIn));
																}
															}
														}

														if(mMode == MODE_EMERGENCY){
															if(emailIntent != null && uris != null){
																emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
																startActivity(emailIntent);
															}

															finish();
														}
													}
												}).start();

												if(mMode == MODE_SCENARIO){
													runOnUiThread(new Runnable() {
														public void run() {	
															SystemUtil.showPopup(ScenariosActivity.this,SystemUtil.getString(ScenariosActivity.this, R.string.photos_from_patient_transmitted_to_kitview, patientInfos).toString());
														}
													});

													finish();
												}
											}

											@Override
											public void onCancelClick() {
												gpm.hideLineDeleteDialogPopup();
												finish();
											}
										});
									}
								});
							}
						}).start();

					}else finish();

					return true;
				}
			}else return true;
		}else if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP 
				|| event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_MUTE){

			if(mPersistenceManager != null && mPersistenceManager.getShutterReleaseButtonIndex() == 1){
				takePicture();
			}

			return true;
		}else return super.onKeyDown(keyCode, event);
	}

	public void updateImageView(Bitmap bitmap){
		if(this.mImageView != null && bitmap != null){
			this.mImageView.setImageBitmap(bitmap);
			this.mImageView.invalidate();
		}
	}

	public static void setCameraDisplayOrientation(Activity activity,int cameraId, android.hardware.Camera camera) {
		if(activity != null && camera != null){
			android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
			android.hardware.Camera.getCameraInfo(cameraId, info);
			int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
			int degrees = 0;

			switch (rotation) {
			case Surface.ROTATION_0: degrees = 0; break;
			case Surface.ROTATION_90: degrees = 90; break;
			case Surface.ROTATION_180: degrees = 180; break;
			case Surface.ROTATION_270: degrees = 270; break;
			}

			int result;
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
				result = (info.orientation + degrees) % 360;
				result = (360 - result) % 360;  // compensate the mirror
			} else {  // back-facing
				result = (info.orientation - degrees + 360) % 360;
			}
			camera.setDisplayOrientation(result);
		}
	}

	private void initializeList() {
		this.mVerticalListViewAdapter = new VerticalListViewAdapter(ScenariosActivity.this,mPicturesList,MainActivity.getImageFetcher(null));

		if(this.mVerticalListView != null){
			this.mVerticalListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					mPictureIndex = arg2;

					updateGrid();
					updateVerticalListView();
					updateTitleTextView();
					updateFlash();
					updateFocus();

					if(mPicturesList.get(mPictureIndex) != "" && mPicturesList.get(mPictureIndex) != null){
						int nbClics = mPicturesListClic.get(arg2);
						nbClics = (nbClics + 1)%2;
						int nb = (mPicturesList != null)?mPicturesList.size():0;

						for(int i=0;i<nb;i++){
							if(mPictureIndex == i){
								mPicturesListClic.set(i, nbClics);
							}else{
								mPicturesListClic.set(i, 0);
							}	
						}

						//Show picture
						if(nbClics == 1){
							ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(ScenariosActivity.this);
							int screenWidth = screenDimensions.get(0).intValue();
							int screenHeight = screenDimensions.get(1).intValue();

							Bitmap b = ImageUtil.decodeFile(new File(mPicturesList.get(mPictureIndex)), screenWidth, screenHeight);

							mTopImageView.setImageBitmap(b);
							mPinchToZoomFrameLayout.setVisibility(View.VISIBLE);
							//Hide picture and take picture again ...
						}else if(nbClics == 0){
							mPinchToZoomFrameLayout.setVisibility(View.GONE);
						}

						mPinchToZoomFrameLayout.requestLayout();
						mPinchToZoomFrameLayout.invalidate();
					}
				}
			});

			this.mVerticalListView.setOnScrollListener(new AbsListView.OnScrollListener(){
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
						if(MainActivity.getImageFetcher(null) != null)MainActivity.getImageFetcher(null).setPauseWork(true);	
					}else{
						if(MainActivity.getImageFetcher(null) != null)MainActivity.getImageFetcher(null).setPauseWork(false);	
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {}
			});

			if(this.mVerticalListViewAdapter != null)this.mVerticalListView.setAdapter(mVerticalListViewAdapter);
			this.mVerticalListView.setVisibility((mMode != MODE_PHOTO && mMode != MODE_BARCODE_READER && mMode != MODE_EMERGENCY)?View.VISIBLE:View.GONE);

			this.updateVerticalListView();
		}
	}

	private void initializeCallbacks(){
		this.initializePictureCallback();
	}

	private Camera.PreviewCallback mPreviewCallback;

	public void initializePreviewCallback(){

		mPreviewCallback = new Camera.PreviewCallback() {
			@Override
			public void onPreviewFrame(final byte[] data, final Camera camera) {
				if(camera != null && camera.getParameters() != null && data != null && !mIsPictureTaken && !calledRelease && mStreamingEnabled){
					if(!mIsSendingPicture){
						new Thread(new Runnable() {
							@Override
							public void run() {
								mIsSendingPicture = true;

								try{
									int f = camera.getParameters().getPreviewFormat();
									int w = camera.getParameters().getPreviewSize().width;
									int h = camera.getParameters().getPreviewSize().height;

									YuvImage im = new YuvImage(data, f, w, h, null);

									ByteArrayOutputStream out = new ByteArrayOutputStream();

									if(im != null)im.compressToJpeg(new Rect(0, 0, im.getWidth(), im.getHeight()), 50, out);

									NetworkUtil.sendData(ScenariosActivity.this,out.toByteArray(), mStreamingIP, mStreamingPort);
								}catch(Exception e){
									e.printStackTrace();

								}
								mIsSendingPicture = false;
							}
						}).start();
					}
				}
				if(mPreview != null)mPreview.invalidate();
			}
		};

		if(this.mCamera != null){
			this.mCamera.setPreviewCallback(mPreviewCallback);
		}
	}

	public void changeFlashMode(String flashMode){
		try{
			if(mCamera != null){
				Camera.Parameters p = mCamera.getParameters();
				if(p != null){
					List<String> supportedFlashModes = p.getSupportedFlashModes();

					if(supportedFlashModes != null && supportedFlashModes.contains(flashMode)){
						if(flashMode != "")p.setFlashMode(flashMode);

						try{
							mCamera.setParameters(p);
						}catch(Exception e){
							e.printStackTrace();
						}

						if(mMode == MODE_SCENARIO){
							writeFlash();
						}else if(mMode == MODE_PHOTO || mMode == MODE_BARCODE_READER || mMode == MODE_EMERGENCY){
							mPersistenceManager.setFlash(flashMode);
						}
					}
				}
			}
		}catch(RuntimeException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void changeFocusMode(String focusMode){
		try{
			if(mCamera != null){
				Camera.Parameters p = mCamera.getParameters();
				if(p != null){
					List<String> supportedFocusModes = p.getSupportedFocusModes();

					if(supportedFocusModes != null && supportedFocusModes.contains(focusMode)){
						if(focusMode != "")p.setFocusMode(focusMode);

						try{
							mCamera.setParameters(p);
						}catch(Exception e){
							e.printStackTrace();
						}

						if(mMode == MODE_SCENARIO){
							writeFocus();
						}else if(mMode == MODE_PHOTO || mMode == MODE_BARCODE_READER || mMode == MODE_EMERGENCY){
							mPersistenceManager.setFocus(focusMode);
						}
					}
				}
			}
		}catch(RuntimeException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void changeScenario(int scenarioIndex) {
		this.mIndexScenario = scenarioIndex;
		List<Scenario> mScenarios = mPersistenceManager.getScenarios();
		int nbScenarios = (mScenarios != null)?mScenarios.size():0;

		if(scenarioIndex >= 0 && scenarioIndex < nbScenarios){
			Scenario mCurrentScenario = mScenarios.get(scenarioIndex);
			this.mPictureIndex = 0;

			int nbScenarioItems = (mCurrentScenario != null)?mCurrentScenario.getNbScenarioItems():0;
			this.mPicturesList = new ArrayList<String>(nbScenarioItems);
			this.mPicturesListClic = new ArrayList<Integer>(nbScenarioItems);

			for(int i=0;i<nbScenarioItems;i++){
				this.mPicturesList.add(i, "");
				this.mPicturesListClic.add(i, 0);
			}

			this.updateTitleTextView();
			this.updateVerticalListView();
			this.updateGrid();
			this.updateFlash();
			this.updateFocus();
		}
	}


	private void updateTitleTextView(){
		List<Scenario> mScenarios = mPersistenceManager.getScenarios();
		int nbScenarios = (mScenarios != null)?mScenarios.size():0;
		Scenario mCurrentScenario = (mScenarios != null && mIndexScenario >= 0 && mIndexScenario < nbScenarios)?mScenarios.get(mIndexScenario):null;

		if(mCurrentScenario != null){
			int nbPictures = (mPicturesList != null)?mPicturesList.size():0;

			if(mPictureIndex >= 0 && mPictureIndex < nbPictures){
				if(mPicturesList != null && mPicturesList.get(mPictureIndex) != "" && mPicturesList.get(mPictureIndex) != null && mCurrentScenario.getScenarioItemAt(this.mPictureIndex) != null){
					if(this.mTitleTextView != null){
						Spanned type = SystemUtil.getString(ScenariosActivity.this, R.string.take_photo_again, mCurrentScenario.getScenarioItemAt(this.mPictureIndex).getType());	
						this.mTitleTextView.setText(type);
					}
				}else{
					if(this.mTitleTextView != null && mCurrentScenario.getScenarioItemAt(this.mPictureIndex) != null){
						this.mTitleTextView.setText(SystemUtil.getString(ScenariosActivity.this, R.string.take_photo, mCurrentScenario.getScenarioItemAt(this.mPictureIndex).getType()));
					}
				}
				if(this.mTitleTextView != null)this.mTitleTextView.invalidate();
			}
		}
		if(this.mTitleTextView != null)this.mTitleTextView.setVisibility((mMode == MODE_PHOTO && mMode != MODE_BARCODE_READER && mMode != MODE_EMERGENCY)?View.GONE:View.VISIBLE); 
	}

	public final static int PATIENT_INFOS_MAX_CHARACTERS = 20;

	private void updateCurrentPatientTextView(String patientInfos){
		if(mCurrentPatientInfosTextView != null){
			if(patientInfos != null){
				String patientInfosFormatted = (patientInfos != null && patientInfos.length() <= PATIENT_INFOS_MAX_CHARACTERS)?patientInfos:patientInfos.substring(0, PATIENT_INFOS_MAX_CHARACTERS)+" ...";
				patientInfosFormatted = ""+patientInfosFormatted+"  "+((mMode == MODE_SCENARIO)?getResources().getString(R.string.sur):"")+"";
				mCurrentPatientInfosTextView.setText(Html.fromHtml(patientInfosFormatted));
			}

			mCurrentPatientInfosTextView.setVisibility(View.VISIBLE);
			mCurrentPatientInfosTextView.invalidate();
		}
	}

	private void updateVerticalListView(){
		if(mVerticalListViewAdapter != null && mVerticalListView != null && mPicturesList != null && mPictureIndex != -1){
			mVerticalListViewAdapter.setPhotos(mPicturesList);
			mVerticalListViewAdapter.setIndexPhotoSelected(mPictureIndex);
			mVerticalListViewAdapter.notifyDataSetChanged();
			mVerticalListView.setSelection(mPictureIndex);
			mVerticalListView.requestLayout();
			mVerticalListView.invalidate();
		}
	}

	private void updateGrid(){
		List<Scenario> mScenarios = mPersistenceManager.getScenarios();
		int nbScenarios = (mScenarios != null)?mScenarios.size():0;

		if(mIndexScenario >= 0 && mIndexScenario < nbScenarios){
			Scenario currentScenario = mScenarios.get(mIndexScenario); 
			int nbPictures = (currentScenario != null)?currentScenario.getNbScenarioItems():0;

			if(mPictureIndex >= 0 && mPictureIndex < nbPictures && currentScenario != null){
				if(mPreview != null)mPreview.updateLayout();
			}
		}
	}

	public ScenarioItem getCurrentScenarioItem() {
		List<Scenario> mScenarios = mPersistenceManager.getScenarios();
		ScenarioItem scenarioItem = null;
		int nbScenarios = (mScenarios != null)?mScenarios.size():0;

		if(mIndexScenario >= 0 && mIndexScenario < nbScenarios){
			Scenario currentScenario = mScenarios.get(mIndexScenario); 
			int nbPictures = (currentScenario != null)?currentScenario.getNbScenarioItems():0;

			if(mPictureIndex >= 0 && mPictureIndex < nbPictures && currentScenario != null){
				scenarioItem = currentScenario.getScenarioItemAt(mPictureIndex);
			}
		}
		return scenarioItem;
	}

	public ScenarioItem getScenarioItemAt(int index){
		List<Scenario> mScenarios = mPersistenceManager.getScenarios();
		ScenarioItem scenarioItem = null;
		int nbScenarios = (mScenarios != null)?mScenarios.size():0;

		if(mIndexScenario >= 0 && mIndexScenario < nbScenarios){
			Scenario currentScenario = mScenarios.get(mIndexScenario); 
			int nbPictures = (currentScenario != null)?currentScenario.getNbScenarioItems():0;

			if(index >= 0 && index < nbPictures && currentScenario != null){
				scenarioItem = currentScenario.getScenarioItemAt(index);
			}
		}
		return scenarioItem;
	}

	public int getPictureIndex(){
		return this.mPictureIndex;
	}

	private void updateFlash(){
		List<Scenario> mScenarios = mPersistenceManager.getScenarios();
		//this.mScenarios = mPersistenceManager.getScenarios();
		int nbScenarios = (mScenarios != null)?mScenarios.size():0;

		if(mIndexScenario >= 0 && mIndexScenario < nbScenarios){
			Scenario mCurrentScenario = mScenarios.get(mIndexScenario);

			if(mCurrentScenario != null){
				if(mCurrentScenario.getScenarioItemAt(mPictureIndex) != null){
					String flash = mCurrentScenario.getScenarioItemAt(mPictureIndex).getFlash();

					if(flash != "" && flash != null ){
						changeFlashMode(flash);
					}
				}
			}
		}
	}

	private void writeFlash(){
		//to be sure that scenarios are up to date !
		List<Scenario> mScenarios = mPersistenceManager.getScenarios();
		int nbScenarios = (mScenarios != null)?mScenarios.size():0;

		if(mIndexScenario >= 0 && mIndexScenario < nbScenarios){
			Scenario mCurrentScenario = mScenarios.get(mIndexScenario);

			if(mCurrentScenario != null){
				if(mCamera != null && mCamera.getParameters() != null){
					String flash = mCamera.getParameters().getFlashMode();

					if(flash != "" && flash != null){
						//Log.e("writeFlash","setFlash:"+flash);
						if(mCurrentScenario.getScenarioItemAt(mPictureIndex) != null)mCurrentScenario.getScenarioItemAt(mPictureIndex).setFlash(flash);
						if(mPersistenceManager != null && mScenarios != null)mPersistenceManager.setScenarios(mScenarios);
					}
				}
			}
		}
	}

	private void updateFocus(){
		List<Scenario> mScenarios = mPersistenceManager.getScenarios();
		int nbScenarios = (mScenarios != null)?mScenarios.size():0;

		if(mIndexScenario >= 0 && mIndexScenario < nbScenarios){
			Scenario mCurrentScenario = mScenarios.get(mIndexScenario);

			if(mCurrentScenario != null){
				if(mCurrentScenario.getScenarioItemAt(mPictureIndex) != null){
					String focus = mCurrentScenario.getScenarioItemAt(mPictureIndex).getFocus();

					if(focus != "" && focus != null){
						changeFocusMode(focus);
					}
				}
			}
		}
	}

	private void writeFocus(){
		//to be sure that scenarios are up to date !
		List<Scenario> mScenarios = mPersistenceManager.getScenarios();
		int nbScenarios = (mScenarios != null)?mScenarios.size():0;

		if(mIndexScenario >= 0 && mIndexScenario < nbScenarios){
			Scenario mCurrentScenario = mScenarios.get(mIndexScenario);

			if(mCurrentScenario != null){
				if(mCamera != null && mCamera.getParameters() != null){
					String focus = mCamera.getParameters().getFocusMode();

					if(mCurrentScenario.getScenarioItemAt(mPictureIndex) != null && focus != "" && focus != null){
						mCurrentScenario.getScenarioItemAt(mPictureIndex).setFocus(focus);

						if(mPersistenceManager != null && mScenarios != null)mPersistenceManager.setScenarios(mScenarios);
					}
				}
			}
		}
	}

	public void initializeFlashMode(){
		String flash = null;

		if(mMode == ScenariosActivity.MODE_SCENARIO){
			ScenarioItem scenarioItem = getCurrentScenarioItem();
			flash = (scenarioItem != null)?scenarioItem.getFlash():null;

		}else if(mMode == ScenariosActivity.MODE_PHOTO || mMode == ScenariosActivity.MODE_BARCODE_READER || mMode == ScenariosActivity.MODE_EMERGENCY){
			flash = mPersistenceManager.getFlash();
		}

		if(flash != null){
			try{
				if(mCamera != null){
					Camera.Parameters p = mCamera.getParameters();
					if(p != null){
						List<String> supportedFlashModes = p.getSupportedFlashModes();

						if(supportedFlashModes != null && supportedFlashModes.contains(flash)){
							if(flash != "")p.setFlashMode(flash);

							try{
								mCamera.setParameters(p);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
	}

	public void initializeFocusMode(){
		String focus = null;

		if(mMode == ScenariosActivity.MODE_SCENARIO){
			ScenarioItem scenarioItem = getCurrentScenarioItem();
			focus = (scenarioItem != null)?scenarioItem.getFocus():null;
		}else if(mMode == ScenariosActivity.MODE_PHOTO || mMode == ScenariosActivity.MODE_BARCODE_READER || mMode == ScenariosActivity.MODE_EMERGENCY){
			focus = mPersistenceManager.getFocus();
		}

		if(focus != null){
			try{
				if(mCamera != null){
					Camera.Parameters p = mCamera.getParameters();
					if(p != null){
						List<String> supportedFocusModes = p.getSupportedFocusModes();

						if(supportedFocusModes != null && supportedFocusModes.contains(focus)){
							if(focus != "")p.setFocusMode(focus);

							try{
								mCamera.setParameters(p);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
	}

	private void initializeListeners(){
		this.initializeOrientationListener();

		this.mShakeDetector = new ShakeDetector(ScenariosActivity.this);

		if(this.mShakeDetector != null){
			this.mShakeDetector.initialize();
			this.mShakeDetector.setInterface(new ShakeDetector.onShakeInterface(){
				@Override
				public void setOnShakeEvent(){
					if(mAppLoaded && mScenariosLoaded){
						if(mCameraSettingsPopupManager != null){
							boolean visible = mCameraSettingsPopupManager.isShowing();

							if(visible){
								mCameraSettingsPopupManager.hideCameraSettingsDialogPopup();
							}else{
								String flash = null;
								String focus = null;

								if(mMode == ScenariosActivity.MODE_SCENARIO){
									ScenarioItem scenarioItem = getCurrentScenarioItem();
									flash = (scenarioItem != null)?scenarioItem.getFlash():null;
									focus = (scenarioItem != null)?scenarioItem.getFocus():null;
								}else if(mMode == ScenariosActivity.MODE_PHOTO || mMode == ScenariosActivity.MODE_BARCODE_READER || mMode == ScenariosActivity.MODE_EMERGENCY){
									flash = mPersistenceManager.getFlash();
									focus = mPersistenceManager.getFocus();
								}

								mCameraSettingsPopupManager.showCameraSettingsDialogPopup(ScenariosActivity.this,flash,focus,(mPersistenceManager != null)?mPersistenceManager.getSoundIndex():-1);
							}
						}
					}
				}
			});
			this.mShakeDetector.handleRegister();
		}
	}

	private void initializeOrientationListener(){
		this.myOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL){
			@Override
			public void onOrientationChanged(int orientation) {
				if (orientation == ORIENTATION_UNKNOWN) return;
				android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
				android.hardware.Camera.getCameraInfo(cameraId, info);
				orientation = (orientation + 45) / 90 * 90;
				int rotation = 0;
				if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
					rotation = (info.orientation - orientation + 360) % 360;
				} else {  // back-facing camera
					rotation = (info.orientation + orientation) % 360;
				}
				if(mCamera != null){
					Parameters p = mCamera.getParameters();

					if(p != null){
						p.setRotation(rotation);
						try{
							mCamera.setParameters(p);
						}catch(Exception e){
							e.printStackTrace();
						}
					}

					switch (rotation) {

					case 0:
						mAngleRot = 0;
						break;
					case 90:
						mAngleRot = 270;
						break;
					case 180: 
						mAngleRot = 180;
						break;
					case 270:
						mAngleRot = 90;
						break;
					}

					if(mVerticalListViewAdapter != null)mVerticalListViewAdapter.setAngleRotation(mAngleRot);
					if(mVerticalListView != null)mVerticalListView.requestLayout();	

					if(mCameraSettingsPopupManager != null)mCameraSettingsPopupManager.invalidate(mAngleRot);
				}
			}};

			if (myOrientationEventListener != null && myOrientationEventListener.canDetectOrientation()) {
				myOrientationEventListener.enable();
			} 
	}

	public boolean isPictureTaken(){
		return this.mIsPictureTaken;
	}

	public void takePicture(){
		try{
			if(!mIsPictureTaken && mCamera != null){
				mIsPictureTaken = true;

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(mDialog != null)mDialog.showFRProgressDialog();
					}
				});

				mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				if(mgr != null)mgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);

				mCamera.takePicture(new Camera.ShutterCallback() {
					@Override
					public void onShutter() {
						int soundToPlay = 0;

						if(mPersistenceManager != null){
							List<Sound> sounds = mPersistenceManager.getSounds();
							int nbSounds = (sounds != null)?sounds.size():0;
							int soundIndex = mPersistenceManager.getSoundIndex();

							if(soundIndex >= 0 && soundIndex < nbSounds){
								Sound sound = sounds.get(soundIndex);

								if(sound != null){
									int soundRawId = sound.getRawResourceId();

									if(soundRawId != 0){
										if(mgr != null)mgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
										SoundUtil.playSound(ScenariosActivity.this, soundRawId, true);	
									}
								}
							}
						}
					}
				}, null, mPicture);


			}
		}catch(Exception e){
			if(e != null)e.printStackTrace();
			mIsPictureTaken = false;
		}
	}

	public Camera getCamera(){
		return this.mCamera;
	}

	public Camera getCameraInstance(){
		Camera c = null;
		try {
			c = Camera.open();
		}catch (Exception e){
			if(e != null)e.getMessage();

			launchCameraInaccessible(ScenariosActivity.this);
		}
		return c;
	}

	private void initializePictureCallback(){
		mPicture = new PictureCallback() {
			@Override
			public void onPictureTaken(final byte[] data, Camera camera) {
				mPictureFile = FileUtil.getOutputMediaFile(FileUtil.MEDIA_TYPE_IMAGE);
				boolean diskIsFull = SystemUtil.isStorageFull(ScenariosActivity.this);

				if(diskIsFull){
					launchDiskFull(ScenariosActivity.this);

					if(mDialog != null)mDialog.cancelFRProgressDialog();

					if(mCamera != null)mCamera.startPreview();
				}else if(mPictureFile != null){
					changeViewAnimatorDisplayChild(1);

					try {
						FileOutputStream fos = new FileOutputStream(mPictureFile);

						if(fos != null){
							fos.write(data);
							fos.flush();
							fos.close();
						}

						if(mMode == MODE_PHOTO || mMode == MODE_BARCODE_READER || mMode == MODE_EMERGENCY){
							SystemUtil.showPopup(ScenariosActivity.this, SystemUtil.getString(ScenariosActivity.this, R.string.photo_from_patient_saved1, patientInfos).toString());
						}else if(mMode == MODE_SCENARIO){
							if(getCurrentScenarioItem() != null){
								SystemUtil.showPopup(ScenariosActivity.this,SystemUtil.getString(ScenariosActivity.this, R.string.photo_from_patient_saved2, getCurrentScenarioItem().getType(), patientInfos).toString());
							}
						}

						FileUtil.refreshPhotoInFileSystem(ScenariosActivity.this, mPictureFile.getAbsolutePath());

						if(mCamera != null)mCamera.startPreview();

					}catch(IOException e){
						e.printStackTrace();

						launchWriteToDiskImpossible(ScenariosActivity.this);
					}catch(Exception e){
						e.printStackTrace();
					}

					ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(ScenariosActivity.this);
					int imageMaxWidth = -1, imageMaxHeight = -1; 

					if(screenDimensions != null && screenDimensions.size() == 2){
						imageMaxWidth = screenDimensions.get(0).intValue();
						imageMaxHeight = screenDimensions.get(1).intValue();
					}

					final int imageMaxWidthCop = imageMaxWidth;
					final int imageMaxHeightCop = imageMaxHeight;

					if(mMode == MODE_PHOTO){
						changeViewAnimatorDisplayChild(0);

						mPictureFiles.add(mPictureFile.getAbsolutePath());
					}else if(mMode == MODE_BARCODE_READER){
						String path = mPictureFile.getAbsolutePath();

						sendPictureToKitview(path, -1);

						changeViewAnimatorDisplayChild(0);

						mDialog.cancelFRProgressDialog();

						//MODE_SCENARIO MODE_EMERGENCY
					}else if(mMode == MODE_SCENARIO){
						final Bitmap b = (imageMaxWidthCop != -1 && imageMaxHeightCop != -1)?ImageUtil.decodeFile(mPictureFile, imageMaxWidthCop, imageMaxHeightCop):null;

						updateImageView(b);
						List<Scenario> mScenarios = mPersistenceManager.getScenarios();
						int nbScenario = (mScenarios != null)?mScenarios.size():0;
						Scenario mCurrentScenario = (mScenarios != null && mIndexScenario >= 0 && mIndexScenario < nbScenario)?mScenarios.get(mIndexScenario):null;
						int nbScenarioItems = (mCurrentScenario != null)?mCurrentScenario.getNbScenarioItems():0;

						if(mPicturesList != null && mPictureFile != null){
							if(mPictureIndex < nbScenarioItems - 1){
								if(mPicturesList.get(mPictureIndex+1) != ""){
									mPicturesList.set(mPictureIndex, mPictureFile.getAbsolutePath());
									updateTitleTextView();
								}else{
									mPicturesList.set(mPictureIndex++, mPictureFile.getAbsolutePath());

									updateGrid();
									updateFlash();
									updateFocus();
									updateTitleTextView();
								}
								updateVerticalListView();
							}else{
								mPicturesList.set(mPictureIndex, mPictureFile.getAbsolutePath());

								updateTitleTextView();
								updateVerticalListView();
							}
						}
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									if(mDialog != null)mDialog.cancelFRProgressDialog();

									Thread.sleep(mPersistenceManager.getPictureTimeDisplay());

									runOnUiThread(new Runnable() {
										public void run() {
											changeViewAnimatorDisplayChild(0);
										}
									});
								}catch (InterruptedException e) {
									e.printStackTrace();
								}
							}

						}).start();
					}else if(mMode == MODE_EMERGENCY){
						changeViewAnimatorDisplayChild(0);

						mDialog.cancelFRProgressDialog();

						mPicturesList.add(mPictureFile.getAbsolutePath());
					}
				}
				mIsPictureTaken = false;
			}
		};
	}


	ArrayList<String> mPictureFiles = new ArrayList<String>();

	private Thread mSendPicturesThread;

	private void initializeSendPicturesThread(){
		mSendPicturesThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(!mActivityKilled){
					try{

						synchronized (mPictureFiles) {
							for(String s:mPictureFiles){
								sendPictureToKitview(s,-1);
								mPictureFiles.remove(s);
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		});
		mSendPicturesThread.start();
	}

	private void sendPictureToKitview(String path, int indexPictureToSend){//final Bitmap b, final int nbPictures, final int indexPictureToSend){
		//to do : tester si on a une connection internet en amont sinon GetCurrentIdPatientSync va bloquer  !!!!!!!!!!!!!!

		File pictureFile = new File(path);

		try {
			if(mDialog != null)mDialog.cancelFRProgressDialog();

			String pictureName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg";
			int packetSize = 1000000;//1048576;//1Mo
			int binTot;
			int strTot;
			long mLen;
			boolean isfirst;
			byte[] mBuffer = new byte[packetSize];

			InputStream inFile = new FileInputStream(pictureFile);
			binTot = 0;
			strTot = 0;
			isfirst = true;

			long totalBytes = pictureFile.length();

			KitviewUtil.DeleteSessionFile(ScenariosActivity.this,pictureName);

			while(binTot < totalBytes){
				mLen = (binTot+packetSize < totalBytes)?packetSize:(totalBytes-binTot);
				mBuffer = new byte[(int)mLen];
				inFile.read(mBuffer,0, (int) mLen);

				String encodedImage2 = Base64.encodeToString(mBuffer, Base64.DEFAULT);

				TJSONObject jo2 = new TJSONObject();
				jo2.addPairs("UploadedData", encodedImage2);

				KitviewUtil.UploadFileInMultipleParts(ScenariosActivity.this,pictureName, jo2,binTot);

				binTot += mLen;
			}

			inFile.close();

			String keywords = "";

			if(mMode == MODE_SCENARIO){
				if(getScenarioItemAt(indexPictureToSend) != null)keywords = getScenarioItemAt(indexPictureToSend).getKeywords();			
			}

			if(mMode == MODE_SCENARIO || mMode == MODE_PHOTO){
				KitviewUtil.AddSessionFilenameToIdPatient(ScenariosActivity.this,mPatientId, pictureName, keywords, 1,1);
			}

			if(mMode == MODE_BARCODE_READER){
				boolean sendToKeyboard = (BarCodeModesPopupManager.BAR_CODE_MODE_SELECTED == 0);
				String cmdName = "";
				String cmdParam = "";

				String barcodes = KitviewUtil.GetBC(ScenariosActivity.this, pictureName, sendToKeyboard?"":cmdName, sendToKeyboard?"":cmdParam, sendToKeyboard?1:0);

				if(barcodes != null){
					StringTokenizer st = new StringTokenizer(barcodes, " ");
					int nbTokens = (st != null)?st.countTokens():0;

					String res = "";

					for(int i=0;i<nbTokens;i++){
						String barcode = st.nextToken();
						res += " "+barcode;
					}

					SystemUtil.showPopup(ScenariosActivity.this,res);
				}
			}
			//OutOfMemory
		}catch(OutOfMemoryError e){
			e.printStackTrace();

			launchOOM(ScenariosActivity.this);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause(){
		super.onPause();     
		releaseCamera();   
		firstTime = false;

		if (provider != null && provider.isListening()){
			provider.stopListening();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(!firstTime){
			mCamera = getCameraInstance();

			if(mCamera != null){
				initializePreviewCallback();
				mCamera.startPreview();
				calledRelease = false;
			}
			firstTime = true;
		}

		//Level
		provider = OrientationProvider.getInstance(this);

		//Orientation manager
		if (provider != null && provider.isSupported(this)) {
			provider.startListening(this,ScenariosActivity.this);
		}
	}

	@Override
	public void onOrientationChanged(Orientation orientation, float pitch, float roll, float balance) {
		if(orientation != null && provider != null){
			boolean isLevel = orientation.isLevel(pitch, roll, balance, provider.getSensibility());

			if(view2 != null){
				if (isLevel) view2.setVisibility(View.GONE);
				else view2.setVisibility(View.VISIBLE);

				if(!mIsPictureTaken){
					view2.onOrientationChanged(orientation, pitch, roll, balance);
				}
			}
		}
	}

	@Override
	public void onCalibrationReset(boolean success) {}

	@Override
	public void onCalibrationSaved(boolean success) {}

	public static ScenariosActivity getContext() {
		return CONTEXT;
	}

	public static OrientationProvider getProvider() {
		return getContext().provider;
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();

		mActivityKilled = true;

		if(serverSocket != null)
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		this.releaseCamera();

		if(this.mShakeDetector != null)this.mShakeDetector.handleUnregister();

		if(this.mCameraSettingsPopupManager != null) this.mCameraSettingsPopupManager.destroy();

		if (myOrientationEventListener != null && myOrientationEventListener.canDetectOrientation()) {
			myOrientationEventListener.disable();
		}

		if(provider != null && provider.isListening()){
			provider.destroy();
			provider = null;
		}	

		mgr = null;
		SoundUtil.destroy();

		CONTEXT = null;

		if(mVerticalListViewAdapter != null)mVerticalListViewAdapter.destroy();
		if(mVerticalListView != null)mVerticalListView.setAdapter(null);

		//if(mScenarios != null){
		//	mScenarios.clear();
		//	mScenarios = null;
		//}

		if(mDialog != null){
			mDialog.cancelFRProgressDialog();
			mDialog = null;
		}

	}

	private void releaseCamera(){
		if (mCamera != null){
			calledRelease = true;
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);

			if(mPreview != null && mPreview.getHolder() != null)mPreview.getHolder().removeCallback(mPreview);

			mCamera.release();    
			mCamera = null;
		}
	}

	public int getMode() {
		return this.mMode;
	}

	/**Scenarios**/
	private Spinner mScenariosSpinner;
	private TextView mScenariosTextview;
	private MyAdapter mScenariosAdapter;
	private boolean mFirstTimeScenarioSoundEventThrown = true;

	public void udpateScenarioTextView(String text){
		mScenariosTextview.setText(text);
		mScenariosTextview.requestLayout();
		mScenariosTextview.invalidate();
	}

	public void updateCurrentScenario(int arg2){
		List<Scenario> mScenarios = mPersistenceManager.getScenarios();
		int nbScenarioModes = (mScenarios != null)?mScenarios.size():0;

		if(arg2 >= 0 && arg2 < nbScenarioModes){
			mPersistenceManager.setScenarioIndex(arg2);
			changeScenario(arg2);

			if(getCamera() != null && getCamera().getParameters() != null){
				String flash = getCamera().getParameters().getFlashMode();
				String focus = getCamera().getParameters().getFocusMode();
				mCameraSettingsPopupManager.updateFlashSpinner(flash);
				mCameraSettingsPopupManager.updateFocusSpinner(focus);
			}
		}
	}

	private void initializeScenarios(){
		this.mScenariosTextview = (TextView) this.findViewById(R.id.scenarios_textview);
		this.mScenariosTextview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ChromeHelpPopup c = new ChromeHelpPopup(ScenariosActivity.this, "", R.layout.layout_listview_chrome_popup);
				List<Scenario> mScenarios = mPersistenceManager.getScenarios();
				c.initializeListView(ScenariosActivity.this, mScenarios);
				c.show(mScenariosTextview);
			}
		});

		if(mPersistenceManager != null && ((this)) != null && mScenariosTextview != null){
			//List<Scenario> mScenariosModes = ((this)).getScenarios();
			List<Scenario> mScenarios = mPersistenceManager.getScenarios();
			final int nbScenarioModes = (mScenarios != null)?mScenarios.size():0;

			if(nbScenarioModes > 0){
				int scenarioIndex = (mPersistenceManager.getScenarioIndex() != PersistenceManager.VALUE_UNDEFINED_INT)?mPersistenceManager.getScenarioIndex():-1;

				if(scenarioIndex != -1){
					this.mScenariosTextview.setText(mScenarios.get(scenarioIndex).getNom());
					this.mScenariosTextview.requestLayout();
					this.mScenariosTextview.invalidate();
				}
			}
		}
	}

	public class MyAdapter extends ArrayAdapter<String> { 
		private int mResource;
		private String [] objects;

		public MyAdapter(Context ctx, int txtViewResourceId, String[] objects) {
			super(ctx, txtViewResourceId, objects); 
			this.mResource = txtViewResourceId;
			this.objects = objects;
		}

		@Override public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt); 
		} 

		@Override public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt); 
		}

		public View getCustomView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			TextView tv = null;

			if(convertView == null){
				convertView = inflater.inflate(mResource, parent, false);
			}

			convertView.setBackgroundColor(Color.argb(0, 0, 0, 0));
			convertView.setBackgroundDrawable(null);
			parent.setBackgroundDrawable(null);
			parent.setBackgroundColor(Color.argb(0, 0, 0, 0));

			tv = (TextView) convertView.findViewById(R.id.tvLanguage);
			tv.setBackgroundColor(Color.argb(0, 0, 0, 0));

			int nbItems = (this.objects != null)?this.objects.length:0;

			if(position >= 0 && position < nbItems){
				tv.setText(objects[position]);
			}

			return convertView;
		}
	}

	public TextView getTitleTextView(){
		return this.mTitleTextView;
	}

	public TextView getCurrentPatientInfosTextView(){
		return this.mCurrentPatientInfosTextView;
	}

	public TextView getScenariosTextView(){
		return this.mScenariosTextview;
	}

	public boolean isRightVerticalListViewShowing(){
		return (mVerticalListView.getVisibility()==View.VISIBLE);
	}
}