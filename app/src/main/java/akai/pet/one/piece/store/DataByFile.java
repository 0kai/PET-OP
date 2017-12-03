package akai.pet.one.piece.store;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.qiniu.storage.BucketManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import akai.floatView.op.luffy.R;
import akai.pet.one.piece.person.DownloadPerson;
import akai.pet.one.piece.settings.PersonSettingDialog;

public class DataByFile {

    private final static String TAG = DataByFile.class.getSimpleName();

    private static String SAVE_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/0kai/op/";


    // ----------------------------------------
    private final static String HOST = "http://7rflo1.com1.z0.glb.clouddn.com/";
    private final static String ACCESS_KEY = "24CcPOuqliBpwWOqZtSXUzkCvukw9QcSrJg6n3sY";
    private final static String SECRET_KEY = "w-yz2vdcsyIIvbWjuKo068WBz2c13aXbGuPvXI0S";
//	private final static String mBucketName = "op-pet";
    // ----------------------------------------

    private static String getQiNiuSignUrl(String filePath) {
        String url = HOST + filePath;
//        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
//        url = auth.privateDownloadUrl(url);
        return url;
    }


    public static Bitmap getBitmapByPath(String path) {
        Bitmap bitmap = null;
        try {
            File f = new File(SAVE_FILE_PATH + path + ".dat");
            if (!f.exists()) return null;
            FileInputStream fis = new FileInputStream(f.getAbsolutePath());
//			System.out.println("path:" + SAVE_FILE_PATH + path + ".dat");
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            //decode
            byte temp = buffer[5];
            buffer[5] = buffer[99];
            buffer[99] = temp;
            for (int i = 0; i < 100 - 1; i += 2) {
                temp = buffer[i];
                buffer[i] = buffer[i + 1];
                buffer[i + 1] = temp;
            }

            bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static InputStream getFileInputStream(String path) {
        InputStream is = null;
        try {
            is = new FileInputStream(SAVE_FILE_PATH + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }

    /**
     * return the name by the language, only Chinese and English
     *
     * @param name in English
     * @return
     */
    public static String getPersonName(String name) {
        try {
            String language = Locale.getDefault().getLanguage();
            File f = new File(SAVE_FILE_PATH + name + "/" + "name.config");
            if (!f.exists())
                return name.toUpperCase();

            if ("zh".equals(language)) {
                InputStream is = getFileInputStream(name + "/" + "name.config");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "GB2312"));

                return EncodingUtils.getString(reader.readLine().getBytes(), "utf-8").trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return name.toUpperCase();
    }

    /**
     * from Qiniu
     */
    public static String getWebPersonName(String name) {

        try {

            String language = Locale.getDefault().getLanguage();
            if ("zh".equals(language)) {
                String url = getQiNiuSignUrl(name + "/name.config");
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                InputStream is = conn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "GB2312"));

                return EncodingUtils.getString(reader.readLine().getBytes(), "utf-8").trim();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return name.toUpperCase();
    }

    public static String getBAEPersonName(String name) {
        return getWebPersonName(name);
    }

    /**
     * return the full name by the language, only Chinese and English
     *
     * @param name in English
     * @return
     */
    public static String getPersonFullName(String name) {
        File f = new File(SAVE_FILE_PATH + name + "/" + "name.config");
        if (!f.exists())
            return name.toUpperCase();
        try {
//			InputStream is = context.getAssets().open(name + "/" + "name.config");
            InputStream is = getFileInputStream(name + "/" + "name.config");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "GB2312"));
            reader.readLine();//ignore the first line
            String fullName = EncodingUtils.getString(reader.readLine().getBytes(), "utf-8").trim();
            fullName += "\n" + EncodingUtils.getString(reader.readLine().getBytes(), "utf-8").trim();
            return fullName;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return name.toUpperCase();
    }

    /**
     * get the icon image by the name
     *
     * @param name
     * @return
     */
    public static Bitmap getPersonIcon(String name) {
        return BitmapFactory.decodeFile(SAVE_FILE_PATH + name + "/" + name + "_icon.png");
//		return getBitmapByPath(name+"/"+name+"_icon");
    }

    public static Bitmap getWebPersonIcon(String name) {
        try {
            String url = getQiNiuSignUrl(name + "/" + name + "_icon.png");
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            InputStream is = conn.getInputStream();
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBAEPersonIcon(String name) {
        return getWebPersonIcon(name);
    }

    public static Collection<HashMap<String, Object>> getSettingList(String name) {
        Collection<HashMap<String, Object>> collection = new ArrayList<HashMap<String, Object>>();

        try {
            String language = Locale.getDefault().getLanguage();
            InputStream is = getFileInputStream(name + "/" + "info.config");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "GB2312"));

            String[] strArray = EncodingUtils.getString(reader.readLine().getBytes(), "utf-8")
                    .trim().split(",");
            for (int i = 0; i < strArray.length - 2; i += 3) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(PersonSettingDialog.OBJECT_KEY_SP, name + "_action_" + strArray[i]);
                map.put(PersonSettingDialog.OBJECT_KEY_ICON,
                        getBitmapByPath(name + "/" + name + "_" + strArray[i] + "_" + strArray[i + 2]));
                if ("zh".equals(language)) {
                    map.put(PersonSettingDialog.OBJECT_KEY_NAME, strArray[i + 1]);
                } else {
                    map.put(PersonSettingDialog.OBJECT_KEY_NAME, strArray[i]);
                }
                collection.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return collection;
    }

    public static List<HashMap<String, Object>> getActionList(String name) {
        List<HashMap<String, Object>> collection = new ArrayList<HashMap<String, Object>>();

        try {
            InputStream is = getFileInputStream(name + "/" + "action.config");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "GB2312"));

            String[] strArray = EncodingUtils.getString(reader.readLine().getBytes(), "utf-8")
                    .trim().split(",");
            for (int i = 0; i < strArray.length - 1; i += 2) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(DownloadPerson.ACTION_KEY_NAME, strArray[i]);
                map.put(DownloadPerson.ACTION_KEY_FUN, Integer.parseInt(strArray[i + 1]));
                collection.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return collection;
    }

    /////////////////////

    static boolean flag = true;
    static int total = 100;

    public static void getPersonResToFile(final Context context, final PersonInfo info, final SharedPreferences sp, final String key) {
        try {
            flag = true;
            final ProgressDialog dialog = ProgressDialog.show(context, null, context.getString(R.string.str_store_downloading) + "0/100");

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (!flag) {
                        dialog.dismiss();
                        return;
                    }
                    if (msg.what == -1) {
                        //download error
                        dialog.dismiss();
                        Toast.makeText(context, R.string.str_download_error, Toast.LENGTH_LONG).show();
                        return;
                    }
                    dialog.setMessage(context.getString(R.string.str_store_downloading) + (msg.what * 100 / total) + "/100");
                    if (msg.what == total) {//download over
                        flag = false;
                        sp.edit().putString(key, info.tag).commit();
                        sp.edit().putInt(key + "_version", info.onlineVersion).commit();
                        dialog.dismiss();
                        Toast.makeText(context, context.getString(R.string.str_store_download_over), Toast.LENGTH_SHORT).show();
                    }
                }
            };

            new Thread() {
                @Override
                public void run() {
                    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
                    BucketManager bucketManager = new BucketManager(auth);
                    BucketManager.FileListIterator it = bucketManager.createFileListIterator("z-0kai", "pet/" + info.tag, 100, null);

                    total = 0;
                    while (it.hasNext()) {
                        FileInfo[] items = it.next();
                        total += items.length;
                        File file = new File(SAVE_FILE_PATH + info.tag + "/");
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        int i = 1;
                        for (FileInfo fileInfo : items) {
                            String fileName = fileInfo.key.substring(4);
                            if (!flag) {
                                handler.sendEmptyMessage(0);
                                break;
                            }
                            Log.i(TAG, fileInfo.key);
                            try {
                                URL url = new URL(getQiNiuSignUrl(fileName));
                                URLConnection con = url.openConnection();
                                int contentLength = con.getContentLength();
                                InputStream is = con.getInputStream();
                                byte[] bs = new byte[1024];
                                int len;
                                OutputStream os = new FileOutputStream(new File(SAVE_FILE_PATH + fileName));
                                while ((len = is.read(bs)) != -1) {
                                    os.write(bs, 0, len);
                                }
                                os.close();
                                is.close();
                                handler.sendEmptyMessage(i);
                                i++;
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(-1);
                            }
                        }
                    }
                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
