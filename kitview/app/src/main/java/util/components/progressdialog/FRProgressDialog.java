package util.components.progressdialog;

import com.dentalcrm.kitview.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Window;

public class FRProgressDialog {
	//Views
	private ProgressBarUI mProgressBarUI;
	private FRDialog mDialog;
	private Activity context;

	//Model
	private boolean processing = true;
	private String progressMessage;
	private boolean twoSquareMode;

	//Thread
	private Thread thread;
	private Runnable mRunnable;
	private Handler handler;

	public FRProgressDialog(Activity ctx,String temp_progres_text, boolean twoSquareMode) {
		this.twoSquareMode = twoSquareMode;
		this.context = ctx;
		this.progressMessage = temp_progres_text;
		this.createDialog();
	}

	private void createDialog(){
		handler = new Handler();
		mDialog = new FRDialog(context);
		mProgressBarUI = (ProgressBarUI)mDialog.findViewById(R.id.progressbarui);		
		//mProgressBarUI.setText(progressMessage);
		mProgressBarUI.setDialog(mDialog);
		mProgressBarUI.setTwoSquareMode(twoSquareMode);
	}

	public void showFRProgressDialog(){
		try{
			createDialog();

			if(!context.isFinishing())mDialog.show();
			
			processing = true;

			mRunnable = new Runnable() {
				public void run(){
					try{
						mProgressBarUI.incCurrentRectIndex();
						mProgressBarUI.invalidate();
					}catch (Exception e) {
						e.printStackTrace();
					}	
				}
			};

			thread = new Thread(new Runnable(){
				public void run(){
					try{
						while(processing){			
							Thread.sleep(200);
							if(handler != null)handler.post(mRunnable);
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			});
			thread.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void cancelFRProgressDialog(){
		try{
			processing = false;

			if(mDialog != null){
				mDialog.dismiss();
			}

			if(handler != null && mRunnable != null){
				handler.removeCallbacks(mRunnable);
				mRunnable = null;
			}

			if(mProgressBarUI != null){
				mProgressBarUI.setDialog(null);
				mProgressBarUI = null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}  

	public class FRDialog extends Dialog  {
		public FRDialog(Context context) {
			super(context);

			getWindow().requestFeature(Window.FEATURE_NO_TITLE);

			setContentView(R.layout.layout_progressdialog);
			getWindow().setBackgroundDrawableResource(android.R.color.transparent);

			setCancelable(true);	
		}
	}

	public boolean isProcessing(){
		return processing;
	}

	public void setText(String text){
		this.progressMessage = text;
	}
}