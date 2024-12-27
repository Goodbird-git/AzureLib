package mod.azure.azurelib.cache;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.AzureLibException;
import mod.azure.azurelib.animation.cache.AzBakedAnimationCache;
import mod.azure.azurelib.model.cache.AzBakedModelCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * AzResourceCache is an abstract base class designed for managing and loading mod resources asynchronously. This class
 * provides helper functions for loading and processing resource files of a specific type and storing them in a cache.
 */
public class AzResourceCache implements IResourceManagerReloadListener {

    public static void registerReloadListener() {
        Minecraft mc = Minecraft.getMinecraft();

        if (!(mc.getResourceManager() instanceof IReloadableResourceManager)) {
            throw new AzureLibException("AzureLib was initialized too early!");
        }

        IReloadableResourceManager reloadable = (IReloadableResourceManager) Minecraft.getMinecraft()
                .getResourceManager();
        reloadable.registerReloadListener(new AzResourceCache());
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
        List<IResourcePack> packs = FileZipLoading.getPacks();
        if (packs == null) {
            return;
        }

        for (IResourcePack pack : packs) {
            for (ResourceLocation resourceLocation : FileZipLoading.getLocations(pack, "animations",
                    fileName -> fileName.endsWith(".json"))) {
                try {
                    AzureLib.LOGGER.debug("Found animation file: {}", resourceLocation);
                    AzBakedAnimationCache.getInstance().loadAnimations(resourceLocation, resourceManager);
                } catch (Exception exception) {
                    AzureLib.LOGGER.error("Error loading animation file {}!", resourceLocation, exception);
                }
            }

            for (ResourceLocation resourceLocation : FileZipLoading.getLocations(pack, "geo", fileName -> fileName.endsWith(".json"))) {
                try {
                    AzBakedModelCache.getInstance().loadModels(resourceLocation, resourceManager);
                } catch (Exception exception) {
                    AzureLib.LOGGER.error("Error loading model file {}!", resourceLocation, exception);
                }
            }
        }
    }
}
