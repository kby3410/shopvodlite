package com.ayst.adplayer.http;

import android.os.AsyncTask;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Download extends AsyncTask<String[], Integer, String> {
    private final static String TAG = "Download";
    private Context mContext;
    private ProcessInterface mProcessObserver;
    private RequestTaskInterface mTaskObserver;
    private String mUrl;
    private String mPath;
    private boolean mIsStop = false;

    public Download(Context context, String url, String path,
                    RequestTaskInterface requestTaskInterface,
                    ProcessInterface processInterface) {
        mContext = context;
        mUrl = url;
        mPath = path;
        mTaskObserver = requestTaskInterface;
        mProcessObserver = processInterface;
        mIsStop = false;
    }

    @Override
    protected String doInBackground(String[]... strings) {
        if (!TextUtils.isEmpty(mUrl) && !TextUtils.isEmpty(mPath)) {
            return downloadToPath(mPath);
        } else {
            Log.e(TAG, "doInBackground, mUrl or mPath is null");
        }
        return null;
    }
    
    private String downloadToPath(String path) {
        Log.i(TAG, "downloadToPath start...");
        try {
            Log.i(TAG, "downloadToPath, request=" + mUrl + ", path=" + path);
            URL url = new URL(mUrl);
            HttpURLConnection connect = (HttpURLConnection)url.openConnection();
            connect.setConnectTimeout(60000);
            connect.setRequestMethod("GET");
            if (connect.getResponseCode() == 200) {
                int size = connect.getContentLength();
                InputStream inputStream = connect.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
                byte bytes[] = new byte[1024];
                int readLen = 0;
                int len = 0;
                int progress = 0;
                do {
                    len = inputStream.read(bytes);
                    if (len != -1) {
                        fileOutputStream.write(bytes, 0, len);
                        readLen += len;
                        progress = (int) (100 * ((float)readLen/(float)size));
                        Integer values[] = new Integer[1];
                        values[0] = progress;
                        publishProgress(values);
                        Log.i(TAG, "*********************");
                    } else {
                        inputStream.close();
                        fileOutputStream.close();
                        return path;
                    }
                } while (!mIsStop);
            }
            connect.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stop() {
        mIsStop = true;
    }

    @Override
    protected void onPostExecute(String s) {
        if (mTaskObserver != null) {
            mTaskObserver.postExecute(s);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mProcessObserver != null) {
            mProcessObserver.updateProcess(values[0]);
        }
    }

    public interface ProcessInterface {
        public abstract void updateProcess(int process);
    }
}
