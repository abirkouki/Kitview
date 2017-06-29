package com.example.android.out;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import activity.FolderActivity;
import activity.SameCasesActivity;
import activity.ScenariosActivity;
import activity.SettingsActivity;
import model.Module;
import model.PersistenceManager;
import model.rest.Personne;
import util.FTP.PracticeFTP;
import util.components.progressdialog.FRProgressDialog;
import util.network.KitviewUtil;
import util.system.SystemUtil;
import view.adapter.ModulesAdapter;
import view.popup.GenericPopupManager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kitview.out.mobile.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewAnimator;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends FragmentActivity{
    //Views
    private ViewAnimator mViewAnimator;
    private ModulesAdapter mModulesAdapter0,mModulesAdapter2;
    private GridView mGridView0, mGridView2;
    private ArrayList<Module> mModules0,mModules2;
    private boolean mInitializationFinished0,mInitializationFinished2;
    private int mSpacing;
    private VideoView mVideoView;
    private FRProgressDialog mDialog;
    private TextView mCurrentPatientInfosTextView;
    private TextView mCopyrightTextView;
    private GenericPopupManager mGenericPopupManager;
    private LinearLayout mActualSituationTextView;
    private LinearLayout mBottomInfosLinearLayout;

    //Model
    private PersistenceManager mPersistenceManager;
    private int mOrientation;

    public final static String KEY_TEST_CONNECTION = "KEY_TEST_CONNECTION";

    private boolean mCheckKitViewConnection = true;

    public MainActivity getActivity() {
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        //TODO app bar s'est barre essayer de la remettre

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            mCheckKitViewConnection = (Boolean) savedInstanceState.get(KEY_TEST_CONNECTION);
        }

        this.mOrientation = getResources().getConfiguration().orientation;

        this.mPersistenceManager = PersistenceManager.getInstance();

        //TODO trim layout
        this.mViewAnimator = (ViewAnimator) findViewById(R.id.va_main);
        this.mVideoView = (VideoView)findViewById(R.id.videoview);
        this.mCurrentPatientInfosTextView = (TextView) findViewById(R.id.tv_current_patient_infos);
        this.mCopyrightTextView = (TextView) findViewById(R.id.copyright);
        this.mActualSituationTextView = (LinearLayout) findViewById(R.id.ll_parent_infos);
        this.mBottomInfosLinearLayout = (LinearLayout) findViewById(R.id.ll_bottom_infos);

        this.initializeVideoView();

        this.mGridView0 = (GridView)this.findViewById(R.id.gridview_home0);
        //this.mGridView = (GridView)this.findViewById(R.id.gridview_home);
        this.mGridView2 = (GridView)this.findViewById(R.id.gridview_home2);

        try{
            //int mode = (mPersistenceManager != null)?mPersistenceManager.getMode():PersistenceManager.MODE_SELECTION;

            File CfgDir = new File(getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "Config");

            if(CfgDir.exists()){
                setViewAnimatorIndex(2);
                initializeGridView2();
            }else{
                setViewAnimatorIndex(0);
                initializeGridView0();
            }

            mDialog = new FRProgressDialog(this, "",false);

            if(mViewAnimator.getDisplayedChild() != 0 && mCheckKitViewConnection)checkKitViewConnection();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //TODO garder peut etre utile
    public void launchWifiPopup(Activity context){
        String title = context.getResources().getString(R.string.wifi_title);
        String content = context.getResources().getString(R.string.wifi_content);
        launchGenericPopup(context,title, content, false);
    }

    //TODO garder peut etre utile
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

    //TODO voir pour la connexion au serveur cabinet
    public void checkKitViewConnection(){
		/*mDialog.showFRProgressDialog();
		KitviewUtil.isKitviewAvailable(MainActivity.this, new KitviewUtil.ITestConnectionResponse() {
			@Override
			public void onResponse(final int connectionEstablished){
				mDialog.cancelFRProgressDialog();
				if(connectionEstablished != KitviewUtil.TEST_CONNECTION_OK){// && mSettingsHasBeenLaunched){
					runOnUiThread(new Runnable() {
						public void run() {
							//String text = "";

							if(connectionEstablished == KitviewUtil.TEST_CONNECTION_WIFI_KO){
								//text = getResources().getString(R.string.wifi_ko);

								launchWifiPopup(MainActivity.this);
							}else if(connectionEstablished == KitviewUtil.TEST_CONNECTION_KITVIEW_KO){// && !mSettingsHasBeenLaunched){
								//text = getResources().getString(R.string.connection_to_kitview_ko);

								//SystemUtil.showPopup(MainActivity.this,text);//getResources().getString(R.string.connection_to_kitview_ko));

								//mAppSettingsPopupManager.showCameraSettingsDialogPopup();

								launchSettings(MainActivity.this,true);
							}
						}
					});
				}
			}
		});*/
    }

    //TODO peut etre pour l'image aussi ?? + régler MPEG4Extractor: Reset mWrongNALCounter. Re-check a condition - 'isMalformed = 0'
    private void initializeVideoView(){
        Context context = getApplicationContext();
        Uri path;
        String pathStr = context.getFilesDir().getAbsolutePath() + File.separator + "Config" + File.separator + "video.mp4";
        File videoPractice = new File(pathStr);
        if (videoPractice.exists()) path = Uri.parse(pathStr);
        else path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);

        if(mVideoView != null){
            if(path != null )mVideoView.setVideoURI(path);

            mVideoView.seekTo(0);
            mVideoView.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mVideoView.start();
                    mp.setLooping(true);
                }
            });
        }
    }

    //TODO conserver pour le moment (utile pour la photothèque)
    public void launchMyCase(final Activity context, final int patientId){
        if(mDialog != null)mDialog.showFRProgressDialog();
        KitviewUtil.isKitviewAvailable(context, new KitviewUtil.ITestConnectionResponse() {
            @Override
            public void onResponse(final int connectionEstablished) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        context.runOnUiThread(new Runnable() {
                            public void run() {
                                if(connectionEstablished == KitviewUtil.TEST_CONNECTION_OK){
                                    Intent intent = new Intent(context.getApplicationContext(), FolderActivity.class);
                                    if(intent != null){
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra(FolderActivity.EXTRA_KEY_PATIENTID, (patientId != -1)?patientId:KitviewUtil.GetCurrentIdPatientSync(context));
                                        //mSettingsHasBeenLaunched = true;
                                        context.getApplicationContext().startActivity(intent);
                                    }
                                    if(mDialog != null)mDialog.cancelFRProgressDialog();
                                }else{
                                    if(mDialog != null)mDialog.cancelFRProgressDialog();

                                    String text = "";

                                    if(connectionEstablished == KitviewUtil.TEST_CONNECTION_WIFI_KO){
                                        launchWifiPopup(context);
                                    }else if(connectionEstablished == KitviewUtil.TEST_CONNECTION_KITVIEW_KO){
                                        text = context.getResources().getString(R.string.connection_to_kitview_ko);

                                        launchSettings(context,true);
                                    }
                                }
                            }
                        });
                    }
                }).start();
            }
        });
    }

    //TODO garder et modifier
    public static void launchSettings(Activity context, boolean connectionKo){
        if(!SettingsActivity.IS_LAUNCHED){
            if(connectionKo){
                String text = context.getResources().getString(R.string.connection_to_kitview_ko);
                SystemUtil.showPopup(context,text);
            }
            SettingsActivity.IS_LAUNCHED = true;
            Intent intent = new Intent(context.getApplicationContext(), SettingsActivity.class);
            context.startActivity(intent);
        }
    }

    //TODO mettre PracticeAct dans cette fonction
    public void initializeGridView0(){
        this.mModules0 = new ArrayList<Module>();
        this.mModules0.add(new Module(R.string.practice_id, R.color.color1, R.drawable.ic_action_group));
        this.mModules0.add(new Module(R.string.practice_scan, R.color.color2, R.drawable.barcode));

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
                                    nbRows = 2;
                                }else if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
                                    nbItemsPerRow = 2;
                                    nbRows = 3;
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
                    //checkKitViewConnection();
                }
            });
        }
    }

    //TODO c'est ecrit en fr, changer ça
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
        builder.setMessage("Cabinet").setTitle("Practice");//TODO R.strings
        builder.setView(inflater.inflate(R.layout.dialog_practicecode,null));
        builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
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
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);//TODO enlever, pour mon portable uniquement
    }

    public void runFTPQuery(final String code_input){//TODO degager tous les println
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
            thread1.join();
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
                                        setViewAnimatorIndex(2);
                                        initializeGridView2();
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


    //TODO garder et modifier selon actions
    public void initializeGridView2(){
        this.mModules2 = new ArrayList<Module>();
        this.mModules2.add(new Module(R.string.picture_shot_emergency, R.color.color1, R.drawable.ic_action_camera));
        this.mModules2.add(new Module(R.string.picture_shot_several_patient, R.color.color2, R.drawable.ic_action_new_picture));
        this.mModules2.add(new Module(R.string.folder_patient, R.color.color3, R.drawable.ic_action_person));
        this.mModules2.add(new Module(R.string.folder2, R.color.color4, R.drawable.ic_action_group));//degager
        this.mModules2.add(new Module(R.string.settings, R.color.color5, R.drawable.ic_action_settings));
        this.mModules2.add(new Module(R.string.practice, R.color.color6, R.drawable.ic_action_settings));//degager

        this.mInitializationFinished2 = false;

        if(this.mGridView2 != null){
            this.mGridView2.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onGlobalLayout() {
                            if(!mInitializationFinished2){
                                mSpacing = getWindowManager().getDefaultDisplay().getWidth()/50; // TODO voir pour les deprecated

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
                                    nbRows = 2;
                                }else if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
                                    nbItemsPerRow = 2;
                                    nbRows = 3;
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
                    switch (arg2) {

                        //Emergency
                        case 0:
                            if(mDialog != null){
                                mDialog.showFRProgressDialog();
                                System.out.println("dialog lancée");
                            }

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(MainActivity.this.getApplicationContext(), ScenariosActivity.class);
                                    if(intent != null){
                                        intent.putExtra(ScenariosActivity.KEY_MODE, ScenariosActivity.MODE_EMERGENCY);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        MainActivity.this.getApplicationContext().startActivity(intent);
                                    }

                                    if(mDialog != null){
                                        runOnUiThread(new  Runnable(){
                                            @Override
                                            public void run() {
                                                mDialog.cancelFRProgressDialog();
                                                System.out.println("dialog finie UIthread");
                                            }
                                        });
                                    }
                                }
                            }).start();

                            break;


                        //Seance photos
                        case 1:
                            if(mDialog != null)mDialog.showFRProgressDialog();

                            KitviewUtil.isKitviewAvailable(MainActivity.this, new KitviewUtil.ITestConnectionResponse() {
                                @Override
                                public void onResponse(final int connectionEstablished) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    if(connectionEstablished == KitviewUtil.TEST_CONNECTION_OK){
                                                        Intent intent = new Intent(MainActivity.this.getApplicationContext(), ScenariosActivity.class);

                                                        if(intent != null){
                                                            intent.putExtra(ScenariosActivity.KEY_MODE, ScenariosActivity.MODE_SCENARIO);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            MainActivity.this.getApplicationContext().startActivity(intent);
                                                        }
                                                        if(mDialog != null)mDialog.cancelFRProgressDialog();
                                                    }else{
                                                        if(mDialog != null)mDialog.cancelFRProgressDialog();

                                                        String text = "";

                                                        if(connectionEstablished == KitviewUtil.TEST_CONNECTION_WIFI_KO){
                                                            text = getResources().getString(R.string.wifi_ko);

                                                            launchWifiPopup(MainActivity.this);

                                                        }else if(connectionEstablished == KitviewUtil.TEST_CONNECTION_KITVIEW_KO){
                                                            launchSettings(MainActivity.this,true);
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            });

                            break;

                        //Ma phototheque
                        case 2:
                            launchMyCase(MainActivity.this,-1);

                            break;

                        //Cas similaires
                        case 3:
                            if(mDialog != null)mDialog.showFRProgressDialog();

                            KitviewUtil.isKitviewAvailable(MainActivity.this, new KitviewUtil.ITestConnectionResponse() {
                                @Override
                                public void onResponse(final int connectionEstablished) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    if(connectionEstablished == KitviewUtil.TEST_CONNECTION_OK){
                                                        KitviewUtil.GetCurrentIdPatient(MainActivity.this,new KitviewUtil.IIntResponse() {
                                                            @Override
                                                            public void onResponse(final int patientId) {
                                                                Intent intent = new Intent(MainActivity.this.getApplicationContext(), SameCasesActivity.class);
                                                                if(intent != null){
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    intent.putExtra(SameCasesActivity.EXTRA_KEY_PATIENTID, patientId);
                                                                    MainActivity.this.getApplicationContext().startActivity(intent);
                                                                }
                                                                if(mDialog != null)mDialog.cancelFRProgressDialog();
                                                            }
                                                        });
                                                    }else{
                                                        if(mDialog != null)mDialog.cancelFRProgressDialog();

                                                        String text = "";

                                                        if(connectionEstablished == KitviewUtil.TEST_CONNECTION_WIFI_KO){
                                                            text = getResources().getString(R.string.wifi_ko);

                                                            launchWifiPopup(MainActivity.this);

                                                        }else if(connectionEstablished == KitviewUtil.TEST_CONNECTION_KITVIEW_KO){
                                                            launchSettings(MainActivity.this,true);
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            });
                            break;

                        //Settings
                        case 4:
                            launchSettings(MainActivity.this,false);

                            break;

                        //Test
                        case 5:
                            if(mDialog != null)mDialog.showFRProgressDialog();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(MainActivity.this.getApplicationContext(), PracticeActivity.class);
                                    if(intent != null){
                                        intent.putExtra("practice", "5");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        MainActivity.this.getApplicationContext().startActivity(intent);
                                    }

                                    if(mDialog != null){
                                        runOnUiThread(new  Runnable(){
                                            @Override
                                            public void run() {
                                                mDialog.cancelFRProgressDialog();
                                            }
                                        });
                                    }
                                }
                            }).start();

                            break;

                    }
                }
            });
        }
    }

    //TODO garder peut etre utile
    public static File getOutputTextFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");

        // Create the storage directory if it does not exist
        if (mediaStorageDir != null && !mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        if(mediaStorageDir != null){
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator +"BARCODE_"+ timeStamp + ".txt");
            return mediaFile;
        }else return null;
    }

    @Override
    protected void onResume(){
        super.onResume();

        startVideo();
        //TODO peut etre changer a partir d'ici
        KitviewUtil.GetCurrentIdPatient(MainActivity.this,new KitviewUtil.IIntResponse() {
            @Override
            public void onResponse(int patientId) {
                final Personne personne = (patientId != -1)?KitviewUtil.getPersonneFromId(MainActivity.this,patientId):null;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(personne != null){
                            String lastName = personne.getLastName().trim();
                            String firstName = personne.getFirstName().trim();

                            String patientInfosFormatted = (lastName != null && lastName.length() <= ScenariosActivity.PATIENT_INFOS_MAX_CHARACTERS)?lastName:lastName.substring(0, ScenariosActivity.PATIENT_INFOS_MAX_CHARACTERS)+" ...";
                            String patientInfosFormatted2 = (firstName != null && firstName.length() <= ScenariosActivity.PATIENT_INFOS_MAX_CHARACTERS)?firstName:firstName.substring(0, ScenariosActivity.PATIENT_INFOS_MAX_CHARACTERS)+" ...";

                            mCurrentPatientInfosTextView.setText(patientInfosFormatted2+" "+patientInfosFormatted);

                            String text = Html.fromHtml(getResources().getString(R.string.copyright_kitview_labs_2015))+" v"+SystemUtil.getAppVersion(MainActivity.this);

                            mCopyrightTextView.setText(text);//patientInfosFormatted+" "+patientInfosFormatted2+" "+text);
                        }
                    }
                });
            }
        });
    }

    //TODO garder
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
        pauseVideo();
    }

    //TODO garder
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

    //TODO orientation change -> passe ici (3) apres onPause
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null)outState.putBoolean(KEY_TEST_CONNECTION,false);
    }

    //TODO orientation change -> passe ici (1)
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