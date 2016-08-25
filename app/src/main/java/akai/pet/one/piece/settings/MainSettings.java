package akai.pet.one.piece.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import a.b.c.AdManager;
import a.b.c.update.AppUpdateInfo;
import a.b.c.update.CheckAppUpdateCallBack;
import akai.floatView.op.luffy.R;
import akai.pet.one.piece.AppService;
import akai.pet.one.piece.AppWebView;
import akai.pet.one.piece.store.StoreActivity;

public class MainSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    /**数据存储类*/
    private static SharedPreferences sp;
    /**服务项*/
    private Intent floatViewService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(getString(R.string.sp_name));
        addPreferencesFromResource(R.xml.main_settings);
        //数据监听器
        sp = getPreferenceManager().getSharedPreferences();
        sp.registerOnSharedPreferenceChangeListener(this);
        floatViewService = new Intent(this, AppService.class);
        //获得版本号,设置值,避免无取到值
        int versonCode = 0;
        try {
            versonCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
//			System.out.println(versonCode);
        } catch (NameNotFoundException e) {}
        if(sp.getInt("verson_code", 0) < versonCode){
            welcomeAndHelp();
            sp.edit().putInt("verson_code", versonCode).commit();
        }
        if(sp.getBoolean("person_visible", false))
            startService(floatViewService);

        try{
            String appId = "3db7ce74f7d5c9ca", appSecret = "ada5571bdb35b62f";
            AdManager.getInstance(this).init(appId, appSecret, false);
        }catch(Exception e){
            e.printStackTrace();
        }
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         final Preference preference) {
        if("person_select".equals(preference.getKey())){
            dialog_select_person();
        }
        else if("app_help".equals(preference.getKey())){
            welcomeAndHelp();
        }
        else if("app_share".equals(preference.getKey())){
            shareApp();
        }
        else if("app_update".equals(preference.getKey())){
            updateApp();
        }
        else if("my_apps".equals(preference.getKey())){
            openMyApps();
            MobclickAgent.onEvent(this,"App");
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp,
                                          String key) {
        if("person_visible".equals(key)){
            if(sp.getBoolean(key, false)){
                startService(floatViewService);
            }
            else{
                stopService(floatViewService);
                //配合试用时间过后的界面显示问题
                ((CheckBoxPreference)findPreference(key)).setChecked(false);
            }
        }
    }

    /**
     * 第一次启动本程序,或者帮助
     */
    private void welcomeAndHelp(){
        //免费版本，只能在12.31前激活
//		Date d = new Date(113, 2, 13);
//		Date d_now = new Date();
//		if(d_now.before(d)){
//			System.out.println("right to free");
//			sp.edit().putBoolean("open_zoro", true).commit();
//			sp.edit().putBoolean("open_delete_ad", true).commit();
//		}
        //窗口
        new AlertDialog.Builder(this)
                .setMessage(R.string.welcome_hlep)
                .setCancelable(false)
                .setPositiveButton(R.string.str_ok, null)
                .create().show();
    }

    private void shareApp(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.str_share));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.str_share_content));
        startActivity(Intent.createChooser(intent, getString(R.string.str_share)));
    }
    /**
     * 选择人物窗口
     */
    private void dialog_select_person(){
//		PersonSelectDialog dialog = new PersonSelectDialog(this);
//		dialog.setTitle(R.string.str_person_settings);
//		dialog.show();
        startActivity(new Intent(this, StoreActivity.class));
    }

    /**
     * 检测更新，从有米下载
     */
    private void updateApp(){
        final ProgressDialog dialog = ProgressDialog.show(MainSettings.this, null, getString(R.string.str_loading));
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        AdManager.getInstance(this).asyncCheckAppUpdate(new CheckAppUpdateCallBack() {
            @Override
            public void onCheckAppUpdateFinish(final AppUpdateInfo updateInfo) {
                if(!dialog.isShowing())
                    return;

                dialog.dismiss();
                //检查更新回调，注意，这里是在UI线程回调的，因此您可以直接与UI交互，但不可以进行长时间的操作(如在这里访问网络  是不允许的)
                if(updateInfo==null){
                    //当前已经是最新版本
                    Toast.makeText(MainSettings.this, R.string.str_no_new_version, Toast.LENGTH_SHORT).show();
                }else{
                    //有更新信息
                    new AlertDialog.Builder(MainSettings.this)
                            .setTitle(R.string.str_new_version)
                            .setMessage(updateInfo.getUpdateTips())//这里是版本更新信息
                            .setNegativeButton(R.string.str_store_download,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Intent intent = new Intent(
                                                    Intent.ACTION_VIEW, Uri
                                                    .parse(updateInfo
                                                            .getUrl()));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            MainSettings.this.startActivity(intent);
                                            //ps:这里示例点击"马上升级"按钮之后简单地调用系统浏览器进行新版本的下载，
                                            //但强烈建议开发者实现自己的下载管理流程，这样可以获得更好的用户体验。
                                        }
                                    })
                            .setPositiveButton(R.string.str_cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.cancel();
                                        }
                                    }).create().show();
                }

            }
        });
    }

    private void openMyApps(){
        startActivity(new Intent(this, AppWebView.class));
    }
}