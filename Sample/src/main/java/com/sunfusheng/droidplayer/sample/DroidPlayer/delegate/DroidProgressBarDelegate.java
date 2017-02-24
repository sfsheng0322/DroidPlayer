package com.sunfusheng.droidplayer.sample.DroidPlayer.delegate;

import android.util.Log;
import android.widget.ProgressBar;

import com.sunfusheng.droidplayer.sample.DroidPlayer.DroidMediaPlayer;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sunfusheng on 2017/2/24.
 */
public class DroidProgressBarDelegate implements IBaseDelegate {

    private static final String TAG = "----> ProgressDelegate";
    private WeakReference<ProgressBar> progressBar;

    private static final int TIME_DELAY = 1000; // 延时时间
    private static final int TIME_INTERVAL = 1000; // 间隔时间
    public long mDuration; // 视频总时间，毫秒
    private long mCurrentPosition; // 当前的播放位置
    private int mLastBufferingProgress; // 上一次缓冲进度

    private Timer mTimer;
    private PositionTimerTask mTimerTask;

    public DroidProgressBarDelegate() {
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = new WeakReference<ProgressBar>(progressBar);
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    @Override
    public void init() {
        mCurrentPosition = 0;
        mLastBufferingProgress = 0;
        if (getProgressBar() != null) {
            getProgressBar().setSecondaryProgress(0);
            getProgressBar().setProgress(0);
        }
        startTimer();
    }

    @Override
    public void unInit() {
        cancelTimer();
    }

    public void startTimer() {
        cancelTimer();
        mTimer = new Timer();
        mTimerTask = new PositionTimerTask();
        mTimer.schedule(mTimerTask, TIME_DELAY, TIME_INTERVAL);
    }

    public void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    public ProgressBar getProgressBar() {
        if (progressBar.get() != null) {
            return progressBar.get();
        }
        return null;
    }

    // 设置缓冲进度
    public void setBufferingProgress(int progress) {
        if (getProgressBar() == null) return;
        if (mLastBufferingProgress == progress) return;
        if (progress < 0) progress = 0;
        if (progress > 100) progress = 100;
        Log.d(TAG, "setBufferingProgress() progress: " + progress);
        this.mLastBufferingProgress = progress;
        getProgressBar().setSecondaryProgress(progress);
    }

    // 设置播放进度
    public void setPlayedProgress() {
        if (getProgressBar() == null || mDuration == 0) return;
        mCurrentPosition += TIME_INTERVAL;
        if (mCurrentPosition > mDuration) mCurrentPosition = mDuration;
        int playedProgress = (int) (mCurrentPosition * 100f / mDuration);
        if (playedProgress > 100) playedProgress = 100;
        getProgressBar().setProgress(playedProgress);
    }

    public class PositionTimerTask extends TimerTask {
        @Override
        public void run() {
            if (DroidMediaPlayer.getInstance().isPlaying()) {
                setPlayedProgress();
            }
        }
    }
}
