package cz.intesys.trainalert.utility;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.RawRes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayerQueue implements MediaPlayer.OnCompletionListener {

    Context mContext;
    List<Integer> mSoundResources;
    MediaPlayer mMediaPlayer;
    OnInteractionListener mListener;

    public interface OnInteractionListener {
        void onCompletion(MediaPlayer mp);
    }

    private MediaPlayerQueue(Context context) {
        mContext = context;
        mSoundResources = new ArrayList<>();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.reset();
        MediaPlayer mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
    }

    public static MediaPlayerQueue create(Context context) {
        return new MediaPlayerQueue(context);
    }

    @Override public void onCompletion(MediaPlayer mp) {
        if (!mSoundResources.isEmpty()) {
            mMediaPlayer.reset(); // Enables initialization.
            playNextFile();
        } else {
            if (mListener != null) {
                mListener.onCompletion(mp);
            }
            cancel();
        }
    }

    public void setOnInteractionListener(OnInteractionListener listener) {
        mListener = listener;
    }

    public void addToQueue(@RawRes int id) {
        mSoundResources.add(id);
    }

    public void clearQueue() {
        if (!mMediaPlayer.isPlaying()) {
            mSoundResources.clear();
        }
    }

    public void play() {
        playNextFile();
    }

    public void cancel() {
        mMediaPlayer.release();
    }

    private void playNextFile() {
        try {
            mMediaPlayer.setOnCompletionListener(this);
            AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(mSoundResources.get(0));
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mSoundResources.remove(0);
    }
}


