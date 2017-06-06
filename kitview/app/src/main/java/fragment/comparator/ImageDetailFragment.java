package fragment.comparator;

import java.util.ArrayList;
import java.util.List;
import model.rest.Photo;
import com.dentalcrm.kitview.R;
import util.components.imagezoom.ImageViewTouch;
import util.components.gallery.ImageFetcher;
import util.components.gallery.ImageResizer;
import util.components.gallery.ImageWorker;
import util.network.KitviewUtil;
import activity.FolderActivity;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ImageView.ScaleType;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ImageDetailFragment extends android.app.Fragment {
	private static final String IMAGE_DATA_EXTRA = "extra_image_data";
	private static final String EXTRA_PICTURES = "extra_pictures";
	private static final String EXTRA_FRAGMENT_ACTIVITY = "extra_fragment_activity";

	private static final String CHECK_VISIBLE_EXTRA = "check_visible_extra";
	private static final String CHECK_SELECTED_EXTRA = "check_selected_extra";

	public final static String EXTRA_DATA_TYPE = "EXTRA_DATA_TYPE";

	private String mImageUrl;

	private ProgressBar mProgressBar;
	private ImageViewTouch mImageView;

	private ImageFetcher mImageFetcher;

	private static FolderActivity _mFragmentActivity;

	private int index = 0;

	private boolean mLongClickEnabled = false;

	public static ImageDetailFragment newInstance(FolderActivity mFragmentActivity, List<Photo> mPicturesList, Photo imageUrl,ImageResizer mImageFetcherParentClass, boolean mIsCheckVisible) {
		Bundle args = new Bundle();
		args.putSerializable(IMAGE_DATA_EXTRA, imageUrl.getId());
		args.putBoolean(CHECK_VISIBLE_EXTRA, mIsCheckVisible);

		ArrayList<Photo> picturesList = new ArrayList<Photo>();
		picturesList.addAll(mPicturesList);

		_mFragmentActivity = mFragmentActivity;

		ImageDetailFragment f = new ImageDetailFragment();
		f.setImageFetcherParentClass(mImageFetcherParentClass);
		f.setArguments(args);

		return f;
	}

	/**
	 * Empty constructor as per the Fragment documentation
	 */
	public ImageDetailFragment() {}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mImageUrl = (String) (getArguments() != null ? getArguments().getSerializable(IMAGE_DATA_EXTRA) : null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_folder_image_detail, container, false);
		mProgressBar = (ProgressBar)v.findViewById(R.id.loading_progressbar);
		mImageView = (ImageViewTouch) v.findViewById(R.id.imageView);
		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!mLongClickEnabled)_mFragmentActivity.changeVisibility();
			}
		});

		mImageView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if(event.getAction() == MotionEvent.ACTION_UP){
					if(mLongClickEnabled)mLongClickEnabled = false;
				}

				return false;
			}
		});

		mImageView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				mLongClickEnabled = true;

				ScaleType st = null;

				switch (index) {

				case 0:
					st = ScaleType.CENTER_CROP;
					break;

				case 1:
					st = ScaleType.CENTER_INSIDE;
					break;
				}

				index = (index +1)%2;

				return true;
			}
		});

		return v;
	}

	private ImageResizer mImageFetcherParentClass;

	public void setImageFetcherParentClass(ImageResizer mImageFetcherParentClass){
		this.mImageFetcherParentClass = mImageFetcherParentClass;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mImageFetcher = ((ImageFetcher)(mImageFetcherParentClass));
		mImageFetcher.loadImage(mImageUrl, mImageView);

		mProgressBar.setVisibility(View.VISIBLE);

		// Pass clicks on the ImageView to the parent activity to handle
		if (OnClickListener.class.isInstance(getActivity())){// && Utils.hasHoneycomb()) {
			mImageView.setOnClickListener((OnClickListener) getActivity());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mImageView != null) {
			// Cancel any pending image work
			ImageWorker.cancelWork(mImageView);
			mImageView.setImageDrawable(null);
		}
	}
}