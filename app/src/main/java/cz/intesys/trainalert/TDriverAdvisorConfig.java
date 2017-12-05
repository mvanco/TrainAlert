package cz.intesys.trainalert;

public class TDriverAdvisorConfig {

    public static final int TIME_SPEED = 1000; // Default is 500, if bigger time is slower
    public static final boolean INFINITE_ANIMATION = true;
    public static final int TIME_OF_ANIMATION = 0; // in seconds, applicable only if INFINITE_ANIMATION is set to false
    public static final int GPS_TIME_INTERVAL = 2000; // Time between two gps coordinates measurements in milliseconds
    public static final int MAP_REFRESH_TIME = 3; // in seconds
}
