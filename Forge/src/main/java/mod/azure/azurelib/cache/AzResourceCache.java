package mod.azure.azurelib.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * AzResourceCache is an abstract base class designed for managing and loading mod resources asynchronously. This class
 * provides helper functions for loading and processing resource files of a specific type and storing them in a cache.
 */
public class AzResourceCache implements IResourceManagerReloadListener {

    private static AzResourceCache INSTANCE;

    public static AzResourceCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AzResourceCache();
            return INSTANCE;
        }
        return INSTANCE;
    }

    public static void registerReloadListener() {
        Minecraft mc = Minecraft.getMinecraft();

        if (!(mc.getResourceManager() instanceof IReloadableResourceManager)) {
            throw new RuntimeException("AzureLib was initialized too early!");
        }
    }

    private static final List<String> EXCLUDED_NAMESPACES = Arrays.asList(
        "moreplayermodels",
        "customnpcs",
        "creeperoverhaul",
        "geckolib",
        "gunsrpg",
        "born_in_chaos_v1",
        "neoforge"
    );

    /**
     * TODO: complete this
     */
    protected final <T> CompletableFuture<Void> loadResources(
        Executor executor,
        IResourceManager resourceManager,
        String type,
        Function<ResourceLocation, T> loader,
        BiConsumer<ResourceLocation, T> map
    ) {
        return CompletableFuture.supplyAsync(
            () -> resourceManager.listResources(type, fileName -> fileName.endsWith(".json")),
            executor
        )
            .thenApplyAsync(resources -> {
                Object2ObjectOpenHashMap<ResourceLocation, CompletableFuture<T>> tasks = new Object2ObjectOpenHashMap<ResourceLocation, CompletableFuture<T>>();

                for (ResourceLocation resource : resources) {
                    tasks.put(resource, CompletableFuture.supplyAsync(() -> loader.apply(resource), executor));
                }

                return tasks;
            }, executor)
            .thenAcceptAsync(tasks -> {
                for (java.util.Map.Entry<ResourceLocation, CompletableFuture<T>> entry : tasks.entrySet()) {
                    if (!EXCLUDED_NAMESPACES.contains(entry.getKey().getResourceDomain().toLowerCase(Locale.ROOT))) {
                        map.accept(entry.getKey(), entry.getValue().join());
                    }
                }
            }, executor);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        /**
         * TODO: complete this
         */
    }
}
