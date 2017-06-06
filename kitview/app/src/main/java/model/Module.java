package model;

public class Module{
	private int mTextId;
	private int mBackgroundColorId;
	private int mDrawableId;
	
	public Module(int mTextId, int mBackgroundColorId, int mDrawableId){
		this.mTextId = mTextId;
		this.mBackgroundColorId = mBackgroundColorId;
		this.mDrawableId = mDrawableId;
	}

	public int getNameId(){
		return this.mTextId;
	}

	public int getBackgroundColorId(){
		return this.mBackgroundColorId;
	}
	
	public int getDrawableId(){
		return this.mDrawableId;
	}
}