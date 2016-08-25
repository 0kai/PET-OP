package akai.pet.one.piece.store;

import java.util.ArrayList;
import java.util.List;

import akai.floatView.op.luffy.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class StorePersonAdapter extends SimpleAdapter{
	
	public final static int TYPE_LOCAL = 0;
	public final static int TYPE_ONLINE = 1;
	
	private Context mContext;
	
	private List<PersonInfo> mData;
	
	public StorePersonAdapter(Context context, int type){
		super(context, null, 0, null, null);
		mContext = context;
		mData = getDataByType(type);
	}
	
	public void setAdapterData(List<PersonInfo> data){
		mData = data;
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}



	private List<PersonInfo> getDataByType(int type){
		if(type == TYPE_ONLINE)
			return initOnline(mContext);
		else
			return initLocal(mContext);
	}
	
	private List<PersonInfo> initLocal(Context context){
//		System.out.println("init local");
		List<PersonInfo> data = new ArrayList<PersonInfo>();
		
		String name[] = {"luffy", "chopper", "zoro", "law"};
		int iconId[] = {R.drawable.luffy_eat_2, R.drawable.chopper_eat_1_1, R.drawable.zoro_down_2, R.drawable.law_stand};
		int nameId[] = {R.string.luffy, R.string.chopper, R.string.zoro, R.string.law};
		
		for(int i = 0; i < name.length; i++){
			try{
				PersonInfo info = new PersonInfo();
				info.tag = name[i];
				info.image = BitmapFactory.decodeResource(context.getResources(), iconId[i]);
				info.name = context.getResources().getText(nameId[i]).toString();
				data.add(info);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_name), Activity.MODE_PRIVATE);
//		sp.edit().putString("download_0", "sanji").commit();
		
		for(int i = 0; true; i++){
			String downloadName = sp.getString("download_" + i, "");
			if("".equals(downloadName))
				break;
			
			try{
				PersonInfo info = new PersonInfo();
				info.name = DataByFile.getPersonName(downloadName);
				info.image = DataByFile.getPersonIcon(downloadName);
				info.tag = downloadName;
				data.add(info);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return data;
	}
	
	private List<PersonInfo> initOnline(Context context){
//		System.out.println("init On line");
		List<PersonInfo> data = new ArrayList<PersonInfo>();
		return data;//nothing
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		view = LayoutInflater.from(mContext).inflate(R.layout.store_item_layout, null);
		ImageView image = (ImageView) view.findViewById(R.id.store_item_image);
		TextView name = (TextView) view.findViewById(R.id.store_item_text);
		switch(mData.get(position).flag){
		case PersonInfo.FLAG_DOWNLOAD:
			view.findViewById(R.id.store_item_ic_downloaded).setVisibility(View.VISIBLE);
			break;
		case PersonInfo.FLAG_UPDATE:
			view.findViewById(R.id.store_item_ic_update).setVisibility(View.VISIBLE);
			break;
		}
		view.setTag(mData.get(position));
		try{
			image.setImageBitmap((Bitmap) mData.get(position).image);
			name.setText(mData.get(position).name);
		}catch(Exception e){
			e.printStackTrace();
		}
		return view;
	}

}
