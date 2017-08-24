package util.network;

import android.content.Context;

import com.embarcadero.javaandroid.DBXException;
import com.embarcadero.javaandroid.DSProxy;
import com.embarcadero.javaandroid.DSRESTConnection;
import com.embarcadero.javaandroid.TJSONObject;

/**
 * Created by Administrateur on 24/08/2017.
 */

public class KitviewUtilApp extends KitviewUtil {

    private static DSRESTConnection _mConnection;

    private static DSRESTConnection getInstanceConnection(Context context){
//        _mConnection = getConnection("diedendorf.dyndns.org",8080);
//        _mConnection = getConnection("192.168.2.74",8080);
        _mConnection = getConnection("192.168.2.77",8080);//TODO mettre variables xml
        return _mConnection;
    }


    private static DSRESTConnection getConnection(String ip, int port){
        DSRESTConnection connection = new DSRESTConnection();
        connection.setHost(ip);
        connection.setPort(port);
        connection.setProtocol("http");
        return connection;
    }


    public static void DeleteSessionFile(final Context context, final String FileName) {
        final DSRESTConnection conn = getInstanceConnection(context);
        final DSProxy.TKitviewClass tserverclass = new DSProxy.TKitviewClass(conn);

        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    tserverclass.DeleteSessionFile(FileName);
                } catch (DBXException e){
                    if(e != null)e.printStackTrace();
                    _responseUpload = -1;
                }
                conn.CloseSession();
            }
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void AddSessionFilenameToIdPatient(final Context context,final int IdPatient, final String SessionFilename, final String lstAttributs, final int WithRefresh, final int WithPreview){
        final DSRESTConnection conn = getInstanceConnection(context);
        final DSProxy.TKitviewClass tserverclass = new DSProxy.TKitviewClass(conn);

        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    int response = tserverclass.AddSessionFilenameToIdPatient(IdPatient, SessionFilename, lstAttributs, WithRefresh,WithPreview);
                    //System.out.println("KitviewUtilApp:AddSessionFilename "+response);
                } catch (DBXException e){
                    if(e != null)e.printStackTrace();
                }
                conn.CloseSession();
            }
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static int UploadFileInMultipleParts(final Context context, final String SessionFilename, final TJSONObject UploadedData, final int IsEndOfFile){
        _responseUpload = -1;

        final DSRESTConnection conn = getInstanceConnection(context);
        final DSProxy.TKitviewClass tserverclass = new DSProxy.TKitviewClass(conn);

        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    _responseUpload = tserverclass.UploadFileInMultipleParts(SessionFilename, UploadedData, IsEndOfFile);
                } catch (DBXException e){
                    if(e != null)e.printStackTrace();
                    _responseUpload = -1;
                }
                conn.CloseSession();
            }
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return _responseUpload;
    }
}
