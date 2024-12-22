package mod.azure.azurelib.model.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.azure.azurelib.cache.AzResourceCache;
import mod.azure.azurelib.loading.FileLoader;
import mod.azure.azurelib.loading.json.raw.Model;
import mod.azure.azurelib.loading.object.GeometryTree;
import mod.azure.azurelib.model.AzBakedModel;
import mod.azure.azurelib.model.factory.registry.AzBakedModelFactoryRegistry;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * AzBakedModelCache is a singleton class that extends {@link AzResourceCache} and is designed to manage and cache
 * baked models of type {@link AzBakedModel}. It provides functionality to asynchronously load and store models
 * associated with specific resource locations.
 */
public class AzBakedModelCache extends AzResourceCache {

    private static final AzBakedModelCache INSTANCE = new AzBakedModelCache();

    public static AzBakedModelCache getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, AzBakedModel> bakedModels;

    private AzBakedModelCache() {
        this.bakedModels = new Object2ObjectOpenHashMap<>();
    }

    public CompletableFuture<Void> loadModels(Executor backgroundExecutor, IResourceManager resourceManager) {
        return loadResources(backgroundExecutor, resourceManager, "geo", resource -> {
            Model model = FileLoader.loadModelFile(resource, resourceManager);

            return AzBakedModelFactoryRegistry.getForNamespace(resource.getResourceDomain())
                .constructGeoModel(GeometryTree.fromModel(model));
        }, bakedModels::put);
    }

    public AzBakedModel getNullable(ResourceLocation resourceLocation) {
        return bakedModels.get(resourceLocation);
    }
}
