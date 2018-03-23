package cz.intesys.trainalert;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.repository.Repository;
import cz.intesys.trainalert.repository.SimulatedRepository;

public class TaConfig {
    public static final int GPS_TIME_INTERVAL = 2000; // Time between two gps coordinates measurements in milliseconds, GetLocation service polling.
    public static final int TRIP_TIME_INTERVAL = 5000; // GetNextStops, GetPreviousStops, GetFinalStop services polling.
    public static final int TRIP_STATUS_TIME_INTERVAL = 3000;

    public static final int SERVER_TIMEOUT = 30000; // In miliseconds.
    public static final int GPS_TIMEOUT_DELAY = 15000;

    public static final int TRIP_ID_LOADER_DURATION = SERVER_TIMEOUT;


    public static final String REST_BASE_URL = "http://192.168.0.21:5000"; // Application server with offline maps
    public static final boolean LOGS = BuildConfig.LOGS;
    public static final int MAP_DEFAULT_ZOOM = 15; //

    public static final int[] SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE = {0, 200}; // [0, 0] or [x, y] where x, y > 0
    public static final int[] SIMULATED_REPOSITORY_GET_TRIPS_RESPONSE_DELAY_RANGE = {50, 5000};
    public static final int[] SIMULATED_REPOSITORY_SET_TRIP_RESPONSE_DELAY_RANGE = {50, 500}; // [0, 0] or [x, y] where x, y > 0

    public static final Repository REPOSITORY = SimulatedRepository.getInstance(); // TODO: Make sure this is real PostgreSqlRepository.
    public static final long UPDATE_INTERVAL = 5;
    public static final TimeUnit UPDATE_INTERVAL_UNIT = TimeUnit.MINUTES;
    public static final Location DEFAULT_LOCATION = new Location(50.48365189588503, 14.039404579177328);
    public static final boolean OSMDROID_DEBUGGING = true;
    public static final boolean USE_OFFLINE_MAPS = false;

    public static final String BASIC_DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss"; // Used in PostgreSQL database and as basic string format for java.util.Date.
    public static final String ARRIVAL_DATE_FORMAT_STRING = "HH:mm"; // Used to display arrival of train.

    public static final SimpleDateFormat BASIC_DATE_FORMAT = new SimpleDateFormat(BASIC_DATE_FORMAT_STRING);
    public static final SimpleDateFormat ARRIVAL_DATE_FORMAT = new SimpleDateFormat(ARRIVAL_DATE_FORMAT_STRING);

    public static final int TRIP_FRAGMENT_PREVIOUS_STOP_COUNT = 1;
    public static final int TRIP_FRAGMENT_NEXT_STOP_COUNT = 2;

    public static final int LOCATION_INITIAL_DELAY = 5000;

    public static final int ADMINISTRATOR_PASSWORD = 2003;
}