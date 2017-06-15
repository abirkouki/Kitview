package util.camera;

import java.util.List;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

public class CameraUtil {
	public static List<String> getSupportedFlashModes(Camera c){
		List<String> flashModes = null;

		if(c != null){

			Parameters params = c.getParameters();

			if(params != null){
				flashModes = params.getSupportedFlashModes();
			}

		}

		return flashModes;
	}

	public static List<String> getSupportedFocusModes(Camera c){
		List<String> focusModes = null;	
		if(c != null){
			Parameters params = c.getParameters();

			if(params != null){
				focusModes = params.getSupportedFocusModes();
			}
		}
		return focusModes;
	}


}
