package util.components.multileveltreelist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import view.popup.CollectionsPopupManager;
import view.popup.CollectionsPopupManager.IClick;
import com.dentalcrm.kitview.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<Entity> objects;
	private LayoutInflater lLayoutInflator;
	private View mView;
	private IClick mIClick;

	public ListAdapter(Context context, int textViewResourceId, ArrayList<Entity> objects, IClick mIClick){
		this.mIClick = mIClick;
		this.objects = objects;
		this.mContext = context;
		this.lLayoutInflator = LayoutInflater.from(mContext);
		this.mView = lLayoutInflator.inflate(R.layout.layout_row_treelist,null);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		try{
			final Entity CC = getItem(position);

			if (convertView == null){
				convertView = View.inflate(mContext,R.layout.layout_row_treelist,null);

				holder = new ViewHolder();
				holder.txtName=(TextView)convertView.findViewById(R.id.row_cell_text_multilevel);
				holder.tvDummy=(TextView)convertView.findViewById(R.id.row_cell_text_dummy_multilevel);
				holder.btn=(Button)convertView.findViewById(R.id.row_cell_btn_multilevel);

				convertView.setTag(holder);
			}

			holder = (ViewHolder) convertView.getTag();

			if(CC.HasChild){
				holder.btn.setVisibility(View.VISIBLE);

				if(!CC.isOpened)holder.btn.setText("+");
				else holder.btn.setText("-");

				holder.btn.setEnabled(true);
			}else{
				holder.btn.setVisibility(View.INVISIBLE);
				holder.btn.setText("");
				holder.btn.setEnabled(false);
			}

			holder.btn.setTag(position);
			holder.txtName.setTag(position);

			
			String name =  CC.Name;
			name = name.replace(CollectionsPopupManager.SPECIAL_CHAR_STRING_REPLACEMENT_SLASH,"/");
			name = name.replace(CollectionsPopupManager.SPECIAL_CHAR_STRING_REPLACEMENT_BACKSLASH,"\\");
			name = name.replace(CollectionsPopupManager.SPECIAL_CHAR_STRING_REPLACEMENT_DOUBLEPOINT,":");
			name = name.replace(CollectionsPopupManager.SPECIAL_CHAR_STRING_REPLACEMENT_STAR,"*");
			name = name.replace(CollectionsPopupManager.SPECIAL_CHAR_STRING_REPLACEMENT_QUESTION,"?");
			name = name.replace(CollectionsPopupManager.SPECIAL_CHAR_STRING_REPLACEMENT_QUOTE,"\"");
			name = name.replace(CollectionsPopupManager.SPECIAL_CHAR_STRING_REPLACEMENT_GREATERTHAN,">");
			name = name.replace(CollectionsPopupManager.SPECIAL_CHAR_STRING_REPLACEMENT_LOWER_THAN,"<");
			name = name.replace(CollectionsPopupManager.SPECIAL_CHAR_STRING_REPLACEMENT_PIPE,"|");
			
			
			CC.Name = name;//CC.Name.replace(CollectionsPopupManager.SPECIAL_CHAR_SLASH_STRING_REPLACEMENT,"/");

			holder.txtName.setText(CC.Name);

			String str="";
			int level = CC.level;

			for(int i=0;i<level*3;i++){
				str+="-";
			}

			holder.tvDummy.setText(""+str);
			holder.txtName.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(mIClick != null)mIClick.onClickOnText(v,CC.AbsolutePath);
				}
			});

			holder.btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mIClick != null)mIClick.onClickOnButton(v);
				}
			});

		}catch (Exception e){
			e.printStackTrace();
		}
		return convertView;
	}

	static class ViewHolder{
		public TextView txtName;
		public Button btn;
		public TextView tvDummy;
	}

	public String GetUTCdateAsString(){
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date());
	}

	public int getCount() {
		return this.objects==null?0:this.objects.size();
	}

	public Entity getItem(int position) {
		return this.objects==null?null:this.objects.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}
}