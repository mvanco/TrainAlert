package cz.intesys.trainalert.entity;

import android.content.Context;
import android.support.annotation.DrawableRes;

import java.util.Collections;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.utility.Utility.POIType;

import static cz.intesys.trainalert.utility.Utility.POI_TYPE_BRIDGE;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_CROSSING;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_SPEED_LIMITATION_50;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_SPEED_LIMITATION_70;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_TRAIN_STATION;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_TURNOUT;

/**
 * Configuration for Poi - e.g. list of alarms, marker drawable, ... Enable group setting of configuration
 * according to {@link POIType}, however this configuration is
 * specific for each Poi because it stores unique {@link Alarm} objects which can be enabled/disabled
 * separately for each Poi.
 */
public class PoiConfiguration {

    private @POIType int type;
    private List<Alarm> alarmList;

    /**
     * @param type    type from predefined Poi types
     * @param poi     handle to {@link Poi} object, creates 1:1 relationship between Poi and PoiConfiguration
     * @param context used to access string resources
     */
    public PoiConfiguration(@POIType int type, Poi poi, Context context) {
        this.type = type;
        this.alarmList = createAlarmList(type, poi, context);
    }

    /**
     * Alarm configuration for each {@link POIType}
     *
     * @param type    type of Poi
     * @param poi     Poi which is being configured
     * @param context used to access string resources
     * @return
     */
    public List<Alarm> createAlarmList(@POIType int type, Poi poi, Context context) {
        switch (type) {
            case POI_TYPE_CROSSING:
                return Alarm.createAlarms(context, R.string.fragment_main_poi_type_message_crossing, poi, new int[]{160, 320, 480});
            case POI_TYPE_SPEED_LIMITATION_50:
                return new Alarm(context.getString(R.string.fragment_main_poi_type_message_speed_limitation, 50), 80, poi).toArray();
            case POI_TYPE_SPEED_LIMITATION_70:
                return new Alarm(context.getString(R.string.fragment_main_poi_type_message_speed_limitation, 50), 80, poi).toArray();
            case POI_TYPE_TRAIN_STATION:
                return Alarm.createAlarms(context, R.string.fragment_main_poi_type_message_train_station, poi, new int[]{160, 320, 480});
            case POI_TYPE_TURNOUT:
                return Alarm.createAlarms(context, R.string.fragment_main_poi_type_message_turnout, poi, new int[]{160, 320, 480});
            case POI_TYPE_BRIDGE:
                return Alarm.createAlarms(context, R.string.fragment_main_poi_type_message_bridge, poi, new int[]{160, 320, 480});
            default:
                return Collections.emptyList();

        }
    }

    public List<Alarm> getAlarmList() {
        return alarmList;
    }

    /**
     * Setting of icon for each {@link POIType}
     *
     * @return drawable resource of mapped icon
     */
    @DrawableRes
    public int getMarkerDrawable() {
        switch (type) {
            case POI_TYPE_CROSSING:
                return R.drawable.poi_crossing;
            case POI_TYPE_SPEED_LIMITATION_50:
            case POI_TYPE_SPEED_LIMITATION_70:
            case POI_TYPE_BRIDGE:
                return R.drawable.poi_speed_limitation;
            case POI_TYPE_TRAIN_STATION:
                return R.drawable.poi_train_station;
            case POI_TYPE_TURNOUT:
                return R.drawable.poi_turnout;
            default:
                return R.drawable.poi_crossing;
        }
    }
}