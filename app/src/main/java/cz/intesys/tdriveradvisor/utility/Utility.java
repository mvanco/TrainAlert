package cz.intesys.tdriveradvisor.utility;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.IntDef;

import org.osmdroid.util.GeoPoint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static cz.intesys.tdriveradvisor.TDriverAdvisorConfig.GPS_TIME_INTERVAL;
import static cz.intesys.tdriveradvisor.TDriverAdvisorConfig.MAP_REFRESH_TIME;
import static java.lang.Math.PI;
import static java.lang.Math.atan;

public class Utility {

    public static final int POI_TYPE_UNDERPASS = 0;
    public static final int POI_TYPE_SPEED_LIMITATION = 1;

    public static final int POI_TYPE_SEARCH_ALGORITHM_ENABLED_ALARM = 0;
    public static final int POI_TYPE_SEARCH_ALGORITHM_DISABLED_ALARM = 1;


    @Retention (RetentionPolicy.SOURCE)
    @IntDef ( {POI_TYPE_UNDERPASS, POI_TYPE_SPEED_LIMITATION})
    public @interface POIType {
    }

    public static int getMapRefreshGPSCycles() {
        return (MAP_REFRESH_TIME * 1000) / GPS_TIME_INTERVAL;
    }

    public static void playSound(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
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
}
