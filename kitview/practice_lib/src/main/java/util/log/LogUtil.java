package util.log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.file.FileUtil;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class LogUtil {
	private final static String LOG_FILE_NAME = "log_%s.txt";

	private static LogUtil mLogUtilInstance;
	private String mLogFileName;

	public LogUtil(){
		createLog();
	}

	public static LogUtil getInstance(){
		if(mLogUtilInstance == null){
			mLogUtilInstance = new LogUtil();
		}

		return mLogUtilInstance;
	}

	private static File mLogFile = null;

	public File getLog(){
		return mLogFile;
	}
	
	private void createLog(){
		String date = new SimpleDateFormat("dd_MM_yyyy HH_mm_ss").format(new Date());

		mLogFileName = Environment.getExternalStorageDirectory()+"/"+LOG_FILE_NAME.replace("%s", date);

		boolean fileCreated = FileUtil.createFile(mLogFileName);

		if(fileCreated)mLogFile = new File(mLogFileName);
	}

	public void insertDataInLog(Context context, String methodNameAndAttributs){
		Log.e("infos",""+methodNameAndAttributs);
		String now = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
		String newLine = now+";"+methodNameAndAttributs;

		try{
			FileWriter fw = new FileWriter(mLogFile,true);
			fw.append(newLine+"\n");
			fw.close();

			MediaScannerConnection.scanFile(context, new String[] {mLogFile.toString()}, null, null);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}