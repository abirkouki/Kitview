package util.components.scanner;

import java.io.File;
import java.util.ArrayList;
import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MyMediaScannerConnectionClient implements MediaScannerConnectionClient{
	private MediaScannerConnection mConn;
	private String[] fileNames;
	private Context context;

	public MyMediaScannerConnectionClient(String [] fileNames, Context context){ 
		this.context = context;
		this.fileNames = fileNames;

		try{
			if(context != null){
				mConn = new MediaScannerConnection(context, this);
				if(mConn != null)mConn.connect();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void onMediaScannerConnected() {
		ArrayList<String> fileNamesOK = new ArrayList<String>();
		int nbFilesToTest = (fileNames != null)?fileNames.length:0;
		File currentFile = null;

		for(int i=0;i<nbFilesToTest;i++){
			currentFile = new File(fileNames[i]);

			if(currentFile.canRead() && currentFile.exists() && currentFile.length() > 0 && currentFile.isFile()){
				fileNamesOK.add(fileNames[i]);
			}
		}

		int nbFilesOK = (fileNamesOK != null)?fileNamesOK.size():0;

		if(nbFilesOK > 0){
			String [] fileNamesTested = new String[nbFilesOK];
			fileNamesOK.toArray(fileNamesTested);

			mConn.scanFile(context, fileNamesTested,null,null);
			context = null;
			mConn = null;	
		}
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		if(mConn != null){
			mConn.disconnect();
			mConn = null;
		}
		this.context = null;
	}    
}