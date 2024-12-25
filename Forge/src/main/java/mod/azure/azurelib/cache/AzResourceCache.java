package mod.azure.azurelib.cache;

import mod.azure.azurelib.AzureLibException;
import mod.azure.azurelib.animation.cache.AzBakedAnimationCache;
import mod.azure.azurelib.model.cache.AzBakedModelCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * AzResourceCache is an abstract base class designed for managing and loading mod resources asynchronously. This class
 * provides helper functions for loading and processing resource files of a specific type and storing them in a cache.
 */
public class AzResourceCache implements ISelectiveResourceReloadListener {

    public static void registerReloadListener() {
        Minecraft mc = Minecraft.getMinecraft();

        if (!(mc.getResourceManager() instanceof IReloadableResourceManager)) {
            throw new RuntimeException("AzureLib was initialized too early!");
        }

        IReloadableResourceManager reloadable = (IReloadableResourceManager) Minecraft.getMinecraft()
                .getResourceManager();
        reloadable.registerReloadListener(new AzResourceCache());
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        AzBakedAnimationCache.getInstance().loadAnimations(resourceManager);
        AzBakedModelCache.getInstance().loadModels(resourceManager);
    }

    protected final Collection<ResourceLocation> loadResources(
            IResourceManager resourceManager,
            String type
    ) {
        Collection<ResourceLocation> resources = new ArrayList<>();
        for (String domain : resourceManager.getResourceDomains()) { // Iterate over all namespaces
            try {
                ResourceLocation resourceLocation = new ResourceLocation(domain, type);
                if (type.endsWith(".json") && resourceManager.getResource(resourceLocation) != null) {
                    resources.add(resourceLocation);
                }
            } catch (IOException e) {
                throw new AzureLibException("Resource not found");
            }
        }
        return resources;
    }
}
