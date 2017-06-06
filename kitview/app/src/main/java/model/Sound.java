package model;

public class Sound {

	//Off sound1 ...
	private int mTitleId;
	private int mRawResourceId;
	
	public Sound(int mTitleId, int mRawResourceId){
		this.mTitleId = mTitleId;
		this.mRawResourceId = mRawResourceId;
	}

	public int getTitleId() {
		return mTitleId;
	}

	public void setTitleId(int mTitleId) {
		this.mTitleId = mTitleId;
	}

	public int getRawResourceId() {
		return mRawResourceId;
	}

	public void setRawResourceId(int mRawResourceId) {
		this.mRawResourceId = mRawResourceId;
	}
	
	
}
