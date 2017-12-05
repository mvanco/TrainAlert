package cz.intesys.trainalert.entity;

import android.content.Context;
import android.support.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.utility.Utility;

import static cz.intesys.trainalert.utility.Utility.POI_TYPE_SPEED_LIMITATION;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_UNDERPASS;

public class POIType {

    private @Utility.POIType int type;
    private List<Alarm> alarmList;

    public POIType(@Utility.POIType int type, POI poi, Context context) {
        this.type = type;
        this.alarmList = getListOfAlarms(type, poi, context);
    }

    public List<Alarm> getListOfAlarms(@Utility.POIType int type, POI poi, Context context) {
        switch (type) {
            case POI_TYPE_UNDERPASS:
                return Alarm.createAlarms(context, R.string.fragment_main_poi_type_message_underpass, poi, new int[]{160, 320, 480});
            case POI_TYPE_SPEED_LIMITATION:
                List<Alarm> alarmList = new ArrayList<Alarm>();
                alarmList.add(new Alarm(context.getString(R.string.fragment_main_poi_type_message_speed_limitation), 160, poi));
                return alarmList;
        }
        return null;
    }

    public List<Alarm> getAlarmList() {
        return alarmList;
    }

    public @DrawableRes
    int getMarkerDrawable() {
        switch (type) {
            case POI_TYPE_UNDERPASS:
                return R.drawable.ic_clear;
            case POI_TYPE_SPEED_LIMITATION:
                return R.drawable.ic_clear;
        }
        return R.drawable.ic_clear;
    }
}
