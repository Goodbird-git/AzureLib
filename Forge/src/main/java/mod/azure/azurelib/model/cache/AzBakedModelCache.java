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

public class AzBakedModelCache extends AzResourceCache {

    private static final AzBakedModelCache INSTANCE = new AzBakedModelCache();

    public static AzBakedModelCache getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, AzBakedModel> bakedModels;

    private AzBakedModelCache() {
        this.bakedModels = new Object2ObjectOpenHashMap<>();
    }

    public void loadModels(ResourceLocation resourceLocation, IResourceManager resourceManager) {
        Model model = FileLoader.loadModelFile(resourceLocation, resourceManager);
        bakedModels.put(resourceLocation, AzBakedModelFactoryRegistry.getForNamespace(resourceLocation.getResourceDomain())
                .constructGeoModel(GeometryTree.fromModel(model)));
    }

    public AzBakedModel getNullable(ResourceLocation resourceLocation) {
        return bakedModels.get(resourceLocation);
    }
}
