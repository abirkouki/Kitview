package view.popup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import model.PersistenceManager;
import model.rest.Personne;
import model.rest.Photo;
import util.components.multileveltreelist.Entity;
import util.file.FileUtil;
import util.image.ImageUtil;
import util.network.KitviewUtil;
import util.network.KitviewUtil.Collection;
import util.system.SystemUtil;
import activity.FolderActivity;
import activity.MainActivity;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.dentalcrm.kitview.R;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CollectionsPopupManager {
	private List<Photo> photos;

	private FolderActivity mContext;

	private Dialog mCollectionsDialog;
	private LinearLayout mParentLinearLayout;
	private ListView mCollectionsListView;
	private List<util.network.KitviewUtil.Collection> mListCollections;
	private ArrayList<Entity> arrTrades;
	private util.components.multileveltreelist.ListAdapter adapter;

	private Spinner mCollectionCategoriesSpinner;
	private ArrayAdapter<CharSequence> mCollectionCategoriesAdapter;

	private int mPatientId;
	private Personne personne;

	private boolean mHideDialog;

	private boolean mFirstTimeSpinnerEvent = true;

	private boolean mFirstTimeLoad = true;

	public CollectionsPopupManager(FolderActivity mContext){
		this.mContext = mContext;	
	}

	public ArrayList<Entity> initializePopup(final int mPatientId){
		this.mPatientId = mPatientId;
		personne = KitviewUtil.getPersonneFromId(mContext, mPatientId);

		this.arrTrades = new ArrayList<Entity>();

		if(mContext != null){
			this.mCollectionsDialog = new Dialog(mContext);

			if(this.mCollectionsDialog != null){
				this.mCollectionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				this.mCollectionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				this.mCollectionsDialog.setContentView(R.layout.popup_collections);

				mHideDialog = true;
				hidePopup();

				this.mParentLinearLayout = (LinearLayout) this.mCollectionsDialog.findViewById(R.id.action_adobe_reader_appinit_dialog);

				this.mCollectionsListView = (ListView) this.mCollectionsDialog.findViewById(R.id.listview);

				this.mCollectionCategoriesSpinner = (Spinner) this.mCollectionsDialog.findViewById(R.id.sp_collection_categories);

				final ArrayList<Collection> l = KitviewUtil.getCollectionCategories(mContext);
				int nb = (l != null)?l.size():0;

				if(nb > 0){
					final CharSequence [] vues = new CharSequence[nb];

					for(int i=0;i<nb;i++){
						vues[i] = l.get(i).getName();
					}

					this.mCollectionCategoriesAdapter = new ArrayAdapter<CharSequence>(mContext, android.R.layout.simple_spinner_item, vues);

					if(this.mCollectionCategoriesAdapter != null){
						this.mCollectionCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

						if(this.mCollectionCategoriesSpinner != null){
							this.mCollectionCategoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
								@Override
								public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
									if(mFirstTimeSpinnerEvent)mHideDialog = false;
									else mHideDialog = true;

									mFirstTimeSpinnerEvent = false;

									try{
										if(l != null && position >= 0 && position < l.size()){
											int currentVue = Integer.parseInt(l.get(position).getId());//Integer.parseInt(vues[position].toString());

											KitviewUtil.getCollections(mContext, mPatientId, currentVue, new KitviewUtil.ICollectionArrayListResponse() {
												@Override
												public void onResponse(final List<Collection> collections) {
													mListCollections = collections;

													mContext.runOnUiThread(new Runnable() {
														@Override
														public void run() {
															initializeCategoriesListView(collections);
														}
													});
												}
											});
										}
									}catch(NumberFormatException e){
										if(e != null)e.printStackTrace();
									}

									try{
										if(l != null && position >= 0 && position < l.size()){
											int currentVue = Integer.parseInt(l.get(position).getId());

											KitviewUtil.getCollections(mContext, mPatientId, currentVue, new KitviewUtil.ICollectionArrayListResponse() {
												@Override
												public void onResponse(final List<Collection> collections) {
													mListCollections = collections;

													mContext.runOnUiThread(new Runnable() {
														@Override
														public void run() {
															initializeCategoriesListView(collections);
														}
													});
												}
											});
										}
									}catch(NumberFormatException e){
										if(e != null)e.printStackTrace();
									}
								}
								@Override
								public void onNothingSelected(AdapterView<?> parent) {}
							});
							this.mCollectionCategoriesSpinner.setAdapter(this.mCollectionCategoriesAdapter);
							this.mCollectionCategoriesSpinner.setSelection(0, true);
							this.mCollectionCategoriesSpinner.requestLayout();
							this.mCollectionCategoriesSpinner.invalidate();
						}
					}
				}
			}
		}
		return arrTrades;
	}

	private List<Collection> listFiles(String parentId){
		int nbCollections = (mListCollections != null)?mListCollections.size():0;

		List<Collection> res = new ArrayList<Collection>();

		for(int i=0;i<nbCollections;i++){
			if(parentId.equals("-1")){
				if(mListCollections.get(i).getId().equals(mListCollections.get(i).getParentId())){
					res.add(mListCollections.get(i));
				}
			}else if(mListCollections.get(i).getParentId().equals(parentId) && !mListCollections.get(i).getId().equals(mListCollections.get(i).getParentId())){
				res.add(mListCollections.get(i));
			}
		}

		return res;
	}

	public void populateList(){
		try{
			List<Collection> t = null;

			try{
				t = listFiles("-1");

			}catch(Exception e){
				if(e != null)e.printStackTrace();
			}

			int nb = (t != null)?t.size():0;

			for(int i=0;i<nb;i++){	
				String path = t.get(i).getName()+"/";
				List <Collection> childs = listFiles(t.get(i).getId());

				int nbChilds = (childs != null)?childs.size():0;
				boolean hasChild = (nbChilds > 0);

				Entity e = getEntity(0, hasChild, path);
				e.IdParent = t.get(i).getId();
				e.Id = t.get(i).getId();
				e.Name = t.get(i).getName();
				e.AbsolutePath = path;
				e.mParent = null;

				arrTrades.add(e);
			}
		}catch (Exception e){
			Log.d(" populateList Exception",""+e.getMessage());
		}
	}

	public int calcNbChilds(Entity e){
		int nb = 0;
		int nbC = (e.mChilds != null)?e.mChilds.size():0;

		for(int i=0;i<nbC;i++){
			if(!e.mChilds.get(i).isOpened)nb += 1;
			else nb = 1 + nb + calcNbChilds(e.mChilds.get(i));
		}
		return nb;
	}

	public interface IClick{
		public void onClickOnButton(View v);
		public void onClickOnText(View v, String absoluthPath);
	}

	public ArrayList<Entity> CellButtonClick(View v){
		try{
			Button b = (Button)v;
			int index;
			index=(Integer) b.getTag();

			if(arrTrades.get(index) != null){
				String path = arrTrades.get(index).AbsolutePath;

				if(path != "" && path != null){
					List <Collection> childs = listFiles(arrTrades.get(index).Id);
					int nbSubItems = (childs != null)?childs.size():0;

					if(b.getText().toString().equals("+")){
						b.setText("-");
						Entity temp[]=new Entity[nbSubItems];
						int PLevel=arrTrades.get(index).level+1;

						for(int i=0;i<nbSubItems;i++){
							temp[i]=getEntity(PLevel, true,arrTrades.get(index).AbsolutePath+"/"+childs.get(i).getName());
							temp[i].IdParent = arrTrades.get(index).IdParent;
							temp[i].Id = childs.get(i).getId();
							temp[i].Name = childs.get(i).getName();
							temp[i].AbsolutePath = arrTrades.get(index).AbsolutePath+"/"+childs.get(i).getName();
							temp[i].mParent = arrTrades.get(index);
						}

						List<Entity> l = Arrays.asList(temp);

						if(arrTrades.get(index) != null && l != null){
							arrTrades.get(index).reinitChilds();

							for(int i=0;i<nbSubItems;i++){
								arrTrades.get(index).addChild(l.get(i));
							}

							arrTrades.get(index).isOpened=true;

							if(temp!=null){
								int addindex=index + 1;
								for(int i=0;i<temp.length;i++){
									arrTrades.add(addindex, temp[i]);
									addindex++;
								}
							}
							temp=null;
						}
					}else{
						b.setText("+");

						if(arrTrades.get(index) != null){
							arrTrades.get(index).isOpened=false;

							int removeindex=index+1;
							int nbChildsToRemove = calcNbChilds(arrTrades.get(index));

							for(int i=0;i<nbChildsToRemove;i++){
								arrTrades.remove(removeindex);
							}
							arrTrades.get(index).reinitChilds();
						}
					}
					adapter.notifyDataSetChanged();
				}
			}
		}catch(Exception e){
			if(adapter != null)adapter.notifyDataSetChanged();
			if(e != null)Log.d("Error=", ""+e.getMessage());
		}
		return arrTrades;
	}

	public Entity getEntity(int level,boolean haschild,String AbsolutePath){
		Entity E=new Entity();
		E.Name="Level "+level;
		E.isOpened=false;
		E.level=level;
		E.HasChild=haschild;
		E.AbsolutePath = AbsolutePath;
		return E;
	}


	private LinkedHashMap<String, Collection> mLinkedHashMap = new LinkedHashMap<String, KitviewUtil.Collection>();

	//Replace '/' character present in folder name with the 'slash' string which is allowed to create a physical folder on Android device
	public final static String SPECIAL_CHAR_STRING_REPLACEMENT_SLASH = "SlAsH";
	public final static String SPECIAL_CHAR_STRING_REPLACEMENT_BACKSLASH = "SPECIAL_BaCkSlAsH_SPECIAL";
	public final static String SPECIAL_CHAR_STRING_REPLACEMENT_DOUBLEPOINT = "SPECIAL_DoUbLePoInT_SPECIAL";
	public final static String SPECIAL_CHAR_STRING_REPLACEMENT_STAR = "SPECIAL_StAr_SPECIAL";
	public final static String SPECIAL_CHAR_STRING_REPLACEMENT_QUESTION = "SPECIAL_QuEsTiOn_SPECIAL";
	public final static String SPECIAL_CHAR_STRING_REPLACEMENT_QUOTE = "SPECIAL_QuOtE_SPECIAL";
	public final static String SPECIAL_CHAR_STRING_REPLACEMENT_GREATERTHAN = "SPECIAL_GrEaTeRtHaN_SPECIAL";
	public final static String SPECIAL_CHAR_STRING_REPLACEMENT_LOWER_THAN = "SPECIAL_LoWeRtHaN_SPECIAL";
	public final static String SPECIAL_CHAR_STRING_REPLACEMENT_PIPE = "SPECIAL_PiPe_SPECIAL";

	private void createFolders(List<util.network.KitviewUtil.Collection> collections){
		int nbFolders = (collections != null)?collections.size():0;
		int i = 0;

		while(i < nbFolders){
			util.network.KitviewUtil.Collection collectionItem = collections.get(i);

			if(collectionItem.getName() != null && collectionItem.getName() != ""){
				String name = collectionItem.getName();

				name = name.replace("/", SPECIAL_CHAR_STRING_REPLACEMENT_SLASH);
				name = name.replace("\\", SPECIAL_CHAR_STRING_REPLACEMENT_BACKSLASH);
				name = name.replace(":", SPECIAL_CHAR_STRING_REPLACEMENT_DOUBLEPOINT);
				name = name.replace("*", SPECIAL_CHAR_STRING_REPLACEMENT_STAR);
				name = name.replace("?", SPECIAL_CHAR_STRING_REPLACEMENT_QUESTION);
				name = name.replace("\"", SPECIAL_CHAR_STRING_REPLACEMENT_QUOTE);
				name = name.replace(">", SPECIAL_CHAR_STRING_REPLACEMENT_GREATERTHAN);
				name = name.replace("<", SPECIAL_CHAR_STRING_REPLACEMENT_LOWER_THAN);
				name = name.replace("|", SPECIAL_CHAR_STRING_REPLACEMENT_PIPE);

				collectionItem.setName(name);

				String relativePath = getCollectionItemCompletePath(collections, collectionItem, i);

				if(relativePath != null && relativePath != ""){
					mLinkedHashMap.put(relativePath, collectionItem);
				}
			}

			i++;
		}
	}

	private int obtainItemIndex(String itemId, List<util.network.KitviewUtil.Collection> collections){
		boolean found = false;
		int i = 0;
		int index = -1;
		int nbItems = (collections != null)?collections.size():0;

		while(i<nbItems && !found){
			found = (collections.get(i).getId() != null && collections.get(i).getId() != "")?(collections.get(i).getId().equals(itemId)):false;

			if(found){
				index = i;
			}

			i++;
		}

		return index;
	}

	private String getCollectionItemCompletePath(List<util.network.KitviewUtil.Collection> collections, util.network.KitviewUtil.Collection collectionItem, int itemIndex){
		final int MAX_SUB_LEVEL = 100;
		int level = 0;
		util.network.KitviewUtil.Collection currentItem = collectionItem;
		String parentId = null,parentName = null, completePath = "";

		ArrayList<String> completePathElements = new ArrayList<String>();

		if(collectionItem != null){
			completePathElements.add(collectionItem.getName());

			boolean finish = (currentItem.getId() != null && currentItem.getId() != "")?(currentItem.getId().equals(currentItem.getParentId())):false;

			while(!finish && level < MAX_SUB_LEVEL){
				parentId = currentItem.getParentId();

				if(parentId != null){
					int _itemIndex = obtainItemIndex(parentId, collections);
					if(collections.get(_itemIndex) != null){
						parentName = collections.get(_itemIndex).getName();
						completePathElements.add(0, parentName);
						currentItem = collections.get(_itemIndex);
						parentId = currentItem.getParentId();
					}
				}

				finish = (parentId != null && parentId != "")?(parentId.equals(currentItem.getId())):false;//null);
				level ++;
			}

			int nbCompletePathElements = (completePathElements != null)?completePathElements.size():0;

			for(int j=0;j<nbCompletePathElements;j++){
				String elem = completePathElements.get(j);
				if(j != nbCompletePathElements -1)completePath += elem+"/";
				else completePath += elem;
			}
		}
		return completePath;
	}

	private String mCollectionName;

	public String getCurrentCollectionName(){
		return this.mCollectionName;
	}

	public void initializeCategoriesListView(final List<Collection> collections){
		if(mContext != null){
			mListCollections = collections;

			File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");

			if(mediaStorageDir != null){
				FileUtil.deleteFolderRecursively(mediaStorageDir);

				this.arrTrades = new ArrayList<Entity>();

				createFolders(mListCollections);

				populateList();

				adapter = new util.components.multileveltreelist.ListAdapter(mContext, R.id.row_cell_text_multilevel, arrTrades, new IClick() {
					@Override
					public void onClickOnButton(View v) {
						CellButtonClick(v);
					}

					@Override
					public void onClickOnText(View v, String absoluthPath){
						mContext.showDialog();

						if(MainActivity.getImageFetcher(personne) != null)MainActivity.getImageFetcher(personne).clearCache();

						File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");

						if(mediaStorageDir != null && mediaStorageDir.getAbsolutePath() != null && absoluthPath != null){
							String relativePath = absoluthPath.replace(mediaStorageDir.getAbsolutePath(), "");

							if(relativePath != null){
								relativePath = relativePath.replaceFirst("/", "");

								if(relativePath != null && mLinkedHashMap != null && mLinkedHashMap.get(relativePath) != null){

									mCollectionName = mLinkedHashMap.get(relativePath).getName();//Id();

									final String collectionId2 = mLinkedHashMap.get(relativePath).getId();
									mContext.initializePictures();

									KitviewUtil.GetObjects(mContext, ""+mPatientId, collectionId2, new KitviewUtil.IPhotoInfosArrayListResponse() {
										@Override
										public void onResponse(final List<Photo> photos) {
											int nbPhotos = (photos != null)?photos.size():0;

											if(nbPhotos > 0){
												mHideDialog = true;
												hidePopup();
											}

											processPictures(photos,collectionId2);
										}
									});
								}
							}
						}
					}	
				});

				mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(mCollectionsListView != null && adapter != null)mCollectionsListView.setAdapter(adapter);
					}
				});

				int indexCollectionCategory = PersistenceManager.getInstance().getCollectionCategorySelectedIndex();
				final String collectionId = PersistenceManager.getInstance().getCollectionId();

				if(indexCollectionCategory != -1 && collectionId != ""){

					if(mFirstTimeLoad){
						Collection c = searchCollectionInCollections(mListCollections,collectionId);
						mCollectionName = (c != null)?c.getName():"";//mLinkedHashMap.get(collectionId).getName();

						mContext.initializePictures();

						KitviewUtil.GetObjects(mContext, ""+mPatientId, collectionId, new KitviewUtil.IPhotoInfosArrayListResponse() {
							@Override
							public void onResponse(final List<Photo> photos){
								processPictures(photos,collectionId);
							}
						});
						mFirstTimeLoad = false;
					}

				}else{
					mContext.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showPopup();
						}
					});
				}
			}		
		}
	}

	public KitviewUtil.Collection searchCollectionInCollections(List<KitviewUtil.Collection> collections, String collectionId){
		KitviewUtil.Collection c = null;
		int nbCollections = (collections != null)?collections.size():0;
		boolean found = false;
		int i = 0;

		while(i<nbCollections && !found){
			found = (collections.get(i).getId().equals(collectionId));
			
			if(found)c = collections.get(i);
			
			i++;
		}

		return c;
	}

	public void processPictures(final List<Photo> photos, final String collectionId){
		final int nbPhotos = (photos != null)?photos.size():0;
		mContext.cancelDialog();
		mContext.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(nbPhotos > 0){
					int indexCollectionCategory = mCollectionCategoriesSpinner.getSelectedItemPosition();

					if(PersistenceManager.getInstance().getCollectionCategorySelectedIndex() == -1 && PersistenceManager.getInstance().getCollectionId() == ""){
						PersistenceManager.getInstance().setCollectionCategorySelectedIndex(indexCollectionCategory);
						PersistenceManager.getInstance().setCollectionId(collectionId);
					}
				}

				if(nbPhotos == 1){
					CollectionsPopupManager.this.photos = photos;
					mContext.setPicturesPath();

					if(photos.get(0).getId() != null){
						ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(mContext);
						int screenWidth = screenDimensions.get(0).intValue();
						int screenHeight = screenDimensions.get(1).intValue();

						if(mContext.getImageViewTouch() != null){
							mContext.getImageViewTouch().setVisibility(View.VISIBLE);
							mContext.getImageViewTouch().invalidate();
							mContext.getLeftTextView().setVisibility(View.VISIBLE);
							mContext.getLeftIndexTextView().setVisibility(View.VISIBLE);
							mContext.getPreviousPreviousButton().setVisibility(View.VISIBLE);
							mContext.getPreviousLeftButton().setVisibility(View.VISIBLE);
							mContext.getNextLeftButton().setVisibility(View.VISIBLE);
						}

						mContext.getImageViewTouch2().setVisibility(View.GONE);
						mContext.getNextNextButton().setVisibility(View.GONE);
						mContext.getRightTextView().setVisibility(View.GONE);
						mContext.getRightIndexTextView().setVisibility(View.GONE);
						mContext.getPreviousRightButton().setVisibility(View.GONE);
						mContext.getNextRightButton().setVisibility(View.GONE);
					}
				}else if(nbPhotos >= 2){
					CollectionsPopupManager.this.photos = photos;
					mContext.setPicturesPath();//photos);


					ArrayList<Integer> screenDimensions = ImageUtil.getScreenDimensions(mContext);
					int screenWidth = screenDimensions.get(0).intValue();
					int screenHeight = screenDimensions.get(1).intValue();

					if(photos.get(0).getId() != null){
						if(mContext.getImageViewTouch() != null){
							mContext.getImageViewTouch().setVisibility(View.VISIBLE);
							mContext.getImageViewTouch().invalidate();
							mContext.getPreviousPreviousButton().setVisibility(View.VISIBLE);
							mContext.getLeftTextView().setVisibility(View.VISIBLE);
							mContext.getLeftIndexTextView().setVisibility(View.VISIBLE);
							mContext.getPreviousLeftButton().setVisibility(View.VISIBLE);
							mContext.getNextLeftButton().setVisibility(View.VISIBLE);
						}
					}

					if(photos.get(1).getId() != null){
						if(mContext.getImageViewTouch2() != null){
							mContext.getImageViewTouch2().setVisibility(View.VISIBLE);
							mContext.getImageViewTouch2().invalidate();
							mContext.getNextNextButton().setVisibility(View.VISIBLE);
							mContext.getRightTextView().setVisibility(View.VISIBLE);
							mContext.getRightIndexTextView().setVisibility(View.VISIBLE);
							mContext.getPreviousRightButton().setVisibility(View.VISIBLE);
							mContext.getNextRightButton().setVisibility(View.VISIBLE);
						}
					}
				}else{
					mContext.getImageViewTouch().setVisibility(View.GONE);
					mContext.getPreviousPreviousButton().setVisibility(View.GONE);
					mContext.getRightTextView().setVisibility(View.GONE);
					mContext.getRightIndexTextView().setVisibility(View.GONE);
					mContext.getPreviousLeftButton().setVisibility(View.GONE);
					mContext.getNextLeftButton().setVisibility(View.GONE);
					mContext.getImageViewTouch2().setVisibility(View.GONE);
					mContext.getNextNextButton().setVisibility(View.GONE);
					mContext.getPreviousRightButton().setVisibility(View.GONE);
					mContext.getNextRightButton().setVisibility(View.GONE);

					SystemUtil.showPopup(mContext, mContext.getResources().getString(R.string.no_pictures));
				}
			}
		});
	}

	public boolean isVisible(){
		return (mCollectionsDialog != null)?mCollectionsDialog.isShowing():false;
	}

	public void destroy(){
		mContext = null;
	}

	public void showPopup(){
		if(mCollectionsDialog != null){
			if(mParentLinearLayout != null)mParentLinearLayout.setVisibility(View.VISIBLE);
			if(!mContext.isFinishing())mCollectionsDialog.show();
		}
	}

	public void hidePopup(){
		if(mCollectionsDialog != null){
			if(mHideDialog){
				mCollectionsDialog.dismiss();
			}
		}
	}

	public Dialog getPopup(){
		return mCollectionsDialog;
	}

	public LinkedHashMap<String, Collection> getLinkedHashmap(){
		return this.mLinkedHashMap;
	}

	public int getPatientId(){
		return mPatientId;
	}

	public List<Photo> getListPhotos(){
		return this.photos;
	}
}