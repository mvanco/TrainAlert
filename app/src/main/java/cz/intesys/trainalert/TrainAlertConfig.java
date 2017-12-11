package cz.intesys.trainalert;

public class TrainAlertConfig {
    public static final int GPS_TIME_INTERVAL = 2000; // Time between two gps coordinates measurements in milliseconds
    public static final String REST_BASE_URL = "http://192.168.16.29:5000";
    public static final boolean LOGS = BuildConfig.LOGS;
}
