package view.adapter;

import java.util.ArrayList;

import model.Module;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.dentalcrm.kitview.R;

public class ModulesAdapter extends BaseAdapter{
	private ArrayList<model.Module> mModules;
	private GridView mGridView;
	private Context mContext;
	private int mSpacing,w,h,newWidth,newHeight;

	public ModulesAdapter(ArrayList<Module> mModules, int nbItemsPerRow, int nbRows, GridView mGridView, Context context, int mSpacing){
		this.mModules = mModules;
		this.mGridView = mGridView;
		this.mContext = context;
		this.mSpacing = mSpacing;
		this.w = this.mGridView.getWidth();
		this.h = this.mGridView.getHeight();
		this.newHeight = ((this.h-(nbRows+1)*this.mSpacing)/nbRows);
		this.newWidth = (this.w-(nbItemsPerRow+1)*this.mSpacing)/nbItemsPerRow;
	}

	@Override
	public int getCount() {
		return (mModules != null)?mModules.size():0;
	}

	@Override
	public Object getItem(int position) {
		return (mModules != null && position>=0 && position < mModules.size())?mModules.get(position):null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LinearLayout ll = null;
		ImageView iv = null;
		TextView tv = null;

		if(convertView == null){
			ll = new LinearLayout(mContext);
			ll.setOrientation(LinearLayout.HORIZONTAL);

			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(newWidth, newHeight);

			ll.setLayoutParams(lp);

			iv = new ImageView(mContext);
			
			int h = mContext.getResources().getDimensionPixelSize(R.dimen.main_menu_module_height);
			
			LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, h);
			
			iv.setLayoutParams(llp);

			ll.addView(iv);

			tv = new TextView(mContext);
			
			llp.gravity = Gravity.CENTER;
			tv.setLayoutParams(llp);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			tv.setPadding(0, 0, 4, 0);
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(Color.WHITE);

			ll.addView(tv);
		}else{
			ll = (LinearLayout)convertView;
			iv = ((ImageView)(ll.getChildAt(0)));
			tv = ((TextView)(ll.getChildAt(1)));
		}

		int nbData = (mModules != null)?mModules.size():0;

		if(mModules != null && nbData > 0 && position >= 0 && position < nbData){
			String text = mContext.getResources().getString(mModules.get(position).getNameId());
			tv.setText(text);

			ll.setBackgroundColor(mContext.getResources().getColor(mModules.get(position).getBackgroundColorId()));

			iv.setImageResource(mModules.get(position).getDrawableId());
		}
		return ll;
	}
}