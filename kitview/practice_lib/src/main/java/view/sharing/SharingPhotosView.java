package view.sharing;

import java.util.ArrayList;
import java.util.List;
import com.dentalcrm.kitview.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class SharingPhotosView {
	private Context mContext;
	private Dialog mCloudPrintingPopup;
	private TextView mTitle;
	private ListView mListView;

	public SharingPhotosView(Context context){
		this.mContext = context;

		mCloudPrintingPopup = new Dialog(mContext);
		mCloudPrintingPopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mCloudPrintingPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mCloudPrintingPopup.setContentView(R.layout.share_popup);

		mListView = (ListView) mCloudPrintingPopup.findViewById(R.id.listView);

		mTitle = (TextView) mCloudPrintingPopup.findViewById(R.id.title);
	}

	public void showDialog(final Context context, final boolean sharingOneItem, final ArrayList<Uri> fileNames){
		Intent sendIntent = sharingOneItem?(new Intent(Intent.ACTION_SEND)):(new Intent(Intent.ACTION_SEND_MULTIPLE));
		sendIntent.setType("image/*");//sharingPhoto?"image/*":"video/*");

		List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(sendIntent, 0);

		String title = mContext.getResources().getString(R.string.share_photo_title);//"";

		//if(sharingPhoto)title = sharingOneItem?"Share this photo":"Share these photos";	
		//else title = sharingOneItem?"Share this video":"Share these videos";

		mTitle.setText(title);

		final ShareIntentListAdapter adapter = new ShareIntentListAdapter(context, activities.toArray());

		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				ResolveInfo info = (ResolveInfo) adapter.getItem(arg2);

				Intent intent = sharingOneItem?(new Intent(Intent.ACTION_SEND)):(new Intent(Intent.ACTION_SEND_MULTIPLE));
				intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
				intent.setType("image/*");//sharingPhoto?"image/*":"video/*");

				if(fileNames != null && fileNames.size()>0){
					if(sharingOneItem){
						intent.putExtra(Intent.EXTRA_STREAM, fileNames.get(0));		
					}else{
						intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileNames);	
					}

					((Activity)context).startActivity(intent);
				}

			}
		});

		/*builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ResolveInfo info = (ResolveInfo) adapter.getItem(which);

				Intent intent = sharingOneItem?(new Intent(Intent.ACTION_SEND)):(new Intent(Intent.ACTION_SEND_MULTIPLE));
				intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
				intent.setType(sharingPhoto?"image/*":"video/*");


				if(fileNames != null && fileNames.size()>0){
					if(sharingOneItem){
						intent.putExtra(Intent.EXTRA_STREAM, fileNames.get(0));		
					}else{
						intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileNames);	
					}


					((Activity)context).startActivity(intent);
				}
			}
		});

		return builder.create();*/

		mCloudPrintingPopup.show();

	}

	public void hideDialog(){
		this.mCloudPrintingPopup.hide();
	}

	static class ShareIntentListAdapter extends BaseAdapter{
		private Context context;
		private Object[] items;

		public ShareIntentListAdapter(Context context, Object[] items) {
			this.context = context;
			this.items = items;
		}

		public View getView(int pos, View convertView, ViewGroup parent) {
			LinearLayout row = null;
			TextView label = null;
			ImageView image = null;

			if(convertView == null){
				row = new LinearLayout(context);

				AbsListView.LayoutParams lp3 = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

				row.setLayoutParams(lp3);

				label = new TextView(context);
				label.setTextColor(Color.GRAY);

				LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lp2.weight = 1;
				lp2.gravity = Gravity.CENTER;

				label.setLayoutParams(lp2);

				row.addView(label);

				image = new ImageView(context);
				LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				image.setLayoutParams(lp);
				row.addView(image);
			}else{
				row = (LinearLayout) convertView;
				label = (TextView) row.getChildAt(0);
				image = (ImageView) row.getChildAt(1);
			}

			if(pos >= 0 && pos < getCount()){
				ResolveInfo resolveInfo = ((ResolveInfo)items[pos]);

				if(resolveInfo != null && resolveInfo.activityInfo != null && resolveInfo.activityInfo.applicationInfo != null){
					label.setText(resolveInfo.activityInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
					image.setImageDrawable(resolveInfo.activityInfo.applicationInfo.loadIcon(context.getPackageManager()));
				}
			}
			return(row);
		}

		@Override
		public int getCount() {
			return (items != null)?items.length:0;
		}

		@Override
		public Object getItem(int position) {
			return items[position];//View;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}	
}
