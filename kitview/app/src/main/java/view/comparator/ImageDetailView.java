package view.comparator;

import java.util.ArrayList;
import java.util.List;

import model.rest.Personne;
import model.rest.Photo;

import com.dentalcrm.kitview.R;

import util.components.gallery.ImageCache;
import util.components.gallery.ImageFetcher;
import util.components.gallery.Utils;
import util.image.ImageUtil;
import view.popup.CollectionsPopupManager;
import activity.MainActivity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ImageDetailView {
	private final static String IMAGE_CACHE_DIR = "images2";

	private Activity mActivity;
	private ListView list;
	private FrameLayout mPanel;
	private boolean mPanelVisible = true;
	private ObjectAnimator mPanelAnimator;
	private int imageMinWidth,imageMinHeight;
	private ImageCache.ImageCacheParams cacheParams;
	private ImagePagerAdapter mAdapter;
	private ImageFetcher mImageFetcherFromDisk;
	private ViewPager mPager;
	private AlbumAdapter mAlbumAdapter;
	private int extraCurrentItem;

	private CollectionsPopupManager mCollectionsPopupManager;

	private Personne personne;
	
	@SuppressLint("NewApi")
	public ImageDetailView(Activity mActivity, final Personne personne,final int extraCurrentItem, final IAction mInterface,CollectionsPopupManager mCollectionsPopupManager){
		this.personne = personne;
		this.mActivity = mActivity;
		this.mCollectionsPopupManager = mCollectionsPopupManager;
		this.mInterface = mInterface;
		this.extraCurrentItem = extraCurrentItem;
		this.mPanel = (FrameLayout) mActivity.findViewById(R.id.panel);
		this.list = (ListView) mActivity.findViewById(R.id.album_list);
		this.list.setFriction(ViewConfiguration.getScrollFriction() * 10);
		this.list.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState){
				if(mImageFetcherFromDisk != null){
					// Pause fetcher to ensure smoother scrolling when flinging
					if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING){
						// Before Honeycomb pause image loading on scroll to help with performance
						if(!Utils.hasHoneycomb())mImageFetcherFromDisk.setPauseWork(true);	
					}else mImageFetcherFromDisk.setPauseWork(false);	
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {}
		});

		final DisplayMetrics displayMetrics = new DisplayMetrics();
		this.mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		final int height = displayMetrics.heightPixels;
		final int width = displayMetrics.widthPixels;

		this.cacheParams = new ImageCache.ImageCacheParams(mActivity, IMAGE_CACHE_DIR);
		this.cacheParams.setMemCacheSizePercent(0.25f); 

		this.imageMinWidth = mActivity.getResources().getDimensionPixelSize(R.dimen.list_item_width);
		this.imageMinHeight = (int) ((9.0f/16.0f) * imageMinWidth*1.0f);

		this.mImageFetcherFromDisk = new ImageFetcher(mActivity, (int)(width),(int)(height));
		
		//big pictures : force imageViewtouch to load a picture
		this.mImageFetcherFromDisk.setUseAttachedViewModified(true);
		this.mImageFetcherFromDisk.setUseCache(true);
		this.mImageFetcherFromDisk.setPatientId(personne);
		
		this.mImageFetcherFromDisk.addImageCache(mActivity.getFragmentManager(),cacheParams);

		this.setupAlbumList();

		this.mAdapter = new ImagePagerAdapter(mActivity.getFragmentManager());

		this.mPager = (ViewPager) mActivity.findViewById(R.id.pager);
		this.mPager.setAdapter(mAdapter);

		this.updateFirstItem();
	}

	public void updateFirstItem(){
		int nb = (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;
		if (extraCurrentItem >= 0 && extraCurrentItem < nb) {
			if(mPager != null)mPager.setCurrentItem(extraCurrentItem);

			//Force update since not automatically updated by function setOnPageChangeListener when index equals 0
			if(extraCurrentItem == 0){
				if(mInterface != null){
					mInterface.setOnPageChangeListener(mCollectionsPopupManager.getListPhotos(), extraCurrentItem);
				}
			}
		}
	}

	private void setupAlbumList(){
		mAlbumAdapter = new AlbumAdapter(mActivity, mCollectionsPopupManager.getListPhotos());
		if(list != null){
			list.setAdapter(mAlbumAdapter);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,long id){
					if(mPager != null)mPager.setCurrentItem(position);
				}
			});
			if(extraCurrentItem != -1)list.setSelection(extraCurrentItem);
		}
	}

	private IAction mInterface;

	public interface IAction{
		public void setOnPageChangeListener(List<Photo> mPicturesList,int position);
		public ImageDetailFragment setImagePagerAdapterGetItem(List<Photo> mPictures,Photo data,ImageFetcher mImageFetcher);
		public View setVerticalListViewAdapterGetView(int position, View convertView, ViewGroup parent,List<Photo> mPhotos,LayoutInflater mInflater,ImageFetcher mImageFetcherFromDisk, int imageMinWidth, int imageMinHeight);
	}

	/**Setters & Getters**/

	public ViewPager getPager(){
		return this.mPager;
	}

	public IAction getInterface(){
		return this.mInterface;
	}

	public ImagePagerAdapter getAdapter(){
		return this.mAdapter;
	}

	public AlbumAdapter getAlbumAdapter(){
		return this.mAlbumAdapter;
	}

	public ListView getList(){
		return this.list;
	}

	public ImageFetcher getImageFetcher(){
		return this.mImageFetcherFromDisk;
	}

	public void setPictureList(){
		if(mAlbumAdapter != null)mAlbumAdapter.notifyDataSetChanged();
		if(mAdapter != null){
			mAdapter.notifyDataSetChanged();
			this.mPager.requestLayout();
			this.mPager.invalidate();
		}
		if(list != null)list.invalidate();
	}

	public class AlbumAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public AlbumAdapter(Context context, List<Photo> photos){
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;
		}

		@Override
		public Object getItem(int position) {
			return (position >= 0 && position < getCount())?mCollectionsPopupManager.getListPhotos().get(position):null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(mInterface != null)return mInterface.setVerticalListViewAdapterGetView(position, convertView, parent,mCollectionsPopupManager.getListPhotos(),mInflater,MainActivity.getImageFetcher(personne),imageMinWidth, imageMinHeight);
			else return null;
		}
	}

	/**
	 * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
	 * could be a large number of items in the ViewPager and we don't want to retain them all in
	 * memory at once but create/destroy them on the fly.
	 */
	public class ImagePagerAdapter extends util.components.fragmentstatepageradapter.FragmentStatePagerAdapter{
		public ImagePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return (mCollectionsPopupManager.getListPhotos() != null)?mCollectionsPopupManager.getListPhotos().size():0;//mSize;
		}

		@Override
		public Fragment getItem(int position) {
			if(position >= 0 && position < getCount()){
				if(mInterface != null){
					return mInterface.setImagePagerAdapterGetItem(mCollectionsPopupManager.getListPhotos(),mCollectionsPopupManager.getListPhotos().get(position),mImageFetcherFromDisk);
				}else{
					return null;
				}
			}else return null;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			super.destroyItem(arg0, arg1, arg2);

		}

		@Override
		public void finishUpdate(View arg0) {
			super.finishUpdate(arg0);

		}

		@Override
		public void startUpdate(View arg0) {
			super.startUpdate(arg0);

		}
	}

	public List<Photo> getPicturesList() {
		return mCollectionsPopupManager.getListPhotos();
	}

	@SuppressLint("NewApi")
	public void destroy() {
		if(mPanelAnimator != null){
			this.mPanelAnimator.removeListener(null);
			this.mPanelAnimator = null;
		}

		this.cacheParams = null;

		if(this.mImageFetcherFromDisk != null){
			this.mImageFetcherFromDisk.clearCache();
			this.mImageFetcherFromDisk.closeCache();
			this.mImageFetcherFromDisk = null;
		}

		if(this.list != null){
			this.list.setOnScrollListener(null);
			this.list = null;
		}

		this.mAdapter = null;

		if(this.mPager != null){
			this.mPager.removeAllViews();
			this.mPager.clearAnimation();
			this.mPager.clearDisappearingChildren();
			this.mPager.clearFocus();
			this.mPager.setOnSystemUiVisibilityChangeListener(null);
			this.mPager.setOnPageChangeListener(null);
			this.mPager = null;
		}
	}

	public void pause() {
		if(mImageFetcherFromDisk != null){
			mImageFetcherFromDisk.setExitTasksEarly(true);
			mImageFetcherFromDisk.flushCache();
		}

		if(MainActivity.getImageFetcher(personne) != null)MainActivity.getImageFetcher(personne).setExitTasksEarly(true);
	}

	public void onResume() {
		if(mImageFetcherFromDisk != null)mImageFetcherFromDisk.setExitTasksEarly(false);
		if(MainActivity.getImageFetcher(personne) != null)MainActivity.getImageFetcher(personne).setExitTasksEarly(false);
	}

	@SuppressLint("NewApi")
	public void togglePanel() {
		mPanelVisible = !mPanelVisible;

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			if (mPanelAnimator != null && mPanelAnimator.isRunning()) {
				mPanelAnimator.reverse();
				return;
			}

			ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(mActivity);

			if(screenDimensions != null && screenDimensions.size() == 2){
				int screeenWidth = screenDimensions.get(0).intValue();

				if(mPanelVisible) mPanelAnimator = ObjectAnimator.ofFloat(mPanel, "x", screeenWidth - mPanel.getWidth());
				else mPanelAnimator = ObjectAnimator.ofFloat(mPanel, "x", screeenWidth);

				if(mPanelAnimator != null){
					mPanelAnimator.setDuration(250);
					mPanelAnimator.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							if(mPanel != null){
								mPanel.invalidate();
								mPanel.refreshDrawableState();
							}
						}
					});
					mPanelAnimator.start();
				}
			}
		}else if(mPanel != null)mPanel.setVisibility(mPanelVisible?View.VISIBLE:View.GONE);
	}

	public String getCurrentImagePath(){
		return mCollectionsPopupManager.getListPhotos().get(extraCurrentItem).getId();
	}
}