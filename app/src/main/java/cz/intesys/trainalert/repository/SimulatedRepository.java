package cz.intesys.trainalert.repository;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.utility.Utility.LocationPoller;

import static cz.intesys.trainalert.TaConfig.SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_BRIDGE;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_CROSSING;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_SPEED_LIMITATION_50;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_SPEED_LIMITATION_70;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_TRAIN_STATION;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_TURNOUT;

public class SimulatedRepository implements Repository {
    private final List<Poi> mExamplePois;
    private static SimulatedRepository sInstance;
    private int mLocationIterator = 0; // 0 - the most right Poi, 230 - the most left Poi
    private boolean mLoaded = false;
    private boolean toTheLeftDirection = true;
    private List<Location> mExampleRoute;
    private LocationPoller mLocationPoller;
    private MutableLiveData<Location> mCurrentLocation;
    private MutableLiveData<List<Poi>> mPois;

    private SimulatedRepository() {
        mLocationPoller = new LocationPoller(() -> loadCurrentLocation());
        mCurrentLocation = new MutableLiveData<>();
        mPois = new MutableLiveData<>();
        mExampleRoute = getExampleRoute();
        mExamplePois = getExamplePois();
    }

    public static SimulatedRepository getInstance() {
        if (sInstance == null) {
            sInstance = new SimulatedRepository();
        }

        return sInstance;
    }

    @Override
    public MutableLiveData<Location> getCurrentLocation() {
        return mCurrentLocation;
    }

    public void loadPois() {
        if (!mLoaded) {
            new Handler().postDelayed(() -> {
                mPois.setValue(mExamplePois);
                mLoaded = true;
            }, getRandomServerDelay());
        }
    }

    @Override
    public LiveData<List<Poi>> getPois() {
        return mPois;
    }

    @OnLifecycleEvent (Lifecycle.Event.ON_RESUME)
    public void startLocationPolling() {
        mLocationPoller.startPolling();
    }

    @OnLifecycleEvent (Lifecycle.Event.ON_PAUSE)
    public void stopLocationPolling() {
        mLocationPoller.stopPolling();
    }

    public void restartRepository() {
        mLocationIterator = 0;
    }

    public List<Poi> getExamplePois() {
        List<Poi> sExamplePOIs = new ArrayList<Poi>();

        sExamplePOIs.add(new Poi("Přechod 1", 50.47902, 14.03453, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi("Omezení (50) 1", 50.47394, 14.00254, POI_TYPE_SPEED_LIMITATION_50));
        sExamplePOIs.add(new Poi("Přechod 2", 50.47916, 13.99642, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi("Stanice 1", 50.48079, 13.99086, POI_TYPE_TRAIN_STATION));
        sExamplePOIs.add(new Poi("Přechod 3", 50.46866, 13.97693, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi("Omezení (70) 1", 50.46641, 13.96887, POI_TYPE_SPEED_LIMITATION_70));
        sExamplePOIs.add(new Poi("Přechod 4", 50.46964, 13.95576, POI_TYPE_CROSSING));
        sExamplePOIs.add(new Poi("Most 1", 50.46404, 13.93943, POI_TYPE_BRIDGE));
        sExamplePOIs.add(new Poi("Omezení (70) 2", 50.45779, 13.92928, POI_TYPE_SPEED_LIMITATION_70));
        sExamplePOIs.add(new Poi("Most 2", 50.45158, 13.88418, POI_TYPE_BRIDGE));
        sExamplePOIs.add(new Poi("Výhybka 1", 50.47950, 13.69669, POI_TYPE_TURNOUT));

//        sExamplePOIs.add(new Poi(50.47902254646468, 14.03452249583824, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.47817915699491, 14.033283647782222, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.468671536801395, 13.976941624253527, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.46681518367927, 13.967698539937949, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.46711419751372, 13.96569837980194, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.470352221395615, 13.95201231288346, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.457425251189875, 13.906925863491022, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.45325162770703, 13.892963746152365, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.44820354253554, 13.865930429153735, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.44444858393916, 13.85523290545108, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.44753197413999, 13.825485004491675, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.443651263354525, 13.81312538535105, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.45203159415806, 13.76749867754598, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.455747679477696, 13.75331515627523, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi(50.46815261073638, 13.719186194335025, POI_TYPE_CROSSING));

        for (int i = 0; i < sExamplePOIs.size(); i++) {
            sExamplePOIs.get(i).setMetaIndex(i);
        }

        return sExamplePOIs;
    }

    public int getRandomServerDelay() {
        if (SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE[0] == 0 && SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE[0] == 0) {
            return 0;
        }

        int rangeSize = SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE[1] - SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE[0];
        int serverDelay = new Random().nextInt(rangeSize) + SIMULATED_REPOSITORY_RESPONSE_DELAY_RANGE[0]; // <500, 1500)
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

    private void loadCurrentLocation() {
        new Handler().postDelayed(() -> {
            mCurrentLocation.setValue(mExampleRoute.get(mLocationIterator));
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
    }
}
