package view.popup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import util.components.mail.Mail;
import util.components.progressdialog.FRProgressDialog;
import util.log.LogUtil;

import com.dentalcrm.kitview.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class QuitPopupManager{
	private Activity mMainMenuActivity;
	private Dialog mLineDeleteDialog;
	private Button mLineDeleteCancelButton,mLineValidateButton;
	private TextView mTitleTextView, mContentTextView;
	private CheckBox mCheckBox;

	public QuitPopupManager(Activity mMainMenuActivity){
		this.mMainMenuActivity = mMainMenuActivity;
	}

	public void initializePopup(){
		this.mLineDeleteDialog = new Dialog(mMainMenuActivity);
		this.mLineDeleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.mLineDeleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		this.mLineDeleteDialog.setContentView(R.layout.popup_quit);

		this.mTitleTextView = (TextView) mLineDeleteDialog.findViewById(R.id.title2);
		this.mContentTextView = (TextView) mLineDeleteDialog.findViewById(R.id.content2);
		this.mLineValidateButton = (Button)this.mLineDeleteDialog.findViewById(R.id.ok);
		this.mLineDeleteCancelButton = (Button)this.mLineDeleteDialog.findViewById(R.id.cancel);
		this.mCheckBox = (CheckBox)this.mLineDeleteDialog.findViewById(R.id.chkAndroid);
	}

	public void showPopup(String title, String content,final IClick _mIClick){
		if(mLineDeleteDialog != null && !mMainMenuActivity.isFinishing()){
			this.mTitleTextView.setText(title);
			this.mContentTextView.setText(content);

			this.mLineValidateButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					hideLineDeleteDialogPopup();

					if(_mIClick != null){
						if(mCheckBox.isChecked()){
							final FRProgressDialog mDialog = new FRProgressDialog(mMainMenuActivity, "",false);
							mDialog.showFRProgressDialog();

							//Need valid email & password to work ...
							final Mail m = new Mail("","");



							m.setTo(new String[]{"kitview.mobileapp@gmail.com"});
							m.setFrom("kitview.mobileapp@gmail.com");
							m.setSubject("Log");

							StringBuilder body = new StringBuilder();

							try{
								File file = LogUtil.getInstance().getLog();
								BufferedReader br = new BufferedReader(new FileReader(file));
								String line;

								while ((line = br.readLine()) != null){
									body.append(line);
									body.append('\n');
								}
								br.close();
							}catch(IOException e) {
								e.printStackTrace();
							}

							m.setBody(body.toString());

							new Thread(new Runnable(){
								@Override
								public void run() {
									try {
										m.send();
									} catch (Exception e) {
										e.printStackTrace();
									}

									mDialog.cancelFRProgressDialog();
									System.exit(0);
								}
							}).start();
						}else System.exit(0);
					}
				}
			});

			this.mLineDeleteCancelButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					hideLineDeleteDialogPopup();
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

	public interface IClick{
		public void onValidateClick();
		public void onCancelClick();
	}
}