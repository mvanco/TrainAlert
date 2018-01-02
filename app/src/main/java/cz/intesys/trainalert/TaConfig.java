package cz.intesys.trainalert;

import cz.intesys.trainalert.repository.Repository;
import cz.intesys.trainalert.repository.SimulatedRepository;

public class TaConfig {
    public static final int GPS_TIME_INTERVAL = 2000; // Time between two gps coordinates measurements in milliseconds
    public static final String REST_BASE_URL = "http://192.168.16.29:5000";
    public static final boolean LOGS = BuildConfig.LOGS;
    public static final int MAP_DEFAULT_ZOOM = 15; //
    public static final int[] SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE = {200, 800}; // [0, 0] or [x, y] where x, y > 0
    public static final Repository REPOSITORY = SimulatedRepository.getInstance(); // TODO: change to real PostgreSqlRepository
}
