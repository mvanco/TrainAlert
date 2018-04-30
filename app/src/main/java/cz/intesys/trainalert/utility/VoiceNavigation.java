package cz.intesys.trainalert.utility;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.RawRes;

import java.util.ArrayList;
import java.util.List;

public class VoiceNavigation implements MediaPlayerQueue.OnInteractionListener {

    public static final int PARALLEL_REQUEST = 2;
    private static VoiceNavigation sInstance;
    private Context mContext;
    private boolean mPlaying = false;
    private MediaPlayerQueue mWorkingQueue;
    private List<MediaPlayerQueue> mQueues;

    private VoiceNavigation(Context context) {
        mContext = context;
        mWorkingQueue = MediaPlayerQueue.create(context); // Create initial working queue;
        mQueues = new ArrayList<MediaPlayerQueue>();
    }

    public static VoiceNavigation getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new VoiceNavigation(context);
        }

        return sInstance;
    }

    @Override public void onCompletion(MediaPlayer mp) {
        if (!hasNextNavigation()) {
            mPlaying = false;
        } else {
            MediaPlayerQueue mpq = getNextNavigation();
            mpq.setOnInteractionListener(this);
            mpq.play();
        }

        mQueues.remove(0); // Removes playing navigation and makes next as currently playign.
    }

    public void addToQueue(@RawRes int id) {
        mWorkingQueue.addToQueue(id);
    }

    public void play() {
        if (mQueues.size() >= PARALLEL_REQUEST) {
            mWorkingQueue.clearQueue(); // We only throw this prepared navigation.
            return;
        }

        mQueues.add(mWorkingQueue); // Add to playlist.

        if (!mPlaying) {
            mWorkingQueue.setOnInteractionListener(this);
            mWorkingQueue.play();
            mPlaying = true;
        }

        mWorkingQueue = MediaPlayerQueue.create(mContext); // Create new working queue.
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    public void cancel() {
        if (isPlaying()) {
            getPlayingNavigation().cancel();
            mPlaying = false;
        }
        mWorkingQueue.clearQueue();
        mQueues.clear();
    }

    private MediaPlayerQueue getPlayingNavigation() {
        if (!mQueues.isEmpty()) {
            return mQueues.get(0);
        } else {
            return null;
        }
    }

    private boolean hasNextNavigation() {
        return mQueues.size() > 1;
    }

    private MediaPlayerQueue getNextNavigation() {
        if (hasNextNavigation()) {
            return mQueues.get(1);
        } else {
            return null;
        }
    }
}
