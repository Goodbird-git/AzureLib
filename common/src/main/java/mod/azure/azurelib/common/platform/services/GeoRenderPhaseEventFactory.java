package mod.azure.azurelib.common.platform.services;

import mod.azure.azurelib.common.internal.common.event.GeoRenderEvent;

public interface GeoRenderPhaseEventFactory {

    GeoRenderPhaseEvent create();

    interface GeoRenderPhaseEvent {

        boolean handle(GeoRenderEvent geoRenderEvent);
    }
}
