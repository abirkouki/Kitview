package activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.PersistenceManager;
import model.rest.Personne;
import model.rest.Photo;
import util.components.imagezoom.ImageViewTouch;
import util.components.progressdialog.FRProgressDialog;
import util.components.shake.ShakeDetector;
import util.image.ImageUtil;
import util.network.KitviewUtil;
import view.comparator.ImageDetailView;
import view.popup.CollectionsPopupManager;
import view.sharing.SharingPhotosView;

import com.dentalcrm.kitview.R;

import fragment.comparator.ImageDetailFragment;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.ViewAnimator;
import util.components.gallery.ImageFetcher;
import util.file.FileUtil;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class FolderActivity extends Activity{
	//Constants
	public final static String EXTRA_KEY_PATIENTID = "EXTRA_KEY_PATIENTID";

	//Model
	private int mIndexLeftPicture = 0, mIndexRightPicture = 1;
	private boolean mClickOnLeft = false, mClickOnRight = false;
	private int mPatientId;

	//Sensors
	private ShakeDetector mShakeDetector;

	//Views
	private TextView mLeftIndexTextView,mRightIndexTextView;
	private TextView mLeftTextView,mDeltaTextView,mRightTextView;
	private LinearLayout mParentLinearLayout;
	private ImageViewTouch mIvt,mIvt2;
	private ViewAnimator mViewAnimator;
	private ViewPager mViewPager;
	private ImageDetailView mImageDetailView;
	private FrameLayout mVerticalList;
	private CollectionsPopupManager mCollectionsPopupManager;
	private Button mPreviousLeftPictureButton, mNextLeftPictureButton;
	private Button mPreviousRightPictureButton, mNextRightPictureButton;
	private Button mPreviousPreviousPictureButton, mNextNextPictureButton;
	private static FRProgressDialog mDialog;

	private Personne personne;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.activity_folder);

		this.mPatientId = getIntent().getExtras().getInt(EXTRA_KEY_PATIENTID);

		this.personne = KitviewUtil.getPersonneFromId(FolderActivity.this, mPatientId);

		if(MainActivity.getImageFetcher(personne) != null){
			MainActivity.getImageFetcher(personne).clearCache();

			//don't use cache for vertical little pictures since cache sometimes doesn't work properly
			MainActivity.setUseCache(false);
			MainActivity.setUseAttachedViewModified(false);
		}

		this.mDialog = new FRProgressDialog(this, "",true);

		this.mParentLinearLayout = (LinearLayout)findViewById(R.id.item1);
		this.mVerticalList = (FrameLayout)findViewById(R.id.panel);

		this.mLeftIndexTextView = (TextView) findViewById(R.id.tv_left_index);
		this.mRightIndexTextView = (TextView) findViewById(R.id.tv_right_index);


		this.mLeftTextView = (TextView) findViewById(R.id.tv_left);
		this.mDeltaTextView = (TextView) findViewById(R.id.tv_delta);
		this.mRightTextView = (TextView) findViewById(R.id.tv_right);

		this.mCollectionsPopupManager = new CollectionsPopupManager(this);
		this.mCollectionsPopupManager.initializePopup(mPatientId);

		this.mViewAnimator = (ViewAnimator) findViewById(R.id.viewpager);


		this.mViewAnimator.setBackgroundColor(PersistenceManager.getInstance().getBgColor());

		this.mViewPager = (ViewPager) findViewById(R.id.pager);

		this.mPreviousLeftPictureButton = (Button) findViewById(R.id.left_picture_previous);
		this.mPreviousLeftPictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int nbPictures = (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;
				int newIndex = (mIndexLeftPicture>0)?mIndexLeftPicture-1:nbPictures-1;

				if(newIndex == mIndexRightPicture){
					newIndex = (newIndex>0)?newIndex-1:nbPictures-1;
				}

				mIndexLeftPicture = newIndex;

				mDialog.showFRProgressDialog();
				updateLeftPicture(true);

				updateDelta();
			}
		});

		this.mNextLeftPictureButton = (Button) findViewById(R.id.left_picture_next);
		this.mNextLeftPictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int nbPictures = (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;
				int newIndex = (mIndexLeftPicture +1 )%nbPictures;

				if(newIndex == mIndexRightPicture){
					newIndex = (newIndex +1 )%nbPictures;
				}

				mIndexLeftPicture = newIndex;

				mDialog.showFRProgressDialog();
				updateLeftPicture(true);
			}
		});

		this.mPreviousPreviousPictureButton = (Button) findViewById(R.id.picture_previous_previous);
		this.mPreviousPreviousPictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Previous Left
				int nbPictures = (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;
				int newIndex = (mIndexLeftPicture>0)?mIndexLeftPicture-1:nbPictures-1;

				if(newIndex == mIndexRightPicture){
					newIndex = (newIndex>0)?newIndex-1:nbPictures-1;
				}

				mIndexLeftPicture = newIndex;

				mDialog.showFRProgressDialog();
				updateLeftPicture(false);

				//Previous Right
				int newIndex2 = (mIndexRightPicture>0)?mIndexRightPicture-1:nbPictures-1;

				if(newIndex2 == mIndexLeftPicture){
					newIndex2 = (newIndex2>0)?newIndex2-1:nbPictures-1;
				}

				mIndexRightPicture = newIndex2;

				updateRightPicture(true);
			}
		});

		this.mNextNextPictureButton = (Button) findViewById(R.id.picture_next_next);
		this.mNextNextPictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Next Left
				int nbPictures = (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;
				int newIndex = (mIndexLeftPicture +1 )%nbPictures;

				if(newIndex == mIndexRightPicture){
					newIndex = (newIndex +1 )%nbPictures;
				}

				mIndexLeftPicture = newIndex;

				mDialog.showFRProgressDialog();
				updateLeftPicture(false);

				//Next right
				int newIndex2 = (mIndexRightPicture + 1 )%nbPictures;

				if(newIndex2 == mIndexLeftPicture){
					newIndex2 = (newIndex2 + 1 )%nbPictures;
				}

				mIndexRightPicture = newIndex2;

				updateRightPicture(true);
			}
		});


		this.mPreviousRightPictureButton = (Button) findViewById(R.id.right_picture_previous);
		this.mPreviousRightPictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int nbPictures = (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;
				int newIndex = (mIndexRightPicture>0)?mIndexRightPicture-1:nbPictures-1;//(mIndexRightPicture -1 )%nbPictures;

				if(newIndex == mIndexLeftPicture){
					newIndex = (newIndex>0)?newIndex-1:nbPictures-1;
				}

				mIndexRightPicture = newIndex;

				mDialog.showFRProgressDialog();
				updateRightPicture(true);
			}
		});

		this.mNextRightPictureButton = (Button) findViewById(R.id.right_picture_next);
		this.mNextRightPictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int nbPictures = (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;
				int newIndex = (mIndexRightPicture + 1 )%nbPictures;

				if(newIndex == mIndexLeftPicture){
					newIndex = (newIndex + 1 )%nbPictures;
				}

				mIndexRightPicture = newIndex;

				mDialog.showFRProgressDialog();

				updateRightPicture(true);
			}
		});

		this.mViewAnimator.setDisplayedChild(0);
		this.mViewAnimator.requestLayout();
		this.mViewAnimator.invalidate();

		this.initializeShakeDetector();

		if(getResources().getConfiguration() != null){
			int orientation = getResources().getConfiguration().orientation;
			this.updateLinearLayout(orientation);
		}
	}	
	
	public String getCurrentCollectionName(){
		return this.mCollectionsPopupManager.getCurrentCollectionName();
	}

	public void initializePictures(){
		ImageDetailView.IAction _interface = new ImageDetailView.IAction(){
			@Override
			public void setOnPageChangeListener(List<Photo> mPicturesList,int position){
				/*if(mClickOnLeft){
					//mIndexLeftPicture = position;
					//updateLeftPicture();
				}else if(mClickOnRight){
					//mIndexRightPicture = position;
					//mDialog.showFRProgressDialog();
					//updateRightPicture();
				}
				 */}

			@Override
			public ImageDetailFragment setImagePagerAdapterGetItem(List<Photo> mPictures,Photo data,ImageFetcher mImageFetcher) {
				return ImageDetailFragment.newInstance(FolderActivity.this,mPictures,data,mImageFetcher,false);
			}

			@Override
			public View setVerticalListViewAdapterGetView(int position,
					View convertView, ViewGroup parent,
					List<Photo> mPhotos,
					LayoutInflater mInflater,
					ImageFetcher mImageFetcherFromDisk, int imageMinWidth,
					int imageMinHeight) {
				FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(imageMinWidth, imageMinHeight);
				fl.gravity = Gravity.CENTER;
				fl.setMargins(0, 0, 0, 0);

				View view = null;
				ImageView iv = null;

				if (convertView == null) {
					view = mInflater.inflate(R.layout.layout_photo_item, parent, false);
				}else view = convertView;

				iv = ((ImageView) view.findViewById(R.id.photo));
				iv.setLayoutParams(fl);
				iv.setScaleType(ScaleType.CENTER_INSIDE);

				String path = mPhotos.get(position).getId();

				if(mImageFetcherFromDisk != null)mImageFetcherFromDisk.loadImage(path, iv);

				return view;
			}
		};

		this.mImageDetailView = new ImageDetailView(FolderActivity.this,personne,0,_interface,mCollectionsPopupManager);

		this.mIvt = (ImageViewTouch) findViewById(R.id.ivt);

		this.mIvt.setDoubleTapListener(new ImageViewTouch.OnImageViewTouchDoubleTapListener() {
			@Override
			public void onDoubleTap() {
				mClickOnLeft = true;

				mViewPager.getAdapter().notifyDataSetChanged();
				mViewAnimator.setDisplayedChild(1);	
				mViewAnimator.invalidate();

				mViewPager.setCurrentItem(mIndexLeftPicture);
				mViewPager.requestLayout();
				mViewPager.invalidate();
			}
		});

		this.mIvt.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {		
				ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(FolderActivity.this);
				int screenWidth = screenDimensions.get(0).intValue();
				int screenHeight = screenDimensions.get(1).intValue();
				int photoId = Integer.parseInt(mCollectionsPopupManager.getListPhotos().get(mIndexLeftPicture).getId().replace("kitview;", ""));

				KitviewUtil.downloadPhotoAsync(FolderActivity.this,personne,photoId,screenWidth, screenHeight, new KitviewUtil.IBitmapResponse() {
					@Override
					public void onResponse(final Bitmap bitmap) {
						File mPictureFile = FileUtil.getOutputMediaFile(FileUtil.MEDIA_TYPE_IMAGE);
						String fileName = mPictureFile.getAbsolutePath();

						FileUtil.savePictureAsJPEG(FolderActivity.this, bitmap, fileName, 100, false, true, false);

						final ArrayList<Uri> uris = new ArrayList<Uri>();
						uris.add(Uri.fromFile(new File(fileName)));

						runOnUiThread(new Runnable() {
							public void run() {
								new SharingPhotosView(FolderActivity.this).showDialog(FolderActivity.this, true, uris);		
							}
						});
					}
				});
				return true;
			}
		});

		this.mIvt2 = (ImageViewTouch) findViewById(R.id.ivt2);
		this.mIvt2.setDoubleTapListener(new ImageViewTouch.OnImageViewTouchDoubleTapListener(){		
			@Override
			public void onDoubleTap(){
				mClickOnRight = true;

				if(mViewPager != null && mViewAnimator != null && mViewPager.getAdapter() != null){
					mViewPager.getAdapter().notifyDataSetChanged();
					mViewAnimator.setDisplayedChild(1);	
					mViewAnimator.invalidate();

					mViewPager.setCurrentItem(mIndexRightPicture);
					mViewPager.requestLayout();
					mViewPager.invalidate();
				}
			}
		});

		this.mIvt2.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {		
				ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(FolderActivity.this);
				int screenWidth = screenDimensions.get(0).intValue();
				int screenHeight = screenDimensions.get(1).intValue();
				int photoId = Integer.parseInt(mCollectionsPopupManager.getListPhotos().get(mIndexRightPicture).getId().replace("kitview;", ""));

				KitviewUtil.downloadPhotoAsync(FolderActivity.this,personne,photoId,screenWidth, screenHeight, new KitviewUtil.IBitmapResponse() {
					@Override
					public void onResponse(final Bitmap bitmap) {
						File mPictureFile = FileUtil.getOutputMediaFile(FileUtil.MEDIA_TYPE_IMAGE);	
						String fileName = mPictureFile.getAbsolutePath();

						FileUtil.savePictureAsJPEG(FolderActivity.this, bitmap, fileName, 100, false, true, false);

						final ArrayList<Uri> uris = new ArrayList<Uri>();
						uris.add(Uri.fromFile(new File(fileName)));

						runOnUiThread(new Runnable() {
							public void run() {
								new SharingPhotosView(FolderActivity.this).showDialog(FolderActivity.this, true, uris);		
							}
						});
					}
				});
				return true;
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if(newConfig != null)updateLinearLayout(newConfig.orientation);
	}


	private void updateLinearLayout(int orientation){
		if(mParentLinearLayout != null){
			if(orientation == Configuration.ORIENTATION_LANDSCAPE){
				mParentLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
			}else if(orientation == Configuration.ORIENTATION_PORTRAIT){
				mParentLinearLayout.setOrientation(LinearLayout.VERTICAL);
			}

			mParentLinearLayout.invalidate();
		}

		if(mViewPager != null)mViewPager.invalidate();
	}


	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);

		if(outState != null)outState.putString("test", "test");
	}

	@Override
	protected void onPause() {
		super.onPause();

		if(this.mShakeDetector != null){
			this.mShakeDetector.handleUnregister();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(this.mShakeDetector == null){
			this.initializeShakeDetector();
		}

		if(this.mShakeDetector != null)this.mShakeDetector.handleRegister();
	}

	private boolean mFirstTimeActivityLoaded = true;

	private void initializeShakeDetector(){
		this.mShakeDetector = new ShakeDetector(FolderActivity.this);

		if(this.mShakeDetector != null){
			this.mShakeDetector.initialize();
			this.mShakeDetector.setInterface(new ShakeDetector.onShakeInterface(){
				@Override
				public void setOnShakeEvent(){
					if(mCollectionsPopupManager != null && !mFirstTimeActivityLoaded){
						if(mCollectionsPopupManager.isVisible()){
							//mCollectionsPopupManager.hidePopup();
						}else{
							mCollectionsPopupManager.showPopup();
						}
					}
					mFirstTimeActivityLoaded = false;
				}
			});
			this.mShakeDetector.handleRegister();
		}
	}

	public void setPicturesPath(){
		this.mIndexLeftPicture = 0;

		int nbPictures = (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;	

		if(PersistenceManager.getInstance().getDisplayModeMyCase() == 0){
			this.mIndexRightPicture = 1;
		}else if(PersistenceManager.getInstance().getDisplayModeMyCase() == 1){
			this.mIndexRightPicture = (nbPictures > 0)?(nbPictures - 1):0;
		}

		this.updateLeftPicture(false);
		this.updateRightPicture(true);

		if(this.mImageDetailView != null && mCollectionsPopupManager.getListPhotos() != null)this.mImageDetailView.setPictureList();//mPicturesPath);
	}

	public void setFirstTimeActivityLoaded(boolean mFirstTimeActivityLoaded){
		this.mFirstTimeActivityLoaded = mFirstTimeActivityLoaded;
	}

	private void updateDelta(){
		int nbPictures = (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;	

		if(nbPictures >= 2){
			long t1 = mCollectionsPopupManager.getListPhotos().get(mIndexLeftPicture).getCollectionDateCreation().getTime();
			long t2 = mCollectionsPopupManager.getListPhotos().get(mIndexRightPicture).getCollectionDateCreation().getTime();

			long diff = (t1>=t2)?(t1 - t2):(t2-t1);
			long deno = 365*24*60*60*1000L;

			double raw = diff*1.0d/deno*1.0d;
			int years = (int)raw;
			int months = ((int)((raw - years)*12));

			if(years > 0 || months > 0){
				String _years = (years > 0)?((years == 1)?(years+" "+getResources().getString(R.string.year)):(years+" "+getResources().getString(R.string.years))):"";
				String _months = (months > 0)?((months == 1)?(months+" "+getResources().getString(R.string.month)):(months+" "+getResources().getString(R.string.months))):"";

				_years = (_years != null)?_years.trim():_years;
				_months = (_months != null)?_months.trim():_months;

				if(_years != ""){
					if(_months != ""){
						mDeltaTextView.setText(""+_years+"\n "+_months);
					}else{
						mDeltaTextView.setText(""+_years);
					}
					mDeltaTextView.setVisibility(View.VISIBLE);
				}else{
					if(_months != ""){
						mDeltaTextView.setText(_months);
						mDeltaTextView.setVisibility(View.VISIBLE);
					}else mDeltaTextView.setVisibility(View.INVISIBLE);
				}
			}else mDeltaTextView.setVisibility(View.INVISIBLE);

			mDeltaTextView.requestLayout();
			mDeltaTextView.invalidate();
		}
	}
	private void updateLeftPicture(final boolean updateDeltaAtEnd){
		final int nbPictures = (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;	

		if(mIndexLeftPicture >= 0 && mIndexLeftPicture < nbPictures){
			runOnUiThread(new Runnable() {
				@Override
				public void run(){
					if(mCollectionsPopupManager.getListPhotos() != null && mIndexLeftPicture >= 0 && mIndexLeftPicture < nbPictures && mCollectionsPopupManager.getListPhotos().get(mIndexLeftPicture) != null
							&& mCollectionsPopupManager.getListPhotos().get(mIndexLeftPicture).getId() != "" && mCollectionsPopupManager.getListPhotos().get(mIndexLeftPicture).getId() != null){

						ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(FolderActivity.this);
						int screenWidth = screenDimensions.get(0).intValue();
						int screenHeight = screenDimensions.get(1).intValue();

						int photoId = Integer.parseInt(mCollectionsPopupManager.getListPhotos().get(mIndexLeftPicture).getId().replace("kitview;", ""));

						KitviewUtil.downloadPhotoAsync(FolderActivity.this,personne,photoId,screenWidth, screenHeight, new KitviewUtil.IBitmapResponse() {
							@Override
							public void onResponse(final Bitmap bitmap) {
								runOnUiThread(new Runnable(){
									public void run() {
										mLeftIndexTextView.setText((mIndexLeftPicture+1==nbPictures)?(getResources().getString(R.string.last)):""+(mIndexLeftPicture+1));

										if(bitmap != null && getImageViewTouch() != null){
											getImageViewTouch().setImageBitmap(bitmap);
											getImageViewTouch().invalidate();
										}

										long diff = (mCollectionsPopupManager.getListPhotos().get(mIndexLeftPicture).getCollectionDateCreation().getTime() - personne.getDateNaiss().getTime());
										long deno = 365*24*60*60*1000L;
										String d = (diff/deno != 0)?((diff/deno == 1)?"\n1 "+getResources().getString(R.string.year):("\n("+(diff/deno)+" "+getResources().getString(R.string.years)+")")):"";

										int nbPhotos = (mCollectionsPopupManager != null && mCollectionsPopupManager.getListPhotos()  != null)?mCollectionsPopupManager.getListPhotos().size():0;

										if(mIndexLeftPicture >= 0 && mIndexLeftPicture < nbPhotos && mCollectionsPopupManager != null && mCollectionsPopupManager.getListPhotos() != null 
												&& mCollectionsPopupManager.getListPhotos().get(mIndexLeftPicture) != null 
												&& mCollectionsPopupManager.getListPhotos().get(mIndexLeftPicture).getCollectionDateCreation() != null ){
											if(mLeftTextView != null)mLeftTextView.setText(new SimpleDateFormat("dd/MM/yyyy").format(mCollectionsPopupManager.getListPhotos().get(mIndexLeftPicture).getCollectionDateCreation()).toString()+d);
										}

										if(updateDeltaAtEnd)updateDelta();

										mDialog.cancelFRProgressDialog();
									}
								});
							}
						});
					}
				}
			});
		}
	}

	private void updateRightPicture(final boolean updateDeltaAtEnd){
		final int nbPictures = (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;	

		if(mIndexRightPicture >= 0 && mIndexRightPicture < nbPictures){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(mCollectionsPopupManager.getListPhotos() != null && mIndexRightPicture >= 0 && mIndexRightPicture < nbPictures && mCollectionsPopupManager.getListPhotos().get(mIndexRightPicture) != null
							&& mCollectionsPopupManager.getListPhotos().get(mIndexRightPicture).getId() != "" && mCollectionsPopupManager.getListPhotos().get(mIndexRightPicture).getId() != null){}

					ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(FolderActivity.this);
					int screenWidth = screenDimensions.get(0).intValue();
					int screenHeight = screenDimensions.get(1).intValue();

					int photoId = Integer.parseInt(mCollectionsPopupManager.getListPhotos().get(mIndexRightPicture).getId().replace("kitview;", ""));

					KitviewUtil.downloadPhotoAsync(FolderActivity.this,personne,photoId,screenWidth, screenHeight, new KitviewUtil.IBitmapResponse() {
						@Override
						public void onResponse(final Bitmap bitmap) {
							runOnUiThread(new Runnable() {
								public void run() {
									mRightIndexTextView.setText((mIndexRightPicture+1==nbPictures)?(getResources().getString(R.string.last)):""+(mIndexRightPicture+1));

									if(bitmap != null && getImageViewTouch2() != null){
										getImageViewTouch2().setImageBitmap(bitmap);
										getImageViewTouch2().invalidate();
									}

									long diff = (mCollectionsPopupManager.getListPhotos().get(mIndexRightPicture).getCollectionDateCreation().getTime() - personne.getDateNaiss().getTime());
									long deno = 365*24*60*60*1000L;
									String d = (diff/deno != 0)?((diff/deno == 1)?"\n1 "+getResources().getString(R.string.year):("\n("+(diff/deno)+" "+getResources().getString(R.string.years)+")")):"";

									int nbPhotos = (mCollectionsPopupManager != null && mCollectionsPopupManager.getListPhotos()  != null)?mCollectionsPopupManager.getListPhotos().size():0;

									if(mIndexRightPicture >= 0 && mIndexRightPicture < nbPhotos && mCollectionsPopupManager != null && mCollectionsPopupManager.getListPhotos() != null 
											&& mCollectionsPopupManager.getListPhotos().get(mIndexRightPicture) != null 
											&& mCollectionsPopupManager.getListPhotos().get(mIndexRightPicture).getCollectionDateCreation() != null ){
										if(mRightTextView != null)mRightTextView.setText(new SimpleDateFormat("dd/MM/yyyy").format(mCollectionsPopupManager.getListPhotos().get(mIndexRightPicture).getCollectionDateCreation()).toString()+d);
									}

									if(updateDeltaAtEnd)updateDelta();

									mDialog.cancelFRProgressDialog();
								}
							});
						}
					});
				}
			});
		}
	}

	public void changeVisibility(){
		boolean visibility = (mVerticalList.getVisibility() == View.VISIBLE);
		if(this.mVerticalList != null)this.mVerticalList.setVisibility(visibility?View.GONE:View.VISIBLE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			if(mViewAnimator != null && mViewAnimator.getDisplayedChild()==1){
				mViewAnimator.setDisplayedChild(0);

				KitviewUtil.closeConnection();

				/*if(mClickOnLeft){
					updateLeftPicture();
					mClickOnLeft = false;
				}else if(mClickOnRight){
					updateRightPicture();
					mClickOnRight = false;
				}*/

				return true;
			}else{
				KitviewUtil.closeConnection();
				finish();
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public ImageViewTouch getImageViewTouch(){
		return mIvt;
	}

	public ImageViewTouch getImageViewTouch2(){
		return mIvt2;
	}

	public Button getPreviousLeftButton(){
		return this.mPreviousLeftPictureButton;
	}

	public Button getNextLeftButton(){
		return this.mNextLeftPictureButton;
	}

	public Button getPreviousRightButton(){
		return this.mPreviousRightPictureButton;
	}

	public Button getNextRightButton(){
		return this.mNextRightPictureButton;
	}

	public Button getPreviousPreviousButton(){
		return this.mPreviousPreviousPictureButton;
	}

	public Button getNextNextButton(){
		return this.mNextNextPictureButton;
	}

	public TextView getLeftTextView(){
		return this.mLeftTextView;
	}

	public TextView getRightTextView(){
		return this.mRightTextView;
	}

	public TextView getRightIndexTextView(){
		return this.mRightIndexTextView;
	}

	public TextView getLeftIndexTextView(){
		return this.mLeftIndexTextView;
	}


	public TextView getDeltaTextView(){
		return this.mDeltaTextView;
	}


	public void cancelDialog(){
		this.mDialog.cancelFRProgressDialog();
	}

	public void showDialog(){
		this.mDialog.showFRProgressDialog();
	}
}