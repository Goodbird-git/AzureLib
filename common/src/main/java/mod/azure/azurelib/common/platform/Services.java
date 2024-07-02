package mod.azure.azurelib.common.platform;

import mod.azure.azurelib.common.platform.services.AzureEvents;
import mod.azure.azurelib.common.platform.services.AzureLibNetwork;
import mod.azure.azurelib.common.platform.services.IPlatformHelper;
import mod.azure.azurelib.common.platform.services.AzureLibInitializer;

import java.util.ServiceLoader;

public class Services {

    public static final AzureEvents GEO_RENDER_PHASE_EVENT_FACTORY = load(AzureEvents.class);
    public static final AzureLibInitializer INITIALIZER = load(AzureLibInitializer.class);
    public static final AzureLibNetwork NETWORK = load(AzureLibNetwork.class);
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}