package cz.intesys.trainalert.entity;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

public class Alarm {
    private String message;
    private Uri ringtone;
    private int distance;
    private boolean enabled;
    private POI poi; // Must have exactly one POI which is related to

    public Alarm(String message, int distance, POI poi) {
        this.message = message;
        this.distance = distance;
        this.ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        this.enabled = true; // Default behaviour.
        this.poi = poi;
    }

    /**
     * Simlify creation of multiple alarms using the same message
     *
     * @param context
     * @param messageRes must have %1$d format parameter
     * @param distances  number in unit used by {@link org.osmdroid.util.GeoPoint} distanceTo() method
     * @return list of created alarms
     */
    public static List<Alarm> createAlarms(Context context, @StringRes int messageRes, POI poi, int[] distances) {
        List<Alarm> alarms = new ArrayList<Alarm>();
        for (int distance : distances) {
            String message = context.getString(messageRes, distance);
            alarms.add(new Alarm(message, distance, poi));
        }
        return alarms;
    }

    public String getMessage() {
        return message;
    }

    public POI getPoi() {
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
}
