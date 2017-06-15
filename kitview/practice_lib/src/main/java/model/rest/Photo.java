package model.rest;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable{
	private String mId;
	private String mPatientId;
	private String mCollectionId;
	private Date mDateInsertion;
	

	public Photo(String mId, String mPatientId, String mCollectionId, Date mDateInsertion){
		this.mId = mId;
		this.mPatientId = mPatientId;
		this.mCollectionId = mCollectionId;
		this.mDateInsertion = mDateInsertion;
	}

	public String getId(){
		return this.mId;
	}

	public void setId(String mId){
		this.mId = mId;
	}

	public String getPatientId(){
		return this.mPatientId;
	}

	public void setPatientId(String mPatientId){
		this.mPatientId = mPatientId;
	}

	public String getCollectionId(){
		return this.mCollectionId;
	}

	public void setCollectionId(String mCollectionId){
		this.mCollectionId = mCollectionId;
	}
	
	public void setCollectionDateInsertion(Date dateInsertion){
		this.mDateInsertion = dateInsertion;
	}

	public Date getCollectionDateCreation(){
		return this.mDateInsertion;
	}
	
	

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mId);
		dest.writeString(mPatientId);
		dest.writeString(mCollectionId);
	}
}
