package mod.azure.azurelib.animation.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.azure.azurelib.animation.primitive.AzBakedAnimations;
import mod.azure.azurelib.cache.AzResourceCache;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class AzBakedAnimationCache extends AzResourceCache {

    private static final AzBakedAnimationCache INSTANCE = new AzBakedAnimationCache();

    public static AzBakedAnimationCache getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, AzBakedAnimations> bakedAnimations;

    private AzBakedAnimationCache() {
        this.bakedAnimations = new Object2ObjectOpenHashMap<>();
    }

    public void loadAnimations(IResourceManager resourceManager) {
        loadResources(resourceManager, "animations");
    }

    public AzBakedAnimations getNullable(ResourceLocation resourceLocation) {
        return bakedAnimations.get(resourceLocation);
    }
}
