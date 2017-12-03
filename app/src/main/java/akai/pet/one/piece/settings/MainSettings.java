package akai.pet.one.piece.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.umeng.analytics.MobclickAgent;

import akai.floatView.op.luffy.R;
import akai.pet.one.piece.AppService;
import akai.pet.one.piece.AppWebView;
import akai.pet.one.piece.store.StoreActivity;

public class MainSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * 数据存储类
     */
    private static SharedPreferences sp;
    /**
     * 服务项
     */
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
        } catch (NameNotFoundException e) {
        }
        if (sp.getInt("verson_code", 0) < versonCode) {
            welcomeAndHelp();
            sp.edit().putInt("verson_code", versonCode).commit();
        }
        if (sp.getBoolean("person_visible", false))
            startService(floatViewService);

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
        if ("person_select".equals(preference.getKey())) {
            dialog_select_person();
        } else if ("app_help".equals(preference.getKey())) {
            welcomeAndHelp();
        } else if ("app_share".equals(preference.getKey())) {
            shareApp();
        } else if ("app_update".equals(preference.getKey())) {
            updateApp();
        } else if ("my_apps".equals(preference.getKey())) {
            openMyApps();
            MobclickAgent.onEvent(this, "App");
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp,
                                          String key) {
        if ("person_visible".equals(key)) {
            if (sp.getBoolean(key, false)) {
                startService(floatViewService);
            } else {
                stopService(floatViewService);
                //配合试用时间过后的界面显示问题
                ((CheckBoxPreference) findPreference(key)).setChecked(false);
            }
        }
    }

    /**
     * 第一次启动本程序,或者帮助
     */
    private void welcomeAndHelp() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.welcome_hlep)
                .setCancelable(false)
                .setPositiveButton(R.string.str_ok, null)
                .create().show();
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.str_share));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.str_share_content));
        startActivity(Intent.createChooser(intent, getString(R.string.str_share)));
    }

    /**
     * 选择人物窗口
     */
    private void dialog_select_person() {
        startActivity(new Intent(this, StoreActivity.class));
    }

    /**
     * 检测更新，从有米下载
     */
    private void updateApp() {
    }

    private void openMyApps() {
        startActivity(new Intent(this, AppWebView.class));
    }
}