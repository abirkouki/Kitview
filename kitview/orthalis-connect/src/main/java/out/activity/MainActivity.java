package out.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewAnimator;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.orthalis.connect.R;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.smooch.core.Smooch;
import io.smooch.core.User;
import io.smooch.ui.ConversationActivity;
import model.Module;
import util.FTP.PracticeFTP;
import util.app.AppController;
import util.components.progressdialog.FRProgressDialog;
import util.helper.ActionBarHelper;
import util.session.SQLiteHandler;
import util.session.SessionManager;
import util.system.SystemUtil;
import view.adapter.ModulesAdapter;
import view.popup.GenericPopupManager;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends AppCompatActivity{
    //Views
    private ViewAnimator mViewAnimator;
    private ModulesAdapter mModulesAdapter0,mModulesAdapter2;
    private GridView mGridView0, mGridView2;
    private ArrayList<Module> mModules0,mModules2;
    private boolean mInitializationFinished0,mInitializationFinished2;
    private int mSpacing;
    private VideoView mVideoView;
    private ImageView mImageView;
    private FRProgressDialog mDialog;
    private TextView mCurrentPatientInfosTextView;
    private TextView mCopyrightTextView;
    private GenericPopupManager mGenericPopupManager;
    private LinearLayout mActualSituationTextView;
    private LinearLayout mBottomInfosLinearLayout;

    private int mOrientation;

    private SessionManager session;

    public MainActivity getActivity() {
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_main);

        // Session manager
        session = new SessionManager(getApplicationContext());
        this.mOrientation = getResources().getConfiguration().orientation;

        this.mViewAnimator = (ViewAnimator) findViewById(R.id.va_main);
        this.mVideoView = (VideoView)findViewById(R.id.videoview);
        this.mImageView = (ImageView)findViewById(R.id.imageview);
        this.mCurrentPatientInfosTextView = (TextView) findViewById(R.id.tv_current_patient_infos);
        this.mCopyrightTextView = (TextView) findViewById(R.id.copyright);
        this.mActualSituationTextView = (LinearLayout) findViewById(R.id.ll_parent_infos);
        this.mBottomInfosLinearLayout = (LinearLayout) findViewById(R.id.ll_bottom_infos);

        this.mGridView0 = (GridView)this.findViewById(R.id.gridview_home0);
        this.mGridView2 = (GridView)this.findViewById(R.id.gridview_home2);



        File CfgDir = new File(getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "Config");

        if(CfgDir.exists()){
            ActionBarHelper.actionBarCustom(this,false);
            if (session.isLoggedIn()) {
                //System.out.println("isLogged");
                setViewAnimatorIndex(2);
                initializeGridView2();
            }//cas où il est pas loggé -> dans onResume()
        }else{
            ActionBarHelper.actionBarOrthalis(this);
            setViewAnimatorIndex(0);
            initializeGridView0();
        }

        mDialog = new FRProgressDialog(this, "",false);
    }


    public void launchWifiPopup(Activity context){
        String title = context.getResources().getString(R.string.wifi_title);
        String content = context.getResources().getString(R.string.wifi_content);
        launchGenericPopup(context,title, content, false);
    }


    private void launchGenericPopup(Activity context, final String title, final String content, final boolean exitOnClose){
        mGenericPopupManager = new GenericPopupManager(context);

        if(mGenericPopupManager != null){
            mGenericPopupManager.initializePopup();
        }

        mGenericPopupManager.showPopup(title,content,new GenericPopupManager.IClick() {
            @Override
            public void onValidateClick(){
                if(exitOnClose)System.exit(0);
            }

            @Override
            public void onCancelClick(){
                mGenericPopupManager.hideLineDeleteDialogPopup();
            }
        });
    }


    //TODO régler MPEG4Extractor: Reset mWrongNALCounter. Re-check a condition - 'isMalformed = 0'
    private void initializeVideoView(){
        Context context = getApplicationContext();
        Uri path;
        String pathStr = context.getFilesDir().getAbsolutePath() + File.separator + "Config" + File.separator + "video.mp4";
        File videoPractice = new File(pathStr);
        if (videoPractice.exists()) path = Uri.parse(pathStr);
        else path = Uri.parse("android.resource://" + getPackageName() + File.separator + R.raw.video);

        if(mVideoView != null){
            if(path != null )mVideoView.setVideoURI(path);
            mImageView.setBackgroundResource(R.color.translucent);
            mVideoView.seekTo(0);
            mVideoView.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //System.out.println("startVideo");
                    mVideoView.start();
                    mp.setLooping(true);
                }
            });
        }
    }

    //pour gérer les fichiers jpg et png (et autres s'il le faut)
    public String findExtension(String filePath){
        //crée un tableau d'extensions
        String[] ext=new String[]{".jpg",".png"};
        String path = "";
        for (String anExt : ext) {
            File file = new File(filePath + anExt);
            if (file.exists()) {
                path = filePath + anExt;
                break;
            }
        }
        return path;
    }

    private void initializeImageView(){
        Context context = getApplicationContext();
        if (mImageView != null){
            String pathStr = findExtension(context.getFilesDir().getAbsolutePath() + File.separator + "Config" + File.separator + "photo0");
            File imagePractice = new File(pathStr);
            if (imagePractice.exists()){
                Bitmap mBitMap = BitmapFactory.decodeFile(pathStr);
                mImageView.setImageBitmap(mBitMap);
            }
            else{
                InputStream is = getResources().openRawResource(R.raw.orthalis);
                Bitmap mBitMap = BitmapFactory.decodeStream(is);
                mImageView.setImageBitmap(mBitMap);
            }
        }
    }

    public void initializeGridView0(){
        this.mModules0 = new ArrayList<Module>();
        this.mModules0.add(new Module(R.string.practice_id, R.color.logo_orange, R.drawable.ic_action_group));
        this.mModules0.add(new Module(R.string.practice_scan, R.color.logo_brown, R.drawable.barcode));

        this.mInitializationFinished0 = false;

        if(this.mGridView0 != null){
            this.mGridView0.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onGlobalLayout() {
                            if(!mInitializationFinished0){

                                Point size = new Point();
                                getWindowManager().getDefaultDisplay().getSize(size);
                                int width = size.x;
                                //int height = size.y;

                                mSpacing = width/40;

                                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mGridView0.getLayoutParams();

                                if(mlp != null){
                                    mlp.setMargins(mSpacing, mSpacing, mSpacing, mSpacing);
                                    mGridView0.setLayoutParams(mlp);
                                }

                                mGridView0.setHorizontalSpacing(mSpacing);
                                mGridView0.setVerticalSpacing(mSpacing);

                                int nbItemsPerRow = 0,nbRows = 0;

                                if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
                                    nbItemsPerRow = 2;
                                    nbRows = 1;
                                }else if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
                                    nbItemsPerRow = 2;
                                    nbRows = 1;
                                }

                                mInitializationFinished0 = true;
                                mModulesAdapter0 = new ModulesAdapter(mModules0, nbItemsPerRow,nbRows, mGridView0, MainActivity.this, mSpacing);

                                //After set setMargins ==> addOnGlobalLayoutListener called second time later
                            }else{
                                mGridView0.setAdapter(mModulesAdapter0);

                                if(mGridView0.getViewTreeObserver() != null){
                                    if (SystemUtil.hasJellyBean())mGridView0.getViewTreeObserver().removeOnGlobalLayoutListener(this);//versions récentes
                                    else mGridView0.getViewTreeObserver().removeGlobalOnLayoutListener(this);//versions antérieures
                                }
                            }
                        }
                    });

            this.mGridView0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                    switch (arg2) {
                        case 0:
                            practiceDialog();
                            break;
                        case 1:
                            practiceScan();
                            break;
                    }
                }
            });
        }
    }


    public void practiceScan(){
        new IntentIntegrator(getActivity()).initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // pour parser le résultat du scan
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String code_input = scanningResult.getContents();
            runFTPQuery(code_input);
        }
        else Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.no_data_received),Toast.LENGTH_LONG).show();
    }

    //TODO changer skin du dialog
    public void practiceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle(R.string.practice_id);
        builder.setView(inflater.inflate(R.layout.dialog_practicecode,null));
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText code_input = (EditText) ((Dialog) dialog).findViewById(R.id.code_input);
                runFTPQuery(code_input.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //dialog.cancel();//a priori marche meme sans
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    public void runFTPQuery(final String code_input){
        final PracticeFTP ftp = new PracticeFTP(code_input,getApplicationContext(),getActivity());
        Thread thread1;
        final Thread thread2;
        final AtomicBoolean[] folderExists = {new AtomicBoolean(false)};//pour que tous les thread puissent accéder à ce booléen
        final Handler handler = new Handler();

        Runnable checkRun = new Runnable() {
            @Override
            public void run() {
                folderExists[0] = new AtomicBoolean(ftp.distantFolderExists());
            }
        };

        Runnable downloadRun = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mDialog != null)mDialog.showFRProgressDialog();
                    }
                });
                folderExists[0] = new AtomicBoolean(ftp.downloadServerFolder());
            }
        };

        try {
            thread1 = new Thread(checkRun);
            thread1.start();
            thread1.join();//Attend que le thread finisse avant de continuer
            thread2 = new Thread(downloadRun);

            if (folderExists[0].get()){
                thread2.start();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //System.out.println("postDelayed");
                        if (thread2.getState() == Thread.State.TERMINATED){
                            //System.out.println("thread terminated");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mDialog != null)mDialog.cancelFRProgressDialog();
                                    if (folderExists[0].get()){//que si tout a réussi
                                        AppController.parseConfigFile(getApplicationContext());
                                        if (AppController.practiceConfigServer.chatSmooch != null) Smooch.init(getApplication(), AppController.practiceConfigServer.chatSmooch);
                                        recreate();
                                    };
                                }
                            });
                        }
                        else handler.postDelayed(this, 5000);//toutes les 5 secondes
                    }
                });

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void initializeGridView2(){
        this.mModules2 = new ArrayList<Module>();
        this.mModules2.add(new Module(R.string.appointment, R.color.color6, R.drawable.ic_action_time));
        this.mModules2.add(new Module(R.string.balance, R.color.color6, R.drawable.ic_action_refresh));
        this.mModules2.add(new Module(R.string.notification, R.color.color6, R.drawable.ic_action_view_as_list));
        this.mModules2.add(new Module(R.string.Smooch_activityConversation, R.color.color6, R.drawable.ic_action_group));
        this.mModules2.add(new Module(R.string.contact, R.color.color6, R.drawable.ic_action_person));
        this.mModules2.add(new Module(R.string.title_activity_opening, R.color.color6, R.drawable.ic_action_go_to_today));
        this.mModules2.add(new Module(R.string.settings, R.color.color6, R.drawable.ic_action_settings));
        this.mModules2.add(new Module(R.string.about, R.color.color6, R.drawable.ic_action_about));
        this.mModules2.add(new Module(R.string.take_a_pic, R.color.color6, R.drawable.ic_action_camera));

        this.mInitializationFinished2 = false;

        if(this.mGridView2 != null){
            this.mGridView2.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onGlobalLayout() {
                            if(!mInitializationFinished2){
                                Point size = new Point();
                                getWindowManager().getDefaultDisplay().getSize(size);
                                int width = size.x;
                                //int height = size.y;

                                mSpacing = width/40;
                                LinearLayout.LayoutParams llp3 = (android.widget.LinearLayout.LayoutParams) mActualSituationTextView.getLayoutParams();//new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                                llp3.setMargins(mSpacing, 0, mSpacing, 0);
                                mActualSituationTextView.setLayoutParams(llp3);
                                mActualSituationTextView.requestLayout();
                                mActualSituationTextView.invalidate();

                                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mGridView2.getLayoutParams();
                                if(mlp != null)mlp.setMargins(mSpacing, mSpacing, mSpacing, mSpacing);

                                mGridView2.setLayoutParams(mlp);
                                mGridView2.setHorizontalSpacing(mSpacing);
                                mGridView2.setVerticalSpacing(mSpacing);

                                mInitializationFinished2 = true;

                                int nbItemsPerRow = 0,nbRows = 0;

                                if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
                                    nbItemsPerRow = 3;
                                    nbRows = 4;
                                }else if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
                                    nbItemsPerRow = 2;
                                    nbRows = 6;
                                }

                                mModulesAdapter2 = new ModulesAdapter(mModules2,nbItemsPerRow,nbRows,mGridView2, MainActivity.this, mSpacing);

                                //After set setMargins ==> addOnGlobalLayoutListener called second time later
                            }else{
                                if(mModulesAdapter2 != null)mGridView2.setAdapter(mModulesAdapter2);

                                if(mGridView2.getViewTreeObserver() != null){
                                    if (SystemUtil.hasJellyBean())mGridView2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    else mGridView2.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    });

            this.mGridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                    Intent intent;
                    switch (arg2) {
                        //RDV
                        case 0:
                            intent = new Intent(MainActivity.this, ProchainRdvActivity.class);
                            startActivity(intent);
                            break;
                        //Balance
                        case 1:
                            intent = new Intent(MainActivity.this, BalanceActivity.class);
                            startActivity(intent);
                            break;
                        //Notif
                        case 2:
                            intent = new Intent(MainActivity.this, NotificationsActivity.class);
                            startActivity(intent);
                            break;
                        //Chat
                        case 3:
                            if (AppController.practiceConfigServer.chatSmooch != null) {
                                SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                                HashMap<String, String> user = db.getUserDetails();
                                final String nom = user.get("nom");
                                final String prenom = user.get("prenom");
                                User.getCurrentUser().setFirstName(prenom);
                                User.getCurrentUser().setLastName(nom);
                                ConversationActivity.show(MainActivity.this);
                            }else {
                                Toast.makeText(getApplicationContext(),R.string.no_chat,Toast.LENGTH_SHORT).show();
                            }
                            break;
                        //Contact
                        case 4:
                            intent = new Intent(MainActivity.this, ContactActivity.class);
                            startActivity(intent);
                            break;
                        //Horaires
                        case 5:
                            intent = new Intent(MainActivity.this, OpeningActivity.class);
                            startActivity(intent);
                            break;
                        //Settings
                        case 6:
                            intent = new Intent(MainActivity.this, out.activity.SettingsActivity.class);
                            startActivity(intent);
                            break;
                        //A propos
                        case 7:
                            intent = new Intent(MainActivity.this, AboutActivity.class);
                            startActivity(intent);
                            break;
                        //Photo
                        case 8://TODO masquer pour le moment
                            //intent = new Intent(MainActivity.this, PhotoActivity.class);
                            //startActivity(intent);
                            Toast.makeText(getApplicationContext(),R.string.work_in_progress,Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }
    }


    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("KEY_VIDEO",false)) {
            mVideoView.setVisibility(View.VISIBLE);
            this.initializeVideoView();
            mImageView.setVisibility(View.GONE);
        }
        else {
            mImageView.setVisibility(View.VISIBLE);
            this.initializeImageView();
            mVideoView.setVisibility(View.GONE);
        }

        File CfgDir = new File(getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "Config");

        if (CfgDir.exists() && !session.isLoggedIn()){
            //System.out.println("isNotLogged");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void startVideo(){
        try{
            if(mVideoView != null){
                mVideoView.seekTo(0);
                mVideoView.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        //pauseVideo();
    }

    public void pauseVideo(){
        if(mVideoView != null){
            mVideoView.seekTo(0);
            mVideoView.pause();
        }
    }

    @Override
    public void onBackPressed() {
        String content = getResources().getString(R.string.confirm_quit_application);
        String title = getResources().getString(R.string.quit_application);
        launchGenericPopup(MainActivity.this,title,content,true);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        recreate();
    }

    public void setViewAnimatorIndex(int index) {
        mViewAnimator.setDisplayedChild(index);
        mViewAnimator.requestLayout();
        mViewAnimator.invalidate();
        mBottomInfosLinearLayout.setVisibility((index==1)?View.VISIBLE:View.GONE);
        mBottomInfosLinearLayout.requestLayout();
        mBottomInfosLinearLayout.invalidate();
    }

}