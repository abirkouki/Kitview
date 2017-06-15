package model.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import model.PersistenceManager;

public class ScenarioItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3403117994265037912L;

	//portrait, 3/4, bucal ...
	private String mType;

	//Exploite plus tard par le systeme de classification KitView
	private String mKeywords;

	private List<Boolean> mVisibleHorLines, mVisibleVerLines;

	public ScenarioItem(String mType, String mKeywords){
		this.mType = mType;
		this.mKeywords = mKeywords;
		this.mVisibleHorLines = new ArrayList<Boolean>();

		int nbHorCoeffs = calcNbHorLines();

		for(int i=0;i<nbHorCoeffs;i++){
			mVisibleHorLines.add(true);			
		}

		this.mVisibleVerLines = new ArrayList<Boolean>();

		int nbVerCoeffs = calcNbVerLines();

		for(int i=0;i<nbVerCoeffs;i++){
			mVisibleVerLines.add(true);
		}
	}

	private int calcNbHorLines(){
		int nbHorLines = 0;
		String realKeywords = mKeywords.replace("=true", "");

		HashMap<String, String> hm = PersistenceManager.getInstance().getKeyWordsFlashFocus();	
		String value = hm.get(realKeywords);

		if(value != null){
			StringTokenizer st = new StringTokenizer(value, ";");
			int nbTokens = (st != null)?st.countTokens():0;

			if(nbTokens == 4){
				String flash = st.nextToken();
				String focus = st.nextToken();

				String hor = st.nextToken();
				hor = hor.replace("[", "");
				hor = hor.replace("]", "");

				StringTokenizer st2 = new StringTokenizer(hor, ",");
				int nbTokens2 = (st2 != null)?st2.countTokens():0;

				nbHorLines = nbTokens2;
			}
		}
		return nbHorLines;
	}

	private int calcNbVerLines(){
		int nbVerLines = 0;
		String realKeywords = mKeywords.replace("=true", "");

		HashMap<String, String> hm = PersistenceManager.getInstance().getKeyWordsFlashFocus();	
		String value = hm.get(realKeywords);

		if(value != null){
			StringTokenizer st = new StringTokenizer(value, ";");
			int nbTokens = (st != null)?st.countTokens():0;

			if(nbTokens == 4){
				String flash = st.nextToken();
				String focus = st.nextToken();

				String hor = st.nextToken();
				String ver = st.nextToken();

				ver = ver.replace("[", "");
				ver = ver.replace("]", "");

				StringTokenizer st2 = new StringTokenizer(ver, ",");
				int nbTokens2 = (st2 != null)?st2.countTokens():0;

				nbVerLines = nbTokens2;
			}
		}
		return nbVerLines;
	}


	public String getType(){
		return this.mType;
	}

	public String getKeywords(){
		return this.mKeywords;
	}

	public String getFocus(){
		String realKeywords = mKeywords.replace("=true", "");
		String focus = "";
		HashMap<String, String> hm = PersistenceManager.getInstance().getKeyWordsFlashFocus();	
		String value = hm.get(realKeywords);

		if(value != null){
			StringTokenizer st = new StringTokenizer(value, ";");
			int nbTokens = (st != null)?st.countTokens():0;

			if(nbTokens >= 2){
				st.nextToken();
				focus = st.nextToken();
			}
		}
		return focus;
	}

	public void setFocus(String newFocus){
		String realKeywords = mKeywords.replace("=true", "");
		HashMap<String, String> hm = PersistenceManager.getInstance().getKeyWordsFlashFocus();	
		String value = hm.get(realKeywords);

		if(value != null){
			StringTokenizer st = new StringTokenizer(value, ";");
			int nbTokens = (st != null)?st.countTokens():0;

			if(nbTokens == 4){
				String flash = st.nextToken();
				String focus = st.nextToken();
				String hor = st.nextToken();
				String ver = st.nextToken();

				hm.put(realKeywords, flash+";"+newFocus+";"+hor+";"+ver);	
				PersistenceManager.getInstance().setKeywordsFlashFocus(hm);
			}
		}
	}


	public String getFlash(){
		String realKeywords = mKeywords.replace("=true", "");

		String flash = "";
		HashMap<String, String> hm = PersistenceManager.getInstance().getKeyWordsFlashFocus();	
		String value = hm.get(realKeywords);

		if(value != null){
			StringTokenizer st = new StringTokenizer(value, ";");
			int nbTokens = (st != null)?st.countTokens():0;

			if(nbTokens >= 1){
				flash = st.nextToken();
			}
		}
		return flash;
	}

	public void setFlash(String newFlash){
		String realKeywords = mKeywords.replace("=true", "");

		HashMap<String, String> hm = PersistenceManager.getInstance().getKeyWordsFlashFocus();	
		String value = hm.get(realKeywords);

		if(value != null){
			StringTokenizer st = new StringTokenizer(value, ";");
			int nbTokens = (st != null)?st.countTokens():0;

			if(nbTokens == 4){
				st.nextToken();
				String focus = st.nextToken();
				String hor = st.nextToken();
				String ver = st.nextToken();
				hm.put(realKeywords, newFlash+";"+focus+";"+hor+";"+ver);	
				PersistenceManager.getInstance().setKeywordsFlashFocus(hm);
			}
		}
	}

	public List<Float> getHorizontalCoeffs(){
		ArrayList<Float> horCoeffs = new ArrayList<Float>();
		String realKeywords = mKeywords.replace("=true", "");

		HashMap<String, String> hm = PersistenceManager.getInstance().getKeyWordsFlashFocus();	
		String value = hm.get(realKeywords);

		if(value != null){
			StringTokenizer st = new StringTokenizer(value, ";");
			int nbTokens = (st != null)?st.countTokens():0;

			if(nbTokens == 4){
				String flash = st.nextToken();
				String focus = st.nextToken();

				String hor = st.nextToken();
				hor = hor.replace("[", "");
				hor = hor.replace("]", "");

				StringTokenizer st2 = new StringTokenizer(hor, ",");
				int nbTokens2 = (st2 != null)?st2.countTokens():0;

				for(int i=0;i<nbTokens2;i++){
					horCoeffs.add(new Float(st2.nextToken()));
				}
			}
		}
		return horCoeffs;
	}

	public List<Float> getVerticalCoeffs(){
		ArrayList<Float> verCoeffs = new ArrayList<Float>();
		String realKeywords = mKeywords.replace("=true", "");

		HashMap<String, String> hm = PersistenceManager.getInstance().getKeyWordsFlashFocus();	
		String value = hm.get(realKeywords);

		if(value != null){
			StringTokenizer st = new StringTokenizer(value, ";");
			int nbTokens = (st != null)?st.countTokens():0;

			if(nbTokens == 4){
				String flash = st.nextToken();
				String focus = st.nextToken();

				String hor = st.nextToken();
				String ver = st.nextToken();
				ver = ver.replace("[", "");
				ver = ver.replace("]", "");

				StringTokenizer st2 = new StringTokenizer(ver, ",");
				int nbTokens2 = (st2 != null)?st2.countTokens():0;

				for(int i=0;i<nbTokens2;i++){
					verCoeffs.add(new Float(st2.nextToken()));
				}
			}
		}
		return verCoeffs;
	}

	public void setHorizontalCoeffs(List<Float> mHorCoeffs){
		String realKeywords = mKeywords.replace("=true", "");

		HashMap<String, String> hm = PersistenceManager.getInstance().getKeyWordsFlashFocus();	
		String value = hm.get(realKeywords);

		if(value != null){
			StringTokenizer st = new StringTokenizer(value, ";");
			int nbTokens = (st != null)?st.countTokens():0;

			if(nbTokens == 4){
				String flash = st.nextToken();
				String focus = st.nextToken();

				String hor = st.nextToken();
				String ver = st.nextToken();

				hm.put(realKeywords, flash+";"+focus+";"+mHorCoeffs.toString()+";"+ver);

				PersistenceManager.getInstance().setKeywordsFlashFocus(hm);
			}
		}
	}

	public void setVerticalCoeffs(List<Float> mVerCoeffs){
		String realKeywords = mKeywords.replace("=true", "");

		HashMap<String, String> hm = PersistenceManager.getInstance().getKeyWordsFlashFocus();	
		String value = hm.get(realKeywords);

		if(value != null){
			StringTokenizer st = new StringTokenizer(value, ";");
			int nbTokens = (st != null)?st.countTokens():0;

			if(nbTokens == 4){
				String flash = st.nextToken();
				String focus = st.nextToken();
				String hor = st.nextToken();

				hm.put(realKeywords, flash+";"+focus+";"+hor+";"+mVerCoeffs.toString());

				PersistenceManager.getInstance().setKeywordsFlashFocus(hm);
			}
		}
	}

	public List<Boolean> getVisibleHorizontalLines(){
		return this.mVisibleHorLines;
	}

	public List<Boolean> getVisibleVerticalLines(){
		return this.mVisibleVerLines;
	}

	public void setVisibleHorizontalLines(List<Boolean> visibleHorizontalLines){
		this.mVisibleHorLines = visibleHorizontalLines;
	}

	public void setVisibleVerticalLines(List<Boolean> visibleVerticalLines){
		this.mVisibleVerLines = visibleVerticalLines;
	}
}