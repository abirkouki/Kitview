package model.rest;

import java.util.Date;
import java.util.StringTokenizer;

public class Personne {
	private int mId;
	private Date mDateNaiss;
	private String mFirstName;
	private String mLastName;
	private String mRef1,mRef2;

	public Personne(int mId, Date mDateNaiss, String mFirstName, String mLastName){
		this.mId = mId;
		this.mDateNaiss = mDateNaiss;
		this.mFirstName = mFirstName;
		this.mLastName = mLastName;
	}

	public int getId() {
		return mId;
	}

	public String getRef1(){
		return this.mRef1;
	}
	
	public String getRef2(){
		return this.mRef2;
	}

	public void setRef1(String ref1){
		this.mRef1 = ref1;
	}
	
	public void setRef2(String ref2){
		this.mRef2 = ref2;
	}

	public int getFamillyValue(){
		int familly = -1;

		StringTokenizer st = new StringTokenizer(this.mRef1,"=");

		int nbTokens = (st != null)?st.countTokens():0;

		if(nbTokens == 2){
			st.nextToken();

			try{
				familly = Integer.parseInt(st.nextToken());
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
		}

		return familly;
	}

	public String getFamillyField(){
		String famillyField = null;

		StringTokenizer st = new StringTokenizer(this.mRef1,"=");

		int nbTokens = (st != null)?st.countTokens():0;

		if(nbTokens == 2){
			try{
				famillyField = st.nextToken();
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
		}

		return famillyField;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public Date getDateNaiss() {
		return mDateNaiss;
	}

	public void setDateNaiss(Date mDateNaiss) {
		this.mDateNaiss = mDateNaiss;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public void setFirstName(String mFirstName) {
		this.mFirstName = mFirstName;
	}

	public String getLastName() {
		return mLastName;
	}

	public void setLastName(String mLastName) {
		this.mLastName = mLastName;
	}
}
