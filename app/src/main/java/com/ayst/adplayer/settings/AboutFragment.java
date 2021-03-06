package com.ayst.adplayer.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ayst.adplayer.R;
import com.ayst.adplayer.common.BaseFragment;
import com.ayst.adplayer.upgrade.AppUpgradeManager;
import com.ayst.adplayer.utils.AppUtils;
import com.ayst.adplayer.view.XianHeiFontTextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class AboutFragment extends BaseFragment {

    @BindView(R.id.tv_module)
    XianHeiFontTextView mModuleTv;
    @BindView(R.id.tv_sn)
    XianHeiFontTextView mSnTv;
    @BindView(R.id.tv_app_version)
    XianHeiFontTextView mAppVersionTv;
    @BindView(R.id.tv_sys_version)
    XianHeiFontTextView mSysVersionTv;
    @BindView(R.id.tv_android_version)
    XianHeiFontTextView mAndroidVersionTv;
    @BindView(R.id.tv_mac)
    XianHeiFontTextView mMacTv;
    @BindView(R.id.btn_check_update)
    Button mCheckUpdateBtn;

    Unbinder unbinder;

    private boolean canUpgrade = false;
    private AppUpgradeManager mUpgradeManager = null;
    private String UpdateUrl = "http://krizer.co.kr/krizer_edit/krizer_app_version.html";
    private String Shopvodlite_Url = "http://krizer.co.kr/krizer_edit/adplayer.apk";
    private String TAG = "AboutFragment" ;
    private String App_Version = "2.3.15.3";
    private String Server_Version;
    private Intent AppListIntent;
    private ProgressDialog pDialog;
    private File targetFile;
    private String filename = "update.apk";
    private volatile boolean running = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        return view;
    }

    private void initView() {
        mModuleTv.setText(AppUtils.getProperty("ro.product.model", "adplayer"));
        mSnTv.setText(AppUtils.getProperty("ro.serialno", "AO6U44GBIM"));
        mSysVersionTv.setText(AppUtils.getProperty("ro.product.version", "1.0"));
        mAndroidVersionTv.setText(AppUtils.getProperty("ro.build.version.release", "5.1"));
        mAppVersionTv.setText(AppUtils.getVersionName(mContext));
        mMacTv.setText(AppUtils.getWifiMacAddr(mContext));
        targetFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename);

        mCheckUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (canUpgrade) {
                    mUpgradeManager.download();
                    mCheckUpdateBtn.setText(R.string.downloading);
                    mCheckUpdateBtn.setEnabled(false);
                    */
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute(UpdateUrl);
                Log.d(TAG,"App version = "+App_Version);


            }
        });

        mUpgradeManager = new AppUpgradeManager(mContext);
        mUpgradeManager.check(new AppUpgradeManager.OnFoundNewVersionInterface() {
            @Override
            public void onFoundNewVersion(String version, String introduction, String url) {
                mCheckUpdateBtn.setText(getString(R.string.upgrade_to) + version);
                canUpgrade = true;
            }

            @Override
            public void onNotFoundNewVersion() {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        terminate();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_check_update)
    public void onViewClicked() {

    }

    public void terminate() {
        running = false;
    }

    private class JsoupAsyncTask extends AsyncTask<String, Void, Void> {
        @Override protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override protected Void doInBackground(String... params) {
            Log.i(TAG, Arrays.toString(params));
            try {
                // ??????????????? ????????? ?????? ???????????? url ??? ????????? ????????????.
                String callUrl = params[0];
                Log.e(TAG, callUrl);
                Document doc = Jsoup.connect(callUrl).get();
                // ?????? html tag?????? ??????????????? ?????? ?????? span tag ??? class?????? ?????????.
                Elements links = doc.select(".Shopvod_lite_version");
                Log.e(TAG, "links=" + links.size());

                for(Element el : links) {
                    Log.e(TAG, el.text()) ;
                    Server_Version = el.text() ;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } return null;
        }
        @Override protected void onPostExecute(Void result) {
            if(App_Version.equals(Server_Version)) {
                Log.d(TAG,"Server version = "+Server_Version);
                Toast.makeText(mContext, R.string.newest, Toast.LENGTH_SHORT).show();
            }else{
                Log.d(TAG,"else Server version = "+Server_Version);
                if(InternetCheck()){
                        CheckVer();
                }else {
                    Toast.makeText(mContext, "???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    void CheckVer() {
        new Thread() {
            public void run() {
                    try {
                        URL url;
                        HttpURLConnection conn = null;
                        url = new URL(Shopvodlite_Url); // ????????? ????????????.
                        conn = (HttpURLConnection) url.openConnection();
                        AppListIntent = new Intent(Intent.ACTION_MAIN, null);
                        AppListIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                       // Runtime.getRuntime().exec("su");

                        if (200 != conn.getResponseCode()) {
                            System.out.println("con test" + conn.getResponseCode());
                        } else {
                            System.out.println("con test22 = " + conn.getResponseCode());
                            //System.out.println("contest" + conn.getResponseCode());
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // ??????????????? ?????? ??????
                                    DownloadFileAsync downloadFileAsync = new DownloadFileAsync();
                                    downloadFileAsync.execute();
                                }
                            }, 0);
                        }

                    } catch (MalformedURLException malformedURLException) {
                        malformedURLException.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

            }
        }.start();
    }

    class DownloadFileAsync extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog= new ProgressDialog(mContext); //ProgressDialog ?????? ??????
            pDialog.setTitle("???????????? ??? ?????????.");                   //ProgressDialog ??????
            pDialog.setMessage("Loading.....");             //ProgressDialog ?????????
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); //??????????????? ProgressDialog ????????? ??????
            pDialog.setCanceledOnTouchOutside(false); //ProgressDialog??? ???????????? ?????? dialog??? ???????????? ?????? ???????????? ?????? ??????
            pDialog.show(); //ProgressDialog ????????????
        }

        @Override
        protected String doInBackground(String... strings) {
            int contentLength;
            URL url;
            HttpURLConnection conn = null;
            InputStream inStream = null;
            OutputStream outStream = null;
            BufferedInputStream bin = null;
            BufferedReader reader = null;
            BufferedOutputStream bout = null;
            try {
                //Process p = Runtime.getRuntime().exec("su");
                url = new URL(Shopvodlite_Url); // ????????? ????????????.
                conn = (HttpURLConnection)url.openConnection();
                // DataOutputStream os = new DataOutputStream(p.getOutputStream());

                System.out.println("conn" + conn);

                contentLength = conn.getContentLength();
                // BufferedInputStream??? ?????? ????????? ????????? ????????? ?????? ?????????.
                inStream = conn.getInputStream();
                outStream = new FileOutputStream(targetFile.getPath());
                bin = new BufferedInputStream(inStream);
                bout = new BufferedOutputStream(outStream);
                int bytesRead = 0;
                byte[] buffer = new byte[83886080];
                long total = 0;
                while ((bytesRead = bin.read(buffer, 0, 1024)) != -1) {
                    total += bytesRead;
                    bout.write(buffer, 0, bytesRead);
                    publishProgress((int)((total*100)/contentLength));
                }
                System.out.println("start = ");

            }catch (Exception e){
                try {
                    throw e;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }finally {
                try{
                    bin.close();
                    bout.close();
                    inStream.close();
                    outStream.close();
                    conn.disconnect();
                }catch (Exception e){

                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            pDialog.setProgress(values[0]); //???????????? pos_dialog????????? ProgressDialog??? ????????? ?????? ??????
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pDialog.dismiss(); //ProgressDialog ????????? ?????? ??????
            pDialog=null;      //???????????? ?????????
            doRootStuff();
            //doInBackground() ?????????????????? ????????? ?????? "Complete Load" string Toast??? ????????? ??????
        }
    }

    boolean InternetCheck(){
        ConnectivityManager cm = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ninfo = cm.getActiveNetworkInfo();
        if(ninfo == null){
            Log.d(TAG,"??????????????? ???????????????????????????.");
            return false;
        }else{
            Log.d(TAG,"??????????????? ?????????????????????.");
            return true;
        }

    }

    public void doRootStuff(){
        try {
            String line;
            Process process = Runtime.getRuntime().exec("su");
            OutputStream stdin = process.getOutputStream();
            InputStream stderr = process.getErrorStream();
            InputStream stdout = process.getInputStream();

            if (Build.MODEL.equals("Infos_Duple")){
                stdin.write(("busybox mount -o remount,rw -t ext4 /dev/block/platform/ff0f0000.dwmmc/by-name/system /system\n").getBytes()); // "Permissive"
                stdin.write(("cp /storage/emulated/0/update.apk /system/app/ResponsiveWebview/ResponsiveWebview.apk\n").getBytes()); // E/[Error]: cp: /system/media/bootanimation_test.zip: Read-only file system
                stdin.write(("chmod 644 /system/app/ResponsiveWebview/ResponsiveWebview.apk\n").getBytes());
                stdin.write(("reboot\n").getBytes());
                Log.d(TAG, "Infos ???????????????.");
            }else if(Build.MODEL.equals("minicube_X10")){
                stdin.write(("mount -o rw,remount /system\n").getBytes()); // "Permissive"
                stdin.write(("cp /sdcard/update.apk /system/app/Adplayer_release/Adplayer_release.apk\n").getBytes()); // E/[Error]: cp: /system/media/bootanimation_test.zip: Read-only file system
                stdin.write(("chmod 644 /system/app/Adplayer_release/Adplayer_release.apk\n").getBytes());
                stdin.write(("reboot\n").getBytes());
                Log.d(TAG, "minicube_x10 ???????????????.");
            }else if(Build.MODEL.equals("U4X+CM")){
                stdin.write(("mount -o rw,remount /system\n").getBytes()); // "Permissive"
                stdin.write(("cp /sdcard/update.apk /system/app/Adplayer_release/Adplayer_release.apk\n").getBytes()); // E/[Error]: cp: /system/media/bootanimation_test.zip: Read-only file system
                stdin.write(("chmod 644 /system/app/Adplayer_release/Adplayer_release.apk\n").getBytes());
                stdin.write(("reboot\n").getBytes());
                Log.d(TAG, "cm ???????????????.");
            }

            stdin.write("exit\n".getBytes());
            stdin.flush();
            stdin.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while ((line = br.readLine()) != null) {
                Log.d("[Output]", line);
            }
            br.close();
            br = new BufferedReader(new InputStreamReader(stderr));
            while ((line = br.readLine()) != null) {
                Log.e("[Error]", line);
            }
            br.close();
            process.waitFor();
            process.destroy();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
