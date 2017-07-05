package util.FTP;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.kitview.out.mobile.R;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.MalformedServerReplyException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;

import exception.FolderNonExistent;


/**
 * Created by orthalis on 22/05/2017.
 */


public class PracticeFTP {//TODO clean tout ce bordel et ranger les exceptions

    private String code_input;
    private Activity activity;
    private Context context;

    private final static String SERVER = "diedendorf.dyndns.org";//"90.48.177.143"
    private final static int PORT = 21;
    private final static String USER = "smartphone";
    private final static String PASS = "smartphone";

    public PracticeFTP(String code_input, Context context, Activity activity) {
        this.code_input = code_input;
        this.context = context;
        this.activity = activity;
    }

    //Envoyer un toast message sur l'IHM depuis un thread en background
    private void toastToUserFromBackgroundThread(final String msg, final int duration){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(context, msg, duration).show();
            }
        });
    }

    //Vérification de l'existence du dossier demandé par l'user sur le serveur
    public boolean distantFolderExists(){

        FTPClient ftpClient = new FTPClient();
        boolean folderExists = false;

        try{
            ftpClient.connect(SERVER, PORT);
            ftpClient.login(USER, PASS);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            String remoteFolder = code_input;

            String[] folders = ftpClient.listNames();
            for (String folder : folders){
                if (folder.equals(remoteFolder)) folderExists = true;
            }
            if (!folderExists) throw new FolderNonExistent(context.getString(R.string.folder_non_existent));
        } catch (FolderNonExistent ex) {
            ex.printStackTrace();
            System.out.println("FolderNonExistent\nError: " + ex.getMessage());
            toastToUserFromBackgroundThread(context.getString(R.string.folder_non_existent), Toast.LENGTH_LONG);//TODO changer le R.string
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            System.out.println("UnknownHostException\nError: " + ex.getMessage());
            toastToUserFromBackgroundThread("Veuillez vérifiez votre connexion internet",Toast.LENGTH_LONG);//TODO changer le R.string
        } catch (MalformedServerReplyException ex) {
            ex.printStackTrace();
            System.out.println("MalformedServerReplyException\nError: " + ex.getMessage());
            toastToUserFromBackgroundThread("Une erreur est survenue, veuillez réessayer dans un instant",Toast.LENGTH_LONG);//TODO changer le R.string
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("IOException\nError: " + ex.getMessage());
            toastToUserFromBackgroundThread("Une erreur est survenue, veuillez réessayer dans un instant",Toast.LENGTH_LONG);//TODO changer le R.string
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Exception\nError: " + ex.getMessage());
            }
        }
        return folderExists;
    }

    //Téléchargement de fichier cfg et autres, relatifs au cabinet
    public boolean downloadServerFolder() {

        FTPClient ftpClient = new FTPClient();
        boolean dlSuccess = true;

        try {//TODO degager println
            System.out.println("/////////////////////////////////////////////////////////////\n" +
                    "/////////////////////////////////////////////////////////////\n" +
                    "/////////////////////////////FTP/////////////////////////////\n" +
                    "/////////////////////////////////////////////////////////////\n" +
                    "/////////////////////////////////////////////////////////////\n");
//           if (android.os.Build.VERSION.SDK_INT > 9) {
//                 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                 StrictMode.setThreadPolicy(policy);
//           }
            ftpClient.connect(SERVER, PORT);
            ftpClient.login(USER, PASS);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            String remoteFolder = code_input;

            toastToUserFromBackgroundThread(context.getString(R.string.downloading),Toast.LENGTH_LONG);
            File CfgDir = new File(context.getCacheDir().getAbsolutePath() + File.separator + "Config");
            //Création du dossier Config s'il n'existe pas déja
            if (!CfgDir.exists()) CfgDir.mkdir();

            String[] files = ftpClient.listNames(remoteFolder + File.separator);//Fichiers du serveur
            File downloadFile;
            OutputStream outputStream;
            String file;
            int i;

            //Téléchargement de tous les fichiers du dossier de cabinet dans Config/
            for (String filePath : files){
                file = "";
                i = filePath.lastIndexOf('/');
                if (i > 0) file = filePath.substring(i+1).toLowerCase();//nom du fichier apres le dernier '/' et en minuscule
                downloadFile = new File(CfgDir + File.separator + file);
                outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
                dlSuccess = dlSuccess && ftpClient.retrieveFile(filePath, outputStream);
                outputStream.close();
            }

            if (dlSuccess) {//Si le téléchargement de tous les fichiers est un succès
                System.out.println(context.getString(R.string.success_download));//TODO enlever println
                toastToUserFromBackgroundThread(context.getString(R.string.success_download),Toast.LENGTH_SHORT);
                moveCacheFolder();
///////////////////////////////////////INUTILE VERIF FICHIER.CONF/////////////////////////////////////////////////////TODO degager println
//                try {
//                    FileInputStream fis = context.openFileInput("kitpatient.conf");
//                    InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
//                    BufferedReader bufferedReader = new BufferedReader(isr);
//                    StringBuilder sb = new StringBuilder();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        sb.append(line).append("\n");
//                    }
//                    System.out.println(sb.toString());
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            } else {
                toastToUserFromBackgroundThread(context.getString(R.string.download_failed),Toast.LENGTH_SHORT);
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            System.out.println("UnknownHostException\nError: " + ex.getMessage());
            toastToUserFromBackgroundThread("Veuillez vérifiez votre connexion internet",Toast.LENGTH_LONG);//TODO changer le R.string
            dlSuccess = false;
        } catch (MalformedServerReplyException ex) {
            ex.printStackTrace();
            System.out.println("MalformedServerReplyException\nError: " + ex.getMessage());
            toastToUserFromBackgroundThread("Une erreur est survenue, veuillez réessayer dans un instant",Toast.LENGTH_LONG);//TODO changer le R.string
            dlSuccess = false;
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("IOException\nError: " + ex.getMessage());
            toastToUserFromBackgroundThread("Une erreur est survenue, veuillez réessayer dans un instant",Toast.LENGTH_LONG);//TODO changer le R.string
            dlSuccess = false;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("ExceptionError: " + ex.getMessage());
            }
        }
        return dlSuccess;
    }

    //Copie le dossier cache/Config/ vers files/Config/
    private void moveCacheFolder() {
        String cacheFolder = context.getCacheDir().getAbsolutePath() + File.separator + "Config";
        File CacheDir = new File(cacheFolder);
        String internalFolder = context.getFilesDir().getAbsolutePath() + File.separator + "Config";
        File CfgDir = new File(internalFolder);
        if (!CfgDir.exists()) CfgDir.mkdir();
        try {
            FileUtils.copyDirectory(CacheDir,CfgDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Suppression de tous les fichiers de cache/Config/
        File[] CacheFiles = CacheDir.listFiles();
        for (File cFile : CacheFiles) cFile.delete();
        CacheDir.delete();//Suppression de cache/Config/
    }
}