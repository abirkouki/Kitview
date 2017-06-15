package activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import model.PersistenceManager;
import model.rest.Personne;

import com.dentalcrm.kitview.R;

import util.components.coverflow.CoverAdapterView;
import util.components.coverflow.CoverFlow;
import util.components.progressdialog.FRProgressDialog;
import util.network.KitviewUtil;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ViewAnimator;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SameCasesActivity extends Activity {
	//Constants
	public final static String EXTRA_KEY_PATIENTID = "EXTRA_KEY_PATIENTID";

	private final static int DOUBLE_CLIC_MAX_THRESHOLD = 250;

	//Model
	private int IMAGE_MAX_WIDTH,IMAGE_MAX_HEIGHT;
	private ArrayList<Date> mDates = new ArrayList<Date>();
	private ArrayList<Integer> mActionDownSelectedIndex = new ArrayList<Integer>();
	private ArrayList<Photo> photos;
	private int mPatientId;

	//Views
	private static FRProgressDialog mDialog;
	private CoverFlow coverFlow;
	private TextView mLeftTextView,mTextView,mRightTextView;
	private ViewAnimator mViewAnimator;
	private TextView mTextViewClicTwice;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.activity_samecases);

		this.mOrientation = getResources().getConfiguration().orientation;
		
		this.mPatientId = getIntent().getExtras().getInt(EXTRA_KEY_PATIENTID);

		this.mLeftTextView = (TextView) findViewById(R.id.tv_left);
		this.mTextView = (TextView) findViewById(R.id.tv_middle);
		this.mRightTextView = (TextView) findViewById(R.id.tv_right);
		this.coverFlow = (CoverFlow) findViewById(R.id.coverflow);		

		this.mViewAnimator = (ViewAnimator) findViewById(R.id.va_same_cases);
		this.mViewAnimator.setBackgroundColor(PersistenceManager.getInstance().getBgColor());

		this.mTextViewClicTwice = (TextView)findViewById(R.id.tv_clic_twice);

		MainActivity.getImageFetcher(null).clearCache();
		MainActivity.getImageFetcher(null).setUseCache(true);
		MainActivity.setUseAttachedViewModified(false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(mDialog != null)mDialog.cancelFRProgressDialog();
	}

	private HashMap<Integer, Personne> mHashMap = new HashMap<Integer, Personne>();

	public class ImageAdapter extends BaseAdapter{
		int mGalleryItemBackground;
		private Context mContext;

		public ImageAdapter(Context c){
			mContext = c;
		}

		public int getCount() {
			return (photos != null)?photos.size():0;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			LinearLayout ll = null;
			ImageView imageView = null;
			TextView tv = null;

			if(convertView == null){
				imageView = new ImageView(mContext);

				ll = new LinearLayout(mContext);
				ll.addView(imageView);

				tv = new TextView(mContext);
				ll.addView(tv);
			}else{
				ll = (LinearLayout)convertView;
				imageView = (ImageView)ll.getChildAt(0);
				tv = (TextView)ll.getChildAt(1);
			}

			imageView.setScaleType(ScaleType.CENTER_INSIDE);

			if(position >= 0 && position < getCount()){
				Bitmap originalImage = null;

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inMutable = true;

				originalImage = BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_warning,options);

				if(originalImage != null){
					int picWidth = IMAGE_MAX_WIDTH;
					int picHeight = IMAGE_MAX_HEIGHT;

					CoverFlow.LayoutParams clp = new CoverFlow.LayoutParams(picWidth,picHeight);
					ll.setLayoutParams(clp);

					int height = originalImage.getHeight();

					//This will not scale but will flip on the Y axis
					Matrix matrix = new Matrix();
					matrix.preScale(1, -1);

					int ymiddle = (IMAGE_MAX_HEIGHT - height)/2;

					Canvas canvas = new Canvas();
					canvas.drawBitmap(originalImage, 0, ymiddle, null);

					LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(picWidth,picHeight);
					llp.gravity = Gravity.CENTER;
					imageView.setLayoutParams(llp);

					Personne p = null;
					final ImageView imageViewCop = imageView;

					if(!mHashMap.containsKey(photos.get(position).getId())){
						KitviewUtil.getPersonneFromIdAsync(SameCasesActivity.this, photos.get(position).getId(), new KitviewUtil.IPersonneResponse() {

							@Override
							public void onResponse(final Personne personne) {
								mHashMap.put(photos.get(position).getId(), personne);

								runOnUiThread(new Runnable() {
									public void run() {
										MainActivity.getImageFetcher(personne).loadImage("kitview;"+photos.get(position).getPhotoId(), imageViewCop);
									}
								});
							}
						});
					}else{
						p = mHashMap.get(photos.get(position).getId());
						MainActivity.getImageFetcher(p).loadImage("kitview;"+photos.get(position).getPhotoId(), imageView);
					}
				}

				tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				tv.setVisibility(View.GONE);
			}
			return ll;
		}

		/** Returns the size (0.0f to 1.0f) of the views 
		 * depending on the 'offset' to the center. */ 
		public float getScale(boolean focused, int offset) { 
			/* Formula: 1 / (2 ^ offset) */ 
			return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset))); 
		} 
	}

	public class Photo{
		private int mId;
		private String mAge;
		private String mPrenom;
		private String mNom;
		private Bitmap mBitmap;
		private String idPhoto;

		public Photo(int mId, String mAge, String mPrenom, String nom, String idPhoto){// Bitmap mBitmap){
			this.mId = mId;
			this.mAge = mAge;
			this.mPrenom = mPrenom;
			this.mNom = nom;
			this.idPhoto = idPhoto;
		}

		public int getId(){
			return this.mId;
		}

		public String getPhotoId(){
			return this.idPhoto;
		}

		public String getAge(){
			return this.mAge;
		}

		public String getPrenom(){
			return this.mPrenom;
		}

		public String getNom(){
			return this.mNom;
		}

		public Bitmap getBitmap(){
			return this.mBitmap;
		}
	}

	private int mOrientation;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		coverFlow.requestLayout();
		coverFlow.invalidate();
	}
	
	private void init(){
		if(true){//hasFocus ){//&& firstTimeLoad){
				mDialog = new FRProgressDialog(this, "",false);

				this.mOrientation = getResources().getConfiguration().orientation;

				IMAGE_MAX_WIDTH = coverFlow.getWidth();
				IMAGE_MAX_HEIGHT = coverFlow.getHeight();

				MainActivity.getImageFetcher(null).setImageSize(IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT);
				photos = new ArrayList<Photo>();

				KitviewUtil.GetCurrentIdPatient(SameCasesActivity.this,new KitviewUtil.IIntResponse() {
					@Override
					public void onResponse(int patientId) {
						if(patientId != -1){
							Personne personne = KitviewUtil.getPersonneFromId(SameCasesActivity.this,patientId);

							if(personne != null){
								String famillyField = personne.getFamillyField();
								String famillyValue = ""+personne.getFamillyValue();

								if(famillyField != null && famillyValue != "-1"){
									ArrayList<Personne> personnes = KitviewUtil.GetPersonnesFromFormField(SameCasesActivity.this,famillyField, famillyField+" :"+famillyValue);
									int nbPersonnes = (personnes != null)?personnes.size():0;

									for(int i=0;i<nbPersonnes;i++){
										String photoIdentityId = ""+KitviewUtil.GetPatientIdentityId(SameCasesActivity.this,personnes.get(i).getId());
										Date now = new Date();
										String lastName = (personnes.get(i).getLastName() != "" && personnes.get(i).getLastName().trim() != "" && personnes.get(i).getLastName().trim().length() > 0)?personnes.get(i).getLastName().trim().charAt(0)+".":"";

										SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");
										String yearsDiff = ""+(Integer.parseInt(simpleDateformat.format(now))- Integer.parseInt(simpleDateformat.format(personnes.get(i).getDateNaiss())))+" "+getResources().getString(R.string.years);

										if(mPatientId != personnes.get(i).getId() && personnes.get(i).getId() > 0)photos.add(new Photo(personnes.get(i).getId(), yearsDiff, personnes.get(i).getFirstName().trim(),lastName, photoIdentityId));//originalImage));
									}

									runOnUiThread(new Runnable() {
										public void run() {
											final int nbPhotos = (photos != null)?photos.size():0;

											if(nbPhotos == 0)mViewAnimator.setDisplayedChild(1);
											else mTextViewClicTwice.setVisibility(View.VISIBLE);

											coverFlow.setAdapter(new ImageAdapter(SameCasesActivity.this));
											coverFlow.setSpacing(30);
											coverFlow.setOnTouchListener(new View.OnTouchListener() {
												@Override
												public boolean onTouch(View v, MotionEvent event) {
													if(event != null && event.getAction() == MotionEvent.ACTION_DOWN){
														mDates.add(new Date());
														mActionDownSelectedIndex.add(coverFlow.getSelectedItemPosition());
														int nbDates = (mDates != null)?mDates.size():0;

														if(nbDates >= 2){
															Date start = mDates.get(nbDates-2);
															Date now = mDates.get(nbDates-1);

															if(now.getTime() - start.getTime() <= DOUBLE_CLIC_MAX_THRESHOLD &&
																	mActionDownSelectedIndex.get(nbDates-2) == mActionDownSelectedIndex.get(nbDates-1)){
																mDialog.showFRProgressDialog();

																int pos = coverFlow.getSelectedItemPosition();

																if(pos >= 0 && pos < nbPhotos ){
																	int patientId = photos.get(pos).getId();
																	MainActivity.launchMyCase(SameCasesActivity.this,patientId);
																	mDates.clear();
																}
																finish();
															}
														}
													}
													return false;
												}
											});

											coverFlow.setOnItemSelectedListener(new CoverAdapterView.OnItemSelectedListener() {
												@Override
												public void onItemSelected(CoverAdapterView<?> parent, View view,int position, long id) {
													mTextView.setText(photos.get(position).getPrenom()+" "+photos.get(position).getNom()+" - "+photos.get(position).getAge());//+" "+getResources().getString(R.string.years));

													int leftNbItems = position;
													int nbPictures = (photos != null)?photos.size():0;
													int rightNbItems = nbPictures - position - 1;

													mLeftTextView.setText("("+leftNbItems+")");
													mLeftTextView.setVisibility((leftNbItems>0)?View.VISIBLE:View.INVISIBLE);

													mRightTextView.setText("("+rightNbItems+")");
													mRightTextView.setVisibility((rightNbItems>0)?View.VISIBLE:View.INVISIBLE);
												}

												@Override
												public void onNothingSelected(CoverAdapterView<?> parent) {}
											});

											coverFlow.setSelection(0, false);
											coverFlow.requestFocus();
											coverFlow.requestLayout();
											coverFlow.invalidate();	
										}
									});		
								}else{
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											mViewAnimator.setDisplayedChild(1);	
										}
									});
								}
							}
						}
					}
				});
			}
	}
	
	private boolean firstTime = true;
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if(hasFocus && firstTime){
			firstTime = false;
			init();
		}
	}
}