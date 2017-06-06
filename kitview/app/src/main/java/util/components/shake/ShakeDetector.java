package util.components.shake;

import model.PersistenceManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {
	//public final static float SPEED_VECTOR_THRESHOLD = 6.0f;
	
	public static final int TIME_THRESHOLD = 25;

	private float[] gravity = new float[3],linear_acceleration = new float[3];

	private Sensor mAccelerometer;

	private long mLastTime,now,last_shake_time = -1;
	private boolean mShakeNotSupported = false;

	private float delay_between_two_shake = 1500.0f;

	private onShakeInterface mInterface;

	private SensorManager sensorManager;

	private Context mContext;
	
	public ShakeDetector(Context mContext){
		super();

		this.mContext = mContext;
		
		this.sensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);//sensorManager;
	}

	public void initialize(){
		if(sensorManager != null) {
			mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
	}

	public static boolean isShaking(float speed_vector_norm){
		return (speed_vector_norm >= PersistenceManager.getInstance().getShakeSensibility());//SPEED_VECTOR_THRESHOLD;
	}

	public void setInterface(onShakeInterface mInterface){
		this.mInterface = mInterface;
	}

	private boolean mHasBeenRegistered = false;

	public void handleRegister(){
		if(mShakeNotSupported)return;

		if(sensorManager == null) {
			doShakeNotSupported();
			return;
		}

		if(!mHasBeenRegistered){
			this.initialize();

			boolean supported = sensorManager.registerListener(this,mAccelerometer, SensorManager.SENSOR_DELAY_UI);//SENSOR_DELAY_UI

			mHasBeenRegistered = supported;

			if (!supported) {
				sensorManager.unregisterListener(this);
				doShakeNotSupported();
			}
		}
	}

	public void handleUnregister(){
		if(!mShakeNotSupported) {
			sensorManager.unregisterListener(this);

			mHasBeenRegistered = false;
		}
		
		this.mContext = null;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		now = System.currentTimeMillis();

		if ((now - mLastTime) > TIME_THRESHOLD) {
			float alpha = 0.8f;

			gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
			gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
			gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

			linear_acceleration[0] = event.values[0] - gravity[0];
			linear_acceleration[1] = event.values[1] - gravity[1];
			linear_acceleration[2] = event.values[2] - gravity[2];

			float d = (float) Math.sqrt(linear_acceleration[0]*linear_acceleration[0]+linear_acceleration[1]*linear_acceleration[1]+linear_acceleration[2]*linear_acceleration[2]);

			if(ShakeDetector.isShaking(d)){
				if(last_shake_time == -1){
					last_shake_time = now;
					doShake();
				}else{
					if((now - last_shake_time) > delay_between_two_shake){
						last_shake_time = now;
						doShake();
					}
				}
			}

			mLastTime = now;
		}
	}

	public void doShake() {
		if(this.mInterface != null){
			this.mInterface.setOnShakeEvent();
		}
	}

	public void doShakeNotSupported() {
		if(!mShakeNotSupported) {
			mShakeNotSupported = true;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	public interface onShakeInterface{
		public void setOnShakeEvent();
	}
}