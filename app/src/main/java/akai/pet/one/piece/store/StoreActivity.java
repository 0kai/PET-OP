package akai.pet.one.piece.store;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import akai.floatView.op.luffy.R;
import akai.pet.one.piece.settings.PersonSettingDialog;

public class StoreActivity extends Activity {

    private SharedPreferences mSP;

    /**
     * the grid view of person
     */
    private StoreGridView mStoreGV;

    private Context mContext;

    private int mType = StorePersonAdapter.TYPE_LOCAL;

    private List<PersonInfo> mData;

    private StorePersonAdapter mAdapter;

    private boolean mIsLoading;

    private final static int DOWNLOAD_PERSON_SCORE = 35;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_layout);
        mContext = this;
        mSP = getSharedPreferences(getString(R.string.sp_name), MODE_PRIVATE);

        mStoreGV = (StoreGridView) findViewById(R.id.store_gridview);
        mStoreGV.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                final PersonInfo info = (PersonInfo) view.getTag();
                if (mType == StorePersonAdapter.TYPE_LOCAL) {
                    PersonSettingDialog dialog = null;
                    try {
                        dialog = new PersonSettingDialog(mContext, info.tag);
                        if (dialog != null)
                            dialog.show();
                    } catch (Exception e) {
                        KLog2File.saveLog2File(e);
                        Toast.makeText(mContext, mSP.getString("person_show_name", "")
                                + getString(R.string.str_app_res_error), Toast.LENGTH_LONG).show();
                        mSP.edit().putBoolean("person_visible", false).commit();
                    }
                } else {//TYPE_ONLINE
                    for (int i = 0; true; i++) {
                        String downloadName = mSP.getString("download_" + i, "");
                        if ("".equals(downloadName)) {
                            break;
                        }
//						else if(info.tag.equals(downloadName) && mSP.getInt("download_" + i + "_version", -1) != -1){//if have version value, had download over
                        else if (info.tag.equals(downloadName)) {//5.0.3 modify, never to spend points again
                            //can download normally
                            AlertDialog.Builder b = new AlertDialog.Builder(mContext);
                            String updateString = info.onlineVersion > mSP.getInt("download_" + i + "_version", -1) ? getString(R.string.str_new_res) : "";
                            b.setTitle(info.name + getString(R.string.str_store_download) + updateString);
                            b.setMessage(R.string.str_person_download);
                            b.setPositiveButton(R.string.str_store_download, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //start download
                                    downloadResByPerson(info);
                                }
                            });
                            b.setNegativeButton(R.string.str_cancel, null);
                            b.show();
                            return;
                        }
                    }
                    downloadResByPerson(info);
                }
            }
        });
        mStoreGV.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				Toast.makeText(StoreActivity.this, "���ԣ�Ҫɾ����" + ((PersonInfo)arg1.getTag()).name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        StorePersonAdapter adapter = new StorePersonAdapter(this, mType);
        mStoreGV.setAdapter(adapter);

        findViewById(R.id.store_local_btn).setSelected(true);
        findViewById(R.id.store_local_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.store_online_btn).setSelected(false);
                findViewById(R.id.store_local_btn).setSelected(true);
                if (mType != StorePersonAdapter.TYPE_LOCAL) {
                    mType = StorePersonAdapter.TYPE_LOCAL;
                    StorePersonAdapter adapter = new StorePersonAdapter(StoreActivity.this, mType);
                    mStoreGV.setAdapter(adapter);
                }
            }
        });

        findViewById(R.id.store_online_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.store_local_btn).setSelected(false);
                findViewById(R.id.store_online_btn).setSelected(true);
                if (mType != StorePersonAdapter.TYPE_ONLINE) {
                    mType = StorePersonAdapter.TYPE_ONLINE;
                    mAdapter = new StorePersonAdapter(StoreActivity.this, mType);
                    mStoreGV.setAdapter(mAdapter);

                    mIsLoading = true;
                    //handler and loading
                    final ProgressDialog dialog = ProgressDialog.show(StoreActivity.this, null, getString(R.string.str_loading));
                    dialog.setCancelable(true);
                    dialog.setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mIsLoading = false;
                        }
                    });

                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 0) {
                                if (mIsLoading) {
                                    //update
                                    mStoreGV.setAdapter(mAdapter);
                                    mStoreGV.invalidate();
                                }
                            } else if (msg.what == -1) {
                                Toast.makeText(mContext, mContext.getString(R.string.str_connect_fail), Toast.LENGTH_SHORT).show();
                                try {//the dialog may not attach to this window
                                    dialog.dismiss();
                                } catch (Exception e) {
                                }
                            } else {
                                try {
                                    dialog.dismiss();
                                } catch (Exception e) {
                                }
                            }
                        }

                    };

                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                URL uri = new URL("http://d.0kai.net/op/persons.html");
                                HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
                                conn.setRequestMethod("GET");
                                conn.setConnectTimeout(3000);
                                if (conn.getResponseCode() == 200) {
                                    InputStream is = conn.getInputStream();
                                    byte buffer[] = new byte[1];
                                    String data = "";
                                    while (is.read(buffer) != -1) {
                                        data += new String(buffer);
                                    }
                                    is.read(buffer);
                                    is.close();
//									data = "mihawk,1,crocodile,1,hancock,1,kid,1,buggy,1,sanji,1,ace,2,joker,1";
                                    String[] mPersonList = data.split(",");
                                    if (mPersonList.length != 0) {
                                        mData = new ArrayList<PersonInfo>();
                                        for (int i = 0; i < mPersonList.length - 1; i += 2) {
                                            String name = mPersonList[i];
                                            int version = Integer.parseInt(mPersonList[i + 1]);
                                            //had download, and now has new version
                                            PersonInfo info = new PersonInfo();
                                            info.name = DataByFile.getPersonName(name);
                                            Bitmap b = DataByFile.getPersonIcon(name);
                                            if (b == null)
                                                b = BitmapFactory.decodeResource(getResources(), R.drawable.person_loading);
                                            info.image = b;
                                            info.tag = name;
                                            info.onlineVersion = version;
                                            //
                                            for (int j = 0; true; j++) {
                                                String downloadName = mSP.getString("download_" + j, "");
                                                if ("".equals(downloadName)) {
                                                    break;
                                                } else if (name.equals(downloadName)) {
                                                    info.flag = PersonInfo.FLAG_DOWNLOAD;
                                                    if (mSP.getInt("download_" + j + "_version", 1) < version)
                                                        info.flag = PersonInfo.FLAG_UPDATE;
                                                    break;
                                                }
                                            }

                                            mData.add(info);
//											System.out.println("DownLoad Name: " + name);
                                        }
                                        mAdapter.setAdapterData(mData);
                                        handler.sendEmptyMessage(0);//close the progressing bar and update

                                        //update from web(bae)
                                        for (PersonInfo info : mData) {
                                            if (!mIsLoading)
                                                break;
                                            String name = info.tag;
                                            if (name.toUpperCase().equals(info.name)) {
                                                info.name = DataByFile.getBAEPersonName(name);
                                                Bitmap b = DataByFile.getBAEPersonIcon(name);
                                                if (b != null)
                                                    info.image = b;
                                                handler.sendEmptyMessage(0);
                                            }
                                        }
                                        handler.sendEmptyMessage(1);
                                    }
                                } else {
                                    handler.sendEmptyMessage(-1);
                                }
                                conn.disconnect();
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(-1);
                            }
                        }
                    }.start();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * download the recourse by the person name
     */
    private void downloadResByPerson(PersonInfo info) {
        int i = 0;
        for (; true; i++) {
            String downloadName = mSP.getString("download_" + i, "");
            if ("".equals(downloadName) || downloadName.equals(info.tag)) {
                break;
            }
        }
        //download start
        mSP.edit().putString("download_" + i, info.tag).commit();//save before download, it had offer the ad
        DataByFile.getPersonResToFile(mContext, info, mSP, "download_" + i);
    }

}
