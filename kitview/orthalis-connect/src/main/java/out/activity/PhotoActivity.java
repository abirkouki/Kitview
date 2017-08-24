package out.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.embarcadero.javaandroid.TJSONObject;
import com.orthalis.connect.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import util.components.progressdialog.FRProgressDialog;
import util.helper.ActionBarHelper;
import util.network.KitviewUtilApp;

/**
 * Created by Administrateur on 18/08/2017.
 */

public class PhotoActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;

    private int mPatientId = -1;
    private Thread mSendPicturesThread;
    private boolean mActivityKilled = false;
    private ArrayList<String> mPictureFiles = new ArrayList<String>();
    private File mPicture = null;
    private FRProgressDialog mDialog;
    private Button PictureButton;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {//TODO demander pour quel patient suivant le statut (payeur, respo, etc) et ESSAYER EN NON LOCAL
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo);
        ActionBarHelper.actionBarCustom(this,true);

        //TODO methode pour récuperer id patient dans kitview
        mPatientId = 10544;//10544 -> Anael MEJEAN | 9299 -> Gaetan MEJEAN

        mDialog = new FRProgressDialog(this, "",false);

        PictureButton = (Button) findViewById(R.id.PictureButton);

        initializeSendPicturesThread();

        PictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPatientId != -1) dispatchTakePictureIntent();
            }
        });

    }

    // Fait une copie de la photo et la decoupe en paquets qu'elle envoie sur le kitdsserver
    private void sendPictureToKitview(String path){
        File pictureFile = new File(path);
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mDialog != null)mDialog.showFRProgressDialog();
                }
            });
            String pictureName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg";
            int packetSize = 1000000;//1048576;//1Mo
            int binTot;
            long mLen;
            byte[] mBuffer = new byte[packetSize];

            InputStream inFile = new FileInputStream(pictureFile);
            binTot = 0;
            long totalBytes = pictureFile.length();
            //KitviewUtil.DeleteSessionFile(PhotoActivity.this,pictureName);
            KitviewUtilApp.DeleteSessionFile(PhotoActivity.this,pictureName);

            while(binTot < totalBytes){
                mLen = (binTot+packetSize < totalBytes)?packetSize:(totalBytes-binTot);
                mBuffer = new byte[(int)mLen];
                inFile.read(mBuffer,0, (int) mLen);
                String encodedImage2 = Base64.encodeToString(mBuffer, Base64.DEFAULT);
                TJSONObject jo2 = new TJSONObject();
                jo2.addPairs("UploadedData", encodedImage2);
                //KitviewUtil.UploadFileInMultipleParts(PhotoActivity.this,pictureName, jo2,binTot);
                KitviewUtilApp.UploadFileInMultipleParts(PhotoActivity.this,pictureName, jo2,binTot);
                binTot += mLen;
            }
            inFile.close();
            //KitviewUtil.AddSessionFilenameToIdPatient(PhotoActivity.this,mPatientId, pictureName, "", 1,1);
            KitviewUtilApp.AddSessionFilenameToIdPatient(PhotoActivity.this,mPatientId, pictureName, "", 1,1);
            //OutOfMemory
        }catch(OutOfMemoryError e){
            e.printStackTrace();
            //launchOOM(ScenariosActivity.this);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            mPicture.delete();
            mPicture = null;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mDialog != null)mDialog.cancelFRProgressDialog();
                    Toast.makeText(getApplicationContext(),R.string.photo_sent,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // Démarre l'appareil photo
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    // Donne le chemin absolu de la photo depuis son URI
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //System.out.println(requestCode+" "+resultCode);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            mPicture = new File(getRealPathFromURI(data.getData()));
            //System.out.println("OnActivityResult():resultOK"+mPicture.getAbsolutePath());
            mPictureFiles.add(mPicture.getAbsolutePath());
        }
        else if (resultCode == RESULT_CANCELED){
        }
    }

    // Dès qu'un chemin est ajouté à mPictureFiles
    private void initializeSendPicturesThread(){
        mSendPicturesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!mActivityKilled){
                    try{

                        synchronized (mPictureFiles) {
                            for(String s:mPictureFiles){
                                sendPictureToKitview(s);
                                mPictureFiles.remove(s);
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        mSendPicturesThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivityKilled = true;
    }
}
