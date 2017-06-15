package util.components.multileveltreelist;

import java.util.ArrayList;

public class Entity{
	public String Name;
	public String IdParent;
	public String Id;
	
	public String AbsolutePath;
	public boolean HasChild;
	public int level;
	public boolean isOpened;
	
	public Entity mParent;
	
	public ArrayList<Entity> mChilds = new ArrayList<Entity>();
	
	public int nbSubFolders;
	
	
	
	public void reinitChilds(){
		mChilds.clear();
	}
	public void addChild(Entity e){
		mChilds.add(e);
	}
	
	
}
