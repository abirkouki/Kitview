package view.popup;

import com.dentalcrm.kitview.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class GenericPopupManager{
	private Activity mMainMenuActivity;
	private Dialog mLineDeleteDialog;
	private Button mLineDeleteCancelButton,mLineValidateButton;
	private TextView mTitleTextView, mContentTextView;
	private IClick mIClick;

	public GenericPopupManager(Activity mMainMenuActivity){
		this.mMainMenuActivity = mMainMenuActivity;
	}

	public void initializePopup(){
		this.mLineDeleteDialog = new Dialog(mMainMenuActivity);
		this.mLineDeleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.mLineDeleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		this.mLineDeleteDialog.setContentView(R.layout.popup_line_delete);

		this.mTitleTextView = (TextView) mLineDeleteDialog.findViewById(R.id.title);
		this.mContentTextView = (TextView) mLineDeleteDialog.findViewById(R.id.content);
		this.mLineValidateButton = (Button)this.mLineDeleteDialog.findViewById(R.id.ok);
		this.mLineDeleteCancelButton = (Button)this.mLineDeleteDialog.findViewById(R.id.cancel);
	}

	public void initializeLayoutReinitPopup(final IClick _mIClick){
	}

	public void showPopup(String title, String content,final IClick _mIClick){
		if(mLineDeleteDialog != null && !mMainMenuActivity.isFinishing()){
			this.mIClick = _mIClick;
			this.mTitleTextView.setText(title);
			this.mContentTextView.setText(content);

			this.mLineValidateButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					hideLineDeleteDialogPopup();

					if(_mIClick != null){
						mIClick = _mIClick;
						mIClick.onValidateClick();
					}
				}
			});
			
			this.mLineDeleteCancelButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					hideLineDeleteDialogPopup();

					if(_mIClick != null){
						mIClick = _mIClick;
						mIClick.onCancelClick();
					}
				}
			});

			mLineDeleteDialog.show();
		}
	}

	public void hideLineDeleteDialogPopup(){
		if(mLineDeleteDialog != null)mLineDeleteDialog.dismiss();
	}

	public Dialog getLineDeleteDialogPopup(){
		return mLineDeleteDialog;
	}

	public void setInterface(IClick mIClick){
		this.mIClick = mIClick;
	}

	public interface IClick{
		public void onValidateClick();
		public void onCancelClick();
	}
}