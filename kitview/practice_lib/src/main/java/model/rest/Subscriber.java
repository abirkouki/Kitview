package model.rest;

import android.content.Context;
import android.graphics.Color;

public class Subscriber {

	public static final int BLACK       = 0xFF000000;
	public static final int DKGRAY      = 0xFF444444;
	public static final int GRAY        = 0xFF888888;
	public static final int LTGRAY      = 0xFFCCCCCC;
	public static final int WHITE       = 0xFFFFFFFF;
	public static final int RED         = 0xFFFF0000;
	public static final int GREEN       = 0xFF00FF00;
	public static final int BLUE        = 0xFF0000FF;
	public static final int YELLOW      = 0xFFFFFF00;
	public static final int CYAN        = 0xFF00FFFF;
	public static final int MAGENTA     = 0xFFFF00FF;
	public static final int TRANSPARENT = 0;


	private static int [] colors = new int[]{Color.RED,Color.CYAN,Color.YELLOW,Color.BLUE,Color.GREEN,Color.RED,Color.WHITE,Color.LTGRAY,
		Color.GRAY,Color.DKGRAY,Color.BLACK,Color.MAGENTA,Color.RED,Color.RED,Color.RED,Color.RED,Color.RED,Color.RED,
		Color.RED,Color.RED};

	private int mIndex;
	private String mName;
	private String mHost;
	private int mPort;
	private int mHttpPort;

	public Subscriber(int indexSubscriber, String mName, String mHost, int mPort, int mHttpPort){
		this.mIndex = indexSubscriber;
		this.mName = mName;
		this.mHost = mHost;
		this.mPort = mPort;
		this.mHttpPort = mHttpPort;
	}


	public int getmIndex() {
		return mIndex;
	}

	public int getColor(){
		int defaultColor = Color.BLACK; 
		int nbColors = (colors != null)?colors.length:0;

		if(mIndex >= 1 && mIndex <= nbColors){
			defaultColor = colors[mIndex-1];
		}
		return defaultColor;
	}

	public void setmIndex(int mIndex) {
		this.mIndex = mIndex;
	}



	public int getmHttpPort() {
		return mHttpPort;
	}

	public void setmHttpPort(int mHttpPort) {
		this.mHttpPort = mHttpPort;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getmHost() {
		return mHost;
	}

	public void setmHost(String mHost) {
		this.mHost = mHost;
	}

	public int getmPort() {
		return mPort;
	}

	public void setmPort(int mPort) {
		this.mPort = mPort;
	}


}
