package cz.intesys.tdriveradvisor.entity;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

/**
 * Variant of {@link OverlayItem} with constructor which takes {@link POI}
 */
public class TDAOverlayItem extends OverlayItem {

    public TDAOverlayItem(POI poi) {
        super("", "", new GeoPoint(poi.getLatitude(), poi.getLongitude()));
    }
}
