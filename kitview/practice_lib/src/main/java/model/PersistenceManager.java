package model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.dentalcrm.kitview.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.rest.Categorie;
import model.rest.Scenario;
import model.rest.Subscriber;
import util.network.KitviewUtil;
import util.network.NetworkUtil;
import util.system.SystemUtil;

public class PersistenceManager{
	private final static String SHARED_PREFERENCES_FILE_NAME = ".prefs";
	//private final static String KEY_SHARED_PREFERENCES_CREATED = "created";

	public final static String VALUE_UNDEFINED_STRING = "";
	public final static int VALUE_UNDEFINED_INT = Integer.MIN_VALUE;
	public final static boolean VALUE_UNDEFINED_BOOLEAN = false;

	private final static String KEY_SPLASH_SCREEN_ENABLED = "KEY_SPLASH_SCREEN_ENABLED";

	private final static String KEY_CONNECTION_TIMEOUT = "KEY_CONNECTION_TIMEOUT";

	private final static String KEY_DOCTOR_EMAIL = "KEY_DOCTOR_EMAIL";
	private final static String KEY_REMOTE_SERVER_PATIENT = "KEY_REMOTE_SERVER_PATIENT";

	private final static String KEY_REMOTE_SERVER_SURGERY = "KEY_REMOTE_SERVER_SURGERY";

	private final static String KEY_KITVIEW_IP = "KEY_KITVIEW_IP";
	private final static String KEY_KITVIEW_PORT = "KEY_KITVIEW_PORT";
	//private final static String KEY_KITVIEW_PROTOCOL = "KEY_KITVIEW_PROTOCOL";
	private final static String KEY_STREAMING_IP = "KEY_STREAMING_IP";
	private final static String KEY_STREAMING_PORT = "KEY_STREAMING_PORT";
	//private final static String KEY_STREAMING_PROTOCOL = "KEY_STREAMING_PROTOCOl";
	private final static String KEY_STREAMING_ENABLED = "KEY_STREAMING_ENABLED";
	private final static String KEY_ANDROID_PORT = "KEY_ANDROID_PORT";
	private final static String KEY_CAMERA_SCENARIO_INDEX = "KEY_CAMERA_SCENARIO_INDEX";
	private final static String KEY_SCENARIOS = "KEY_SCENARIOS";
	private final static String KEY_FOLDERS = "KEY_FOLDERS";

	private final static String KEY_CAMERA_SHUTTER_RELEASE_BUTTON_INDEX = "KEY_CAMERA_SHUTTER_RELEASE_BUTTON_INDEX";

	private final static String KEY_CAMERA_SOUNDS = "KEY_CAMERA_SOUNDS";
	private final static String KEY_CAMERA_SOUND_INDEX = "KEY_CAMERA_SOUND_INDEX";

	private final static String KEY_CAMERA_FLASH = "KEY_CAMERA_FLASH";
	private final static String KEY_CAMERA_FOCUS = "KEY_CAMERA_FOCUS";

	private final static String KEY_CAMERA_RESOLUTION_INDEX = "KEY_CAMERA_RESOLUTION_INDEX";


	private final static String KEY_MODE_SURGERY_OR_PATIENT = "KEY_MODE_SURGERY_OR_PATIENT";

	public final static int MODE_SELECTION = 0;
	public final static int MODE_SURGERY = 1;
	public final static int MODE_PATIENT = 2;

	public final static String KEY_KEYWORDS_FLASH_FOCUS = "KEY_KEYWORDS_FLASH_FOCUS";

	public final static String KEY_SPEED_VECTOR_THRESHOLD = "KEY_SPEED_VECTOR_THRESHOLD";

	public final static String KEY_BGCOLOR = "KEY_BGCOLOR";

	public final static float SHAKE_SENSIBILITY_HIGH = 2.0f;
	public final static float SHAKE_SENSIBILITY_NORMAL = 6.0f;
	public final static float SHAKE_SENSIBILITY_LOW = 10.0f;

	public final static String KEY_DISPLAY_MODE_MY_CASE = "KEY_DISPLAY_MODE_MY_CASE";

	public final static String KEY_SUBSCRIBER_INDEX = "KEY_SUBSCRIBER_INDEX";
	public final static String KEY_SUBSCRIBERS = "KEY_SUBSCRIBERS";



	public final static String KEY_PICTURE_TIME_DISPLAY = "KEY_PICTURE_TIME_DISPLAY";
	public final static int PICTURE_TIME_DISPLAY_1 = 1000;
	public final static int PICTURE_TIME_DISPLAY_2 = 2000;
	public final static int PICTURE_TIME_DISPLAY_3 = 5000;

	public final static String KEY_COLLECTION_CATEGORY_INDEX = "KEY_COLLECTION_CATEGORY_INDEX";
	public final static String KEY_COLLECTION_ID = "KEY_COLLECTION_ID";

	public final static String KEY_APP_VERSION = "KEY_APP_VERSION";

	private static PersistenceManager _persistenceManager;
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mSharedPreferencesEditor;

	private final static String REMOTE_SERVER_ADRESS = "109.190.25.5:2015";
	//private final static String REMOTE_SERVER_ADRESS = "192.168.2.77::8080";
	//private final static String REMOTE_SERVER_ADRESS = "92.154.32.8:8081";
	//private final static String REMOTE_SERVER_ADRESS_SURGERY = "109.190.25.5:2016";

	public static PersistenceManager getInstance(){
		if(_persistenceManager == null)_persistenceManager = new PersistenceManager();

		return _persistenceManager;
	}

	public void initializeSharedPreferences(Activity mContext, boolean force){
		if(mContext != null){
			mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);

			if(mSharedPreferences != null){
				mSharedPreferencesEditor = mSharedPreferences.edit();

				if(mSharedPreferencesEditor != null){
					if(mSharedPreferences.contains(KEY_APP_VERSION)){
						String currentAppVersion = SystemUtil.getAppVersion(mContext);

						boolean hasAppVersionChanged = !currentAppVersion.equals(mSharedPreferences.getString(KEY_APP_VERSION, VALUE_UNDEFINED_STRING));

						// Application has been updated : need to be reset to avoid potential wrong behaviour & issues ...
						if(hasAppVersionChanged || force){
							this.mSharedPreferencesEditor.clear();
							this.mSharedPreferencesEditor.commit();

							initializeData(mContext);
						}else{
							//==> need to check all parameters : and reset all params if saved data are invalid ...
						}

						//first time application launched ...
					}else{
						initializeData(mContext);
					}

					/*if(!mSharedPreferences.contains(KEY_SHARED_PREFERENCES_CREATED)){
						mSharedPreferencesEditor.putBoolean(KEY_SHARED_PREFERENCES_CREATED, true);
						initializeData();
					}else{
						//check data stored ... 
					}*/
				}
			}
		}
	}

	private void initializeData(Context mContext){
		setSplashScreenEnabled(true);

		setDoctorEMail("");

		setRemoteServerAdress(REMOTE_SERVER_ADRESS);//8080");
		//setRemoteServerSurgeryAdress(REMOTE_SERVER_ADRESS_SURGERY);

		setAppVersion(SystemUtil.getAppVersion(mContext));

		setBackgroundColor(Color.BLACK);

		setDisplayModeMyCase(1);

		//Default behaviour: virtual button used to take pictures
		setShutterReleaseIndex(0);

		initializeSound();

		setScenarioIndex(0);

		setConnectionTimeout(10);

		setFlash("");
		setFocus("");

		setResolutionIndex(0);

		setMachineIp("0.0.0.0");
		setMachinePort(8080);

		setStreamingEnabled(false);

		setStreamingIP("0.0.0.0");
		setStreamingPort(5001);

		setAndroidPort(5000);

		setMode(MODE_SELECTION);

		setKeywordsFlashFocus(KitviewUtil.initializeFormatsFlashFocusInPersitenceManager(mContext,this));

		setShakeSensibility(SHAKE_SENSIBILITY_NORMAL);

		setPictureTimeDisplay(PICTURE_TIME_DISPLAY_2);
	}

	private void initializeSound(){
		setSoundIndex(1);
		ArrayList<Sound> sounds = new ArrayList<Sound>();
		sounds.add(new Sound(R.string.off, 0));
		sounds.add(new Sound(R.string.shutter1, R.raw.shutter));
		setSounds(sounds);
	}

	/*Getters*/

	public boolean getSplashScreenEnabled(){
		return mSharedPreferences.getBoolean(KEY_SPLASH_SCREEN_ENABLED, VALUE_UNDEFINED_BOOLEAN);	
	}

	public String getStreamingIP(boolean convertToIp){
		String ip = mSharedPreferences.getString(KEY_STREAMING_IP, VALUE_UNDEFINED_STRING);//putString(KEY_KITVIEW_IP, ip);

		if(convertToIp && ip != null && ip.startsWith("\\")){
			String netBios = ip.replace("\\", "");

			String newIp = NetworkUtil.resolveIpFromNetbios(netBios);

			if(newIp != null)ip = newIp;
			else ip = "0.0.0.0";
		}
		
		return ip;
	}


	public String getDoctorEmail(){
		return mSharedPreferences.getString(KEY_DOCTOR_EMAIL, VALUE_UNDEFINED_STRING);
	}

	public String getRemoteServerAdress(boolean convertToIp){
		String ip = mSharedPreferences.getString(KEY_REMOTE_SERVER_PATIENT, REMOTE_SERVER_ADRESS);//putString(KEY_KITVIEW_IP, ip);
		//String ip = "92.154.32.8:8080";
		//String ip = "192.168.2.77:8080";

		if(convertToIp && ip != null && ip.startsWith("\\")){
			int doublePointIndex = ip.lastIndexOf(":");
			String port = ip.substring(doublePointIndex+1);
			String netBiosRaw = ip.substring(0, doublePointIndex);
			String netBios = netBiosRaw.replace("\\", "");
			String newIp = NetworkUtil.resolveIpFromNetbios(netBios);

			if(newIp != null)ip = newIp+":"+port;
			else ip = REMOTE_SERVER_ADRESS;//"0.0.0.0";
		}
		System.out.println("PersisMan:debug --------------------------------------------------------->>> "+ip);
		
		return ip;
	}

	public int getMode(){
		return mSharedPreferences.getInt(KEY_MODE_SURGERY_OR_PATIENT, 0);
	}

	public int getStreamingPort(){
		return mSharedPreferences.getInt(KEY_STREAMING_PORT, 5001);	
	}

	public int getAndroidPort(){

		return mSharedPreferences.getInt(KEY_ANDROID_PORT, 5000);//VALUE_UNDEFINED_INT);	
	}	

	public boolean getStreamingEnabled(){
		return mSharedPreferences.getBoolean(KEY_STREAMING_ENABLED, VALUE_UNDEFINED_BOOLEAN);	
	}

	public float getShakeSensibility(){
		return mSharedPreferences.getFloat(KEY_SPEED_VECTOR_THRESHOLD, SHAKE_SENSIBILITY_NORMAL);
	}

	public int getBgColor(){
		return mSharedPreferences.getInt(KEY_BGCOLOR, Color.WHITE);
	}

	public int getDisplayModeMyCase(){
		return mSharedPreferences.getInt(KEY_DISPLAY_MODE_MY_CASE, 0);
	}

	public String getAppVersion(){

		return mSharedPreferences.getString(KEY_APP_VERSION, "1.0");//VALUE_UNDEFINED_STRING);
	}

	public int getScenarioIndex(){
		return mSharedPreferences.getInt(KEY_CAMERA_SCENARIO_INDEX, VALUE_UNDEFINED_INT);	
	}

	public int getSoundIndex(){	
		return mSharedPreferences.getInt(KEY_CAMERA_SOUND_INDEX, VALUE_UNDEFINED_INT);	
	}

	public int getShutterReleaseButtonIndex(){
		return mSharedPreferences.getInt(KEY_CAMERA_SHUTTER_RELEASE_BUTTON_INDEX, -1);
	}

	public String getFlash(){
		return mSharedPreferences.getString(KEY_CAMERA_FLASH, "");
	}

	public String getFocus(){
		return mSharedPreferences.getString(KEY_CAMERA_FOCUS, "");
	}

	public int getResolutionIndex(){
		return mSharedPreferences.getInt(KEY_CAMERA_RESOLUTION_INDEX, 0);
	}

	public void setFlash(String flash){
		mSharedPreferencesEditor.putString(KEY_CAMERA_FLASH, flash);
		mSharedPreferencesEditor.commit();
	}

	public void setFocus(String focus){
		mSharedPreferencesEditor.putString(KEY_CAMERA_FOCUS, focus);
		mSharedPreferencesEditor.commit();
	}

	public void setResolutionIndex(int index){
		mSharedPreferencesEditor.putInt(KEY_CAMERA_RESOLUTION_INDEX, index);
		mSharedPreferencesEditor.commit();
	}

	public int getCollectionCategorySelectedIndex(){
		return mSharedPreferences.getInt(KEY_COLLECTION_CATEGORY_INDEX, -1);
	}

	public String getCollectionId(){
		return mSharedPreferences.getString(KEY_COLLECTION_ID, "");
	}

	//{key1;keyN:} ==> "current flash";"current whitebalance"
	public HashMap<String, String> getKeyWordsFlashFocus(){
		Gson gson = new Gson();
		String json = mSharedPreferences.getString(KEY_KEYWORDS_FLASH_FOCUS, "");
		Type type = new TypeToken<HashMap<String, String>>(){}.getType();
		return gson.fromJson(json, type);
	}

	public ArrayList<Subscriber> getSubscribers(){
		Gson gson = new Gson();
		String json = mSharedPreferences.getString(KEY_SUBSCRIBERS, "");
		Type type = new TypeToken<ArrayList<Subscriber>>(){}.getType();
		return gson.fromJson(json, type);
	}


	public void setSubscribers(ArrayList<Subscriber> subscribers){	
		Gson gson= new Gson();
		String json = gson.toJson(subscribers);

		mSharedPreferencesEditor.putString(KEY_SUBSCRIBERS, json);
		mSharedPreferencesEditor.commit();	
	}

	public void setSubscriberIndex(int index){
		mSharedPreferencesEditor.putInt(KEY_SUBSCRIBER_INDEX, index);
		mSharedPreferencesEditor.commit();
	}

	public int getSubscriberIndex(){
		return mSharedPreferences.getInt(KEY_SUBSCRIBER_INDEX, -1);
	}

	public void setKeywordsFlashFocus(HashMap<String, String> hm){	
		Gson gson= new Gson();
		String json = gson.toJson(hm);

		mSharedPreferencesEditor.putString(KEY_KEYWORDS_FLASH_FOCUS, json);
		mSharedPreferencesEditor.commit();	
	}

	public void setSounds(List<Sound> sounds){
		Gson gson= new Gson();
		String json = gson.toJson(sounds);

		mSharedPreferencesEditor.putString(KEY_CAMERA_SOUNDS, json);
		mSharedPreferencesEditor.commit();	
	}

	public void setMode(int mode){
		this.mSharedPreferencesEditor.putInt(KEY_MODE_SURGERY_OR_PATIENT, mode);
		this.mSharedPreferencesEditor.commit();
	}

	public void setShakeSensibility(float shakeSensibility){
		this.mSharedPreferencesEditor.putFloat(KEY_SPEED_VECTOR_THRESHOLD, shakeSensibility);
		this.mSharedPreferencesEditor.commit();
	}

	public void setBackgroundColor(int color){
		this.mSharedPreferencesEditor.putInt(KEY_BGCOLOR, color);
		this.mSharedPreferencesEditor.commit();
	}

	public void setDisplayModeMyCase(int displayModeMyCase){
		this.mSharedPreferencesEditor.putInt(KEY_DISPLAY_MODE_MY_CASE, displayModeMyCase);
		this.mSharedPreferencesEditor.commit();
	}

	public void setScenarios(List<Scenario> scenario){
		Gson gson= new Gson();
		String json = gson.toJson(scenario);

		mSharedPreferencesEditor.putString(KEY_SCENARIOS, json);
		mSharedPreferencesEditor.commit();	
	}

	public void setFolders(List<Categorie> folders) {
		Gson gson= new Gson();
		String json = gson.toJson(folders);

		mSharedPreferencesEditor.putString(KEY_FOLDERS, json);
		mSharedPreferencesEditor.commit();	
	}

	public void setPictureTimeDisplay(int pictureTimeDisplay){
		this.mSharedPreferencesEditor.putInt(KEY_PICTURE_TIME_DISPLAY, pictureTimeDisplay);
		this.mSharedPreferencesEditor.commit();
	}

	public void setCollectionCategorySelectedIndex(int index){
		this.mSharedPreferencesEditor.putInt(KEY_COLLECTION_CATEGORY_INDEX, index);
		this.mSharedPreferencesEditor.commit();
	}

	public void setCollectionId(String collectionId){
		this.mSharedPreferencesEditor.putString(KEY_COLLECTION_ID, collectionId);
		this.mSharedPreferencesEditor.commit();
	}

	public List<Scenario> getScenarios(){
		Gson gson = new Gson();
		String json = mSharedPreferences.getString(KEY_SCENARIOS, "");
		Type type = new TypeToken<List<Scenario>>(){}.getType();
		return  gson.fromJson(json, type);
	}

	public List<Sound> getSounds(){
		Gson gson = new Gson();
		String json = mSharedPreferences.getString(KEY_CAMERA_SOUNDS, "");
		Type type = new TypeToken<List<Sound>>(){}.getType();
		return  gson.fromJson(json, type);
	}

	public ArrayList<Categorie> getFolders() {
		Gson gson = new Gson();
		String json = mSharedPreferences.getString(KEY_FOLDERS, "");
		Type type = new TypeToken<List<Categorie>>(){}.getType();
		return gson.fromJson(json, type);
	}

	public int getPictureTimeDisplay(){
		return this.mSharedPreferences.getInt(KEY_PICTURE_TIME_DISPLAY, PICTURE_TIME_DISPLAY_2);
	}

	/*Setters*/

	public void setSplashScreenEnabled(boolean splashScreenEnabled){
		this.mSharedPreferencesEditor.putBoolean(KEY_SPLASH_SCREEN_ENABLED, splashScreenEnabled);	
		this.mSharedPreferencesEditor.commit();
	}

	public void setDoctorEMail(String email){
		this.mSharedPreferencesEditor.putString(KEY_DOCTOR_EMAIL, email);
		this.mSharedPreferencesEditor.commit();
	}

	public void setRemoteServerAdress(String remoteServerAdress){
		this.mSharedPreferencesEditor.putString(KEY_REMOTE_SERVER_PATIENT, remoteServerAdress);
		this.mSharedPreferencesEditor.commit();
	}

	public void setRemoteServerSurgeryAdress(String remoteServerAdress){
		this.mSharedPreferencesEditor.putString(KEY_REMOTE_SERVER_SURGERY, remoteServerAdress);
		this.mSharedPreferencesEditor.commit();
	}

	public void setStreamingIP(String ip){
		this.mSharedPreferencesEditor.putString(KEY_STREAMING_IP, ip);
		this.mSharedPreferencesEditor.commit();
	}

	public void setStreamingPort(int port){
		this.mSharedPreferencesEditor.putInt(KEY_STREAMING_PORT, port);
		this.mSharedPreferencesEditor.commit();
	}

	private void checkKeyInt(String key, int defaultValue){
		boolean exist = this.mSharedPreferences.contains(key);

		if(!exist){
			this.mSharedPreferencesEditor.putInt(key, defaultValue);
		}
	}

	private void checkKeyString(String key, String defaultValue){
		boolean exist = this.mSharedPreferences.contains(key);

		if(!exist){
			this.mSharedPreferencesEditor.putString(key, defaultValue);
		}
	}

	private void checkKeyFloat(String key, float defaultValue){
		boolean exist = this.mSharedPreferences.contains(key);

		if(!exist){
			this.mSharedPreferencesEditor.putFloat(key, defaultValue);
		}
	}

	private void checkKeyBoolean(String key, boolean defaultValue){
		boolean exist = this.mSharedPreferences.contains(key);

		if(!exist){
			this.mSharedPreferencesEditor.putBoolean(key, defaultValue);
		}
	}

	public void setAndroidPort(int port){
		this.mSharedPreferencesEditor.putInt(KEY_ANDROID_PORT, port);
		this.mSharedPreferencesEditor.commit();
	}

	public void setStreamingEnabled(boolean streamingEnabled){
		this.mSharedPreferencesEditor.putBoolean(KEY_STREAMING_ENABLED, streamingEnabled);
		this.mSharedPreferencesEditor.commit();
	}

	public void setConnectionTimeout(int connectionTimeout){
		mSharedPreferencesEditor.putInt(KEY_CONNECTION_TIMEOUT, connectionTimeout);
		mSharedPreferencesEditor.commit();
	}

	public int getConnectionTimeout(){
		return mSharedPreferences.getInt(KEY_CONNECTION_TIMEOUT, 10);//-1);
	}

	public void setMachineIp(String ip){
		mSharedPreferencesEditor.putString(KEY_KITVIEW_IP, ip);
		mSharedPreferencesEditor.commit();
	}

	public void setMachinePort(int port){
		mSharedPreferencesEditor.putInt(KEY_KITVIEW_PORT, port);
		mSharedPreferencesEditor.commit();
	}

	//Always return an ip address even if a netbios/UNC has been entered by user...
	public String getMachineIp(boolean convertToIp){
		String ip = mSharedPreferences.getString(KEY_KITVIEW_IP, "");//putString(KEY_KITVIEW_IP, ip);

		if(convertToIp && ip != null && ip.startsWith("\\")){
			String netBios = ip.replace("\\", "");

			String newIp = NetworkUtil.resolveIpFromNetbios(netBios);

			if(newIp != null)ip = newIp;
			else ip = "0.0.0.0";
		}
		
		return ip;
	}

	public int getMachinePort(){
		return mSharedPreferences.getInt(KEY_KITVIEW_PORT, -1);
	}

	public void setScenarioIndex(int index){
		mSharedPreferencesEditor.putInt(KEY_CAMERA_SCENARIO_INDEX, index);
		mSharedPreferencesEditor.commit();
	}

	public void setShutterReleaseIndex(int index){
		mSharedPreferencesEditor.putInt(KEY_CAMERA_SHUTTER_RELEASE_BUTTON_INDEX, index);
		mSharedPreferencesEditor.commit();
	}

	public void setSoundIndex(int index){
		mSharedPreferencesEditor.putInt(KEY_CAMERA_SOUND_INDEX, index);
		mSharedPreferencesEditor.commit();
	}


	public void setAppVersion(String appVersion){
		mSharedPreferencesEditor.putString(KEY_APP_VERSION, appVersion);
		mSharedPreferencesEditor.commit();
	}

	public void reinit(Activity activity){
		this.mSharedPreferencesEditor.clear();
		this.mSharedPreferencesEditor.commit();

		initializeSharedPreferences(activity,true);
	}
}