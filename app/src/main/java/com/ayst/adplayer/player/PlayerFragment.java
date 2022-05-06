package com.ayst.adplayer.player;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.ayst.adplayer.R;
import com.ayst.adplayer.common.BaseFragment;
import com.ayst.adplayer.data.FileInfo;
import com.ayst.adplayer.data.HomeTemplate;
import com.ayst.adplayer.data.PlanInfo;
import com.ayst.adplayer.data.PlanListInfo;
import com.ayst.adplayer.data.PlayListInfo;
import com.ayst.adplayer.dialogs.CustomDialog;
import com.ayst.adplayer.event.MessageEvent;
import com.ayst.adplayer.service.StreamService;
import com.ayst.adplayer.settings.Setting;
import com.ayst.adplayer.settings.SettingActivity;
import com.ayst.adplayer.smb.HttpServer;
import com.ayst.adplayer.utils.AppUtils;
import com.ayst.adplayer.utils.GetFilesUtil;
import com.ayst.adplayer.utils.ImageEffectUtils;
import com.ayst.adplayer.utils.SupportFileUtils;
import com.ayst.adplayer.view.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class PlayerFragment extends BaseFragment implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener,
        SurfaceHolder.Callback,
        MediaController.MediaPlayerControl {

    private static final String TAG = "PlayerFragment";

    private static final int MSG_LOAD_PLAYLIST_COMPLETE = 1000;

    @BindView(R.id.surface)
    SurfaceView mPreview;
    @BindView(R.id.banner)
    Banner mBanner;
    @BindView(R.id.media_controller)
    MediaController mMediaController;
    Unbinder unbinder;
    @BindView(R.id.layout_blank)
    LinearLayout mBlankLayout;
    @BindView(R.id.video_container)
    RelativeLayout mVideoContainer;

    private ArrayList<FileInfo> mPlaylist = new ArrayList<>();
    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder holder;
    private String path;
    private Bundle extras;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private int mNextPlayIndex = 0;
    private int mMusicNextPlayIndex = 0;
    private String mCurPlayPath = "";
    private boolean mIsShortcutPlay = false;

    private List<Uri> mPictureList = new ArrayList<>();
    private List<FileInfo> mMusicList = new ArrayList<>();
    private int mPicturePlayIndex = -1;

    private int Old_Mode,Old_size = 0;
    private int New_Mode,New_size = 0;
    private int check = 0;
    private Calendar mCalendar;
    private int mNextPlanIndex = 0;

    private MediaPlayer mAudioPlayer;
    private Display mDisplay;
    private OnFullscreenListener mOnFullscreenListener;

    private static boolean sUsbLock = false;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_LOAD_PLAYLIST_COMPLETE:
                    Log.d("handle_test","모드변경");
                    check = 1;
                    playNext();

                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCalendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        changeVideoSize();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        //开始轮播
        mNextPlayIndex = 0;
        if(mMediaPlayer != null){
            mPlaylist.clear();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;

        }
        check = 0;
        Log.d("test_stop","stop");
        if (null != mBanner) {
            Log.d("test_stop","banner");
            mBanner.stopAutoPlay();
            mBanner.setVisibility(View.GONE);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        New_Mode = Setting.get(mContext).getPlayMode();
        New_size = Setting.get(mContext).getSelectedPlayListIndex();




        if(Old_Mode == New_Mode && Old_size == New_size) {
            //mMediaPlayer.reset();
            Log.d("test_resume", "모드변경되지 않음");
            Log.d("test_resume", "old_size = " + Old_size);
            Log.d("test_resume", "new_size = " + New_size);
            Log.d("test_resume", "리스트 변경되지 않음");
            if(mMediaPlayer == null){
                

            }else {
                Log.d("test_resume", "재시작");
                //mNextPlayIndex = 0;
                //playNext();
                start();
            }
        }
        else {
            Log.d("test_resume", "리스트 변경됨");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Old_Mode = Setting.get(mContext).getPlayMode();
        Old_size = Setting.get(mContext).getSelectedPlayListIndex();
        Log.d("test_resume", "old_size = " + Old_size);
        Log.d("test_resume", "Old_Mode = " + Old_Mode);
        pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (MessageEvent.MSG_PLAY_MODE_CHANGE.equals(event.getMessage())) {
            loadPlaylist();
        } else if (MessageEvent.MSG_PICTURE_SETTING_CHANGE.equals(event.getMessage())) {
            mBanner.setDelayTime(Setting.get(mContext).getPictureDuration() * 1000);
            mBanner.setBannerAnimation(ImageEffectUtils.getEffect(Setting.get(mContext).getPictureEffect()));
            mMusicList = Setting.get(mContext).getPictureMusic();
            mMusicNextPlayIndex = 0;
            if (mBanner.getVisibility() == View.VISIBLE) {
                playMusicNext();
            }
        } else if (MessageEvent.MSG_USB_MOUNTED.equals(event.getMessage())
                || MessageEvent.MSG_USB_UNMOUNTED.equals(event.getMessage())) {
            if (Setting.get(mContext).getPlayMode() == Setting.PLAY_MODE_NONE) {
                if (!sUsbLock) {
                    sUsbLock = true;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadPlaylist();
                            sUsbLock = false;
                        }
                    }, 5000);
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean ret = false;
        Log.i(TAG, "dispatchKeyEvent, key code: " + event.getKeyCode() + ", action: " + event.getAction());

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();

            if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                int code = keyCode - KeyEvent.KEYCODE_0;
                String dir = String.valueOf(code);
                List<String> pathList = GetFilesUtil.getStorageList(mContext);
                for (String rootDir : pathList) {
                    String path = rootDir + "/" + dir;
                    if (SupportFileUtils.hasMediaFile(path)) {
                        mIsShortcutPlay = true;
                        loadPlaylist(path);
                    }
                }
                ret = true;
            }

            if (KeyEvent.KEYCODE_DPAD_UP == keyCode
                    || KeyEvent.KEYCODE_DPAD_DOWN == keyCode
                    || KeyEvent.KEYCODE_DPAD_LEFT == keyCode
                    || KeyEvent.KEYCODE_DPAD_RIGHT == keyCode) {
                mMediaController.show(5000);
            }
        }

        if (!ret) {
            ret = super.dispatchKeyEvent(event);
        }
        return ret;
    }

    private void initView() {
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        //设置图片集合
//        mBanner.setImages(images);
        //设置banner动画效果
        mBanner.setBannerAnimation(ImageEffectUtils.getEffect(Setting.get(mContext).getPictureEffect()));
        //设置标题集合（当banner样式有显示title时）
//        mBanner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);
        //设置轮播时间
        mBanner.setDelayTime(Setting.get(mContext).getPictureDuration() * 1000);
        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
//        mBanner.start();
        mBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected, position=" + position + " size=" + mPictureList.size());
                Log.d("test_duration = ", String.valueOf(Setting.get(mContext).getPictureDuration()));
                if (mPictureList.size() != mPlaylist.size()) {

                    if (position != mPicturePlayIndex && position >= mPictureList.size() - 1) {
                        mBanner.stopAutoPlay();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("test_page = ", "run start");
                                playNext();
                            }
                        }, Setting.get(mContext).getPictureDuration() * 1000);
                    }
                } else {
                    if (position >= mPictureList.size() - 1) {
                        Log.i(TAG, "onPageSelected, mIsShortcutPlay=" + mIsShortcutPlay);
                        if (mIsShortcutPlay) {
                            mIsShortcutPlay = false;
                            loadPlaylist();
                        }
                    }
                }
                mPicturePlayIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mMediaController.setControlCallback(this);
    }

    private void initData() {

        mPreview.setBackgroundResource(R.drawable.init_image);
        System.out.println("initDate시작!");
        holder = mPreview.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBA_8888);

        WindowManager windowMgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = windowMgr.getDefaultDisplay();

        loadPlaylist();
        mMediaController.hide();
    }

    private void loadPlaylist(final String path) {
        System.out.println("loadPlaylist시작!");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "loadPlaylist, path: " + path);
                mNextPlayIndex = 0;
                mPlaylist.clear();
                mHandler.removeCallbacks(mLoadPlanRunnable);
                if (TextUtils.isEmpty(path)) {
                    int mode = Setting.get(mContext).getPlayMode();
                    Log.i(TAG, "loadPlaylist, mode: " + mode);
                    switch (mode) {
                        case Setting.PLAY_MODE_DIR:
                            loadPlaylistFromSelectedPath();
                            break;
                        case Setting.PLAY_MODE_PLAYLIST:
                            loadPlaylistFromPlaylist();
                            break;
                        case Setting.PLAY_MODE_PLAN:
                            loadPlaylistFromPlan(true);
                            mHandler.postDelayed(mLoadPlanRunnable, 60 * 1000);
                            break;
                        case Setting.PLAY_MODE_SHARE:
                            loadPlaylistFromSharePath();
                            break;
                        case Setting.PLAY_MODE_HTTP:
                            loadPlaylistFromHttpPlaylist();
                            break;
                        case Setting.PLAY_MODE_NONE:
                        default:
                            loadPlaylistFromRootPath();
                            break;
                    }
                } else {
                    loadPlaylistFromPath(path);
                }

                mMusicList = Setting.get(mContext).getPictureMusic();
                mHandler.sendEmptyMessage(MSG_LOAD_PLAYLIST_COMPLETE);
            }
        }).start();
    }

    private void loadPlaylist() {
        loadPlaylist("");
    }

    private void loadPlaylistFromRootPath() {
        List<String> pathList = GetFilesUtil.getStorageList(mContext);
        for (String path : pathList) {
            if (!TextUtils.isEmpty(path)) {
                Log.i(TAG, "loadPlaylistFromRootPath, path=" + path);
                List<FileInfo> list = GetFilesUtil.getSonNode(path, GetFilesUtil.TYPE_FILE, SupportFileUtils.SUPPORT_MEDIA_FILE_SUFFIX);
                if (list != null && !list.isEmpty()) {
                    Collections.sort(list, GetFilesUtil.defaultOrder());
                    mPlaylist.addAll(list);
                }
            }
        }
    }

    private void loadPlaylistFromSelectedPath() {
        String path = Setting.get(mContext).getSelectedPlayPath();
        loadPlaylistFromPath(path);
    }

    private void loadPlaylistFromPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            List<FileInfo> list = GetFilesUtil.getSonNode(path, GetFilesUtil.TYPE_FILE, SupportFileUtils.SUPPORT_MEDIA_FILE_SUFFIX);
            if (list != null) {
                Collections.sort(list, GetFilesUtil.defaultOrder());
                mPlaylist.addAll(list);
            }
        }
    }

    private void loadPlaylistFromPlaylist() {
        List<PlayListInfo> infos = Setting.get(mContext).getPlayList();
        int index = Setting.get(mContext).getSelectedPlayListIndex();

        if (infos != null && index < infos.size()) {
            List<FileInfo> list = infos.get(index).getPlaylist();
            mPlaylist.addAll(list);
        }
    }

    private boolean loadPlaylistFromPlan(boolean isFirst) {
        List<PlanListInfo> infos = Setting.get(mContext).getPlanList();
        int index = Setting.get(mContext).getSelectedPlanListIndex();
        if (infos != null && index < infos.size()) {
            List<PlanInfo> planInfoList = infos.get(index).getPlanInfoList();
            if (!planInfoList.isEmpty()) {
                if (planInfoList.size() > 1) {
                    mCalendar.setTimeInMillis(System.currentTimeMillis());
                    int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                    int min = mCalendar.get(Calendar.MINUTE);

                    if (isFirst) {
                        mNextPlanIndex = 0;
                        for (int i = 0; i < planInfoList.size() - 1; i++) {
                            PlanInfo plan = planInfoList.get(i);
                            PlanInfo nextPlan = planInfoList.get(i + 1);
                            if (hour * 100 + min > plan.getHour() * 100 + plan.getMin()
                                    && hour * 100 + min < nextPlan.getHour() * 100 + nextPlan.getMin()) {
                                break;
                            }
                            mNextPlanIndex++;
                        }
                    }

                    if (mNextPlanIndex >= planInfoList.size()) {
                        mNextPlanIndex = 0;
                    }
                    PlanInfo plan = planInfoList.get(mNextPlanIndex);
                    Log.i(TAG, "loadPlaylistFromPlan, mNextPlanIndex=" + mNextPlanIndex
                            + ", hour=" + hour + ", min=" + min
                            + ", plan hour=" + plan.getHour() + ", plan min=" + plan.getMin()
                            + ", is first:" + isFirst);
                    if ((plan.getHour() == hour && plan.getMin() == min) || isFirst) {
                        PlayListInfo playListInfo = Setting.get(mContext).getPlayListById(plan.getPlaylistId());
                        if (null != playListInfo) {
                            mNextPlayIndex = 0;
                            mPlaylist.clear();

                            List<FileInfo> list = playListInfo.getPlaylist();
                            mPlaylist.addAll(list);
                            mNextPlanIndex++;

                            return true;
                        }
                    }
                } else {
                    if (isFirst) {
                        PlayListInfo playListInfo = Setting.get(mContext).getPlayListById(planInfoList.get(0).getPlaylistId());
                        if (null != playListInfo) {
                            mNextPlayIndex = 0;
                            mPlaylist.clear();

                            List<FileInfo> list = playListInfo.getPlaylist();
                            mPlaylist.addAll(list);

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private void loadPlaylistFromSharePath() {
        String path = Setting.get(mContext).getSelectedPlayPathShare();
        SmbFile smbFile = null;
        try {
            smbFile = new SmbFile(path);
            SmbFile[] files = smbFile.listFiles();
            for (SmbFile file : files) {
                for (String suffix : SupportFileUtils.SUPPORT_MEDIA_FILE_SUFFIX) {
                    if (file.getPath().endsWith("." + suffix)) {
                        final URL url = file.getURL();
                        final Uri.Builder builder = new Uri.Builder();
                        builder.scheme("http").encodedAuthority("127.0.0.1:" + HttpServer.PORT)
                                .encodedPath(HttpServer.URI_PREFIX)
                                .appendEncodedPath(url.getAuthority())
                                .appendEncodedPath(Uri.encode(url.getPath().substring(1), "/"));
                        Uri uri = builder.build();

                        FileInfo fileInfo = new FileInfo();
                        fileInfo.setUri(uri);
                        fileInfo.setType(FileInfo.FILE_TYPE_SMB);
                        Log.i(TAG, "loadPlaylistFromSharePath, uri:" + fileInfo.getUri().toString());

                        mPlaylist.add(fileInfo);
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }
        if (!mPlaylist.isEmpty()) {
            if (!StreamService.isRunning()) {
                mContext.startService(new Intent(mContext, StreamService.class));
            }
        }
    }

    private void loadPlaylistFromHttpPlaylist() {
        List<PlayListInfo> infos = Setting.get(mContext).getHttpPlayList();
        int index = Setting.get(mContext).getSelectedHttpPlayListIndex();
        if (infos != null && index < infos.size()) {
            List<FileInfo> list = infos.get(index).getPlaylist();
            mPlaylist.addAll(list);
        }
    }

    Runnable mLoadPlanRunnable = new Runnable() {
        @Override
        public void run() {
            if (loadPlaylistFromPlan(false)) {
                Log.d("test_index", "Runnable");
                playNext();
            }
            mHandler.removeCallbacks(mLoadPlanRunnable);
            mHandler.postDelayed(this, 60 * 1000);
        }
    };

    private synchronized void playMusicNext() {
        if (null != mMusicList && !mMusicList.isEmpty()) {
            FileInfo nextFile = mMusicList.get(mMusicNextPlayIndex);
            if (SupportFileUtils.isAudio(nextFile.getPath())) {
                play(nextFile.getPath());
                mMusicNextPlayIndex++;
                if (mMusicNextPlayIndex >= mMusicList.size()) {
                    mMusicNextPlayIndex = 0;
                }
            }
        }
    }

    private void playNext() {
        Log.d("test_index", String.valueOf(mNextPlayIndex));
        System.out.println("playNext시작!!");
        Log.i(TAG, "playNext, mPlaylist is " + (mPlaylist.isEmpty() ? "null" : "not null"));
        synchronized (this) {
            if (!mPlaylist.isEmpty()) {
                mPreview.setBackgroundResource(R.color.transparent);
                System.out.println("playNext영상시작!!");
                if (mNextPlayIndex >= mPlaylist.size()) {
                    mNextPlayIndex = 0;
                    if (mIsShortcutPlay) {
                        Log.i(TAG, "playNext, Shortcut play complete");
                        mIsShortcutPlay = false;
                        loadPlaylist();
                        return;
                    }
                }

                FileInfo nextFile = mPlaylist.get(mNextPlayIndex);
                Log.d("test", String.valueOf(mNextPlayIndex));
                if (nextFile.getType() == FileInfo.FILE_TYPE_SMB) {
                    Log.i(TAG, "playNext, index=" + mNextPlayIndex + ", uri=" + nextFile.getUri().toString());
                } else {
                    Log.i(TAG, "playNext, index=" + mNextPlayIndex + ", path=" + nextFile.getPath());
                }

                if (nextFile.getMediaType() == FileInfo.MEDIA_TYPE_IMAGE) {
                    Log.d("test", "이미지파일 시작");
                    if (null != mMediaPlayer) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;

                        Log.d("test","리셋이요.");
                    }
                    mPictureList.clear();
                    for (int i = mNextPlayIndex; i < mPlaylist.size(); i++) {
                        FileInfo fileInfo = mPlaylist.get(i);
                        if (fileInfo.getMediaType() == FileInfo.MEDIA_TYPE_IMAGE) {
                            mPictureList.add(fileInfo.getUri());
                        } else {
                            break;
                        }
                    }
                    mNextPlayIndex += mPictureList.size();
                    Log.d("test", "이미지파일" + mNextPlayIndex);
                    mPicturePlayIndex = -1;
                    if (null != mBanner) {
                        mBanner.update(mPictureList);
                        mBanner.setVisibility(View.VISIBLE);
                    }


                    playMusicNext();
                    Log.d("test", "이미지파일 끝");
                } else {
                    Log.d("test", "영상파일");
                    mBanner.stopAutoPlay();
                    if (null != mBanner) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("test_video = ", "delay");
                                mBanner.stopAutoPlay();
                                mBanner.setVisibility(View.INVISIBLE);
                            }
                        }, 1000);

                        Log.d("test", "banner stop");
                    }

                    if (nextFile.getType() == FileInfo.FILE_TYPE_SMB) {
                        play(nextFile.getUri());
                    } else if (nextFile.getType() == FileInfo.FILE_TYPE_HTTP) {
                        play(Uri.parse(nextFile.getUrl()));
                    } else {
                        Log.d("test", "일반영상파일");
                        play(nextFile.getPath());
                    }

                    mNextPlayIndex++;
                }
            }
        }
    }

    private void playPrev() {
        synchronized (this) {
            if (mPlaylist.size() > 1) {
                int prevIndex = mNextPlayIndex;
                for (int i = 0; i < 2; i++) {
                    prevIndex -= 1;
                    if (prevIndex < 0) {
                        prevIndex = mPlaylist.size() - 1;
                    }
                    while (prevIndex > 0 && SupportFileUtils.isPicture(mPlaylist.get(prevIndex).getPath())) {
                        prevIndex--;
                    }
                }
                mNextPlayIndex = prevIndex;
                playNext();
            }
        }
    }

    private void play(String path) {
        File file = new File(path);
        if (!file.isDirectory() && file.exists()) {
            play(Uri.fromFile(file));
        } else {
            mPreview.setBackgroundResource(R.drawable.init_image);
            Log.e(TAG, "play, path:" + path + " invalid.");
        }
    }

    private void play(Uri uri) {

        Log.i(TAG, "play, Uri: " + uri.toString());
        mCurPlayPath = uri.getPath();
        doCleanUp();
        try {

            // Create a new media player and set the listeners
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnBufferingUpdateListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnVideoSizeChangedListener(this);
                Log.d("test_play","null!!");
                ((Activity) mContext).setVolumeControlStream(AudioManager.STREAM_MUSIC);
            } else {
                mMediaPlayer.reset();
            }


            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.setDataSource(mContext, uri);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated");
        if(mMediaPlayer != null){
            mNextPlayIndex = 0;
            playNext();
        }
        Log.d(TAG,  String.valueOf(check));
        if(check != 1){
            loadPlaylist();
        }

       // playNext();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int width, int height) {
        Log.d(TAG, "surfaceChanged, width(" + width + ") or height(" + height + ")");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfacedestroy");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        Log.i(TAG, "onPrepared");
        if (SupportFileUtils.isAudio(mCurPlayPath)) {
            mMediaPlayer.start();
        } else {
            mIsVideoReadyToBePlayed = true;
            if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
                Log.d("test_prepare","시작");
                startVideoPlayback();
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "onCompletion");
        //mMediaPlayer.release();

        if (SupportFileUtils.isAudio(mCurPlayPath)) {
            playMusicNext();
        } else {
            playNext();
        }

    }



    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.i(TAG, "onBufferingUpdate, percent:" + percent);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.d(TAG, "onVideoSizeChanged, video width(" + width + ") or height(" + height + ")");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;

        changeVideoSize();

        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        mMediaPlayer.start();
        mMediaController.update();
    }

    public void changeVideoSize() {
//        int width = mVideoWidth;
//        int height = mVideoHeight;
//        int windowWidth = mVideoContainer.getWidth();
//        int windowHeight = mVideoContainer.getHeight();
//
//        float ratio = Math.max((float) width / (float) windowWidth, (float) height / (float) windowHeight);
//
//        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
//        width = (int) Math.ceil((float) width / ratio);
//        height = (int) Math.ceil((float) height / ratio);
//
//        Log.i(TAG, "changeVideoSize, windowWidth=" + windowWidth + ", windowHeight=" + windowHeight + ", mVideoWidth=" + width + ", mVideoHeight=" + height + ", ratio=" + ratio);
//
//        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView让视频自动填充。
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPreview.getLayoutParams();
//        params.width = width;
//        params.height = height;
//        mPreview.setLayoutParams(params);
    }

    private void showSaveTemplateDialog() {
        final EditText editText = new EditText(mContext);
        editText.setHint(R.string.save_as_template_hint);
        new CustomDialog.Builder(mContext)
                .setTitle(R.string.save_as_template)
                .setContentView(editText)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HomeTemplate template = Setting.get(mContext).getCurHomeTemplate();
                        if (null != template) {
                            template.setId(System.currentTimeMillis());
                            template.setName(editText.getText().toString());
                            Setting.get(mContext).saveCurHomeTemplate(template);
                            MessageEvent msg = new MessageEvent(MessageEvent.MSG_TEMPLATE_CHANGED);
                            EventBus.getDefault().post(msg);
                        }
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    @Override
    public void start() {
        if (null != mMediaPlayer) {
           // mMediaPlayer.reset();
            //mNextPlayIndex = 0;
            mMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (null != mMediaPlayer) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void previous() {
        playPrev();
    }

    @Override
    public void next() {
        Log.d("test_netxt","next");
        //mPreview.setVisibility(View.GONE);
        playNext();
    }

    @Override
    public void setting() {
        mContext.startActivity(new Intent(mContext, SettingActivity.class));
    }

    @Override
    public void save() {
        showSaveTemplateDialog();
    }

    @Override
    public void fullscreen() {
        if (null != mOnFullscreenListener) {
            mOnFullscreenListener.fullscreen();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeVideoSize();
            }
        }, 100);

    }

    @Override
    public void cancelFullscreen() {
        if (null != mOnFullscreenListener) {
            mOnFullscreenListener.cancelFullscreen();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeVideoSize();
            }
        }, 100);
    }

    @Override
    public boolean isPlaying() {
        if (null != mMediaPlayer) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public boolean isFullscreen() {
        if (null != mOnFullscreenListener) {
            return mOnFullscreenListener.isFullscreen();
        }

        return false;
    }

    public void setOnFullscreenListener(OnFullscreenListener listener) {
        this.mOnFullscreenListener = listener;
    }

    @OnClick({R.id.layout_blank})
    public void onViewClicked(View view) {
        Log.i(TAG, "onViewClicked");
        if(AppUtils.isStartByTimeout){
            System.exit(0);
        }
        switch (view.getId()) {
            case R.id.layout_blank:
                if (mMediaController.isHidden()) {
                    mMediaController.show(5000);
                } else {
                    mMediaController.hide();
                }
                break;
        }
    }

    public interface OnFullscreenListener {
        void fullscreen();

        void cancelFullscreen();

        boolean isFullscreen();
    }
}
