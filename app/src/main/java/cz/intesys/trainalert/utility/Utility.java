package cz.intesys.trainalert.utility;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.entity.Poi;
import io.reactivex.Observable;

import static java.lang.Math.PI;
import static java.lang.Math.atan;

public class Utility {

    public static void playSound(Uri notificationSound, Context context) {
        try {
            Ringtone r = RingtoneManager.getRingtone(context, notificationSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param startPosition
     * @param finalPosition
     * @return range from - PI / 2 to PI / 2
     */
    public static double getMarkerRotation(final GeoPoint startPosition, final GeoPoint finalPosition) {
        double opposite = Math.abs(finalPosition.getLatitude() - startPosition.getLatitude()); // North-south direction
        double adjacent = Math.abs(finalPosition.getLongitude() - startPosition.getLongitude()); // West-east direciton
        double atan = atan(opposite / adjacent);
        if (finalPosition.getLongitude() < startPosition.getLongitude()) { // Left side
            if (finalPosition.getLatitude() > startPosition.getLatitude()) { // Left top quadrant
                return atan;
            } else { // Left bottom quadrant
                return 2 * PI - atan;
            }
        } else { // Right side
            if (finalPosition.getLatitude() > startPosition.getLatitude()) { // Right top quadrant
                return PI - atan;
            } else { // Right bottom quadrant
                return PI + atan;
            }
        }
    }

    /**
     * @param startPosition
     * @param finalPosition
     * @return true if train is moving to the left
     */
    public static boolean isLeftTrainDirection(final GeoPoint startPosition, final GeoPoint finalPosition) {
        // Left side
// Right side
        return finalPosition.getLongitude() < startPosition.getLongitude();
    }

    public static float convertToDegrees(double radians) {
        return (float) (radians / PI * 180);
    }

    public static ItemizedOverlay<OverlayItem> loadOverlayFromPois(List<Poi> pois, ItemizedIconOverlay.OnItemGestureListener<OverlayItem> listener, Context context) {
        final ArrayList<OverlayItem> items = new ArrayList<>();
        for (Poi poi : pois) {
            OverlayItem item = new OverlayItem(poi.getTitle(), "", poi);
            item.setMarker(context.getResources().getDrawable(poi.getMarkerDrawable()));
            item.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
            items.add(item);
        }

        ItemizedOverlay<OverlayItem> overlay = new ItemizedIconOverlay<>(items, context.getResources().getDrawable(R.drawable.poi_crossing), listener, context.getApplicationContext());

        return overlay;
    }

    public static <T> Observable<T> createLongTermUpdateObservable(LifecycleOwner owner, LiveData<T> liveData) {
        return firstAndSample(createObservableFromLiveData(owner, liveData));
    }

    public static <T> Observable<T> createObservableFromLiveData(LifecycleOwner owner, LiveData<T> liveData) {
        return Observable.create(emmiter -> liveData.observe(owner, data -> emmiter.onNext(data)));

    }

    public static <T> Observable<T> firstAndSample(Observable<T> observable) {
        return observable.sample(TaConfig.UPDATE_INTERVAL, TaConfig.UPDATE_INTERVAL_UNIT).mergeWith(observable.take(1));
    }

    public static String getTwoDigitString(int number) {
        if (number > 99) {
            return null;
        }

        if (number < 10) {
            return String.format("0%d", number);
        } else {
            return String.format("%d", number);
        }
    }

    public static class IntervalPoller {
        private Handler mHandler;
        private Runnable mPeriodicUpdateRunnable;
        private boolean mRunning = false;

        public IntervalPoller(long interval, Runnable locationChangedRunnable) {
            mHandler = new Handler();
            mPeriodicUpdateRunnable = () -> {
                locationChangedRunnable.run();
                mHandler.postDelayed(mPeriodicUpdateRunnable, interval);
            };
        }

        public boolean isRunning() {
            return mRunning;
        }

        public void setRunning(boolean running) {
            mRunning = running;
        }

        public void startPolling() {
            if (!isRunning()) {
                mPeriodicUpdateRunnable.run();
                setRunning(true);
            }
        }

        public void stopPolling() {
            if (isRunning()) {
                mHandler.removeCallbacks(mPeriodicUpdateRunnable);
                setRunning(false);
            }
        }
    }
}
