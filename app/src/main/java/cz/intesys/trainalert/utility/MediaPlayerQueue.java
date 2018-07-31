package cz.intesys.trainalert.utility;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.RawRes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.TaConfig;

public class MediaPlayerQueue implements MediaPlayer.OnCompletionListener {

    Context mContext;
    List<Integer> mSoundResources;
    ArrayList<MediaPlayer> mMediaPlayerList;  // Detect already playing sounds in this MediaPlayerQueue
    OnInteractionListener mListener;

    public interface OnInteractionListener {
        void onCompletion(MediaPlayer mp);  // When last sound in queue has been finished.
    }

    private MediaPlayerQueue(Context context) {
        mContext = context;
        mSoundResources = new ArrayList<>();
        mMediaPlayerList = new ArrayList<>();
    }

    public static MediaPlayerQueue create(Context context) {
        return new MediaPlayerQueue(context);
    }

    @Override public void onCompletion(MediaPlayer mp) {
        if (!mMediaPlayerList.isEmpty()) {
            mMediaPlayerList.remove(0).release();
        }

        if (mMediaPlayerList.isEmpty()) {
            if (mListener != null) {
                mListener.onCompletion(mp);
            }
        }
    }

    private boolean isPlaying() {
        return !mMediaPlayerList.isEmpty() && mMediaPlayerList.get(0).isPlaying();
    }

    public void setOnInteractionListener(OnInteractionListener listener) {
        mListener = listener;
    }

    public void addToQueue(@RawRes int id) {
        mSoundResources.add(id);
    }

    public void clearQueue() {
        if (!isPlaying()) {
            mSoundResources.clear();
        }
    }

    public void play() {
        onPlayNextFile();
    }

    public void cancel() {
        for (MediaPlayer mp : mMediaPlayerList) {
            mp.release();
        }
    }

    /**
     * Play next sound from queue, queue cannot be empty!
     */
    private void onPlayNextFile() {
        if (mSoundResources.isEmpty()) {
            return;
        }
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(this);
            AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(mSoundResources.get(0));
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mMediaPlayerList.add(mediaPlayer);
            new Handler().postDelayed(
                    () -> {
                        if (!mSoundResources.isEmpty()) {
//                            mediaPlayer.reset(); // Enables initialization.
                            onPlayNextFile();
                        }
                    },
                    mediaPlayer.getDuration() + TaConfig.VOICE_NAVIGATION_TIME_PADDING
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSoundResources.remove(0);
    }
}


