package cz.intesys.trainalert.repository;

import android.os.Handler;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.Stop;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.entity.TripStatus;

import static cz.intesys.trainalert.TaConfig.SIMULATED_REPOSITORY_GET_TRIPS_RESPONSE_DELAY_RANGE;
import static cz.intesys.trainalert.TaConfig.SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE;
import static cz.intesys.trainalert.TaConfig.SIMULATED_REPOSITORY_SET_TRIP_RESPONSE_DELAY_RANGE;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_BEFORE_LIGHTS;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_CROSSING;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_LIGHTS;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_SPEED_LIMITATION_20;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_SPEED_LIMITATION_30;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_SPEED_LIMITATION_40;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_SPEED_LIMITATION_50;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_SPEED_LIMITATION_70;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_STOP;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_TRAIN_STATION;
import static cz.intesys.trainalert.repository.PostgreSqlRepository.LOG_POSTGRE;

public class SimulatedRepository implements Repository {
    private final List<Poi> mPois;
    private static SimulatedRepository sInstance;
    private int mLocationIterator = 0; // 0 - the most right Poi, 230 - the most left Poi
    private boolean toTheLeftDirection = true;
    private List<Location> mExampleRoute;
    private int mTime = -1;


    private SimulatedRepository() {
        mExampleRoute = getExampleRoute();
        mPois = getPois();
    }

    public static SimulatedRepository getInstance() {
        if (sInstance == null) {
            sInstance = new SimulatedRepository();
        }

        return sInstance;
    }

    public static int getRandomServerDelay() {
        if (SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE[0] == 0 && SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE[0] == 0) {
            return 0;
        }

        int rangeSize = SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE[1] - SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE[0];
        int serverDelay = new Random().nextInt(rangeSize) + SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE[0]; // <500, 1500)
        Log.d("serverDelay", "serverDelay:" + serverDelay);
        return serverDelay;
    }

    @Override
    public void getCurrentLocation(TaCallback<Location> taCallback) {
        //int randomInt = new Random().nextInt(4);
        //if (randomInt == 2) {
        new Handler().postDelayed(() -> {
            Location location = mExampleRoute.get(mLocationIterator);
            location.setTime(new Date());
            taCallback.onResponse(location);
            Log.d(LOG_POSTGRE, "getCurrentLocation response");
        }, getRandomServerDelay());


        // Prepare next location
        if (toTheLeftDirection) {
            if (mLocationIterator < mExampleRoute.size() - 1) {
                mLocationIterator++;
            } else {
                toTheLeftDirection = false;
                mLocationIterator--;
            }
        } else {
            if (mLocationIterator > 1) {
                mLocationIterator--;
            } else {
                toTheLeftDirection = true;
                mLocationIterator++;
            }
        }
        Log.d(LOG_POSTGRE, "getCurrentLocation enqueued");
        //}
    }

    @Override
    public void getPois(TaCallback<List<Poi>> taCallback) {
        new Handler().postDelayed(() -> {
            taCallback.onResponse(mPois);
            Log.d(LOG_POSTGRE, "getPois response");
        }, getRandomServerDelay());
        Log.d(LOG_POSTGRE, "getPois enqueued");
    }

    @Override
    public void addPoi(Poi poi, TaCallback<Poi> taCallback) {
        mPois.add(poi);
        new Handler().postDelayed(() -> {
            taCallback.onResponse(poi);
            Log.d(LOG_POSTGRE, "addPoi response");
        }, getRandomServerDelay());
        Log.d(LOG_POSTGRE, "addPoi enqueued");
    }

    @Override
    public void editPoi(long id, Poi poi, TaCallback<Poi> taCallback) {
        new Handler().postDelayed(() -> {
            for (int i = 0; i < mPois.size(); i++) {
                Poi examplePoi = mPois.get(i);
                if (examplePoi.getId() == id) {
                    mPois.set(i, poi);
                    taCallback.onResponse(poi);
                    Log.d(LOG_POSTGRE, "editPoi response");
                }
            }

            taCallback.onFailure(null);
        }, getRandomServerDelay());
        Log.d(LOG_POSTGRE, "editPoi enqueued");
    }

    @Override public void deletePoi(long id, TaCallback<Poi> taCallback) {
        new Handler().postDelayed(() -> {
            Poi poi = mPois.get((int) id); // In simulated environment int is sufficient.
            mPois.remove((int) id);
            for (int i = 0; i < mPois.size(); i++) {
                mPois.get(i).setId(i);
            }
            taCallback.onResponse(poi);
            Log.d(LOG_POSTGRE, "deletePoi response");
        }, getRandomServerDelay());
        Log.d(LOG_POSTGRE, "deletePoi enqueued");
    }

    @Override public void getTrips(String id, TaCallback<List<String>> taCallback) {
        new Handler().postDelayed(() -> {
            List<String> trips = Arrays.asList("21", "25", "34");
            taCallback.onResponse(trips);
            Log.d(LOG_POSTGRE, "getTrips response");
        }, getCustomRandomServerDelay(SIMULATED_REPOSITORY_GET_TRIPS_RESPONSE_DELAY_RANGE));
        Log.d(LOG_POSTGRE, "getTrips enqueued");
    }

    @Override public void setTrip(String id, TaCallback<Void> taCallback) {
        new Handler().postDelayed(() -> {
            taCallback.onResponse(null);
            Log.d(LOG_POSTGRE, "setTrip response");
        }, getCustomRandomServerDelay(SIMULATED_REPOSITORY_SET_TRIP_RESPONSE_DELAY_RANGE));
        Log.d(LOG_POSTGRE, "setTrip enqueued");
    }

    @Override public void getPreviousStops(int id, TaCallback<List<Stop>> taCallback) {
        new Handler().postDelayed(() -> {
            Log.d(LOG_POSTGRE, "getPreviousStops response");
            taCallback.onResponse(getPreviousStops(mTime));
//            List<Stop> stops = new ArrayList<>();
//            try {
//                stops.add(new Stop("0", "Predosla 1", TaConfig.BASIC_DATE_FORMAT.parse("2018-03-02T11:11:00"), 3900, true, "stop"));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
        }, getRandomServerDelay());
        Log.d(LOG_POSTGRE, "getPreviousStops enqueued");
    }

    @Override public void getNextStops(int id, TaCallback<List<Stop>> taCallback) {
        mTime++;
        new Handler().postDelayed(() -> {
            Log.d(LOG_POSTGRE, "getNextStops response");
            taCallback.onResponse(getNextStops(mTime));
//            List<Stop> stops = new ArrayList<>();
//            try {
//                stops.add(new Stop("0", "Nasledujici 1", TaConfig.BASIC_DATE_FORMAT.parse("2018-03-02T11:11:00"), 300, true));
//                stops.add(new Stop("0", "Nasledujici 2", TaConfig.BASIC_DATE_FORMAT.parse("2018-03-02T11:11:00"), 0, false));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            taCallback.onResponse(stops);
//            Log.d(LOG_POSTGRE, "getNextStops response");
        }, getRandomServerDelay());
        Log.d(LOG_POSTGRE, "getNextStops enqueued");
    }

    @Override public void getFinalStop(TaCallback<Stop> taCallback) {
        new Handler().postDelayed(() -> {
            Log.d(LOG_POSTGRE, "getFinalStop response");
            taCallback.onResponse(getFinalStop(mTime));
//            Stop stop = null;
//            try {
//                stop = new Stop("0", "Finalni", TaConfig.BASIC_DATE_FORMAT.parse("2018-03-02T11:11:00"), 0, true);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            taCallback.onResponse(stop);
//            Log.d(LOG_POSTGRE, "getFinalStop response");
        }, getRandomServerDelay());
        Log.d(LOG_POSTGRE, "getFinalStop enqueued");
    }

    @Override public void getTrainId(TaCallback<String> taCallback) {
        new Handler().postDelayed(() -> {
            Log.d(LOG_POSTGRE, "getTrainId response");
            taCallback.onResponse("TRAINID");
        }, getRandomServerDelay());
        Log.d(LOG_POSTGRE, "getTrainId enqueued");
    }

    @Override public void getTripStatus(TaCallback<TripStatus> taCallback) {
        new Handler().postDelayed(() -> {
            Log.d(LOG_POSTGRE, "getTripStatus response");
            TripStatus ts = new TripStatus(false, false, POI_TYPE_SPEED_LIMITATION_20);
            switch (mTime) {
                case 0:
                    ts = new TripStatus(false, true, POI_TYPE_SPEED_LIMITATION_30);
                    break;
                case 1:
                    ts = new TripStatus(false, true, POI_TYPE_SPEED_LIMITATION_30);
                    break;
                case 2:
                    ts = new TripStatus(false, true, POI_TYPE_SPEED_LIMITATION_50);
                    break;
                case 3:
                    ts = new TripStatus(false, true, POI_TYPE_SPEED_LIMITATION_50);
                    break;
                case 4:
                    ts = new TripStatus(false, true, POI_TYPE_SPEED_LIMITATION_70);
                    break;
            }
            taCallback.onResponse(ts);
        }, getRandomServerDelay());
        Log.d(LOG_POSTGRE, "getTripStatus enqueued");
    }

    private List<Stop> getPreviousStops(int i) {
        List<Stop> stops = new ArrayList<>();
        try {
            switch (i) {
                case 0:
                    break;
                case 1:
                    stops.add(new Stop("0", "Nasledujici 1", TaConfig.BASIC_DATE_FORMAT.parse("2018-03-02T11:11:00"), 300, true, "on_request"));
                    break;
                case 2:
                    stops.add(new Stop("0", "Nasledujici 2", TaConfig.BASIC_DATE_FORMAT.parse("2018-03-02T11:11:00"), 0, false, "stop"));
                    break;
                case 3:
                    stops.add(new Stop("0", "Nasledujici 3", TaConfig.BASIC_DATE_FORMAT.parse("2018-03-02T11:11:00"), 0, false, "stop"));
                    break;
                case 4:
                    // Empty stops
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return stops;
    }

    private List<Stop> getNextStops(int i) {
        List<Stop> stops = new ArrayList<>();
        try {
            switch (i) {
                case 0:
                    stops.add(new Stop("0", "Nasledujici 2", TaConfig.BASIC_DATE_FORMAT.parse("2018-03-02T11:11:00"), 300, true, "on_request"));
                    stops.add(new Stop("0", "Nasledujici 3", TaConfig.BASIC_DATE_FORMAT.parse("2018-03-02T11:11:00"), 0, false, "stop"));
                    break;
                case 1:
                    stops.add(new Stop("0", "Nasledujici 3", TaConfig.BASIC_DATE_FORMAT.parse("2018-03-02T11:11:00"), 0, false, "stop"));
                    break;
                case 2:
                    // Empty stops
                    break;
                case 3:
                    // Empty stops
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return stops;
    }

    private Stop getFinalStop(int i) {
        try {
            switch (i) {
                case 0:
                case 1:
                case 2:
                    return new Stop("0", "Finalni", TaConfig.BASIC_DATE_FORMAT.parse("2018-03-02T11:11:00"), 300, true, "final_stop");
                case 3:
                case 4:
                    // Empty stop
                    break;
                case 5:
                    // Empty stop
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Poi> getPois() {
        List<Poi> sExamplePOIs = new ArrayList<Poi>();

        sExamplePOIs.add(new Poi(1, "Přechod 1", 50.47902, 14.03453, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(5, "Vjezdové návěstidlo TS (do Čížkovic)", 50.47472222, 14.03083333, POI_TYPE_LIGHTS));
        sExamplePOIs.add(new Poi(6, "Rychlost 70 (od Čížkovic)", 50.47472222, 14.03083333, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(7, "Rychlost 50 (do Čížkovic)", 50.4779892062057, 14.0267136517693, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(8, "Předvěst PřTS (do Čížkovic)", 50.47584546479, 14.0218425077551, POI_TYPE_BEFORE_LIGHTS));
        sExamplePOIs.add(new Poi(9, "Předvěst L, rychlost 60 (od Čížkovic)", 50.4740178099051, 14.0207923440372, POI_TYPE_BEFORE_LIGHTS));
        sExamplePOIs.add(new Poi(10, "Přejezd P9231, rychlost 70 (do Čížkovic)", 50.4720564082698, 14.0168744255515, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(11, "Vjezdové návěstidlo L (od Čížkovic)", 50.4718713176752, 14.0107511632583, POI_TYPE_LIGHTS));
        sExamplePOIs.add(new Poi(12, "žst.Třebenice", 50.4739766804309, 14.0011502434226, POI_TYPE_TRAIN_STATION));
        sExamplePOIs.add(new Poi(13, "Přejezd P9232, rychlost 50 (od Třebenic)", 50.4740139540184, 13.9995043137494, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(14, "Rychlost 20 (do Třebenic)", 50.4745653426215, 13.9977291331572, DataHelper.POI_TYPE_SPEED_LIMITATION_20));
        sExamplePOIs.add(new Poi(18, "Rychlost 50 (do Třebenic)", 50.481587594321, 13.9856805240407, POI_TYPE_SPEED_LIMITATION_50));
        sExamplePOIs.add(new Poi(20, "Rychlost 30 (od Třebenic)", 50.4780920210965, 13.9799934835995, POI_TYPE_SPEED_LIMITATION_30));
        sExamplePOIs.add(new Poi(21, "Přejezd P9235, rychlost 50 (do i od Třebenic)", 50.4768068188871, 13.9803408454446, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(22, "Rychlost 30 (do Třebenic)", 50.4750023360137, 13.9804579790901, POI_TYPE_SPEED_LIMITATION_30));
        sExamplePOIs.add(new Poi(24, "Přejezd P9237, rychlost 30 (od Třebenic)", 50.4668170361303, 13.9677308026482, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(25, "Přejezd P9238, rychlost 50 (od Třebenic)", 50.4670792756229, 13.9657051984002, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(26, "Přejezd P9239", 50.4677168714598, 13.9613611557904, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(27, "Rychlost 30 (od Třebenic)", 50.4700974960634, 13.9542361988741, POI_TYPE_SPEED_LIMITATION_30));
        sExamplePOIs.add(new Poi(28, "Rychlost 50 (do Třebenic)", 50.4703378656436, 13.9530890969669, POI_TYPE_SPEED_LIMITATION_50));
        sExamplePOIs.add(new Poi(29, "Přejezd P9240", 50.470357146573, 13.9520268159754, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(31, "Přejezd P9241", 50.468812076499, 13.9474586038029, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(32, "Přejezd P9242", 50.4680613753101, 13.9453178854549, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(33, "Předvěst L (od Třebenic)", 50.4574371616629, 13.9268753949334, POI_TYPE_BEFORE_LIGHTS));
        sExamplePOIs.add(new Poi(34, "Rychlost 40 (od Třebenic)", 50.456876572977, 13.9222264009364, POI_TYPE_SPEED_LIMITATION_40));
        sExamplePOIs.add(new Poi(35, "Přejezd P9243", 50.4571787259611, 13.920332067153, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(36, "Vjezdové návěstidlo L (od Třebenic)", 50.4579025995148, 13.9178601433249, POI_TYPE_LIGHTS));
        sExamplePOIs.add(new Poi(37, "Přejezd P9244", 50.4586470334093, 13.9122094547047, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(38, "žst.Třebívlice", 50.4577200249917, 13.9082208521226, POI_TYPE_TRAIN_STATION));
        sExamplePOIs.add(new Poi(39, "Přejezd P9245", 50.4574165897185, 13.9069323820226, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(40, "Rychlost 50 (od Třebívlic)", 50.4557888062537, 13.9043372659122, POI_TYPE_SPEED_LIMITATION_50));
        sExamplePOIs.add(new Poi(41, "Vjezdové návěstidlo S (do Třebívlic)", 50.4535951958608, 13.9030043658088, POI_TYPE_LIGHTS));
        sExamplePOIs.add(new Poi(42, "Předvěst S (do Třebívlic)", 50.4505784940015, 13.8981291827033, POI_TYPE_BEFORE_LIGHTS));
        sExamplePOIs.add(new Poi(43, "Přejezd P9247,rychlost 50 (do Třebívlic)", 50.4532351563018, 13.8929975173053, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(44, "zastávka Semeč", 50.45233761718650, 13.88780122644760, POI_TYPE_STOP));
        sExamplePOIs.add(new Poi(45, "Rychlost 50 (od Třebívlic)", 50.4517795398678, 13.884594188017, POI_TYPE_SPEED_LIMITATION_50));
        sExamplePOIs.add(new Poi(46, "Přejezd P9248,rychlost 50 (do Třebívlic)", 50.4447181359457, 13.8747690986184, POI_TYPE_SPEED_LIMITATION_50));
        sExamplePOIs.add(new Poi(47, "Rychlost 40 (do Třebívlic)", 50.4466871017385, 13.8737855799058, POI_TYPE_SPEED_LIMITATION_40));
        sExamplePOIs.add(new Poi(48, "Rychlost 50 (do i od Třebívlic)", 50.4482071747433, 13.8660264856675, POI_TYPE_SPEED_LIMITATION_50));
        sExamplePOIs.add(new Poi(49, "Přejezd P9249, zastávka Hnojnice", 50.4481737388065, 13.8659537820255, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(50, "Přejezd P9250", 50.4466536647276, 13.8618137135225, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(51, "Přejezd P9251,rychlost 50 (do i od Třebívlic)", 50.4444236199758, 13.8552905811983, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(52, "Přejezd P9252, rychlost 50 (do i od Třebívlic)", 50.4426024674203, 13.8453503776999, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(53, "Přejezd P9253", 50.4456711204751, 13.8390251608456, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(54, "žst.Libčeves", 50.4486701314398, 13.8309308220358, POI_TYPE_SPEED_LIMITATION_50));
        sExamplePOIs.add(new Poi(55, "Rychlost 50 (od Libčeves)", 50.4487048530095, 13.829163719626, POI_TYPE_SPEED_LIMITATION_50));
        sExamplePOIs.add(new Poi(56, "Přejezd P9254", 50.4475204473008, 13.8254336188821, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(57, "Přejezd P9255", 50.4436365202668, 13.8131830552045, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(58, "Rychlost 50 (od Libčeves)", 50.4411105105638, 13.8046242209042, POI_TYPE_SPEED_LIMITATION_50));
        sExamplePOIs.add(new Poi(59, "Zastávka Sinutec", 50.4404982801755, 13.7976083194508, POI_TYPE_STOP));
        sExamplePOIs.add(new Poi(60, "Přejezd P9258, zastávka Bělušice", 50.4520290038147, 13.7675534416648, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(61, "Přejezd P9259", 50.4557078013481, 13.7533802705652, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(62, "Přejezd P9260, zastávka Skršín", 50.4630478255069, 13.7461220236386, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(63, "Rychlost 20 (od Libčeves)", 50.4678454179661, 13.7213947071749, DataHelper.POI_TYPE_SPEED_LIMITATION_20));
        sExamplePOIs.add(new Poi(64, "Přejezd P9261,rychlost 50 (od i do Libčeves)", 50.4681230772272, 13.7192943797392, POI_TYPE_SPEED_LIMITATION_50));
        sExamplePOIs.add(new Poi(65, "Rychlost 20 (do Libčeves)", 50.4687040996646, 13.7179695578182, POI_TYPE_SPEED_LIMITATION_20));
        sExamplePOIs.add(new Poi(66, "Přejezd P9262", 50.4778966726127, 13.6980124080882, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(67, "zastávka Sedlec u Obrnic", 50.4875911520133, 13.7039417940027, POI_TYPE_STOP));
        sExamplePOIs.add(new Poi(68, "Předvěst PřCL (od Libčeves)", 50.48861111, 13.70777778, POI_TYPE_BEFORE_LIGHTS));
        sExamplePOIs.add(new Poi(69, "Vjezdové návěstidlo CL (od Libčeves)E", 50.49527778, 13.73611111, POI_TYPE_LIGHTS));
        sExamplePOIs.add(new Poi(150, "Přejezd P9257", 50.4405188594768, 13.7971640194163, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi(151, "Přejezd P9256", 50.4411027934233, 13.8045636345358, POI_TYPE_CROSSING));


//        sExamplePOIs.add(new Poi(1, "Přechod 1", 50.47902, 14.03453, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(2, "Omezení (50) 1", 50.47394, 14.00254, POI_TYPE_SPEED_LIMITATION_50));
//        sExamplePOIs.add(new Poi(3, "Přechod 2", 50.47916, 13.99642, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(4, "Stanice 1", 50.48079, 13.99086, POI_TYPE_TRAIN_STATION));
//        sExamplePOIs.add(new Poi(5, "Přechod 3", 50.46866, 13.97693, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(6, "Omezení (70) 1", 50.46641, 13.96887, POI_TYPE_SPEED_LIMITATION_70));
//        sExamplePOIs.add(new Poi(7, "Přechod 4", 50.46964, 13.95576, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(8, "Omezení (70) 2", 50.45779, 13.92928, POI_TYPE_SPEED_LIMITATION_70));

        for (int i = 0; i < sExamplePOIs.size(); i++) {
            sExamplePOIs.get(i).setId(i);
        }

        return sExamplePOIs;
    }

    private int getCustomRandomServerDelay(int[] delayRange) {
        if (delayRange[0] == 0 && delayRange[0] == 0) {
            return 0;
        }

        int rangeSize = delayRange[1] - delayRange[0];
        int serverDelay = new Random().nextInt(rangeSize) + delayRange[0]; // <500, 1500)
        return serverDelay;
    }

    private List<Location> getExampleRoute() {
        List<Location> exampleRoute = new ArrayList<Location>();

        exampleRoute.add(new Location(50.48365189588503, 14.039404579177328));
        exampleRoute.add(new Location(50.48268475946398, 14.038823543179397));
        exampleRoute.add(new Location(50.48186552670313, 14.038029609310989));
        exampleRoute.add(new Location(50.48114185928537, 14.037149844754106));
        exampleRoute.add(new Location(50.48050010721087, 14.03631299554146));
        exampleRoute.add(new Location(50.47946236201443, 14.035025535214311));
        exampleRoute.add(new Location(50.47858845365022, 14.033888278591995));
        exampleRoute.add(new Location(50.47784938526087, 14.032281431035775));
        exampleRoute.add(new Location(50.47784938526087, 14.029599222020884));
        exampleRoute.add(new Location(50.478013246872024, 14.026509317235728));
        exampleRoute.add(new Location(50.477439728747754, 14.02341941245057));
        exampleRoute.add(new Location(50.476634060581574, 14.022325071172496));
        exampleRoute.add(new Location(50.47599224730058, 14.021895917730113));
        exampleRoute.add(new Location(50.474069676506076, 14.020780450638552));
        exampleRoute.add(new Location(50.473414171970745, 14.020329839524049));
        exampleRoute.add(new Location(50.47233530096659, 14.01760471516492));
        exampleRoute.add(new Location(50.4715978306709, 14.014772302445186));
        exampleRoute.add(new Location(50.47157051673531, 14.012347585495721));
        exampleRoute.add(new Location(50.47203485149399, 14.010266191300165));
        exampleRoute.add(new Location(50.472594778519976, 14.00863540821911));
        exampleRoute.add(new Location(50.473250294416715, 14.006940252121698));
        exampleRoute.add(new Location(50.473441484841175, 14.006360894974481));
        exampleRoute.add(new Location(50.47372826902807, 14.005609876450311));
        exampleRoute.add(new Location(50.473919457519514, 14.004300958451042));
        exampleRoute.add(new Location(50.47400139520775, 14.002670175369989));
        exampleRoute.add(new Location(50.47402870773891, 14.000331289109004));
        exampleRoute.add(new Location(50.474093775764345, 13.999086744126085));
        exampleRoute.add(new Location(50.47453077351565, 13.99777782612682));
        exampleRoute.add(new Location(50.47517260663929, 13.997112638291126));
        exampleRoute.add(new Location(50.4757188407315, 13.996919519242052));
        exampleRoute.add(new Location(50.47647280938892, 13.996861809234346));
        exampleRoute.add(new Location(50.47708047770732, 13.99681889389011));
        exampleRoute.add(new Location(50.47734675686843, 13.996797436217992));
        exampleRoute.add(new Location(50.47778372454894, 13.996765249709814));
        exampleRoute.add(new Location(50.47831627344806, 13.996700876693454));
        exampleRoute.add(new Location(50.479008626382495, 13.996507757644375));
        exampleRoute.add(new Location(50.479636744068756, 13.995971315841397));
        exampleRoute.add(new Location(50.480067613863554, 13.995266470717377));
        exampleRoute.add(new Location(50.48039532113799, 13.994343790816256));
        exampleRoute.add(new Location(50.48060013703065, 13.992798838423678));
        exampleRoute.add(new Location(50.480982457655614, 13.989462170409144));
        exampleRoute.add(new Location(50.48125554192285, 13.987616810606896));
        exampleRoute.add(new Location(50.48131698566553, 13.98730567436117));
        exampleRoute.add(new Location(50.48140573759724, 13.986747774886071));
        exampleRoute.add(new Location(50.48155593279432, 13.985889468001306));
        exampleRoute.add(new Location(50.4816924734684, 13.985095534132897));
        exampleRoute.add(new Location(50.481781224695, 13.984194311903897));
        exampleRoute.add(new Location(50.48168564644405, 13.982896122740685));
        exampleRoute.add(new Location(50.48139891053148, 13.98183396797079));
        exampleRoute.add(new Location(50.481020820309254, 13.981063367517718));
        exampleRoute.add(new Location(50.480351757729096, 13.980258704813252));
        exampleRoute.add(new Location(50.479791922577874, 13.979915382059344));
        exampleRoute.add(new Location(50.47926774412022, 13.979783939934082));
        exampleRoute.add(new Location(50.47851672700372, 13.979880499458615));
        exampleRoute.add(new Location(50.47588473213844, 13.980476101722935));
        exampleRoute.add(new Location(50.47166492463265, 13.980261525001744));
        exampleRoute.add(new Location(50.4707908721079, 13.980004032936312));
        exampleRoute.add(new Location(50.46987583105849, 13.978888233986117));
        exampleRoute.add(new Location(50.46953439329434, 13.978287419166778));
        exampleRoute.add(new Location(50.46895053625937, 13.97732692638736));
        exampleRoute.add(new Location(50.4680627755713, 13.97606092373233));
        exampleRoute.add(new Location(50.46713402347345, 13.975653227962068));
        exampleRoute.add(new Location(50.46632817966713, 13.975438651240877));
        exampleRoute.add(new Location(50.4657135438037, 13.974945124782135));
        exampleRoute.add(new Location(50.465235488164524, 13.97136169353824));
        exampleRoute.add(new Location(50.46582281298555, 13.970138606227449));
        exampleRoute.add(new Location(50.46615061901608, 13.969494876063875));
        exampleRoute.add(new Location(50.46632817966713, 13.969044264949375));
        exampleRoute.add(new Location(50.466995494486724, 13.966558232624203));
        exampleRoute.add(new Location(50.467282317774675, 13.964004769642026));
        exampleRoute.add(new Location(50.467779019648816, 13.961215664140687));
        exampleRoute.add(new Location(50.46842094443653, 13.959778000108704));
        exampleRoute.add(new Location(50.46861215439088, 13.959112812273013));
        exampleRoute.add(new Location(50.46892628335155, 13.958254505388249));
        exampleRoute.add(new Location(50.469363328868695, 13.95670955299567));
        exampleRoute.add(new Location(50.469991574722286, 13.954585243455872));
        exampleRoute.add(new Location(50.47015546357204, 13.953962970964419));
        exampleRoute.add(new Location(50.470292037179604, 13.953405071489323));
        exampleRoute.add(new Location(50.46872149966114, 13.947105098451242));
        exampleRoute.add(new Location(50.46754691472964, 13.943736243928536));
        exampleRoute.add(new Location(50.46665101177214, 13.941274328816093));
        exampleRoute.add(new Location(50.465776866557746, 13.939965410816823));
        exampleRoute.add(new Location(50.46169427652359, 13.939891366535281));
        exampleRoute.add(new Location(50.4606287982202, 13.939633874469852));
        exampleRoute.add(new Location(50.45908517810284, 13.937166242176149));
        exampleRoute.add(new Location(50.45810579395331, 13.93110213243608));
        exampleRoute.add(new Location(50.45728328033505, 13.925642915352423));
        exampleRoute.add(new Location(50.45688710682455, 13.922574468239386));
        exampleRoute.add(new Location(50.45709202457181, 13.920729108437138));
        exampleRoute.add(new Location(50.4579116866839, 13.917832322701056));
        exampleRoute.add(new Location(50.458663031142805, 13.91523594437464));
        exampleRoute.add(new Location(50.458963565584725, 13.913819738014777));
        exampleRoute.add(new Location(50.45826686918821, 13.910279222115117));
        exampleRoute.add(new Location(50.45813026084835, 13.909614034279425));
        exampleRoute.add(new Location(50.457898025765076, 13.90851969300135));
        exampleRoute.add(new Location(50.45737890792668, 13.90676016388758));
        exampleRoute.add(new Location(50.456560119115295, 13.904985533261327));
        exampleRoute.add(new Location(50.455494525153796, 13.904127226376561));
        exampleRoute.add(new Location(50.454491960191376, 13.903355456686237));
        exampleRoute.add(new Location(50.45359026590478, 13.902926303243854));
        exampleRoute.add(new Location(50.45266122957349, 13.90303359160445));
        exampleRoute.add(new Location(50.452128391680674, 13.903205252981403));
        exampleRoute.add(new Location(50.451718512294946, 13.903248168325641));
        exampleRoute.add(new Location(50.45036390268851, 13.902426815348106));
        exampleRoute.add(new Location(50.449858365415395, 13.901032066660358));
        exampleRoute.add(new Location(50.449885691892575, 13.899251079874471));
        exampleRoute.add(new Location(50.45073280485721, 13.897856331186729));
        exampleRoute.add(new Location(50.452712925975135, 13.89545459685918));
        exampleRoute.add(new Location(50.45293152416144, 13.894896697384082));
        exampleRoute.add(new Location(50.453027160550334, 13.894660662990772));
        exampleRoute.add(new Location(50.453290985865465, 13.892528739623716));
        exampleRoute.add(new Location(50.45294275678029, 13.890911650474079));
        exampleRoute.add(new Location(50.45245090955757, 13.88921649437667));
        exampleRoute.add(new Location(50.45212300856835, 13.885354113395222));
        exampleRoute.add(new Location(50.450879696672814, 13.882285666282183));
        exampleRoute.add(new Location(50.45025119690871, 13.881041121299265));
        exampleRoute.add(new Location(50.449363085182455, 13.880547594840523));
        exampleRoute.add(new Location(50.44677304702395, 13.881999565120044));
        exampleRoute.add(new Location(50.445912198785635, 13.882149768824878));
        exampleRoute.add(new Location(50.44469605315746, 13.881334377284348));
        exampleRoute.add(new Location(50.44299807666977, 13.879023017887357));
        exampleRoute.add(new Location(50.442861424237634, 13.87698453903604));
        exampleRoute.add(new Location(50.44417327129821, 13.874988975528959));
        exampleRoute.add(new Location(50.44712479423106, 13.87350839615273));
        exampleRoute.add(new Location(50.44943395927615, 13.871555747989872));
        exampleRoute.add(new Location(50.44980286869543, 13.870675983432985));
        exampleRoute.add(new Location(50.44992583786268, 13.869066658024051));
        exampleRoute.add(new Location(50.449775542170414, 13.868122520450811));
        exampleRoute.add(new Location(50.44962524600072, 13.86780065536902));
        exampleRoute.add(new Location(50.44886009446233, 13.866877975467899));
        exampleRoute.add(new Location(50.44806760303922, 13.865354480747438));
        exampleRoute.add(new Location(50.446960825564275, 13.862522068027701));
        exampleRoute.add(new Location(50.445150319727816, 13.858837776873232));
        exampleRoute.add(new Location(50.44460373248998, 13.85613411018622));
        exampleRoute.add(new Location(50.44456623875254, 13.856054129679103));
        exampleRoute.add(new Location(50.44432027224096, 13.854080023844142));
        exampleRoute.add(new Location(50.444532602382, 13.850975263747513));
        exampleRoute.add(new Location(50.44410899229376, 13.849430311354935));
        exampleRoute.add(new Location(50.4429337960691, 13.846426237258257));
        exampleRoute.add(new Location(50.44309146235983, 13.842217481034536));
        exampleRoute.add(new Location(50.446752270993784, 13.837711369889504));
        exampleRoute.add(new Location(50.4472988334158, 13.837132012742291));
        exampleRoute.add(new Location(50.44795469998914, 13.836166417496921));
        exampleRoute.add(new Location(50.44841926664506, 13.83498624553037));
        exampleRoute.add(new Location(50.44858323025818, 13.833355462449314));
        exampleRoute.add(new Location(50.448696448075395, 13.831022686623282));
        exampleRoute.add(new Location(50.44871011165148, 13.828361935280508));
        exampleRoute.add(new Location(50.448600802932305, 13.826945728920647));
        exampleRoute.add(new Location(50.44828653895809, 13.82608742203588));
        exampleRoute.add(new Location(50.44806791931023, 13.82585138764257));
        exampleRoute.add(new Location(50.44705558350344, 13.825228381555483));
        exampleRoute.add(new Location(50.44635871172633, 13.825357127588198));
        exampleRoute.add(new Location(50.44469316178771, 13.824842143457337));
        exampleRoute.add(new Location(50.44402358508313, 13.824026751916811));
        exampleRoute.add(new Location(50.44335399890491, 13.818469214837952));
        exampleRoute.add(new Location(50.44335950604187, 13.814225134619468));
        exampleRoute.add(new Location(50.44345516177907, 13.81390326953768));
        exampleRoute.add(new Location(50.44353715225708, 13.813602862128013));
        exampleRoute.add(new Location(50.443714797805484, 13.81270163989901));
        exampleRoute.add(new Location(50.44374241292158, 13.810064584568678));
        exampleRoute.add(new Location(50.44280675249789, 13.807489663914382));
        exampleRoute.add(new Location(50.44210981815132, 13.806652814701733));
        exampleRoute.add(new Location(50.44153586451284, 13.80579450781697));
        exampleRoute.add(new Location(50.441440204896395, 13.805644304112134));
        exampleRoute.add(new Location(50.440988854744404, 13.803175476023346));
        exampleRoute.add(new Location(50.44067454021743, 13.799742248484284));
        exampleRoute.add(new Location(50.44060621069622, 13.798755195566805));
        exampleRoute.add(new Location(50.44056521293615, 13.79854061884561));
        exampleRoute.add(new Location(50.44056467255075, 13.796583460370151));
        exampleRoute.add(new Location(50.44085165612873, 13.79555349210843));
        exampleRoute.add(new Location(50.44131629251811, 13.7932789788638));
        exampleRoute.add(new Location(50.442465561704324, 13.788275431640114));
        exampleRoute.add(new Location(50.44338113312344, 13.786537360198457));
        exampleRoute.add(new Location(50.44417370303384, 13.785142611510715));
        exampleRoute.add(new Location(50.444538273862804, 13.783573633720014));
        exampleRoute.add(new Location(50.44479790394706, 13.781814104606244));
        exampleRoute.add(new Location(50.44497554476294, 13.780848509360885));
        exampleRoute.add(new Location(50.44709351850321, 13.776235109855257));
        exampleRoute.add(new Location(50.4487136556198, 13.773028720564309));
        exampleRoute.add(new Location(50.44923286854856, 13.77251373643345));
        exampleRoute.add(new Location(50.45033959288108, 13.771912921614113));
        exampleRoute.add(new Location(50.451036406032074, 13.770689834303314));
        exampleRoute.add(new Location(50.45177419700639, 13.768629897779876));
        exampleRoute.add(new Location(50.452211401043655, 13.766634334272794));
        exampleRoute.add(new Location(50.454886333331444, 13.758478642260897));
        exampleRoute.add(new Location(50.45521122431143, 13.756047635288937));
        exampleRoute.add(new Location(50.455539103896655, 13.754545598240597));
        exampleRoute.add(new Location(50.455771350561236, 13.752614407749876));
        exampleRoute.add(new Location(50.45615387199376, 13.749159722538694));
        exampleRoute.add(new Location(50.4576292828302, 13.747421651097044));
        exampleRoute.add(new Location(50.45895438111424, 13.747207074375853));
        exampleRoute.add(new Location(50.46065848882773, 13.747412496953583));
        exampleRoute.add(new Location(50.46115025071859, 13.747283750920866));
        exampleRoute.add(new Location(50.46206546062464, 13.746768766790009));
        exampleRoute.add(new Location(50.46295333390708, 13.746082121282193));
        exampleRoute.add(new Location(50.46637916685248, 13.743281588044441));
        exampleRoute.add(new Location(50.46670013983757, 13.742294535126963));
        exampleRoute.add(new Location(50.46808867486067, 13.732848717554788));
        exampleRoute.add(new Location(50.46845212192787, 13.72968907280695));
        exampleRoute.add(new Location(50.46839841947664, 13.726904828415165));
        exampleRoute.add(new Location(50.46791509467093, 13.721209783068334));
        exampleRoute.add(new Location(50.46794194617863, 13.720492629209845));
        exampleRoute.add(new Location(50.46799564914832, 13.72002858847788));
        exampleRoute.add(new Location(50.46957163332138, 13.716919783955031));
        exampleRoute.add(new Location(50.471290038869654, 13.712448118719742));
        exampleRoute.add(new Location(50.472742054884236, 13.709829222699364));
        exampleRoute.add(new Location(50.47432610602149, 13.706749679659968));
        exampleRoute.add(new Location(50.4744224487186, 13.705223536585796));
        exampleRoute.add(new Location(50.47477146908818, 13.703072075010324));
        exampleRoute.add(new Location(50.475979596623574, 13.70176432385661));
        exampleRoute.add(new Location(50.477053461845856, 13.700836242392683));
        exampleRoute.add(new Location(50.477753719081676, 13.698650392929034));
        exampleRoute.add(new Location(50.480062412498256, 13.696498931353563));
        exampleRoute.add(new Location(50.48216130367487, 13.697427012817494));
        exampleRoute.add(new Location(50.48406714838916, 13.698734763971212));
        exampleRoute.add(new Location(50.48608028116254, 13.700295628251455));
        exampleRoute.add(new Location(50.48722438076044, 13.702291012651697));
        exampleRoute.add(new Location(50.4876806678854, 13.7036409493265));
        exampleRoute.add(new Location(50.48818168067288, 13.705472231622412));
        exampleRoute.add(new Location(50.48920158985274, 13.708889258830515));
        exampleRoute.add(new Location(50.48989940976489, 13.709690783731181));
        exampleRoute.add(new Location(50.49075825089329, 13.710323566547494));
        exampleRoute.add(new Location(50.491939131953984, 13.710323566547494));
        exampleRoute.add(new Location(50.49347606597789, 13.707876806324405));
        exampleRoute.add(new Location(50.49460320642376, 13.705725344748934));
        exampleRoute.add(new Location(50.49548879790794, 13.703700439736727));
        exampleRoute.add(new Location(50.49648171407196, 13.702308317540837));
        exampleRoute.add(new Location(50.497474609365355, 13.70133805055582));
        exampleRoute.add(new Location(50.49976892270729, 13.7013802360769));
        exampleRoute.add(new Location(50.502264365070005, 13.701295865034723));
        exampleRoute.add(new Location(50.50355228373408, 13.701127122950371));
        exampleRoute.add(new Location(50.5043035534014, 13.70062089669732));

        for (int i = 0; i < exampleRoute.size(); i++) {
            exampleRoute.get(i).setMetaIndex(i);
        }
        return exampleRoute;
    }
}
