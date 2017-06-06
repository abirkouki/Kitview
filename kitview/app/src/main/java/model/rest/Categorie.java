package model.rest;

import java.util.List;

public class Categorie{
	private String mType;
	private String mRelativePath;
	private List<String> mPicturesPath;
	private int mLevel;

	public Categorie(String mType, String mPath, List<String> mPicturesPath, int mLevel){
		this.mType = mType;
		this.mRelativePath = mPath;
		this.mPicturesPath = mPicturesPath;
		this.mLevel = mLevel;
	}	

	public String getType(){
		return this.mType;
	}

	public String getRelativePath(){
		return this.mRelativePath;
	}

	public List<String> getPicturesPath(){
		return this.mPicturesPath;
	}
	
	public String getPictureAt(int index){
		int nbPictures = (this.mPicturesPath != null)?this.mPicturesPath.size():0;
		
		if(index >= 0 && index < nbPictures){
			return this.mPicturesPath.get(index);
		}else return null;
		
		
	}
	
	public int getLevel(){
		return this.mLevel;
	}
}