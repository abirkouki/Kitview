package view.adapter;

import java.util.ArrayList;

import com.dentalcrm.kitview.R;

import model.rest.Subscriber;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<Subscriber>{
	private Context mContext;
	private ArrayList<Subscriber> data;

	public CustomAdapter(Context activitySpinner,ArrayList<Subscriber> objects) {
		super(activitySpinner, R.layout.spinner_rows, objects);
		mContext = activitySpinner;
		data = objects;
	}

	@Override
	public View getDropDownView(int position, View convertView,ViewGroup parent) {
		return getCustomView2(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView2(position, convertView, parent);
	}

	@Override
	public int getCount(){
		return (data != null)?data.size():0;
	}

	@Override
	public Subscriber getItem(int position) {
		int nbSubscribers = (data != null)?data.size():0;

		return (data != null && position >= 0 && position < nbSubscribers)?data.get(position):null;
	}

	@Override
	public int getViewTypeCount(){
		return 1;
	}

	@Override
	public int getItemViewType(int position) {
		return super.getItemViewType(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getCustomView2(int position, View convertView, ViewGroup parent) {
		LinearLayout ll = new LinearLayout(mContext);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, 100);
		ll.setLayoutParams(lp);
		ll.setOrientation(LinearLayout.HORIZONTAL);

		ImageView row = new ImageView(mContext);
		row.setLayoutParams(new AbsListView.LayoutParams(100, 100));

		TextView tv = new TextView(mContext);
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp2.gravity = Gravity.CENTER_VERTICAL;
		lp2.setMargins(0, 0, 2, 0);
		tv.setLayoutParams(lp2);

		ll.addView(row);
		ll.addView(tv);
		

		int nbSubscribers = (data != null)?data.size():0;

		if(position >= 0 && position < nbSubscribers){
			Subscriber subscriber = (Subscriber) data.get(position);
			tv.setText(subscriber.getmName());
			row.setBackgroundColor(subscriber.getColor());
		}
		return ll;
	}
}