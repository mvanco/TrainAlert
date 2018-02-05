package cz.intesys.trainalert;

import java.util.concurrent.TimeUnit;

import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.repository.PostgreSqlRepository;
import cz.intesys.trainalert.repository.Repository;

public class TaConfig {
    public static final int GPS_TIME_INTERVAL = 2000; // Time between two gps coordinates measurements in milliseconds
    public static final String REST_BASE_URL = "http://192.168.16.189:5000";
    public static final boolean LOGS = BuildConfig.LOGS;
    public static final int MAP_DEFAULT_ZOOM = 15; //
    public static final int[] SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE = {200, 800}; // [0, 0] or [x, y] where x, y > 0
    public static final Repository REPOSITORY = PostgreSqlRepository.getInstance(); // TODO: Make sure this is real PostgreSqlRepository.
    public static final long UPDATE_INTERVAL = 5;
    public static final TimeUnit UPDATE_INTERVAL_UNIT = TimeUnit.MINUTES;
    public static final Location DEFAULT_LOCATION = new Location(50.48365189588503, 14.039404579177328);
}
