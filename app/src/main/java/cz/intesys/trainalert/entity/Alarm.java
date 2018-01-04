package cz.intesys.trainalert.entity;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.intesys.trainalert.utility.Utility;

public class Alarm implements Parcelable {
    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };
    private @StringRes int message;
    private Uri ringtone;
    private int distance;
    private boolean enabled;
    private Poi poi; // Must have exactly one Poi which is related to

    public Alarm(@StringRes int message, int distance, Poi poi) {
        this.message = message;
        this.distance = distance;
        this.ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        this.enabled = true; // Default behaviour.
        this.poi = poi;
    }

    protected Alarm(Parcel in) {
        message = in.readInt();
        ringtone = in.readParcelable(Uri.class.getClassLoader());
        distance = in.readInt();
        enabled = in.readByte() != 0;
    }

    /**
     * Simlify creation of multiple alarms using the same message
     *
     * @param messageRes must have %1$d format parameter
     * @param distances  number in unit used by {@link org.osmdroid.util.GeoPoint} distanceTo() method
     * @return list of created alarms
     */
    public static List<Alarm> createAlarms(@StringRes int messageRes, Poi poi, int[] distances) {
        List<Alarm> alarms = new ArrayList<Alarm>();
        for (int distance : distances) {
            alarms.add(new Alarm(messageRes, distance, poi));
        }
        return alarms;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(message);
        dest.writeParcelable(ringtone, flags);
        dest.writeInt(distance);
        dest.writeByte((byte) (enabled ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public List<Alarm> toArray() {
        return Collections.singletonList(this);
    }

    public @StringRes
    int getMessage() {
        return message;
    }

    public String getMessageText(Context context) {
        String plainMessage = context.getResources().getString(message);
        if (plainMessage.contains("%1$d")) {
            return context.getResources().getString(message, distance);
        } else {
            return plainMessage;
        }
    }

    public Poi getPoi() {
        return poi;
    }

    public int getDistance() {
        return distance;
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public void playSound(Context context) {
        Utility.playSound(ringtone, context);
    }
}
