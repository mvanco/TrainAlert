package cz.intesys.trainalert.entity;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

/**
 * Variant of {@link OverlayItem} with constructor which takes {@link Poi}
 */
public class TaOverlayItem extends OverlayItem {

    public TaOverlayItem(Poi poi) {
        super("", "", new GeoPoint(poi.getLatitude(), poi.getLongitude()));
    }
}
