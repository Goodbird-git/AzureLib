package mod.azure.azurelib.event;

import mod.azure.azurelib.platform.services.GeoRenderPhaseEventFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author Boston Vanseghi
 */
public class NeoForgeGeoRenderPhaseEvent implements GeoRenderPhaseEventFactory.GeoRenderPhaseEvent {

    public static class NeoForgeGeoRenderEvent extends Event {
        public final GeoRenderEvent geoRenderEvent;

        public NeoForgeGeoRenderEvent(GeoRenderEvent geoRenderEvent) {
            this.geoRenderEvent = geoRenderEvent;
        }

        public GeoRenderEvent getGeoRenderEvent() {
            return this.geoRenderEvent;
        }
    }

    @Override
    public boolean handle(GeoRenderEvent geoRenderEvent) {
        return !MinecraftForge.EVENT_BUS.post(new NeoForgeGeoRenderEvent(geoRenderEvent));
    }
}
