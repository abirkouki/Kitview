package com.example.android.out;

/**
 * Created by orthalis on 22/05/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kitview.out.mobile.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import exception.FolderNonExistent;
import mbanje.kurt.fabbutton.FabButton;
import util.helper.ProgressHelper;

public class PracticeActivity extends FragmentActivity{

    private EditText code_input;
    private Button envoyer;
    private Button scan;
    private FabButton mProgress;
    private Handler mHandler = new Handler();

    public PracticeActivity getActivity(){
        return this;
    }

    //Envoyer un toast message sur l'IHM depuis un thread en background
    public void toastToUserFromBackgroundThread(final String msg, final PracticeActivity activity){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);//pas de barre de menu
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_practice);

        envoyer = (Button) findViewById(R.id.envoyer);
        code_input = (EditText) findViewById(R.id.code_input);
        scan = (Button) findViewById(R.id.scan_button);
        mProgress = (FabButton) findViewById(R.id.determinate);
        final ProgressHelper helper = new ProgressHelper(mProgress,this);
        //scan.setOnClickListener(this);//this instance de View.OnClickListener, je pense

        //Lancement du scanner
        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new IntentIntegrator(getActivity()).initiateScan();
            }
        });

        //Lancement de la recherche / téléchargement de fichier cfg et video relatifs au cabinet
        envoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {//sur un autre thread pour faire la requete en background
                    @Override
                    public void run() {
                        //Connexion au serveur FTP
                        String server = "diedendorf.dyndns.org";//"90.48.177.143"
                        int port = 21;
                        String user = "smartphone";
                        String pass = "smartphone";

                        FTPClient ftpClient = new FTPClient();
                        try {//TODO degager println
                            System.out.println("/////////////////////////////////////////////////////////////\n" +
                                    "/////////////////////////////////////////////////////////////\n" +
                                    "/////////////////////////////FTP/////////////////////////////\n" +
                                    "/////////////////////////////////////////////////////////////\n" +
                                    "/////////////////////////////////////////////////////////////\n");
//                           if (android.os.Build.VERSION.SDK_INT > 9) {
//                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                                StrictMode.setThreadPolicy(policy);
//                           }
                            ftpClient.connect(server, port);
                            ftpClient.login(user, pass);
                            ftpClient.enterLocalPassiveMode();
                            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                            Context context = getApplicationContext();

                            String remoteFolder = code_input.getText().toString();
                            boolean folderExists = false;

                            try {//Vérification de l'existence du dossier demandé par l'user sur le serveur
                                String[] folders = ftpClient.listNames();
                                for (String folder : folders){
                                    if (folder.equals(remoteFolder)) folderExists = true;
                                }
                                if (!folderExists) throw new FolderNonExistent(getString(R.string.folder_non_existent));
                            } catch (FolderNonExistent e){
                                e.printStackTrace();
                                toastToUserFromBackgroundThread(getString(R.string.folder_non_existent),getActivity());
                            }

                            if (folderExists){//que si le dossier existe sur le serveur
                                //désactive le bouton envoyer pour ne pas que l'user envoie plusieurs requetes
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        scan.setEnabled(false);
                                        envoyer.setEnabled(false);
                                    }
                                });

                                String[] files = ftpClient.listNames(code_input.getText().toString()+File.separator);//Fichiers du serveur
                                File downloadFile;
                                OutputStream outputStream;
                                boolean dlSuccess = true;
                                String file;
                                int i;
                                mHandler.post(new Runnable() {
                                    public void run() {
                                        helper.startIndeterminate();
                                    }
                                });
                                toastToUserFromBackgroundThread(getString(R.string.downloading),getActivity());

                                try {//Création du dossier Config s'il n'existe pas déja
                                    File CfgDir = new File(context.getFilesDir().getAbsolutePath() + File.separator + "Config");
                                    if (!CfgDir.exists()) CfgDir.mkdir();
                                }
                                catch(Exception e){
                                    Log.w("creating file error", e.toString());
                                }
                                //Téléchargement de tous les fichiers du dossier de cabinet dans Config/
                                for (String filePath : files){
                                    file = "";
                                    i = filePath.lastIndexOf('/');
                                    if (i > 0) file = filePath.substring(i+1).toLowerCase();//nom du fichier apres le dernier '/' et en minuscule
                                    downloadFile = new File(context.getFilesDir().getAbsolutePath() + File.separator + "Config" + File.separator + file);
                                    outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
                                    dlSuccess = dlSuccess && ftpClient.retrieveFile(filePath, outputStream);
                                    outputStream.close();
                                }

                                if (dlSuccess) {//Si le téléchargement de tous les fichiers est un succès
                                    mHandler.post(new Runnable() {
                                        public void run() {
                                            mProgress.setIndeterminate(false);
                                            helper.startDeterminate();

                                        }
                                    });
                                    System.out.println(getString(R.string.success_download));//TODO enlever println
                                    toastToUserFromBackgroundThread(getString(R.string.success_download),getActivity());
///////////////////////////////////////INUTILE VERIF FICHIER.CONF/////////////////////////////////////////////////////TODO degager println
                                    try {
                                        FileInputStream fis = context.openFileInput("kitpatient.conf");
                                        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                                        BufferedReader bufferedReader = new BufferedReader(isr);
                                        StringBuilder sb = new StringBuilder();
                                        String line;
                                        while ((line = bufferedReader.readLine()) != null) {
                                            sb.append(line).append("\n");
                                        }
                                        System.out.println(sb.toString());
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getActivity(),getString(R.string.download_failed),Toast.LENGTH_SHORT).show();
                                            envoyer.setEnabled(true);
                                            scan.setEnabled(true);
                                            //TODO reinitialiser fab button
                                        }
                                    });
                                }
                            };
                        } catch (IOException ex) {
                            System.out.println("Error: " + ex.getMessage());
                            ex.printStackTrace();
                        } finally {
                            try {
                                //if (ftpClient.isConnected()) {
                                if (ftpClient.completePendingCommand()) {
                                    ftpClient.logout();
                                    ftpClient.disconnect();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        this.finish();//TODO affiche la page d'accueil de practice -> pas normal
        super.onBackPressed();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // nous utilisons la classe IntentIntegrator et sa fonction parseActivityResult pour parser le résultat du scan
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            // nous récupérons le contenu du code barre
            code_input.setText(scanningResult.getContents());
            envoyer.performClick();
            //String scanContent = scanningResult.getContents();
            // nous récupérons le format du code barre
            //String scanFormat = scanningResult.getFormatName();
            //TextView scan_format = (TextView) findViewById(R.id.scan_format);
            //TextView scan_content = (TextView) findViewById(R.id.scan_content);
            // nous affichons le résultat dans nos TextView
            //scan_format.setText("FORMAT: " + scanFormat);
            //scan_content.setText("CONTENT: " + scanContent);
        }
        else Toast.makeText(getActivity(),getString(R.string.no_data_received),Toast.LENGTH_LONG).show();
    }
}