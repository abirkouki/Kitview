package activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import model.Module;
import model.PersistenceManager;
import model.rest.Personne;
import util.components.gallery.ImageCache;
import util.components.gallery.ImageFetcher;
import util.components.progressdialog.FRProgressDialog;
import util.network.KitviewUtil;
import util.system.SystemUtil;
import view.adapter.ModulesAdapter;
import view.popup.GenericPopupManager;
import view.popup.QuitPopupManager;
import com.dentalcrm.kitview.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ViewAnimator;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends FragmentActivity{
	//Views
	private ViewAnimator mViewAnimator;
	private ModulesAdapter mModulesAdapter0,mModulesAdapter,mModulesAdapter2;
	private GridView mGridView0, mGridView, mGridView2;
	private ArrayList<Module> mModules0,mModules,mModules2;
	private boolean mInitializationFinished0,mInitializationFinished1,mInitializationFinished2;
	private int mSpacing;
	private VideoView mVideoView;
	private static FRProgressDialog mDialog;
	private TextView mCurrentPatientInfosTextView;
	private TextView mCopyrightTextView;
	private static GenericPopupManager mGenericPopupManager;
	private LinearLayout mActualSituationTextView;
	private LinearLayout mBottomInfosLinearLayout;

	//Constants
	private final static String IMAGE_THUMBNAIL_CACHE_DIR = "image_thumbnails";

	//Cache
	private static ImageFetcher mThumbnailImageFetcherFromDisk;
	private static ImageCache.ImageCacheParams cacheParams2;

	//Model
	private PersistenceManager mPersistenceManager;
	private int mOrientation;

	public final static String KEY_TEST_CONNECTION = "KEY_TEST_CONNECTION";

	private boolean mCheckKitViewConnection = true;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.activity_main);

		if(savedInstanceState != null){
			mCheckKitViewConnection = (Boolean) savedInstanceState.get(KEY_TEST_CONNECTION);
		}

		this.mOrientation = getResources().getConfiguration().orientation;

		this.mPersistenceManager = PersistenceManager.getInstance();

		this.mViewAnimator = (ViewAnimator) findViewById(R.id.va_main);
		this.mVideoView = (VideoView)findViewById(R.id.videoview);
		this.mCurrentPatientInfosTextView = (TextView) findViewById(R.id.tv_current_patient_infos);
		this.mCopyrightTextView = (TextView) findViewById(R.id.copyright);
		this.mActualSituationTextView = (LinearLayout) findViewById(R.id.ll_parent_infos);
		this.mBottomInfosLinearLayout = (LinearLayout) findViewById(R.id.ll_bottom_infos);

		this.initializeVideoView();

		this.mGridView0 = (GridView)this.findViewById(R.id.gridview_home0);
		this.mGridView = (GridView)this.findViewById(R.id.gridview_home);
		this.mGridView2 = (GridView)this.findViewById(R.id.gridview_home2);

		try{
			int mode = (mPersistenceManager != null)?mPersistenceManager.getMode():PersistenceManager.MODE_SELECTION;

			if(mode == PersistenceManager.MODE_SURGERY){
				setViewAnimatorIndex(1);
				initializeGridView();
			}else if(mode == PersistenceManager.MODE_PATIENT){
				setViewAnimatorIndex(2);
				initializeGridView2();
			}else{
				setViewAnimatorIndex(0);
				initializeGridView0();
			}

			mDialog = new FRProgressDialog(this, "",false);

			this.initializeAppImageCache();

			if(mViewAnimator.getDisplayedChild() != 0 && mCheckKitViewConnection)checkKitViewConnection();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void launchWifiPopup(Activity context){
		String title = context.getResources().getString(R.string.wifi_title);
		String content = context.getResources().getString(R.string.wifi_content);
		launchGenericPopup(context,title, content, false);
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
			public void onCancelClick(){
				mGenericPopupManager.hideLineDeleteDialogPopup();
			}
		});
	}

	public void checkKitViewConnection(){
		/*mDialog.showFRProgressDialog();
		KitviewUtil.isKitviewAvailable(MainActivity.this, new KitviewUtil.ITestConnectionResponse() {
			@Override
			public void onResponse(final int connectionEstablished){
				mDialog.cancelFRProgressDialog();
				if(connectionEstablished != KitviewUtil.TEST_CONNECTION_OK){// && mSettingsHasBeenLaunched){
					runOnUiThread(new Runnable() {
						public void run() {
							//String text = "";

							if(connectionEstablished == KitviewUtil.TEST_CONNECTION_WIFI_KO){
								//text = getResources().getString(R.string.wifi_ko);

								launchWifiPopup(MainActivity.this);
							}else if(connectionEstablished == KitviewUtil.TEST_CONNECTION_KITVIEW_KO){// && !mSettingsHasBeenLaunched){
								//text = getResources().getString(R.string.connection_to_kitview_ko);

								//SystemUtil.showPopup(MainActivity.this,text);//getResources().getString(R.string.connection_to_kitview_ko));

								//mAppSettingsPopupManager.showCameraSettingsDialogPopup();

								launchSettings(MainActivity.this,true);
							}
						}
					});
				}
			}
		});*/
	}

	private void initializeVideoView(){
		Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);

		if(mVideoView != null){
			if(path != null )mVideoView.setVideoURI(path);

			mVideoView.seekTo(0);
			mVideoView.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mVideoView.start();
					mp.setLooping(true);
				}
			});
		}
	}

	private void initializeAppImageCache(){
		this.cacheParams2 = new ImageCache.ImageCacheParams(this, IMAGE_THUMBNAIL_CACHE_DIR);
		if(this.cacheParams2 != null)this.cacheParams2.setMemCacheSizePercent(0.25f);

		int mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);

		this.mThumbnailImageFetcherFromDisk = new ImageFetcher(this, mImageThumbSize,mImageThumbSize);

		if(mThumbnailImageFetcherFromDisk != null && cacheParams2 != null){
			this.mThumbnailImageFetcherFromDisk.setLoadingImage(R.drawable.empty_photo);
			mThumbnailImageFetcherFromDisk.setUseAttachedViewModified(false);
			this.mThumbnailImageFetcherFromDisk.addImageCache(getFragmentManager(), cacheParams2);
		}
	}

	public static ImageFetcher getImageFetcher(Personne p){
		mThumbnailImageFetcherFromDisk.setPatientId(p);
		return mThumbnailImageFetcherFromDisk;
	}

	public static void setUseCache(boolean useCache){
		getImageFetcher(null).setUseCache(useCache);
	}

	public static void setUseAttachedViewModified(boolean useCache){
		getImageFetcher(null).setUseAttachedViewModified(useCache);
	}


	public static ImageCache.ImageCacheParams getImageCache(){
		return cacheParams2;
	}

	public void initializeGridView(){
		this.mModules = new ArrayList<Module>();
		this.mModules.add(new Module(R.string.picture_shot_once, R.color.color1, R.drawable.ic_action_camera));
		this.mModules.add(new Module(R.string.picture_shot_several, R.color.color2, R.drawable.ic_action_new_picture));
		this.mModules.add(new Module(R.string.barcode_reader, R.color.color3, R.drawable.barcode));
		this.mModules.add(new Module(R.string.folder, R.color.color4, R.drawable.ic_action_person));
		this.mModules.add(new Module(R.string.folder2, R.color.color5, R.drawable.ic_action_group));
		this.mModules.add(new Module(R.string.settings, R.color.color6, R.drawable.ic_action_settings));

		this.mInitializationFinished1 = false;

		if(mGridView != null){
			this.mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
					new ViewTreeObserver.OnGlobalLayoutListener() {
						@SuppressLint("NewApi")
						@Override
						public void onGlobalLayout() {
							if(!mInitializationFinished1){
								mSpacing = getWindowManager().getDefaultDisplay().getWidth()/40;

								ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mGridView.getLayoutParams();

								if(mlp != null){
									mlp.setMargins(mSpacing, mSpacing, mSpacing, mSpacing);
									mGridView.setLayoutParams(mlp);
								}

								mGridView.setHorizontalSpacing(mSpacing);
								mGridView.setVerticalSpacing(mSpacing);

								mInitializationFinished1 = true;

								int nbItemsPerRow = 0,nbRows = 0;

								if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
									nbItemsPerRow = 3;
									nbRows = 2;//3;
								}else if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
									nbItemsPerRow = 2;
									nbRows = 3;//5;
								}

								mModulesAdapter = new ModulesAdapter(mModules, nbItemsPerRow,nbRows, mGridView, MainActivity.this, mSpacing);

								//After set setMargins ==> addOnGlobalLayoutListener called second time later
							}else{
								mGridView.setAdapter(mModulesAdapter);

								if(mGridView.getViewTreeObserver() != null){
									if (SystemUtil.hasJellyBean())mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
									else mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
								}
							}
						}
					});
			this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
					switch (arg2) {

					// Photo shot once
					case 0:
						if(mDialog != null)mDialog.showFRProgressDialog();

						KitviewUtil.isKitviewAvailable(MainActivity.this, new KitviewUtil.ITestConnectionResponse() {
							@Override
							public void onResponse(final int connectionEstablished) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										runOnUiThread(new Runnable() {
											public void run() {
												if(connectionEstablished == KitviewUtil.TEST_CONNECTION_OK){
													Intent intent = new Intent(MainActivity.this.getApplicationContext(), ScenariosActivity.class);

													if(intent != null){
														intent.putExtra(ScenariosActivity.KEY_MODE, ScenariosActivity.MODE_PHOTO);
														intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
														MainActivity.this.getApplicationContext().startActivity(intent);
													}
												}else{
													if(connectionEstablished == KitviewUtil.TEST_CONNECTION_WIFI_KO){
														launchWifiPopup(MainActivity.this);

													}else if(connectionEstablished == KitviewUtil.TEST_CONNECTION_KITVIEW_KO){
														launchSettings(MainActivity.this,true);
													}
												}
												if(mDialog != null)mDialog.cancelFRProgressDialog();
											}
										});
									}
								}).start();
							}
						});
						break;

						// Scenarios
					case 1:
						if(mDialog != null)mDialog.showFRProgressDialog();

						KitviewUtil.isKitviewAvailable(MainActivity.this, new KitviewUtil.ITestConnectionResponse() {
							@Override
							public void onResponse(final int connectionEstablished) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										runOnUiThread(new Runnable() {
											public void run() {
												if(connectionEstablished == KitviewUtil.TEST_CONNECTION_OK){
													Intent intent = new Intent(MainActivity.this.getApplicationContext(), ScenariosActivity.class);

													if(intent != null){
														intent.putExtra(ScenariosActivity.KEY_MODE, ScenariosActivity.MODE_SCENARIO);
														intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
														MainActivity.this.getApplicationContext().startActivity(intent);
													}
													if(mDialog != null)mDialog.cancelFRProgressDialog();
												}else{
													if(mDialog != null)mDialog.cancelFRProgressDialog();

													String text = "";

													if(connectionEstablished == KitviewUtil.TEST_CONNECTION_WIFI_KO){
														text = getResources().getString(R.string.wifi_ko);

														launchWifiPopup(MainActivity.this);

													}else if(connectionEstablished == KitviewUtil.TEST_CONNECTION_KITVIEW_KO){
														launchSettings(MainActivity.this,true);
													}
												}
											}
										});
									}
								}).start();
							}
						});

						break;

						// Barcode reader
					case 2:
						if(mDialog != null)mDialog.showFRProgressDialog();

						KitviewUtil.isKitviewAvailable(MainActivity.this, new KitviewUtil.ITestConnectionResponse() {
							@Override
							public void onResponse(final int connectionEstablished) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										runOnUiThread(new Runnable() {
											public void run() {
												if(connectionEstablished == KitviewUtil.TEST_CONNECTION_OK){
													Intent intent = new Intent(MainActivity.this.getApplicationContext(), ScenariosActivity.class);

													if(intent != null){
														intent.putExtra(ScenariosActivity.KEY_MODE, ScenariosActivity.MODE_BARCODE_READER);
														intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
														MainActivity.this.getApplicationContext().startActivity(intent);
													}
												}else{
													String text = "";

													if(connectionEstablished == KitviewUtil.TEST_CONNECTION_WIFI_KO){
														text = getResources().getString(R.string.wifi_ko);

														launchWifiPopup(MainActivity.this);
													}else if(connectionEstablished == KitviewUtil.TEST_CONNECTION_KITVIEW_KO){
														launchSettings(MainActivity.this,true);
													}
												}
												if(mDialog != null)mDialog.cancelFRProgressDialog();
											}
										});
									}
								}).start();
							}
						});

						break;

						// My case
					case 3:
						launchMyCase(MainActivity.this,-1);

						break;

						// Same cases
					case 4:
						if(mDialog != null)mDialog.showFRProgressDialog();

						KitviewUtil.isKitviewAvailable(MainActivity.this, new KitviewUtil.ITestConnectionResponse() {
							@Override
							public void onResponse(final int connectionEstablished) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										runOnUiThread(new Runnable() {
											public void run() {
												if(connectionEstablished == KitviewUtil.TEST_CONNECTION_OK){

													KitviewUtil.GetCurrentIdPatient(MainActivity.this,new KitviewUtil.IIntResponse() {
														@Override
														public void onResponse(final int patientId) {
															Intent intent = new Intent(MainActivity.this.getApplicationContext(), SameCasesActivity.class);
															if(intent != null){
																intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
																intent.putExtra(SameCasesActivity.EXTRA_KEY_PATIENTID, patientId);
																MainActivity.this.getApplicationContext().startActivity(intent);
															}
															if(mDialog != null)mDialog.cancelFRProgressDialog();
														}
													});
												}else{
													if(mDialog != null)mDialog.cancelFRProgressDialog();

													String text = "";

													if(connectionEstablished == KitviewUtil.TEST_CONNECTION_WIFI_KO){
														text = getResources().getString(R.string.wifi_ko);

														launchWifiPopup(MainActivity.this);

													}else if(connectionEstablished == KitviewUtil.TEST_CONNECTION_KITVIEW_KO){
														launchSettings(MainActivity.this,true);
													}
												}
											}
										});
									}
								}).start();
							}
						});
						break;

						//Puzzle

						/*case 5:
						if(mDialog != null)mDialog.showFRProgressDialog();
						new Thread(new Runnable() {
							@Override
							public void run() {
								Intent intent = new Intent(MainActivity.this.getApplicationContext(), PuzzleActivity.class);
								if(intent != null){
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
									MainActivity.this.getApplicationContext().startActivity(intent);
								}
								if(mDialog != null)mDialog.cancelFRProgressDialog();
							}
						}).start();
						break;*/

						//Settings
					case 5:
						launchSettings(MainActivity.this,false);

						break;
					}
				}
			});
		}
	}

	public static void launchMyCase(final Activity context, final int patientId){
		if(mDialog != null)mDialog.showFRProgressDialog();

		KitviewUtil.isKitviewAvailable(context, new KitviewUtil.ITestConnectionResponse() {
			@Override
			public void onResponse(final int connectionEstablished) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						context.runOnUiThread(new Runnable() {
							public void run() {
								if(connectionEstablished == KitviewUtil.TEST_CONNECTION_OK){
									Intent intent = new Intent(context.getApplicationContext(), FolderActivity.class);
									if(intent != null){
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.putExtra(FolderActivity.EXTRA_KEY_PATIENTID, (patientId != -1)?patientId:KitviewUtil.GetCurrentIdPatientSync(context));
										//mSettingsHasBeenLaunched = true;
										context.getApplicationContext().startActivity(intent);
									}
									if(mDialog != null)mDialog.cancelFRProgressDialog();
								}else{
									if(mDialog != null)mDialog.cancelFRProgressDialog();

									String text = "";

									if(connectionEstablished == KitviewUtil.TEST_CONNECTION_WIFI_KO){
										launchWifiPopup(context);
									}else if(connectionEstablished == KitviewUtil.TEST_CONNECTION_KITVIEW_KO){
										text = context.getResources().getString(R.string.connection_to_kitview_ko);

										launchSettings(context,true);
									}
								}
							}
						});
					}
				}).start();
			}
		});
	}

	public static void launchSettings(Activity context, boolean connectionKo){
		if(!SettingsActivity.IS_LAUNCHED){
			if(connectionKo){
				String text = context.getResources().getString(R.string.connection_to_kitview_ko);
				SystemUtil.showPopup(context,text);
			}

			SettingsActivity.IS_LAUNCHED = true;
			Intent intent = new Intent(context.getApplicationContext(), SettingsActivity.class);
			context.startActivity(intent);
		}
	}

	public void initializeGridView0(){
		this.mModules0 = new ArrayList<Module>();
		this.mModules0.add(new Module(R.string.cabinet, R.color.color1, R.drawable.ic_action_group));
		this.mModules0.add(new Module(R.string.patient, R.color.color2, R.drawable.ic_action_person));

		this.mInitializationFinished0 = false;

		if(this.mGridView0 != null){
			this.mGridView0.getViewTreeObserver().addOnGlobalLayoutListener(
					new ViewTreeObserver.OnGlobalLayoutListener() {
						@SuppressLint("NewApi")
						@Override
						public void onGlobalLayout() {
							if(!mInitializationFinished0){
								mSpacing = getWindowManager().getDefaultDisplay().getWidth()/40;

								ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mGridView0.getLayoutParams();

								if(mlp != null){
									mlp.setMargins(mSpacing, mSpacing, mSpacing, mSpacing);
									mGridView0.setLayoutParams(mlp);
								}

								mGridView0.setHorizontalSpacing(mSpacing);
								mGridView0.setVerticalSpacing(mSpacing);

								int nbItemsPerRow = 0,nbRows = 0;

								if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
									nbItemsPerRow = 2;
									nbRows = 2;
								}else if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
									nbItemsPerRow = 2;
									nbRows = 3;
								}

								mInitializationFinished0 = true;
								mModulesAdapter0 = new ModulesAdapter(mModules0, nbItemsPerRow,nbRows, mGridView0, MainActivity.this, mSpacing);

								//After set setMargins ==> addOnGlobalLayoutListener called second time later
							}else{
								mGridView0.setAdapter(mModulesAdapter0);

								if(mGridView0.getViewTreeObserver() != null){
									if (SystemUtil.hasJellyBean())mGridView0.getViewTreeObserver().removeOnGlobalLayoutListener(this);
									else mGridView0.getViewTreeObserver().removeGlobalOnLayoutListener(this);
								}
							}
						}
					});

			this.mGridView0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
					switch (arg2) {

					case 0:
						setViewAnimatorIndex(1);
						initializeGridView();
						mPersistenceManager.setMode(PersistenceManager.MODE_SURGERY);
						break;
					case 1:
						setViewAnimatorIndex(2);
						initializeGridView2();
						mPersistenceManager.setMode(PersistenceManager.MODE_PATIENT);
						break;
					}
					checkKitViewConnection();
				}
			});
		}
	}

	public void initializeGridView2(){
		this.mModules2 = new ArrayList<Module>();
		this.mModules2.add(new Module(R.string.picture_shot_emergency, R.color.color1, R.drawable.ic_action_camera));
		this.mModules2.add(new Module(R.string.picture_shot_several_patient, R.color.color2, R.drawable.ic_action_new_picture));
		this.mModules2.add(new Module(R.string.folder_patient, R.color.color3, R.drawable.ic_action_person));
		this.mModules2.add(new Module(R.string.folder2, R.color.color4, R.drawable.ic_action_group));
		this.mModules2.add(new Module(R.string.settings, R.color.color5, R.drawable.ic_action_settings));

		this.mInitializationFinished2 = false;

		if(this.mGridView2 != null){
			this.mGridView2.getViewTreeObserver().addOnGlobalLayoutListener(
					new ViewTreeObserver.OnGlobalLayoutListener() {
						@SuppressLint("NewApi")
						@Override
						public void onGlobalLayout() {
							if(!mInitializationFinished2){
								mSpacing = getWindowManager().getDefaultDisplay().getWidth()/50;

								LinearLayout.LayoutParams llp3 = (android.widget.LinearLayout.LayoutParams) mActualSituationTextView.getLayoutParams();//new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
								llp3.setMargins(mSpacing, 0, mSpacing, 0);
								mActualSituationTextView.setLayoutParams(llp3);
								mActualSituationTextView.requestLayout();
								mActualSituationTextView.invalidate();

								ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mGridView2.getLayoutParams();
								if(mlp != null)mlp.setMargins(mSpacing, mSpacing, mSpacing, mSpacing);

								mGridView2.setLayoutParams(mlp);
								mGridView2.setHorizontalSpacing(mSpacing);
								mGridView2.setVerticalSpacing(mSpacing);

								mInitializationFinished2 = true;

								int nbItemsPerRow = 0,nbRows = 0;

								if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
									nbItemsPerRow = 3;
									nbRows = 2;
								}else if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
									nbItemsPerRow = 2;
									nbRows = 3;
								}

								mModulesAdapter2 = new ModulesAdapter(mModules2,nbItemsPerRow,nbRows,mGridView2, MainActivity.this, mSpacing);

								//After set setMargins ==> addOnGlobalLayoutListener called second time later
							}else{
								if(mModulesAdapter2 != null)mGridView2.setAdapter(mModulesAdapter2);

								if(mGridView2.getViewTreeObserver() != null){
									if (SystemUtil.hasJellyBean())mGridView2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
									else mGridView2.getViewTreeObserver().removeGlobalOnLayoutListener(this);
								}
							}
						}
					});

			this.mGridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
					switch (arg2) {

					//Emergency
					case 0:
						if(mDialog != null)mDialog.showFRProgressDialog();

						new Thread(new Runnable() {
							@Override
							public void run() {
								Intent intent = new Intent(MainActivity.this.getApplicationContext(), ScenariosActivity.class);
								if(intent != null){
									intent.putExtra(ScenariosActivity.KEY_MODE, ScenariosActivity.MODE_EMERGENCY);
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									MainActivity.this.getApplicationContext().startActivity(intent);
								}

								if(mDialog != null){
									runOnUiThread(new  Runnable(){
										@Override
										public void run() {
											mDialog.cancelFRProgressDialog();
										}
									});
								}
							}
						}).start();

						break;


						//Seance photos
					case 1:
						if(mDialog != null)mDialog.showFRProgressDialog();

						KitviewUtil.isKitviewAvailable(MainActivity.this, new KitviewUtil.ITestConnectionResponse() {
							@Override
							public void onResponse(final int connectionEstablished) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										runOnUiThread(new Runnable() {
											public void run() {
												if(connectionEstablished == KitviewUtil.TEST_CONNECTION_OK){
													Intent intent = new Intent(MainActivity.this.getApplicationContext(), ScenariosActivity.class);

													if(intent != null){
														intent.putExtra(ScenariosActivity.KEY_MODE, ScenariosActivity.MODE_SCENARIO);
														intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
														MainActivity.this.getApplicationContext().startActivity(intent);
													}
													if(mDialog != null)mDialog.cancelFRProgressDialog();
												}else{
													if(mDialog != null)mDialog.cancelFRProgressDialog();

													String text = "";

													if(connectionEstablished == KitviewUtil.TEST_CONNECTION_WIFI_KO){
														text = getResources().getString(R.string.wifi_ko);

														launchWifiPopup(MainActivity.this);

													}else if(connectionEstablished == KitviewUtil.TEST_CONNECTION_KITVIEW_KO){
														launchSettings(MainActivity.this,true);
													}
												}
											}
										});
									}
								}).start();
							}
						});

						break;

						//Ma phototheque
					case 2:
						launchMyCase(MainActivity.this,-1);

						break;

						//Cas similaires
					case 3:
						if(mDialog != null)mDialog.showFRProgressDialog();

						KitviewUtil.isKitviewAvailable(MainActivity.this, new KitviewUtil.ITestConnectionResponse() {
							@Override
							public void onResponse(final int connectionEstablished) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										runOnUiThread(new Runnable() {
											public void run() {
												if(connectionEstablished == KitviewUtil.TEST_CONNECTION_OK){
													KitviewUtil.GetCurrentIdPatient(MainActivity.this,new KitviewUtil.IIntResponse() {
														@Override
														public void onResponse(final int patientId) {
															Intent intent = new Intent(MainActivity.this.getApplicationContext(), SameCasesActivity.class);
															if(intent != null){
																intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
																intent.putExtra(SameCasesActivity.EXTRA_KEY_PATIENTID, patientId);
																MainActivity.this.getApplicationContext().startActivity(intent);
															}
															if(mDialog != null)mDialog.cancelFRProgressDialog();
														}
													});
												}else{
													if(mDialog != null)mDialog.cancelFRProgressDialog();

													String text = "";

													if(connectionEstablished == KitviewUtil.TEST_CONNECTION_WIFI_KO){
														text = getResources().getString(R.string.wifi_ko);

														launchWifiPopup(MainActivity.this);

													}else if(connectionEstablished == KitviewUtil.TEST_CONNECTION_KITVIEW_KO){
														launchSettings(MainActivity.this,true);
													}
												}
											}
										});
									}
								}).start();
							}
						});
						break;

						//Puzzle
						/*case 1:
						mDialog.showFRProgressDialog();
						new Thread(new Runnable() {
							@Override
							public void run() {
								Intent intent = new Intent(MainActivity.this.getApplicationContext(), PuzzleActivity.class);
								if(intent != null){
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
									MainActivity.this.getApplicationContext().startActivity(intent);
								}

								if(mDialog != null)mDialog.cancelFRProgressDialog();
							}
						}).start();
						break;*/

						//Settings
					case 4:
						launchSettings(MainActivity.this,false);

						break;
					}
				}
			});
		}
	}

	public static File getOutputTextFile(){
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");

		// Create the storage directory if it does not exist
		if (mediaStorageDir != null && !mediaStorageDir.exists()){
			if (!mediaStorageDir.mkdirs()){
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		if(mediaStorageDir != null){
			File mediaFile = new File(mediaStorageDir.getPath() + File.separator +"BARCODE_"+ timeStamp + ".txt");
			return mediaFile;
		}else return null;
	}

	@Override
	protected void onResume(){
		super.onResume();

		startVideo();

		KitviewUtil.GetCurrentIdPatient(MainActivity.this,new KitviewUtil.IIntResponse() {
			@Override
			public void onResponse(int patientId) {
				final Personne personne = (patientId != -1)?KitviewUtil.getPersonneFromId(MainActivity.this,patientId):null;

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(personne != null){
							String lastName = personne.getLastName().trim();
							String firstName = personne.getFirstName().trim();

							String patientInfosFormatted = (lastName != null && lastName.length() <= ScenariosActivity.PATIENT_INFOS_MAX_CHARACTERS)?lastName:lastName.substring(0, ScenariosActivity.PATIENT_INFOS_MAX_CHARACTERS)+" ...";
							String patientInfosFormatted2 = (firstName != null && firstName.length() <= ScenariosActivity.PATIENT_INFOS_MAX_CHARACTERS)?firstName:firstName.substring(0, ScenariosActivity.PATIENT_INFOS_MAX_CHARACTERS)+" ...";

							mCurrentPatientInfosTextView.setText(patientInfosFormatted2+" "+patientInfosFormatted);

							String text = Html.fromHtml(getResources().getString(R.string.copyright_kitview_labs_2015))+" v"+SystemUtil.getAppVersion(MainActivity.this);

							mCopyrightTextView.setText(text);//patientInfosFormatted+" "+patientInfosFormatted2+" "+text);
						}
					}
				});
			}
		});
	}

	public void startVideo(){
		try{
			if(mVideoView != null){
				mVideoView.seekTo(0);
				mVideoView.start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause(){
		super.onPause();

		pauseVideo();
	}

	public void pauseVideo(){
		if(mVideoView != null){
			mVideoView.seekTo(0);
			mVideoView.pause();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		String content = getResources().getString(R.string.confirm_quit_application);
		String title = getResources().getString(R.string.quit_application);
		QuitPopupManager mGenericPopupManager = new QuitPopupManager(MainActivity.this);

		if(mGenericPopupManager != null){
			mGenericPopupManager.initializePopup();
		}

		mGenericPopupManager.showPopup(title,content,new QuitPopupManager.IClick() {
			@Override
			public void onValidateClick(){
				System.exit(0);
			}

			@Override
			public void onCancelClick(){
				//	mGenericPopupManager.hideLineDeleteDialogPopup();
			}
		});

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if(outState != null)outState.putBoolean(KEY_TEST_CONNECTION,false);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);

		recreate();
	}

	public void setViewAnimatorIndex(int index) {
		mViewAnimator.setDisplayedChild(index);
		mViewAnimator.requestLayout();
		mViewAnimator.invalidate();

		mBottomInfosLinearLayout.setVisibility((index==1)?View.VISIBLE:View.GONE);
		mBottomInfosLinearLayout.requestLayout();
		mBottomInfosLinearLayout.invalidate();
	}
}