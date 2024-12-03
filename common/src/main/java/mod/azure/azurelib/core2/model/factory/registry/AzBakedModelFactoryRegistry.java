package mod.azure.azurelib.core2.model.factory.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelib.core2.model.factory.AzBakedModelFactory;
import mod.azure.azurelib.core2.model.factory.impl.AzBuiltinBakedModelFactory;

import java.util.Map;

public class AzBakedModelFactoryRegistry {

    private static final Map<String, AzBakedModelFactory> FACTORIES = new Object2ObjectOpenHashMap<>(1);
    private static final AzBakedModelFactory DEFAULT_FACTORY = new AzBuiltinBakedModelFactory();

    public static AzBakedModelFactory getForNamespace(String namespace) {
        return FACTORIES.getOrDefault(namespace, DEFAULT_FACTORY);
    }

    /**
     * Register a custom {@link AzBakedModelFactory} to handle loading models in a custom way.<br>
     * <b><u>MUST be called during mod construct</u></b><br>
     * It is recommended you don't call this directly, and instead call it via
     * {@link AzureLibUtil#addCustomBakedModelFactory}
     *
     * @param namespace The namespace (modid) to register the factory for
     * @param factory   The factory responsible for model loading under the given namespace
     */
    public static void register(String namespace, AzBakedModelFactory factory) {
        FACTORIES.put(namespace, factory);
    }
}
