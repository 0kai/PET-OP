package akai.pet.one.piece.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import akai.floatView.op.luffy.R;
import akai.pet.one.piece.store.DataByFile;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PersonSettingDialog extends AlertDialog{
	
	public final static String OBJECT_KEY_SP = "preference_key";
	public final static String OBJECT_KEY_ICON = "person_setting_icon";
	public final static String OBJECT_KEY_NAME = "person_setting_name";
	
	private Context mContext;
	private SharedPreferences mSP;
	private int mIconId;
	private CharSequence mTitleName;
	private PersonSettingAdapter mAdapter;
	private String mPersonName;
	private final Object luffyData[][] = {
			//perference_key        //icon                    //name
			 {"luffy_action_crawl", R.drawable.luffy_crawl_1, R.string.str_crawl}
			,{"luffy_action_eat",	R.drawable.luffy_eat_1,	  R.string.str_eat_meat}
			,{"luffy_action_sit",	R.drawable.luffy_sit_1,	  R.string.str_sit}
			,{"luffy_action_walk",	R.drawable.luffy_walk_1,  R.string.str_walk}
			,{"luffy_action_stand",	R.drawable.luffy_stand,	  R.string.str_stand}
	};
	private final Object zoroData[][] = {
			//perference_key        //icon                    //name
			 {"zoro_action_sleep",  R.drawable.zoro_sleep_1,  R.string.str_sleep}
			,{"zoro_action_eat",	R.drawable.zoro_eat_1,	  R.string.str_eat}
			,{"zoro_action_sit",	R.drawable.zoro_sit,	  R.string.str_sit}
			,{"zoro_action_walk",	R.drawable.zoro_walk_1,   R.string.str_walk}
			,{"zoro_action_stand",	R.drawable.zoro_stand,	  R.string.str_stand}
	};
	private final Object lawData[][] = {
			//perference_key        //icon                    //name
			 {"law_action_walk",  	R.drawable.law_walk_1,    R.string.str_walk}
			,{"law_action_crawl",  	R.drawable.law_crawl_1,   R.string.str_crawl}
			,{"law_action_sit",  	R.drawable.law_sit_1,     R.string.str_sit}
			,{"law_action_sleepy",  R.drawable.law_sleepy_2,  R.string.str_sleepy}
			,{"law_action_stand",  	R.drawable.law_stand,     R.string.str_stand}
			,{"law_action_dance",  	R.drawable.law_dance_1,   R.string.str_dance}
	};
	private final Object chopperData[][] = {
			//perference_key        //icon                    //name
			 {"chopper_action_bar",  	R.drawable.chopper_walk_0_2,     R.string.str_bar}
			,{"chopper_action_walk",  	R.drawable.chopper_walk_0_1,     R.string.str_walk}
			,{"chopper_action_stand",  	R.drawable.chopper_stand_0_1,    R.string.str_stand}
			,{"chopper_action_sit",  	R.drawable.chopper_sit,    		 R.string.str_sit}
			,{"chopper_action_eat",  	R.drawable.chopper_eat_1_1,    	 R.string.str_eat}
			,{"chopper_action_ball",  	R.drawable.chopper_ball,    	 R.string.str_ball}
			,{"chopper_action_sleep",  	R.drawable.chopper_sleep_1_1,    R.string.str_sleep}
			,{"chopper_action_happy",  	R.drawable.chopper_happy_0_1,    R.string.str_happy}
			,{"chopper_action_shock",  	R.drawable.chopper_shock_2_1,    R.string.str_shock}
			,{"chopper_action_star",  	R.drawable.chopper_star_1_1,    R.string.str_star}
	};
	
	public PersonSettingDialog(Context context, String name) {
		super(context);
		mContext = context;
		mPersonName = name;
		mSP = mContext.getSharedPreferences(mContext.getString(R.string.sp_name), Activity.MODE_PRIVATE);
		
		if("luffy".equals(mPersonName)){
			mIconId = R.drawable.luffy_eat_2;
			mTitleName = mContext.getResources().getText(R.string.luffy_full_name);
		}
		else if("zoro".equals(mPersonName)){
			mIconId = R.drawable.zoro_down_2;
			mTitleName = mContext.getResources().getText(R.string.zoro_full_name);
		}
		else if("law".equals(mPersonName)){
			mIconId = R.drawable.law_stand;
			mTitleName = mContext.getResources().getText(R.string.law_full_name);
		}
		else if("chopper".equals(mPersonName)){
			mIconId = R.drawable.chopper_eat_1_1;
			mTitleName = mContext.getResources().getText(R.string.chopper_full_name);
		}
		else{
			
		}
		mAdapter = new PersonSettingAdapter(mContext, getListData(mPersonName), 0, null, null);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_person_setting);
		//set title and icon
		ImageView iv_icon = (ImageView)findViewById(R.id.dialog_person_setting_icon);
		TextView tv_title = (TextView)findViewById(R.id.dialog_person_setting_title);
		if(mIconId != 0){
			iv_icon.setImageResource(mIconId);
			tv_title.setText(mTitleName);
		}else{//for the download person
			iv_icon.setImageBitmap(DataByFile.getPersonIcon(mPersonName));
			tv_title.setText(DataByFile.getPersonFullName(mPersonName));
		}
		findViewById(R.id.dialog_person_setting_select).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mAdapter.isAnyCheck()){
					mSP.edit().putString("person_show_name", mPersonName).commit();
					PersonSettingDialog.this.cancel();
				}else{
					Toast.makeText(mContext, R.string.str_at_least_one, Toast.LENGTH_SHORT).show();
				}
			}
		});
		findViewById(R.id.dialog_person_setting_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PersonSettingDialog.this.cancel();
			}
		});
		//set the adapter, and set the listview here with different type
		ListView mListView = (ListView)findViewById(R.id.dialog_person_setting_lv);
		mListView.setAdapter(mAdapter);
	}
	/**
	 * get the data for the list
	 */
	private List<HashMap<String, Object>> getListData(String name){
		List<HashMap<String, Object>> listData = new ArrayList<HashMap<String,Object>>();
		do{
			Object dataArray[][] = null;
			if("luffy".equals(name)){
				dataArray = luffyData;
			}else if("zoro".equals(name)){
				dataArray = zoroData;
			}else if("law".equals(name)){
				dataArray = lawData;
			}else if("chopper".equals(name)){
				dataArray = chopperData;
			}else{
				break;
			}
			for(Object data[]: dataArray){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(OBJECT_KEY_SP, 	 data[0]);
				map.put(OBJECT_KEY_ICON, BitmapFactory.decodeResource(mContext.getResources(), (Integer) data[1]));
				map.put(OBJECT_KEY_NAME, mContext.getText((Integer) data[2]));
				listData.add(map);
			}
			return listData;
		}while(false);
		
		//for the download image
		listData.addAll(DataByFile.getSettingList(name));
		return listData;
	}
	
	/**
	 * the adapter for this dialog's list view
	 * @author K
	 */
	class PersonSettingAdapter extends SimpleAdapter{
		private Context mContext;
		private List<HashMap<String, Object>> mData;
		public PersonSettingAdapter(Context context, List<HashMap<String, Object>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			mContext = context;
			mData = data;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//if(convertView == null)
			{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_person_setting, null);
				//the icon of person
				ImageView iv_icon = (ImageView)convertView.findViewById(R.id.person_setting_icon);
				iv_icon.setImageBitmap((Bitmap) mData.get(position).get(OBJECT_KEY_ICON));
				//the name of person
				TextView tv_name = (TextView)convertView.findViewById(R.id.person_setting_name);
				tv_name.setText((String)mData.get(position).get(OBJECT_KEY_NAME));
				//checkedbox
				CheckBox cb_selected = (CheckBox)convertView.findViewById(R.id.person_setting_selected);
				cb_selected.setId(position);
				cb_selected.setChecked(mSP.getBoolean((String)mData.get(position).get(OBJECT_KEY_SP), false));
				cb_selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked){
							mSP.edit()
							.putBoolean((String)mData.get(buttonView.getId()).get(OBJECT_KEY_SP), true)
							.commit();
						}else{//at least one is selected
							boolean isAnyChecked = false;
							for(HashMap<String, Object> map: mData){
								if(mData.indexOf(map) == buttonView.getId())
									continue;
								//add chopper bar
								if(((String)map.get(OBJECT_KEY_SP)).equals("chopper_action_bar")){
									continue;
								}
								if(mSP.getBoolean((String)map.get(OBJECT_KEY_SP), false)){
									isAnyChecked = true;
									break;
								}
							}
							if(!isAnyChecked){
								buttonView.setChecked(true);
								Toast.makeText(mContext, R.string.str_at_least_one, Toast.LENGTH_SHORT).show();
							}else{
								mSP.edit()
								.putBoolean((String)mData.get(buttonView.getId()).get(OBJECT_KEY_SP), false)
								.commit();
							}
						}
					}
				});
			}
			return convertView;
		}
		
		public boolean isAnyCheck(){
			boolean checked = false;
			for(HashMap<String, Object> map: mData){
				if(((String)map.get(OBJECT_KEY_SP)).equals("chopper_action_bar")){
					continue;
				}
				if(mSP.getBoolean((String)map.get(OBJECT_KEY_SP), false)){
					checked = true;
					break;
				}
			}
			return checked;
		}
		
	}

}
