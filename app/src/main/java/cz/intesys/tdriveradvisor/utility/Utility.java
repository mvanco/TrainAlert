package cz.intesys.tdriveradvisor.utility;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static cz.intesys.tdriveradvisor.TDriverAdvisorConfig.GPS_TIME_INTERVAL;
import static cz.intesys.tdriveradvisor.TDriverAdvisorConfig.MAP_REFRESH_TIME;

public class Utility {

    public static final int POI_TYPE_UNDERPASS = 0;
    public static final int POI_TYPE_SPEED_LIMITATION = 1;

    @Retention (RetentionPolicy.SOURCE)
    @IntDef ( {POI_TYPE_UNDERPASS, POI_TYPE_SPEED_LIMITATION})
    public @interface POIType {
    }

    public static int getMapRefreshGPSCycles() {
        return (MAP_REFRESH_TIME * 1000) / GPS_TIME_INTERVAL;
    }
}
