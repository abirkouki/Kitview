package util.network;

import android.content.Context;

import com.embarcadero.javaandroid.DBXException;
import com.embarcadero.javaandroid.DSProxy;
import com.embarcadero.javaandroid.DSRESTConnection;
import com.embarcadero.javaandroid.TJSONObject;

import model.PersistenceManager;

/**
 * Created by Administrateur on 24/08/2017.
 */

public class KitviewUtilApp extends KitviewUtil {

    private static DSRESTConnection _mConnection;
    private static PersistenceManager _mPersistenceManager;


    private static DSRESTConnection getInstanceConnection(Context context){
//        _mPersistenceManager = PersistenceManager.getInstance();
//
//        if(_mConnection != null){
//            _mConnection.CloseSession();
//        }
//
//        _mConnection = new DSRESTConnection();
//
//        boolean modePatient = (_mPersistenceManager.getMode() == PersistenceManager.MODE_PATIENT);
//
//        if(modePatient){
//            String rawRemoteServer = _mPersistenceManager.getInstance().getRemoteServerAdress(true);//false);//true);
//
//            StringTokenizer st = new StringTokenizer(rawRemoteServer, ":");
//
//            int nbTokens = (st != null)?st.countTokens():0;
//
//            if(nbTokens == 2){
//                _mConnection.setHost(st.nextToken());
//
//                try{
//                    String portRaw = st.nextToken();
//                    int portInt = Integer.parseInt(portRaw);
//
//                    _mConnection.setPort(portInt);
//                }catch(NumberFormatException e){
//                    e.printStackTrace();
//                }
//            }
//        }else{
//            ArrayList<Subscriber> subscribers = _mPersistenceManager.getInstance().getSubscribers();
//            int subscriberIndex = _mPersistenceManager.getInstance().getSubscriberIndex();
//            Subscriber currentSubscriber = ((subscriberIndex != -1)?subscribers.get(subscriberIndex):null);
//            String ip = "";
//            int port = -1;
//
//            if(currentSubscriber != null){
//                ip = currentSubscriber.getmHost();
//                port = currentSubscriber.getmHttpPort();
//
//                _mConnection.setHost(ip);//"AG-VAIO");//"orqualpau.dyndns.org");//ip);
//                _mConnection.setPort(port);//8080);//port);
//            }else{
//                _mConnection.setHost("");
//                _mConnection.setPort(-1);
//            }
//        }
//        _mConnection.setProtocol("http");


//        _mConnection = getConnection("diedendorf.dyndns.org",8080);
//        _mConnection = getConnection("192.168.2.74",8080);
        _mConnection = getConnection("192.168.2.77",8080);
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
