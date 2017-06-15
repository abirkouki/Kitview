package activity;

import java.util.ArrayList;
import java.util.List;
import model.PersistenceManager;
import model.rest.Subscriber;
import util.network.KitviewUtil;
import util.network.discovery.ActivityDiscovery;
import util.system.SystemUtil;
import view.adapter.CustomAdapter;
import com.dentalcrm.kitview.R;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SettingsActivity extends FragmentActivity implements ActionBar.TabListener {
	private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	private ViewPager mViewPager;
	private static PersistenceManager mPersistenceManager;

	public static boolean IS_LAUNCHED = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_settings);

		IS_LAUNCHED = true;

		mPersistenceManager = PersistenceManager.getInstance();

		this.mStartPatientRemoteServer = mPersistenceManager.getRemoteServerAdress(false);

		ArrayList<Subscriber> subscribers = mPersistenceManager.getSubscribers();
		int nbSubscribers = (subscribers != null)?subscribers.size():0;
		int subscriberIndex = mPersistenceManager.getSubscriberIndex();

		if(subscriberIndex >= 0 && subscriberIndex < nbSubscribers){
			Subscriber currentSubscriber = subscribers.get(subscriberIndex);
			this.mStartCabinetKitViewPosteIp = currentSubscriber.getmHost();
			this.mStartCabinetKitViewPostePort = currentSubscriber.getmHttpPort();
		}

		this.mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

		final ActionBar actionBar = getActionBar();

		//TODO enlever les deprecated enlevera peut etre les NullPointerException
		try {
			actionBar.setHomeButtonEnabled(false);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.setDisplayUseLogoEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayShowHomeEnabled(false);
		} catch (NullPointerException nPE) {
			nPE.printStackTrace();
		}

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
			@Override
			public void onPageSelected(int position){
				try {
					actionBar.setSelectedNavigationItem(position);
				} catch (NullPointerException nPE) {
					nPE.printStackTrace();
				}
			}
		});

		try {
			actionBar.addTab(actionBar.newTab().setText(getString(R.string.initialisation)).setTabListener(this));
			actionBar.addTab(actionBar.newTab().setText(getString(R.string.exploitation)).setTabListener(this));
		} catch (NullPointerException nPE) {
			nPE.printStackTrace();
		}

		mFragments = new ArrayList<Fragment>();
		mFragments.add(new SettingsInitialisationFragment());
		mFragments.add(new SettingsExploitationFragment());
	}

	private static ArrayList<Fragment> mFragments;

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {

			return mFragments.get(index);

			/*switch (index){

			case 0:
				return new SettingsInitialisationFragment();
			case 1:
				return new SettingsExploitationFragment();
			}
			return null;*/
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}
	}

	public static class SettingsInitialisationFragment extends Fragment {
		private Switch mStreamingSwitch;
		private EditText mConnectionTimeout, mKitViewIpEditText, mKitViewPortEditText, 
		mStreamingIpEditText, mStreamingPortEditText,mAndroidPortEditText, mRemoteServerEditText;

		private ImageView mKitViewIpSearchImageView,mStreamingIpSearchImageView;
		private Spinner mMultiComputersSpinner;

		private CustomAdapter mMultiComputersAdapter;
		private LinearLayout mMultiPostesLinearLayout, mRemoteServerLinearLayout, mKitViewLocalLinearLayout,mStreamingLinearLayout,mAndroidPortLinearLayout;
		private ImageView mMultiComputersRefresh;

		private Button mRestoreButton;

		private boolean mFirstTimeEventMultiPosteSpinner;

		private boolean mFirstTimeEventKitViewIpEditTextLaunched = true;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_settings_initialisation, container, false);

			this.initializeRemoteServerEditText(view);
			this.initializeConnectionTimeout(view);
			this.initializeMultiComputersModes(view);
			this.initializeKitViewIp(view);
			this.initializeKitViewPort(view);
			this.initializeStreamingSwitch(view);
			this.initializeStreamingIp(view);
			this.initializeStreamingPort(view);
			this.initializeAndroidPort(view);
			this.initializeRestoreButton(view);

			// TODO voir pour les serveurs distants en mode patient
			boolean patientMode = (mPersistenceManager.getMode() == PersistenceManager.MODE_PATIENT);

			//mDoctorEmailLinearLayout.setVisibility(patientMode?View.VISIBLE:View.GONE);

			mRemoteServerLinearLayout.setVisibility(patientMode?View.VISIBLE:View.GONE);

			mKitViewLocalLinearLayout.setVisibility(patientMode?View.GONE:View.VISIBLE);

			mStreamingLinearLayout.setVisibility(patientMode?View.GONE:View.VISIBLE);

			mAndroidPortLinearLayout.setVisibility(patientMode?View.GONE:View.VISIBLE);

			if(mPersistenceManager.getMode() == PersistenceManager.MODE_PATIENT)mMultiPostesLinearLayout.setVisibility(View.GONE);
			else mMultiPostesLinearLayout.setVisibility(View.VISIBLE);

			return view;
		}

		private void initializeRemoteServerEditText(View view){
			mRemoteServerLinearLayout = (LinearLayout) view.findViewById(R.id.ll_remoteserver);
			mRemoteServerEditText = (EditText) view.findViewById(R.id.et_remoteserver);

			if(mPersistenceManager != null){
				mRemoteServerEditText.setText(mPersistenceManager.getRemoteServerAdress(false));

				mRemoteServerEditText.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

					@Override
					public void afterTextChanged(Editable s) {
						mPersistenceManager.setRemoteServerAdress(mRemoteServerEditText.getText().toString());
					}
				});	
			}	
		}



		/*private void refreshMultiComputers(){
			ArrayList<Subscriber> subscribers = KitviewUtil.GetSubscribers(getActivity());
			mPersistenceManager.setSubscribers(subscribers);

			int nbSubribers = (subscribers != null)?subscribers.size():0;

			if(nbSubribers > 0){
				mPersistenceManager.setSubscriberIndex(0);
			}else mPersistenceManager.setSubscriberIndex(-1);

			updateMultiPostes(subscribers);
		}*/

		private void initializeMultiComputersModes(View view){
			this.mMultiPostesLinearLayout = (LinearLayout) view.findViewById(R.id.multicomputers_ll);

			if(mPersistenceManager.getMode() == PersistenceManager.MODE_PATIENT)mMultiPostesLinearLayout.setVisibility(View.GONE);
			else mMultiPostesLinearLayout.setVisibility(View.VISIBLE);

			this.mMultiComputersRefresh = (ImageView) view.findViewById(R.id.iv_refresh_multicomputers);
			this.mMultiComputersRefresh.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					mMultiComputersSpinner.setVisibility(View.INVISIBLE);

					//refreshMultiComputers();
					ArrayList<Subscriber> subscribers = KitviewUtil.GetSubscribers(getActivity());
					mPersistenceManager.setSubscribers(subscribers);

					int nbSubscribers = (subscribers != null)?subscribers.size():0;
					int index = (nbSubscribers > 0)?0:-1;

					mPersistenceManager.setSubscriberIndex(index);

					updateMultiPostes(subscribers);

				}
			});

			this.mFirstTimeEventMultiPosteSpinner = true;

			this.mMultiComputersSpinner = (Spinner) view.findViewById(R.id.multicomputers_spinner);

			final ArrayList<String> mMultiComputersModes = new ArrayList<String>();

			ArrayList<Subscriber> subscribers = mPersistenceManager.getSubscribers();
			int nbSubscribers = (subscribers != null)?subscribers.size():0;

			if(nbSubscribers > 0){
				for(int i=0;i<nbSubscribers;i++){
					mMultiComputersModes.add(subscribers.get(i).getmName());
				}
				mMultiComputersSpinner.setVisibility(View.VISIBLE);
			}else{
				mMultiComputersSpinner.setVisibility(View.INVISIBLE);
			}

			final int nbMultiComputersModes = (mMultiComputersModes != null)?mMultiComputersModes.size():0;
			String [] multiComputersModesModesArray = new String[nbMultiComputersModes];

			for(int i=0;i<nbMultiComputersModes;i++){
				multiComputersModesModesArray[i] = mMultiComputersModes.get(i).toString();
			}

			mMultiComputersAdapter =  new CustomAdapter(getActivity(), subscribers);

			if(mMultiComputersAdapter != null){
				mMultiComputersAdapter.setDropDownViewResource(R.layout.spinner_rows);//android.R.layout.simple_spinner_dropdown_item);
				mMultiComputersSpinner.setAdapter(mMultiComputersAdapter);
			}

			mMultiComputersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					if(!mFirstTimeEventMultiPosteSpinner){
						//Log.e("onItemSelected","mFirstTimeEventMultiPosteSpinner:"+mFirstTimeEventMultiPosteSpinner);
						mPersistenceManager.setSubscriberIndex(arg2);

						/*ArrayList<Subscriber> subscribers = mPersistenceManager.getInstance().getSubscribers();
						int nbSubscribers = (subscribers != null)?subscribers.size():0;

						Subscriber currentSubscriber = (arg2 >= 0 && arg2 < nbSubscribers)?subscribers.get(arg2):null;

						if(currentSubscriber != null){
							String newIp = currentSubscriber.getmHost();
							int newPort = currentSubscriber.getmPort();

							//KitviewUtil.updateInstanceConnection(newIp, newPort);
						}*/
					}
					mFirstTimeEventMultiPosteSpinner = false;
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});

			updateMultiPostes(mPersistenceManager.getSubscribers());
		}	

		public void updateMultiPostes(ArrayList<Subscriber> subscribers){
			final ArrayList<String> mMultiComputersModes = new ArrayList<String>();

			int nbSubscribers = (subscribers != null)?subscribers.size():0;

			if(nbSubscribers > 0){
				for(int i=0;i<nbSubscribers;i++){
					mMultiComputersModes.add(subscribers.get(i).getmName());
				}

				int index = 0;

				mMultiComputersSpinner.setSelection(index);
				mMultiComputersSpinner.setVisibility(View.VISIBLE);
			}else mMultiComputersSpinner.setVisibility(View.INVISIBLE);

			final int nbMultiComputersModes = (mMultiComputersModes != null)?mMultiComputersModes.size():0;
			String [] multiComputersModesModesArray = new String[nbMultiComputersModes];

			for(int i=0;i<nbMultiComputersModes;i++){
				multiComputersModesModesArray[i] = mMultiComputersModes.get(i).toString();
			}

			mMultiComputersAdapter =  new CustomAdapter(getActivity(), subscribers);

			if(mMultiComputersAdapter != null){
				mMultiComputersAdapter.setDropDownViewResource(R.layout.spinner_rows);
				mMultiComputersSpinner.setAdapter(mMultiComputersAdapter);
				mMultiComputersSpinner.requestLayout();
				mMultiComputersSpinner.invalidate();
			}

			int subscriberIndex = mPersistenceManager.getSubscriberIndex();//DisplayModeMyCase();//ShakeSensibility();
			if(subscriberIndex != -1)mMultiComputersSpinner.setSelection(subscriberIndex);
		}

		private void initializeConnectionTimeout(View view){
			this.mConnectionTimeout = (EditText) view.findViewById(R.id.et_connection_timeout);

			if(mConnectionTimeout != null){
				this.mConnectionTimeout.addTextChangedListener(new TextWatcher(){
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,int after){}

					@Override
					public void afterTextChanged(Editable s) {
						try{
							int timeout = Integer.parseInt(mConnectionTimeout.getText().toString());

							if(mConnectionTimeout.getText() != null)mPersistenceManager.setConnectionTimeout(timeout);
						}catch(NumberFormatException e){
							e.printStackTrace();
						}
					}
				});
				mConnectionTimeout.setText(""+mPersistenceManager.getConnectionTimeout());
			}
		}

		private void initializeKitViewIp(View view){
			this.mKitViewLocalLinearLayout = (LinearLayout) view.findViewById(R.id.ll_kitview_parent);

			this.mKitViewIpSearchImageView = (ImageView) view.findViewById(R.id.iv_kitview_ip);
			this.mKitViewIpSearchImageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v){
					Intent intent = new Intent(getActivity().getApplicationContext(), ActivityDiscovery.class);

					if(intent != null){
						try{
							int port = Integer.parseInt(mKitViewPortEditText.getText().toString());
							int portStreaming = Integer.parseInt(mStreamingPortEditText.getText().toString());

							intent.putExtra(ActivityDiscovery.EXTRA_KEY_PORT, port);
							intent.putExtra(ActivityDiscovery.EXTRA_KEY_STREAMING_PORT, portStreaming);

							getActivity().startActivityForResult(intent, ActivityDiscovery.RESULT_IP);
						}catch(NumberFormatException e){
							e.printStackTrace();
						}
					}
				}
			});

			this.mKitViewIpEditText = (EditText) view.findViewById(R.id.et_kitview_ip);
			this.mKitViewIpEditText.setText("");

			if(mPersistenceManager != null && this.mKitViewIpEditText != null){
				this.mKitViewIpEditText.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

					@Override
					public void afterTextChanged(Editable s) {
						if(mKitViewIpEditText.getText() != null){
							if(!mFirstTimeEventKitViewIpEditTextLaunched){
								String ip = mKitViewIpEditText.getText().toString().trim();
								mPersistenceManager.setMachineIp(ip);

								updateStreamingIp(ip);
							}
							mFirstTimeEventKitViewIpEditTextLaunched = false;
						}	
					}
				});
				
				String host = mPersistenceManager.getMachineIp(false);

				if(host != ""){
					//String ip = mPersistenceManager.getMachineIp(false);
					mKitViewIpEditText.setText(host);//ip);
					mKitViewIpEditText.requestLayout();
					mKitViewIpEditText.invalidate();
				}
			}
		}

		public void updateKitViewIp(String ip){
			if(mKitViewIpEditText != null){
				mKitViewIpEditText.setText(ip);
				mKitViewIpEditText.requestLayout();
				mKitViewIpEditText.invalidate();
			}
			if(mPersistenceManager != null)mPersistenceManager.setMachineIp(ip);
		}

		public void updateStreamingIp(String ip){
			if(mStreamingIpEditText != null){
				mStreamingIpEditText.setText(ip);
				mStreamingIpEditText.requestLayout();
				mStreamingIpEditText.invalidate();
			}
			if(mPersistenceManager != null)mPersistenceManager.setStreamingIP(ip);
		}

		private void initializeKitViewPort(View view){
			this.mKitViewPortEditText = (EditText) view.findViewById(R.id.et_kitview_port);	

			if(mPersistenceManager != null && mKitViewPortEditText != null){
				this.mKitViewPortEditText.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

					@Override
					public void afterTextChanged(Editable s) {
						if(mKitViewPortEditText.getText() != null && mKitViewPortEditText.getText().toString() != null &&
								mKitViewPortEditText.getText().toString() != ""){
							try{
								String rawPort = mKitViewPortEditText.getText().toString().trim();
								int port = Integer.parseInt(rawPort);
								mPersistenceManager.setMachinePort(port);

								//String ip = mPersistenceManager.getMachineIp();
							}catch(NumberFormatException e){
								e.printStackTrace();
							}
						}	
					}
				});

				if(mPersistenceManager.getMachinePort()!= -1){
					int port = mPersistenceManager.getMachinePort();
					mKitViewPortEditText.setText(""+port);
				}
			}
		}

		private void initializeStreamingSwitch(View view){
			this.mStreamingSwitch = (Switch) view.findViewById(R.id.sw_streaming);

			if(mStreamingSwitch != null && mPersistenceManager != null){
				boolean streamingEnabled = mPersistenceManager.getStreamingEnabled();
				mStreamingSwitch.setChecked(streamingEnabled);

				this.mStreamingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						mPersistenceManager.setStreamingEnabled(isChecked);
					}
				});
			}
		}

		private void initializeStreamingIp(View view){
			this.mStreamingLinearLayout = (LinearLayout) view.findViewById(R.id.ll_streaming_parent);
			this.mStreamingIpSearchImageView = (ImageView) view.findViewById(R.id.iv_streaming_ip);
			this.mStreamingIpSearchImageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity().getApplicationContext(), ActivityDiscovery.class);

					if(intent != null){
						try{
							int port = Integer.parseInt(mKitViewPortEditText.getText().toString());
							int portStreaming = Integer.parseInt(mStreamingPortEditText.getText().toString());

							intent.putExtra(ActivityDiscovery.EXTRA_KEY_PORT, port);
							intent.putExtra(ActivityDiscovery.EXTRA_KEY_STREAMING_PORT, portStreaming);

							getActivity().startActivityForResult(intent, ActivityDiscovery.RESULT_IP2);
						}catch(NumberFormatException e){
							e.printStackTrace();
						}
					}
				}
			});

			this.mStreamingIpEditText = (EditText) view.findViewById(R.id.et_streaming_ip);
			this.mStreamingIpEditText.setText("");

			if(mStreamingIpEditText != null && mPersistenceManager != null){
				this.mStreamingIpEditText.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

					@Override
					public void afterTextChanged(Editable s) {
						if(mStreamingIpEditText.getText() != null)mPersistenceManager.setStreamingIP(mStreamingIpEditText.getText().toString().trim());	
					}
				});

				String ip = mPersistenceManager.getStreamingIP(false);
				mStreamingIpEditText.setText(ip);
				mStreamingIpEditText.requestLayout();
				mStreamingIpEditText.invalidate();
			}
		}

		private void initializeStreamingPort(View view){
			this.mStreamingPortEditText = (EditText) view.findViewById(R.id.et_streaming_port);

			if(mStreamingPortEditText != null && mPersistenceManager != null){
				this.mStreamingPortEditText.addTextChangedListener(new TextWatcher() {	
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

					@Override
					public void afterTextChanged(Editable s) {
						if(mStreamingPortEditText.getText() != null && mStreamingPortEditText.getText().toString() != null &&
								mStreamingPortEditText.getText().toString() != ""){
							try{
								String rawPort = mStreamingPortEditText.getText().toString().trim();
								int port = Integer.parseInt(rawPort);
								mPersistenceManager.setStreamingPort(port);
							}catch(NumberFormatException e){
								e.printStackTrace();
							}
						}
					}
				});

				int port = mPersistenceManager.getStreamingPort();
				mStreamingPortEditText.setText(""+port);
			}
		}

		private void initializeAndroidPort(View view){
			this.mAndroidPortLinearLayout = (LinearLayout) view.findViewById(R.id.ll_android_parent);
			this.mAndroidPortEditText = (EditText) view.findViewById(R.id.et_android_port);

			if(mAndroidPortEditText != null && mPersistenceManager != null){
				this.mAndroidPortEditText.addTextChangedListener(new TextWatcher() {	
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

					@Override
					public void afterTextChanged(Editable s) {
						if(mAndroidPortEditText.getText() != null && mAndroidPortEditText.getText().toString() != null &&
								mAndroidPortEditText.getText().toString() != ""){
							try{
								String rawPort = mAndroidPortEditText.getText().toString().trim();
								int port = Integer.parseInt(rawPort);
								mPersistenceManager.setAndroidPort(port);
							}catch(NumberFormatException e){
								e.printStackTrace();
							}
						}
					}
				});

				int port = mPersistenceManager.getAndroidPort();
				mAndroidPortEditText.setText(""+port);
			}
		}

		public void initializeRestoreButton(View view){
			mRestoreButton = (Button) view.findViewById(R.id.bt_restore);
			mRestoreButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mPersistenceManager.reinit(getActivity());
					SystemUtil.restartApplication(getActivity());
				}
			});
		}
	}

	public static class SettingsExploitationFragment extends Fragment{
		private Switch mSplashScreenSwitch;
		private EditText mDoctorEmailEditText;

		private Spinner mShakeSpinner, mBgColorSpinner, mDisplayModeMyCaseSpinner;
		private ArrayAdapter<CharSequence> mShakeAdapter,mBgColorAdapter, mDisplayModeMyCaseAdapter;

		private LinearLayout  mDoctorEmailLinearLayout;
		private Button mRestoreButton;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_settings_exploitation, container, false);

			this.initializeSpashScreenSwitch(view);
			this.initializeDoctorEmail(view);
			this.initializeShakeModes(view);
			this.initializeBgColorModes(view);
			this.initializeDisplayModeMyCaseModes(view);

			boolean patientMode = (mPersistenceManager.getMode() == PersistenceManager.MODE_PATIENT);

			mDoctorEmailLinearLayout.setVisibility(patientMode?View.VISIBLE:View.GONE);

			return view;
		}

		private void initializeSpashScreenSwitch(View view){
			this.mSplashScreenSwitch = (Switch) view.findViewById(R.id.sw_splashscreen);

			if(mPersistenceManager != null){
				boolean splashScreenEnabled = mPersistenceManager.getSplashScreenEnabled();
				if(mSplashScreenSwitch != null){
					this.mSplashScreenSwitch.setChecked(splashScreenEnabled);
					this.mSplashScreenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							mPersistenceManager.setSplashScreenEnabled(isChecked);
						}
					});
				}
			}
		}



		private void initializeDoctorEmail(View view){
			mDoctorEmailLinearLayout = (LinearLayout) view.findViewById(R.id.ll_doctor_email);
			mDoctorEmailEditText = (EditText) view.findViewById(R.id.et_doctor_email);

			if(mPersistenceManager != null){
				mDoctorEmailEditText.setText(mPersistenceManager.getDoctorEmail());
				mDoctorEmailEditText.addTextChangedListener(new TextWatcher(){
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

					@Override
					public void afterTextChanged(Editable s) {
						mPersistenceManager.setDoctorEMail(mDoctorEmailEditText.getText().toString());
					}
				});
			}
		}

		private void initializeDisplayModeMyCaseModes(View view){
			this.mDisplayModeMyCaseSpinner = (Spinner) view.findViewById(R.id.displaymodemycase_spinner);

			final List<String> mDisplayModeMyCaseModes = new ArrayList<String>();
			mDisplayModeMyCaseModes.add(getActivity().getString(R.string.displaymodemycase1));
			mDisplayModeMyCaseModes.add(getActivity().getString(R.string.displaymodemycase2));

			final int nbDisplayModeMyCaseModes = (mDisplayModeMyCaseModes != null)?mDisplayModeMyCaseModes.size():0;
			String [] displayModeMyCaseModesModesArray = new String[nbDisplayModeMyCaseModes];

			for(int i=0;i<nbDisplayModeMyCaseModes;i++){
				displayModeMyCaseModesModesArray[i] = mDisplayModeMyCaseModes.get(i).toString();
			}

			mDisplayModeMyCaseAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, displayModeMyCaseModesModesArray);

			if(mDisplayModeMyCaseAdapter != null){
				mDisplayModeMyCaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				mDisplayModeMyCaseSpinner.setAdapter(mDisplayModeMyCaseAdapter);
			}

			mDisplayModeMyCaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					mPersistenceManager.setDisplayModeMyCase(arg2);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});

			int displayModeMyCase = mPersistenceManager.getDisplayModeMyCase();
			mDisplayModeMyCaseSpinner.setSelection(displayModeMyCase);
		}

		private void initializeBgColorModes(View view){
			this.mBgColorSpinner = (Spinner) view.findViewById(R.id.bgcolor_spinner);

			final List<String> mBgColorModes = new ArrayList<String>();
			mBgColorModes.add(getActivity().getString(R.string.black));
			mBgColorModes.add(getActivity().getString(R.string.grey));
			mBgColorModes.add(getActivity().getString(R.string.white));

			final int nbBgModes = (mBgColorModes != null)?mBgColorModes.size():0;
			String [] bgModesArray = new String[nbBgModes];

			for(int i=0;i<nbBgModes;i++){
				bgModesArray[i] = mBgColorModes.get(i).toString();
			}

			mBgColorAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, bgModesArray);

			if(mBgColorAdapter != null){
				mBgColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mBgColorSpinner.setAdapter(mBgColorAdapter);
			}

			mBgColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					if(arg2 == 0){
						mPersistenceManager.setBackgroundColor(Color.BLACK);//ShakeSensibility(PersistenceManager.SHAKE_SENSIBILITY_LOW);
					}else if(arg2 == 1){
						mPersistenceManager.setBackgroundColor(Color.GRAY);//setShakeSensibility(PersistenceManager.SHAKE_SENSIBILITY_NORMAL);
					}else if(arg2 == 2){
						mPersistenceManager.setBackgroundColor(Color.WHITE);//setShakeSensibility(PersistenceManager.SHAKE_SENSIBILITY_HIGH);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});

			int bgColor = mPersistenceManager.getBgColor();

			if(bgColor == Color.BLACK){
				mBgColorSpinner.setSelection(0);
			}else if(bgColor == Color.GRAY){
				mBgColorSpinner.setSelection(1);
			}else if(bgColor == Color.WHITE){
				mBgColorSpinner.setSelection(2);
			}	
		}

		private void initializeShakeModes(View view){
			this.mShakeSpinner = (Spinner)view.findViewById(R.id.sensivity_spinner);

			final List<String> mShakeModes = new ArrayList<String>();
			mShakeModes.add(getActivity().getString(R.string.sensivity_low));
			mShakeModes.add(getActivity().getString(R.string.sensivity_normal));
			mShakeModes.add(getActivity().getString(R.string.sensivity_high));

			final int nbShakeModes = (mShakeModes != null)?mShakeModes.size():0;
			String [] shakeModesArray = new String[nbShakeModes];

			for(int i=0;i<nbShakeModes;i++){
				shakeModesArray[i] = mShakeModes.get(i).toString();
			}

			mShakeAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, shakeModesArray);

			if(mShakeAdapter != null){
				mShakeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mShakeSpinner.setAdapter(mShakeAdapter);
			}

			mShakeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					if(arg2 == 0){
						mPersistenceManager.setShakeSensibility(PersistenceManager.SHAKE_SENSIBILITY_LOW);
					}else if(arg2 == 1){
						mPersistenceManager.setShakeSensibility(PersistenceManager.SHAKE_SENSIBILITY_NORMAL);
					}else if(arg2 == 2){
						mPersistenceManager.setShakeSensibility(PersistenceManager.SHAKE_SENSIBILITY_HIGH);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});

			float sensivity = mPersistenceManager.getShakeSensibility();

			if(sensivity == PersistenceManager.SHAKE_SENSIBILITY_LOW){
				mShakeSpinner.setSelection(0);
			}else if(sensivity == PersistenceManager.SHAKE_SENSIBILITY_NORMAL){
				mShakeSpinner.setSelection(1);
			}else if(sensivity == PersistenceManager.SHAKE_SENSIBILITY_HIGH){
				mShakeSpinner.setSelection(2);
			}
		}

		public void initializeRestoreButton(View view){
			mRestoreButton = (Button) view.findViewById(R.id.bt_restore);
			mRestoreButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mPersistenceManager.reinit(getActivity());
					SystemUtil.restartApplication(getActivity());
				}
			});
		}	
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		if(resultCode == RESULT_OK){
			String result = intent.getExtras().get("result").toString();

			if(requestCode == ActivityDiscovery.RESULT_IP){      
				((SettingsInitialisationFragment)(mFragments.get(0))).updateKitViewIp(result);
				((SettingsInitialisationFragment)(mFragments.get(0))).updateStreamingIp(result);

				ArrayList<Subscriber> subscribers = KitviewUtil.GetSubscribers(SettingsActivity.this);
				mPersistenceManager.setSubscribers(subscribers);

				int nbSubscribers = (subscribers != null)?subscribers.size():0;

				if(nbSubscribers > 0){
					mPersistenceManager.setSubscriberIndex(0);

				}else mPersistenceManager.setSubscriberIndex(-1);

				((SettingsInitialisationFragment)(mFragments.get(0))).updateMultiPostes(subscribers);	
			}

			if(requestCode == ActivityDiscovery.RESULT_IP2){      
				((SettingsInitialisationFragment)(mFragments.get(0))).updateStreamingIp(result);
			}
		}
	}

	private String mStartPatientRemoteServer = "";

	private String mStartCabinetKitViewPosteIp = "";
	private int mStartCabinetKitViewPostePort;


	@Override
	protected void onDestroy() {
		IS_LAUNCHED = false;

		//Log.e("onDestroy","onDestroy");
		if(mFragments != null){
			mFragments.clear();
			mFragments = null;
		}

		int mode = mPersistenceManager.getMode();
		boolean modificationHasOccured = false;

		if(mode == PersistenceManager.MODE_PATIENT){
			if(!mStartPatientRemoteServer.equals(mPersistenceManager.getRemoteServerAdress(false))){
				modificationHasOccured = true;
			}
		}else if(mode == PersistenceManager.MODE_SURGERY){
			ArrayList<Subscriber> subscribers = mPersistenceManager.getSubscribers();
			int nbSubscribers = (subscribers != null)?subscribers.size():0;
			int subscriberIndex = mPersistenceManager.getSubscriberIndex();

			if(subscriberIndex >= 0 && subscriberIndex < nbSubscribers){
				Subscriber currentSubscriber = subscribers.get(subscriberIndex);
				String subscriberIp = currentSubscriber.getmHost();
				int subscriberPort = currentSubscriber.getmHttpPort();

				if(!mStartCabinetKitViewPosteIp.equals(subscriberIp) || mStartCabinetKitViewPostePort != subscriberPort){
					modificationHasOccured = true;
				}
			}
		}

		//Need to recreate all scenarios ...
		if(modificationHasOccured){
			mPersistenceManager.setScenarios(null);//new ArrayList<Scenario>());
			mPersistenceManager.setScenarioIndex(0);//-1);
		}

		super.onDestroy();
	}
}