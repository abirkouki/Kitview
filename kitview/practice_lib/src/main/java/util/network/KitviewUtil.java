package util.network;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import util.filter.FilterProcessor;
import util.log.LogUtil;
import model.PersistenceManager;
import model.rest.Personne;
import model.rest.Photo;
import model.rest.Scenario;
import model.rest.ScenarioItem;
import model.rest.Subscriber;
import activity.FolderActivity;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.embarcadero.javaandroid.*;
import com.embarcadero.javaandroid.DSProxy.TKitviewClass;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class KitviewUtil {
	private static DSRESTConnection _mConnection;
	private static PersistenceManager _mPersistenceManager;

	private final static boolean LOG_ENABLED = true;

	public static interface IIntResponse{
		public void onResponse(int response);
	}

	public static interface IStringResponse{
		public void onResponse(String response);
	}

	public static interface IBooleanResponse{
		public void onResponse(boolean response);
	}

	public static interface ITDBXReaderResponse{
		public void onResponse(TDBXReader response);
	}

	public static interface IScenarioArrayListResponse{
		public void onResponse(List<Scenario> response);
	}

	public static interface ICollectionArrayListResponse{
		public void onResponse(List<Collection> response);
	}	

	public static interface IPersonneResponse{
		public void onResponse(Personne personne);
	}

	public final static int TEST_CONNECTION_OK = 0;
	public final static int TEST_CONNECTION_WIFI_KO = 1;
	public final static int TEST_CONNECTION_KITVIEW_KO = 2;

	public static interface ITestConnectionResponse{
		public void onResponse(int connectionStatus);
	}

	public static interface IPhotoInfosArrayListResponse{
		public void onResponse(List<Photo> photos);
	}

	public static interface IStringArrayListResponse{
		public void onResponse(List<String> elems);
	}

	public static interface IBitmapResponse{
		public void onResponse(Bitmap bitmap);
	}

	private static DSRESTConnection getInstanceConnection(Context context){
		_mPersistenceManager = PersistenceManager.getInstance();

		if(_mConnection != null){
			_mConnection.CloseSession();
		}

		_mConnection = new DSRESTConnection();

		boolean modePatient = (_mPersistenceManager.getMode() == PersistenceManager.MODE_PATIENT);

		if(modePatient){
			String rawRemoteServer = _mPersistenceManager.getInstance().getRemoteServerAdress(true);//false);//true);

			StringTokenizer st = new StringTokenizer(rawRemoteServer, ":");

			int nbTokens = (st != null)?st.countTokens():0;

			if(nbTokens == 2){
				_mConnection.setHost(st.nextToken());

				try{
					String portRaw = st.nextToken();
					int portInt = Integer.parseInt(portRaw);

					_mConnection.setPort(portInt);
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}
		}else{
			ArrayList<Subscriber> subscribers = _mPersistenceManager.getInstance().getSubscribers();
			int subscriberIndex = _mPersistenceManager.getInstance().getSubscriberIndex();
			Subscriber currentSubscriber = ((subscriberIndex != -1)?subscribers.get(subscriberIndex):null);
			String ip = "";
			int port = -1;

			if(currentSubscriber != null){
				ip = currentSubscriber.getmHost();
				port = currentSubscriber.getmHttpPort();

				_mConnection.setHost(ip);//"AG-VAIO");//"orqualpau.dyndns.org");//ip);
				_mConnection.setPort(port);//8080);//port);
			}else{
				_mConnection.setHost("");
				_mConnection.setPort(-1);
			}

			if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] getInstanceConnection ip:"+ip+" port:"+port+"");
		}
		_mConnection.setProtocol("http");

		return _mConnection;
	}

	private static DSRESTConnection getConnection(String ip, int port){
		DSRESTConnection connection = new DSRESTConnection();
		connection.setHost(ip);
		connection.setPort(port);
		connection.setProtocol("http");
		return connection;
	}

	public static void closeConnection(){
		if(_mConnection != null)_mConnection.CloseSession();
	}

	static String _gabOutilAttributs = null;
	public static String GetGabOutilAttributs(final Context context, final int IdGabOutil, final String sep){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetGabOutilAttributs(IdGabOutil:"+IdGabOutil+",sep:"+sep+")");
		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);
		_gabOutilAttributs = null;

		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					_gabOutilAttributs = tserverclass.GetGabOutilAttributs(IdGabOutil, sep);
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetGabOutilAttributs response:"+_gabOutilAttributs+")");
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetGabOutilAttributs response:"+((e != null)?e.getMessage():""));
					_gabOutilAttributs = null;
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

		return _gabOutilAttributs;
	}

	public static void DeleteSessionFile(final Context context,final String FileName) {
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] DeleteSessionFile(FileName:"+FileName);

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					tserverclass.DeleteSessionFile(FileName);
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] DeleteSessionFile");
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] DeleteSessionFile:"+((e != null)?e.getMessage():""));
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

		static int _responseUpload = -1;
	public static int UploadFileInMultipleParts(final Context context,final String SessionFilename, final TJSONObject UploadedData, final int IsEndOfFile){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] UploadFileInMultipleParts(SessionFilename:"+SessionFilename+",UploadedData length:"+UploadedData.toString().length()+",IsEndOfFile:"+IsEndOfFile+")");
		_responseUpload = -1;

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					_responseUpload = tserverclass.UploadFileInMultipleParts(SessionFilename, UploadedData, IsEndOfFile);
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] UploadFileInMultipleParts response:"+_responseUpload);
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] UploadFileInMultipleParts response:"+((e != null)?e.getMessage():""));
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

	public static void getPersonneFromIdAsync(final Context context, final int patientId, final IPersonneResponse mIPersonneResponse){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] getPersonneFromIdAsync(patientId:"+patientId+")");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		Thread t = new Thread(new Runnable(){
			@Override
			public void run(){
				try{
					TDBXReader response = tserverclass.GetPersonneFromId(patientId);

					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] getPersonneFromIdAsync response:"+response);

					if(response != null){
						while(response.next()){
							String nom = "";

							try{
								nom = response.getValue("PER_NOM").GetAsString();
							}catch(DBXException e){
								e.printStackTrace();
							}

							String prenom = "";

							try{
								prenom = response.getValue("PER_PRENOM").GetAsString();
							}catch(DBXException e){
								e.printStackTrace();
							}

							Date dateNaiss = null;

							try{
								dateNaiss = response.getValue("PER_DATNAISS").GetAsTimeStamp();
							}catch(DBXException e){
								e.printStackTrace();
							}

							int personneId = -1;

							try{
								personneId = (int) response.getValue("ID_PERSONNE").GetAsInt32();//Double();
							}catch(DBXException e){
								e.printStackTrace();
							}

							String ref1 = "";

							try{
								ref1 = response.getValue("REF1").GetAsString();
							}catch(DBXException e){
								e.printStackTrace();
							}

							String ref2 = "";

							try{
								ref2 = response.getValue("REF2").GetAsString();
							}catch(DBXException e){
								e.printStackTrace();
							}

							mPersonneFromId = new Personne(personneId, dateNaiss, prenom, nom);
							mPersonneFromId.setRef1(ref1);
							mPersonneFromId.setRef2(ref2);

							if(mIPersonneResponse != null)mIPersonneResponse.onResponse(mPersonneFromId);//personne);
						}
					}
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] getPersonneFromIdAsync response:"+((e != null)?e.getMessage():""));
					if(mIPersonneResponse != null)mIPersonneResponse.onResponse(null);
				}
				conn.CloseSession();
			}
		});

		t.start();
	}

	static Personne mPersonneFromId = null;
	public static Personne getPersonneFromId(final Context context,final int patientId){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] getPersonneFromId(patientId:"+patientId+")");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		mPersonneFromId = null;

		Thread t = new Thread(new Runnable(){
			@Override
			public void run(){
				try{
					TDBXReader response = tserverclass.GetPersonneFromId(patientId);

					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] getPersonneFromId response:"+response);

					if(response != null){
						while(response.next()){
							String nom = "";

							try{
								nom = response.getValue("PER_NOM").GetAsString();
							}catch(DBXException e){
								e.printStackTrace();
							}

							String prenom = "";

							try{
								prenom = response.getValue("PER_PRENOM").GetAsString();
							}catch(DBXException e){
								e.printStackTrace();
							}

							Date dateNaiss = null;

							try{
								dateNaiss = response.getValue("PER_DATNAISS").GetAsTimeStamp();
							}catch(DBXException e){
								e.printStackTrace();
							}

							int personneId = -1;

							try{
								personneId = (int) response.getValue("ID_PERSONNE").GetAsInt32();
							}catch(DBXException e){
								e.printStackTrace();
							}

							String ref1 = "";

							try{
								ref1 = response.getValue("REF1").GetAsString();
							}catch(DBXException e){
								e.printStackTrace();
							}

							String ref2 = "";

							try{
								ref2 = response.getValue("REF2").GetAsString();
							}catch(DBXException e){
								e.printStackTrace();
							}

							mPersonneFromId = new Personne(personneId, dateNaiss, prenom, nom);
							mPersonneFromId.setRef1(ref1);
							mPersonneFromId.setRef2(ref2);
						}
					}
				}catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetPersonneFromId response:"+((e != null)?e.getMessage():""));
					mPersonneFromId = null;
				}
				conn.CloseSession();
			}
		});

		t.start();	

		try{
			t.join();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}

		return mPersonneFromId;	
	}

	static ArrayList<Personne> _responsePersonnesFromFormField = null;
	public static ArrayList<Personne> GetPersonnesFromFormField(final Context context,final String FieldName, final String FieldValue){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetPersonnesFromFormField(FieldName:"+FieldName+",FieldValue:"+FieldValue+")");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		_responsePersonnesFromFormField = null;

		Thread t = new Thread(new Runnable(){
			@Override
			public void run(){
				try{
					TDBXReader response = tserverclass.GetPersonnesFromFormField(FieldName,FieldValue);

					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetPersonnesFromFormField response:"+response);

					_responsePersonnesFromFormField = new ArrayList<Personne>();
					mPersonnes = new ArrayList<Personne>();

					if(response != null){
						while(response.next()){
							final String nom = response.getValue("PER_NOM").GetAsString();
							final String prenom = response.getValue("PER_PRENOM").GetAsString();
							final Date dateNaiss = response.getValue("PER_DATNAISS").GetAsTimeStamp();
							long personneId = response.getValue("ID_PERSONNE").GetAsInt32();

							Personne p = new Personne((int) personneId, dateNaiss, prenom, nom);
							mPersonnes.add(p);
						}
					}
				}catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetPersonnesFromFormField response:"+((e != null)?e.getMessage():""));
					_responsePersonnesFromFormField = null;
				}
				conn.CloseSession();
			}
		});

		t.start();	

		try{
			t.join();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}

		return mPersonnes;
	}

	static int _patientIdentityId = -1;
	public static int GetPatientIdentityId(final Context context,final int patientId){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetPatientIdentityId(patientId:"+patientId+")");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		_patientIdentityId = -1;

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					_patientIdentityId = tserverclass.GetPatientIdentityId(patientId);

					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetPatientIdentityId response:"+_patientIdentityId);
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetPatientIdentityId response:"+((e != null)?e.getMessage():""));
					_patientIdentityId = -1;
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
		return _patientIdentityId;
	}

	public static void AddSessionFilenameToIdPatient(final Context context,final int IdPatient, final String SessionFilename, final String lstAttributs, final int WithRefresh, final int WithPreview){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] AddSessionFilenameToIdPatient(IdPatient:"+IdPatient+",SessionFilename:"+SessionFilename+",lstAttributs:"+lstAttributs+",WithRefresh:"+WithRefresh+")");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					int response = tserverclass.AddSessionFilenameToIdPatient(IdPatient, SessionFilename, lstAttributs, WithRefresh,WithPreview);

					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] AddSessionFilenameToIdPatient response:"+response);
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] AddSessionFilenameToIdPatient response:"+((e != null)?e.getMessage():""));
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

	static String _formats = null;
	public static String GetFormats(final Context context){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetFormats()");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		_formats = null;

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String response = tserverclass.GetFormats();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetFormats response:"+response);
					_formats = response;
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetFormats response:"+((e != null)?e.getMessage():""));
					_formats = null;
				}
				conn.CloseSession();
			}
		});
		t.start();

		try{
			t.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		return _formats;
	}

	public static void GetProfilAttributs(final Context context,final Integer IdProfil, final String sep, final IStringResponse iResponse){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetProfilAttributs(IdProfil:"+IdProfil+",sep:"+sep+")");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String response = tserverclass.GetProfilAttributs(IdProfil,sep);
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetProfilAttributs response:"+response);
					if(iResponse != null)iResponse.onResponse(response);
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetProfilAttributs response:"+((e != null)?e.getMessage():""));
					if(iResponse != null)iResponse.onResponse(null);
				}
				conn.CloseSession();
			}
		}).start();	
	}

	static ArrayList<Subscriber> mSubscribers = null;
	public static ArrayList<Subscriber> GetSubscribers(final Context context){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetSubscribers()");

		String ip = _mPersistenceManager.getInstance().getMachineIp(true);
		int port = _mPersistenceManager.getInstance().getMachinePort();

		final DSRESTConnection conn = getConnection(ip, port);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		Thread t = new Thread(new Runnable(){
			@Override
			public void run(){
				try{
					TDBXReader response = tserverclass.GetSubscribers();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetSubscribers response:"+response);

					mSubscribers = new ArrayList<Subscriber>();
					int index = 1;

					if(response != null){
						while(response.next()){
							String name = response.getValue("NAME").GetAsString();
							String host = response.getValue("HOST").GetAsString();
							int port = (int)response.getValue("PORT").GetAsInt32();
							int httpPort = (int)response.getValue("HTTPPORT").GetAsInt32();

							mSubscribers.add(new Subscriber(index++,name, host, port,httpPort));
						}
					}
				}catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetSubscribers response:"+((e != null)?e.getMessage():""));
					mSubscribers = null;
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

		return mSubscribers;
	}

	static ArrayList<Personne> mPersonnes = null;
	public static ArrayList<Personne> GetDSPersonnes(final Context context){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetDSPersonnes()");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		Thread t = new Thread(new Runnable(){
			@Override
			public void run(){
				try {
					TDBXReader response = tserverclass.GetPersonnes();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetDSPersonnes response:"+response);

					mPersonnes = new ArrayList<Personne>();

					if(response != null){
						while(response.next()){
							final String nom = response.getValue("PER_NOM").GetAsString();
							final String prenom = response.getValue("PER_PRENOM").GetAsString();
							final Date dateNaiss = response.getValue("PER_DATNAISS").GetAsTimeStamp();
							int personneId = (int) response.getValue("ID_PERSONNE").GetAsInt32();//Double();

							mPersonnes.add(new Personne(personneId, dateNaiss, prenom, nom));
						}
					}
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetDSPersonnes response:"+((e != null)?e.getMessage():""));
					mPersonnes = null;
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

		return mPersonnes;
	}


	static Personne mPersonne = null;
	public static Personne getPatientInfos(Context mContext, final int currentIdPatient){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(mContext,"[CALL] getPatientInfos(currentIdPatient:"+currentIdPatient+")");
		ArrayList<Personne> personnes = KitviewUtil.GetDSPersonnes(mContext);
		int nbPersonnes = (personnes != null)?personnes.size():0;
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(mContext,"[RESPONSE] getPatientInfos nbPersonnes:"+nbPersonnes);
		boolean found = false;
		int i = 0;

		while(i<nbPersonnes && !found){
			int personneId = personnes.get(i).getId();
			found = (personneId == currentIdPatient);

			if(found){
				final String prenom = personnes.get(i).getFirstName();
				final String nom = personnes.get(i).getLastName();
				final Date dateNaiss = personnes.get(i).getDateNaiss();

				mPersonne = new Personne(personneId, dateNaiss, prenom, nom);
			}
			i++;
		}

		return mPersonne;
	}


	private static int responsePatientId = -1;
	public static void GetCurrentIdPatient(final Context context,final IIntResponse iResponse){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetCurrentIdPatient()");
		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);
		responsePatientId = -1;

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					responsePatientId = tserverclass.GetCurrentIdPatient();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetCurrentIdPatient response:"+responsePatientId);
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetCurrentIdPatient response:"+((e != null)?e.getMessage():""));
					responsePatientId = -1;
				}

				if(iResponse != null)iResponse.onResponse(responsePatientId);

				conn.CloseSession();
			}
		}).start();	
	}

	static int _patientId2 = -1;
	public static int GetCurrentIdPatientSync(final Context context,String ip, int port){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetCurrentIdPatientSync()");//ip:"+ip+", port:"+port+")");
		final DSRESTConnection conn = getConnection(ip, port);//getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);
		_patientId2 = -1;

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					_patientId2 = tserverclass.GetCurrentIdPatient();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetCurrentIdPatientSync response:"+_patientId2);
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetCurrentIdPatientSync response:"+((e != null)?e.getMessage():""));
					_patientId2 = -1;
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

		return _patientId2;
	}

	static int _patientId = -1;
	public static int GetCurrentIdPatientSync(final Context context){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetCurrentIdPatientSync()");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		_patientId = -1;

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					_patientId = tserverclass.GetCurrentIdPatient();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetCurrentIdPatientSync response:"+_patientId);
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetCurrentIdPatientSync response:"+((e != null)?e.getMessage():""));
					_patientId = -1;
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

		return _patientId;
	}

	static TDataSet response = null;

	public static void GetDSGabarits(final Context context,final ITDBXReaderResponse iResponse){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetDSGabarits()");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TDBXReader response = tserverclass.GetGabarits();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetDSGabarits response:"+response);

					if(iResponse != null)iResponse.onResponse(response);
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetDSGabarits response:"+((e != null)?e.getMessage():""));
					if(iResponse != null)iResponse.onResponse(null);
				}
				conn.CloseSession();
			}
		}).start();	
	}

	static TDBXReader response2 = null;

	public static TDBXReader GetDSGabDetails(final Context context,final int GabId, final ITDBXReaderResponse iResponse){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetDSGabDetails(GabId:"+GabId+")");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		response2 = null;

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					response2 = tserverclass.GetGabDetails(GabId);
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetDSGabDetails response:"+response2);
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetDSGabDetails response:"+((e != null)?e.getMessage():""));
					response2 = null;
				}
				conn.CloseSession();
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			if(e != null)e.printStackTrace();
		}

		return response2;
	}

	public static void GetDSProfils(final Context context,final ITDBXReaderResponse iResponse){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetDSProfils()");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TDBXReader response = tserverclass.GetProfils();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetDSProfils response:"+response);

					if(iResponse != null)iResponse.onResponse(response);
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetDSProfils response:"+((e != null)?e.getMessage():""));
					if(iResponse != null)iResponse.onResponse(null);
				}
				conn.CloseSession();
			}
		}).start();	
	}

	static String responseBC = null;
	public static String GetBC(final Context context,final String SessionFilename, final String aCmd, final String aParam, final int SendToKeyboard ){
		int aParamLength = (aParam != null)?aParam.length():0;

		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetBC(aCmd: "+aCmd+",aParam length:"+aParamLength+")");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);
		responseBC = null;

		Thread t = new Thread(new Runnable(){
			@Override
			public void run(){
				try{
					responseBC = tserverclass.GetBC(SessionFilename, aCmd, aParam,SendToKeyboard);
					int reponseBCLength = (responseBC != null)?responseBC.length():0;
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetBC response:"+reponseBCLength);
				}catch(Exception e){
					if(e != null)e.printStackTrace();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetBC response:"+((e != null)?e.getMessage():""));
					responseBC = null;
				}
				conn.CloseSession();
			}
		});

		t.start();	

		try{
			t.join();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		return responseBC;
	}

	public static String getCharacterDataFromElement(Element e) {
		if(e != null){
			Node child = e.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				return cd.getData();
			}
		}
		return "?";
	}

	public static HashMap<String, String> initializeFormatsFlashFocusInPersitenceManager(final Context context,PersistenceManager mPersistenceManager){
		HashMap<String, String> res = new HashMap<String, String>();

		if(mPersistenceManager != null){
			try {
				String formats = GetFormats(context);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(formats));
				Document doc = db.parse(is);
				NodeList nodes = doc.getElementsByTagName("FORMAT");
				int nbFormats = (nodes != null)?nodes.getLength():0;
				int i = 0;
				String flash = Parameters.FLASH_MODE_OFF;
				String focus = Parameters.FOCUS_MODE_AUTO;

				while( i < nbFormats){
					Element element = (Element) nodes.item(i);
					try{
						NodeList _keywords = element.getElementsByTagName("KEYWORDS");
						Element keywords = (Element) _keywords.item(0);
						String currentKeywords = getCharacterDataFromElement(keywords);

						if(currentKeywords != null && !res.containsKey(currentKeywords))res.put(currentKeywords, flash+";"+focus);
					}catch(NumberFormatException e){
						if(e != null)e.printStackTrace();
					}
					i++;
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	static List<Scenario> scenarios = null;

	public static void getScenarios(final Context context, final IScenarioArrayListResponse mInterface){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] getScenarios()");
		scenarios = PersistenceManager.getInstance().getScenarios();
		
		int nbScenarios = (scenarios != null)?scenarios.size():0;
		
		if(scenarios == null || (nbScenarios == 0)){
			isKitviewAvailable(context, new ITestConnectionResponse() {
				@Override
				public void onResponse(int connectionEstablished) {
					if(connectionEstablished == TEST_CONNECTION_OK){
						final String formats = GetFormats(context);
						scenarios = new ArrayList<Scenario>();	
						final HashMap<String, String> hm = PersistenceManager.getInstance().getKeyWordsFlashFocus();

						GetDSGabarits(context,new ITDBXReaderResponse() {
							@Override
							public void onResponse(TDBXReader response) {
								if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] getScenarios response:"+response);

								if(response != null){
									if(response != null){
										while(response.next() ){
											try {
												int GabId = response.getValue("ID_GABARIT").GetAsInt32();
												String GabName = response.getValue("NOM").GetAsString();
												final Scenario scenario = new Scenario(GabName);
												scenarios.add(scenario);
												TDBXReader response2 = GetDSGabDetails(context,GabId, null);

												if(response2 != null){
													while(response2.next()){
														List<Float> mHorCoeffs = new ArrayList<Float>();
														List<Float> mVerCoeffs = new ArrayList<Float>();

														try {
															String name = response2.getValue("GONOM").GetAsAnsiString();
															int idGabaritOutil = response2.getValue("ID_GABARITOUTIL").GetAsInt32();

															String sep = ";";
															String gabOutilAttributs = GetGabOutilAttributs(context,idGabaritOutil, sep);												

															StringTokenizer st = new StringTokenizer(gabOutilAttributs, sep);
															int nbElems = (st != null)?st.countTokens():0;

															String keyWordsKey = "";
															String keyWordsKeyScenarioItem = "";

															for(int i=0;i<nbElems;i++){
																StringTokenizer st2 = new StringTokenizer(st.nextToken(),"=");
																int nbElems2 = (st2 != null)?st2.countTokens():0;

																if(nbElems2 == 2){
																	String key = st2.nextElement().toString();
																	String value = st2.nextElement().toString();

																	if(value.equals("true")){
																		keyWordsKey += key + ((i==nbElems-1)?"":";");
																		keyWordsKeyScenarioItem += key + "=true" + ((i==nbElems-1)?"":";");
																	}
																}
															}

															DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
															DocumentBuilder db = dbf.newDocumentBuilder();

															InputSource is = new InputSource();
															is.setCharacterStream(new StringReader(formats));

															Document doc = db.parse(is);
															NodeList nodes = doc.getElementsByTagName("FORMAT");
															int nbFormats = (nodes != null)?nodes.getLength():0;
															boolean found = false;
															int i = 0;

															while( i < nbFormats && !found){
																Element element = (Element) nodes.item(i);
																try{
																	NodeList _keywords = element.getElementsByTagName("KEYWORDS");
																	Element keywords = (Element) _keywords.item(0);
																	String currentKeywords = getCharacterDataFromElement(keywords);

																	if(element != null && currentKeywords != null && currentKeywords.equals(keyWordsKey)){

																		NodeList _name = element.getElementsByTagName("RH1");
																		if(_name != null){
																			Element line = (Element) _name.item(0);
																			String rh1 =  getCharacterDataFromElement(line);
																			Float rh1Float = Float.parseFloat(rh1);
																			mHorCoeffs.add(rh1Float);
																		}

																		NodeList _name2 = element.getElementsByTagName("RH2");
																		if(_name2 != null){
																			Element line2 = (Element) _name2.item(0);
																			String rh2 =  getCharacterDataFromElement(line2);
																			Float rh2Float = Float.parseFloat(rh2);
																			mHorCoeffs.add(rh2Float);
																		}

																		NodeList _name3 = element.getElementsByTagName("RH3");
																		if(_name3 != null){
																			Element line3 = (Element) _name3.item(0);
																			String rh3 =  getCharacterDataFromElement(line3);
																			Float rh3Float = Float.parseFloat(rh3);
																			mHorCoeffs.add(rh3Float);
																		}

																		NodeList _name4 = element.getElementsByTagName("RV1");
																		if(_name4 != null){
																			Element line4 = (Element) _name4.item(0);
																			String rv1 =  getCharacterDataFromElement(line4);
																			Float rv1Float = Float.parseFloat(rv1);
																			mVerCoeffs.add(rv1Float);
																		}

																		NodeList _name5 = element.getElementsByTagName("RV2");
																		if(_name5 != null){
																			Element line5 = (Element) _name5.item(0);
																			String rv2 =  getCharacterDataFromElement(line5);
																			Float rv2Float = Float.parseFloat(rv2);
																			mVerCoeffs.add(rv2Float);
																		}

																		NodeList _name6 = element.getElementsByTagName("RV3");	
																		if(_name6 != null){
																			Element line6 = (Element) _name6.item(0);
																			String rv3 =  getCharacterDataFromElement(line6);
																			Float rv3Float = Float.parseFloat(rv3);
																			mVerCoeffs.add(rv3Float);
																		}

																		found = true;
																	}
																}catch(NumberFormatException e){
																	if(e != null)e.printStackTrace();
																}
																i++;
															}

															if(!found){
																mHorCoeffs = Arrays.asList(new Float[]{0.25f,0.5f,0.75f});
																mVerCoeffs = Arrays.asList(new Float[]{0.25f,0.5f,0.75f});
															}

															String _flash = Parameters.FLASH_MODE_OFF;
															String _focus = Parameters.FOCUS_MODE_AUTO;

															hm.put(keyWordsKey, _flash+";"+_focus+";"+mHorCoeffs.toString()+";"+mVerCoeffs.toString());
															PersistenceManager.getInstance().setKeywordsFlashFocus(hm);

															ScenarioItem item = new ScenarioItem(name, keyWordsKeyScenarioItem);
															scenario.addScenarioItem(item);
														} catch (DBXException e) {
															if(e != null)e.printStackTrace();
															if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetScenarios response:"+((e != null)?e.getMessage():""));
														}catch(Exception e){
															if(e != null)e.printStackTrace();
															if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetScenarios response:"+((e != null)?e.getMessage():""));
														}
													}
												}
											} catch (DBXException e) {
												if(e != null)e.printStackTrace();
												if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE ERROR] GetScenarios response:"+((e != null)?e.getMessage():""));
											}
										}
									}
								}

								PersistenceManager.getInstance().setScenarios(scenarios);

								if(mInterface != null){
									mInterface.onResponse(scenarios);
								}
							}
						});
					}
				}
			});	
		}else{
			if(mInterface != null){
				mInterface.onResponse(scenarios);
			}
		}
	}

	public static class Collection{
		private String mId;
		private String mParentId;
		private String mName;

		public Collection(String mId, String mParentId, String mName){
			this.mId = mId;
			this.mParentId = mParentId;
			this.mName = mName;
		}

		public String getId(){
			return this.mId;
		}

		public String getParentId(){
			return this.mParentId;
		}

		public String getName(){
			return this.mName;
		}

		public void setName(String mName){
			this.mName = mName;
		}
	}

	static ArrayList<Collection> _collectionCategories = null;
	public static ArrayList<Collection> getCollectionCategories(final Context context){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] getCollectionCategories()");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		_collectionCategories = null;

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TDBXReader response = tserverclass.GetCollectionCategories();
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] getCollectionCategories response:"+response);

					_collectionCategories = new ArrayList<Collection>();

					int vueId = -1;
					String vueName = "";
					if(response != null){
						while(response.next()){
							vueId = response.getValue("ID_VUE").GetAsInt32();	
							vueName = response.getValue("NOM_VUE").GetAsString();
							_collectionCategories.add(new Collection(""+vueId, "", vueName));
						}
					}
				}catch(DBXException e){
					e.printStackTrace();
					_collectionCategories = null;
				}
			}});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return _collectionCategories;
	}

	public static void getCollections(final FolderActivity context, int patientId, final int vueId, final ICollectionArrayListResponse iFolderArrayListResponse){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] getCollections(patientId:"+patientId+" vueId:"+vueId+")");

		final ArrayList<Collection> c = new ArrayList<KitviewUtil.Collection>();
		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TDBXReader response = tserverclass.GetCollections(vueId);
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetCollections response:"+response);

					if(response != null){
						while(response.next()){
							int mId = response.getValue("ID_REPERTOIRE").GetAsInt32();
							int mParentId = response.getValue("PARENT").GetAsInt32();
							String mName = response.getValue("NOM_REPOERTOIRE").GetAsString();

							Collection currentCollection = new Collection(""+mId, ""+mParentId, mName);
							c.add(currentCollection);
						}
					}

					if(iFolderArrayListResponse != null)iFolderArrayListResponse.onResponse(c);
				} catch (DBXException e){
					if(e != null)e.printStackTrace();

					if(iFolderArrayListResponse != null)iFolderArrayListResponse.onResponse(null);
				}
				conn.CloseSession();
			}
		}).start();	
	}

	public static void GetObjects(final Context context, final String idPatient, final String idCollection, final IPhotoInfosArrayListResponse iPhotoInfosArrayListResponse){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] GetObjects(idPatient:"+idPatient+" idCollection:"+idCollection+")");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int _idPatient = Integer.parseInt(idPatient);
					int _idCollection = Integer.parseInt(idCollection);

					TDBXReader response = tserverclass.GetObjectsForCollection(_idPatient, _idCollection);
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] GetObjects response:"+response);

					ArrayList<Photo> photos = new ArrayList<Photo>();

					if(response != null){
						while(response.next()){
							int mId = response.getValue("PK_OBJET").GetAsInt32();
							String ext = response.getValue("EXTENSION").GetAsString();

							final Date mDateInsertion = response.getValue("DATECREATION").GetAsTimeStamp();//(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(response.getValue("DATEINSERTION").GetAsTimeStamp())).toString();
							Photo p = new Photo("kitview;"+mId, idPatient, idCollection,mDateInsertion);
							if(ext.equalsIgnoreCase(".jpg") || ext.equalsIgnoreCase(".bmp") || ext.equalsIgnoreCase(".jpeg") || ext.equalsIgnoreCase(".png")){
								photos.add(p);

							}
						}
					}

					Collections.reverse(photos);

					if(iPhotoInfosArrayListResponse != null)iPhotoInfosArrayListResponse.onResponse(photos);
				} catch (DBXException e){
					if(e != null)e.printStackTrace();

					if(iPhotoInfosArrayListResponse != null)iPhotoInfosArrayListResponse.onResponse(null);
				}catch(NumberFormatException e){
					if(e != null)e.printStackTrace();
					if(iPhotoInfosArrayListResponse != null)iPhotoInfosArrayListResponse.onResponse(null);
				}
				conn.CloseSession();
			}
		}).start();	
	}

	static Bitmap bitmap = null;
	public static Bitmap downloadPhoto(final Context context, final Personne p, final String idPhoto, final int maxWidth, final int maxHeight){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] downloadPhoto(idPhoto:"+idPhoto+",maxWidth:"+maxWidth+",maxHeight:"+maxHeight+")");

		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		bitmap = null;

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					int _idPhoto = Integer.parseInt(idPhoto);
					String response = tserverclass.GetObject(_idPhoto, maxWidth, maxHeight);
					int length = (response != null)?response.length():0;

					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] downloadPhoto response length:"+length);

					byte [] bytes = Base64.decode(response, Base64.DEFAULT);

					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inMutable = true;
					bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,options);

					String currentCollectionName = (context instanceof FolderActivity)?((FolderActivity)context).getCurrentCollectionName():"";

					//Log.e("downloadPhoto","currentCollectionName:"+currentCollectionName);
					
					boolean isPortrait = (currentCollectionName != null && currentCollectionName != "" && (currentCollectionName.toLowerCase().contains("portrait") || currentCollectionName.toLowerCase().contains("portraits") )  );

					if( ( (isPortrait && (context instanceof FolderActivity)) || (!(context instanceof FolderActivity)))   && p != null && p.getRef2() != null && p.getRef2().equals("TRUE")){
						bitmap = FilterProcessor.blurFilter(context,bitmap);
						//Log.e("downloadPhoto","image blurred");
					}else{
						//Log.e("downloadPhoto","image not blurred");
					}

					if(context instanceof FolderActivity)((FolderActivity)(context)).cancelDialog();
				} catch (DBXException e){
					if(e != null)e.printStackTrace();
					bitmap = null;
				}catch(NumberFormatException e){
					if(e != null)e.printStackTrace();
					bitmap = null;
				}catch(OutOfMemoryError e){
					if(e != null)e.printStackTrace();
					bitmap = null;
				}catch(Exception e){
					if(e != null)e.printStackTrace();
					bitmap = null;
				}
				conn.CloseSession();
			}
		});

		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			if(e != null)e.printStackTrace();
		}

		return bitmap;
	}

	public static void downloadPhotoAsync(final Context context, final Personne p, final int idPhoto, final int maxWidth, final int maxHeight, final IBitmapResponse iBitmapResponse){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] downloadPhotoAsync(idPhoto:"+idPhoto+",maxWidth:"+maxWidth+",maxHeight:"+maxHeight+")");
		final DSRESTConnection conn = getInstanceConnection(context);
		final TKitviewClass tserverclass = new TKitviewClass(conn);

		Thread t = new Thread(new Runnable(){
			@Override
			public void run(){
				try {
					String response = tserverclass.GetObject(idPhoto,maxWidth, maxHeight);
					int length = (response != null)?response.length():0;
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] downloadPhotoAsync response length:"+length);

					byte [] bytes = Base64.decode(response, Base64.DEFAULT);

					if(iBitmapResponse != null){
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inMutable = true;
						Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,options);

						String currentCollectionName = (context instanceof FolderActivity)?((FolderActivity)context).getCurrentCollectionName():"";

						//Log.e("downloadPhoto","currentCollectionName:"+currentCollectionName);
						
						boolean isPortrait = (currentCollectionName != null && currentCollectionName != "" && (currentCollectionName.toLowerCase().contains("portrait") || currentCollectionName.toLowerCase().contains("portraits") )  );

						if( ( (isPortrait && (context instanceof FolderActivity)) || (!(context instanceof FolderActivity)))   && p != null && p.getRef2() != null && p.getRef2().equals("TRUE")){
							bitmap = FilterProcessor.blurFilter(context,bitmap);
							//Log.e("downloadPhoto","image blurred");
						}else{
							//Log.e("downloadPhoto","image not blurred");
						}

						iBitmapResponse.onResponse(bitmap);
					}

					if(context instanceof FolderActivity){
						((FolderActivity)(context)).cancelDialog();
					}
				}catch (DBXException e){
					if(e != null)e.printStackTrace();
					if(iBitmapResponse != null)iBitmapResponse.onResponse(null);
				}catch(NumberFormatException e){
					if(e != null)e.printStackTrace();
					if(iBitmapResponse != null)iBitmapResponse.onResponse(null);
				}catch(OutOfMemoryError e){
					if(e != null)e.printStackTrace();
					if(iBitmapResponse != null)iBitmapResponse.onResponse(null);
				}catch(Exception e){
					if(e != null)e.printStackTrace();
					if(iBitmapResponse != null)iBitmapResponse.onResponse(null);
				}
				conn.CloseSession();
			}
		});

		t.start();
	}

	public static int isKitviewAvailableSync(Context context, final String ip, final int port){
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] isKitviewAvailableSync(ip:"+ip+",port:"+port+")");

		int _isAvailable = NetworkUtil.isNetworkAvailable(context)?TEST_CONNECTION_OK:TEST_CONNECTION_WIFI_KO;
		final Date start = new Date();
		final int patientId = KitviewUtil.GetCurrentIdPatientSync(context,ip, port);
		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] isKitviewAvailableSync patientId:"+patientId);

		if(_isAvailable == TEST_CONNECTION_OK){
			boolean killThread = false;

			while(!killThread){
				Date now = new Date();

				boolean timeOutExpired = (now.getTime() - start.getTime()) >= PersistenceManager.getInstance().getConnectionTimeout()*1000;//KITVIEW_CONNECTION_TIMEOUT;
				killThread = timeOutExpired || (patientId != -1);

				if(killThread){
					if(timeOutExpired)_isAvailable = TEST_CONNECTION_KITVIEW_KO;
				}
			}
		}
		return _isAvailable;
	}

	private static boolean killThread;
	private static int currentPatientId;

	public static void isKitviewAvailable(final Context context, final ITestConnectionResponse iTestConnectionResponse) {

		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[CALL] isKitviewAvailable()");

		int isAvailable = NetworkUtil.isNetworkAvailable(context)?TEST_CONNECTION_OK:TEST_CONNECTION_WIFI_KO;

		if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] isKitviewAvailable isAvailable:"+isAvailable);

		final Date start = new Date();
		killThread = true;
		currentPatientId = -1;

		if(isAvailable == TEST_CONNECTION_OK){
			new Thread(new Runnable() {
				@Override
				public void run(){
					killThread = false;

					while(!killThread){
						Date now = new Date();

						boolean timeOutExpired = (now.getTime() - start.getTime()) >= PersistenceManager.getInstance().getConnectionTimeout()*1000;//KITVIEW_CONNECTION_TIMEOUT;
						killThread = timeOutExpired || (currentPatientId != -1);

						if(iTestConnectionResponse != null && killThread){
							boolean connection_ok = (currentPatientId != -1);
							iTestConnectionResponse.onResponse(connection_ok?TEST_CONNECTION_OK:TEST_CONNECTION_KITVIEW_KO);
						}
					}
				}
			}).start();

			KitviewUtil.GetCurrentIdPatient(context,new IIntResponse(){
				@Override
				public void onResponse(int response){
					if(LOG_ENABLED)LogUtil.getInstance().insertDataInLog(context,"[RESPONSE] isKitviewAvailable currentPatientId:"+currentPatientId);
					currentPatientId = response;
				}
			});

		}else{
			if(iTestConnectionResponse != null){
				iTestConnectionResponse.onResponse(TEST_CONNECTION_WIFI_KO);
			}
		}
	}
}