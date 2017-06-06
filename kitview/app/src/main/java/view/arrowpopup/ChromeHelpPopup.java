package view.arrowpopup;

import java.util.ArrayList;
import java.util.List;

import util.system.SystemUtil;
import model.rest.Scenario;

import com.dentalcrm.kitview.R;

import activity.ScenariosActivity;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ChromeHelpPopup {
	protected WindowManager mWindowManager;

	protected Context mContext;
	protected PopupWindow mWindow;

	private ImageView mUpImageView;
	private ImageView mDownImageView;
	protected View mView;

	protected Drawable mBackgroundDrawable = null;
	protected ShowListener showListener;

	private ListView mListView;

	private View mParent;

	public ChromeHelpPopup(Context context, String text, int viewResource) {
		mContext = context;
		mWindow = new PopupWindow(context);

		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mView = layoutInflater.inflate(viewResource, null);

		this.mListView = (ListView)mView.findViewById(R.id.lv_chrome_popup);

		mUpImageView = (ImageView) mView.findViewById(R.id.arrow_up);
		mDownImageView = (ImageView) mView.findViewById(R.id.arrow_down);
	}

	private List<Scenario> scenarios;
	
	public void initializeListView(final ScenariosActivity activity, final List<Scenario> scenarios){
		this.scenarios = scenarios;
		int nbScenarios = (scenarios != null)?scenarios.size():0;

		if(nbScenarios > 0){
			ObjectItem[]ObjectItemData = new ObjectItem[nbScenarios];
			
			for(int i=0;i<nbScenarios;i++){
				ObjectItemData[i] = new ObjectItem(i, scenarios.get(i).getNom());
			}

			ArrayAdapterItem adapter = new ArrayAdapterItem(mContext, R.layout.list_view_row_item, ObjectItemData);
			mListView.setAdapter(adapter);
			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					int nbScenarios = (scenarios != null)?scenarios.size():0;
					String text = (nbScenarios > 0 && position >= 0 && position < nbScenarios)?scenarios.get(position).getNom():"";
					activity.udpateScenarioTextView(text);
					activity.updateCurrentScenario(position);
					mWindow.dismiss();
				}
			});
			
			adapter.notifyDataSetChanged();
			
			mListView.requestLayout();
			mListView.invalidate();
			
		}
	}

	class ArrayAdapterItem extends ArrayAdapter<ObjectItem> {
		Context mContext;
		int layoutResourceId;
		ObjectItem data[] = null;

		public ArrayAdapterItem(Context mContext, int layoutResourceId, ObjectItem[] data) {
			super(mContext, layoutResourceId, data);

			this.layoutResourceId = layoutResourceId;
			this.mContext = mContext;
			this.data = data;
		}

		@Override
		public int getCount() {
			return (data != null)?this.data.length:0;
		}
		
		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}
		
		@Override
		public ObjectItem getItem(int position) {
			return (data != null && position >= 0 && position < data.length)?data[position]:null;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			/*
			 * The convertView argument is essentially a "ScrapView" as described is Lucas post 
			 * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
			 * It will have a non-null value when ListView is asking you recycle the row layout. 
			 * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
			 */
			if(convertView==null){
				// inflate the layout
				LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
				convertView = inflater.inflate(layoutResourceId, parent, false);
			}

			// object item based on the position
			ObjectItem objectItem = data[position];

			// get the TextView and then set the text (item name) and tag (item ID) values
			TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);
			textViewItem.setText(objectItem.itemName.trim());
			textViewItem.setTag(objectItem.itemId);

			return convertView;
		}
	}

	public class ObjectItem {
		public int itemId;
		public String itemName;

		// constructor
		public ObjectItem(int itemId, String itemName) {
			this.itemId = itemId;
			this.itemName = itemName;
		}
	}

	public ChromeHelpPopup(Context context){
		this(context, "", R.layout.layout_listview_chrome_popup);//popup);
	}

	public ChromeHelpPopup(Context context, String text) {
		this(context);
	}

	public void show(View anchor) {
		preShow();

		int[] location = new int[2];
		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());

		mView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight = mView.getMeasuredHeight();
		int rootWidth = mView.getMeasuredWidth();

		final int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		final int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

		//approximation since mListView.getMeasuredHeight() return 0 or an incorrect height !!!
		int h2 = SystemUtil.dpToPx(mContext, 40)* scenarios.size() + 30;
		int yPos = screenHeight - ((ScenariosActivity)mContext).getScenariosTextView().getHeight() - h2;
		boolean onTop = true;

		int whichArrow, requestedX;
		whichArrow = R.id.arrow_down;
		requestedX = anchorRect.centerX();

		View arrow = mUpImageView;
		View hideArrow = mUpImageView;

		final int arrowWidth = arrow.getMeasuredWidth();

		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) arrow.getLayoutParams();

		int xPos = 0;

		// ETXTREME RIGHT CLIKED
		if (anchorRect.left + rootWidth > screenWidth) {
			xPos = (screenWidth - rootWidth);
		}
		// ETXTREME LEFT CLIKED
		else if (anchorRect.left - (rootWidth / 2) < 0) {
			xPos = anchorRect.left;
		}
		// INBETWEEN
		else xPos = (anchorRect.centerX() - (rootWidth / 2));

		param.leftMargin = (requestedX - xPos) - (arrowWidth / 2);

		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	protected void preShow() {
		if (mView == null)throw new IllegalStateException("view undefined");

		if (showListener != null) {
			showListener.onPreShow();
			showListener.onShow();
		}

		if (mBackgroundDrawable == null)mWindow.setBackgroundDrawable(new BitmapDrawable());
		else mWindow.setBackgroundDrawable(mBackgroundDrawable);

		mWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		mWindow.setTouchable(true);
		mWindow.setFocusable(true);
		mWindow.setOutsideTouchable(true);
		mWindow.setContentView(mView);
	}

	public void setBackgroundDrawable(Drawable background) {
		mBackgroundDrawable = background;
	}

	public void setContentView(View root) {
		mView = root;
		mWindow.setContentView(root);
	}

	public void setContentView(int layoutResID) {
		LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setContentView(inflator.inflate(layoutResID, null));
	}

	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		mWindow.setOnDismissListener(listener);
	}

	public void dismiss() {
		mWindow.dismiss();
		if (showListener != null) {
			showListener.onDismiss();
		}
	}

	public static interface ShowListener {
		void onPreShow();
		void onDismiss();
		void onShow();
	}

	public void setShowListener(ShowListener showListener) {
		this.showListener = showListener;
	}
}
