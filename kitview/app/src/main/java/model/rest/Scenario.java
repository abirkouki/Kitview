package model.rest;

import java.io.Serializable;
import java.util.ArrayList;

public class Scenario implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3759726535962696992L;
	private String mNom;
	private ArrayList<ScenarioItem> mItems;
	
	public Scenario(String mNom){
		this.mNom = mNom;
		this.mItems = new ArrayList<ScenarioItem>();
	}
	
	public void addScenarioItem(ScenarioItem scenarioItem){
		this.mItems.add(scenarioItem);
	}
	
	public String getNom(){
		return this.mNom;
	}
	
	public ScenarioItem getScenarioItemAt(int index){
		int nbItems = (this.mItems != null)?this.mItems.size():0;
		return (index >= 0 && index < nbItems)?this.mItems.get(index):null;
	}
	
	public int getNbScenarioItems(){
		return (this.mItems != null)?this.mItems.size():0;
	}
}