package view.adapter;

import java.util.ArrayList;
import util.components.gallery.ImageFetcher;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.dentalcrm.kitview.R;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class VerticalListViewAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<String> mPhotos;
	private LayoutInflater mInflater;
	private ImageFetcher mImageFetcherFromDisk;
	private int imageMinWidth,imageMinHeight;
	private int mIndexPhotoSelected;
	private RotateAnimation ra;

	public VerticalListViewAdapter(Context context, ArrayList<String> photos,ImageFetcher mImageFetcherFromDisk){
		this.context = context;
		this.mInflater = LayoutInflater.from(this.context);
		this.mPhotos = photos;
		this.mImageFetcherFromDisk = mImageFetcherFromDisk;
		this.imageMinWidth = context.getResources().getDimensionPixelSize(R.dimen.list_item_width);
		this.imageMinHeight = imageMinWidth;
		this.mIndexPhotoSelected = 0;
	}

	@Override
	public int getCount() {
		return (mPhotos != null)?mPhotos.size():0;
	}

	public void setImageFetcher(ImageFetcher imageFetcher){
		this.mImageFetcherFromDisk = imageFetcher;
	}

	public void setPhotos(ArrayList<String> photos){
		this.mPhotos = photos;
	}

	public void setIndexPhotoSelected(int index){
		this.mIndexPhotoSelected = index;
	}

	@Override
	public Object getItem(int position) {
		return (position >= 0 && position < getCount() && mPhotos != null)?mPhotos.get(position):null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ImageView iv = null;

		if (convertView == null) {
			view = mInflater.inflate(R.layout.layout_photo_item, parent, false);
			iv = ((ImageView) view.findViewById(R.id.photo));

			FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(imageMinWidth, imageMinHeight);
			fl.gravity = Gravity.CENTER;

			iv.setLayoutParams(fl);
			
		}else{
			view = convertView;
			iv = ((ImageView) view.findViewById(R.id.photo));
		}

		if(mPhotos != null){
			String path =  mPhotos.get(position);

			if(position == mIndexPhotoSelected){
				iv.setBackgroundColor(Color.GREEN);
			}else{
				iv.setBackgroundColor(Color.argb(0, 0, 0, 0));				
			}
			
			if(path == "" || path == null){
				iv.setScaleType(ScaleType.FIT_XY);
			}else iv.setScaleType(ScaleType.CENTER_CROP);

			if(mImageFetcherFromDisk != null)mImageFetcherFromDisk.loadImage(path, iv);

			if(!mAnimationIsRunning){
				ra = new RotateAnimation(mPrevAngleRot, mCurrentAngleRot, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				ra.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						mAnimationIsRunning = true;	
					}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationEnd(Animation animation) {
						mPrevAngleRot = mCurrentAngleRot;
						mAnimationIsRunning = false;	
					}
				});

				ra.setDuration(100);
				ra.setFillAfter(true);

				if(ra != null)view.startAnimation(ra);
			}
		}
		return view;
	}

	private boolean mAnimationIsRunning = false;
	private int mPrevAngleRot = 0, mCurrentAngleRot;

	public void setAngleRotation(int mCurrentAngleRot){
		if(!mAnimationIsRunning){
			this.mCurrentAngleRot = mCurrentAngleRot;
			notifyDataSetChanged();
		}
	}

	public void destroy(){
		this.context = null;

		if(ra != null){
			mAnimationIsRunning = false;
			ra.cancel();
			ra.reset();
			ra.setAnimationListener(null);
			ra = null;
		}
	}
}