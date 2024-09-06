package mod.azure.azurelib.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.core.animatable.model.CoreGeoModel;
import mod.azure.azurelib.loading.FileLoader;
import mod.azure.azurelib.loading.object.BakedAnimations;
import mod.azure.azurelib.loading.object.BakedModelFactory;
import mod.azure.azurelib.loading.object.GeometryTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Cache class for holding loaded {@link mod.azure.azurelib.core.animation.Animation Animations} and {@link CoreGeoModel Models}
 */
public final class AzureLibCache {
	private static final List<String> EXCLUDED_NAMESPACES = Arrays.asList("geckolib3", "animatedmobsmod", "moreplayermodels", "dungeons_mobs", "customnpcs", "gunsrpg", "mimic", "celestisynth", "the_flesh_that_hates", "enemyexpansion", "mutationcraft", "born_in_chaos_v1");

	private static Map<ResourceLocation, BakedAnimations> ANIMATIONS = Collections.emptyMap();
	private static Map<ResourceLocation, BakedGeoModel> MODELS = Collections.emptyMap();

	public static Map<ResourceLocation, BakedAnimations> getBakedAnimations() {
		if (!AzureLib.hasInitialized)
			throw new RuntimeException("AzureLib was never initialized! Please read the documentation!");

		return ANIMATIONS;
	}

	public static Map<ResourceLocation, BakedGeoModel> getBakedModels() {
		if (!AzureLib.hasInitialized)
			throw new RuntimeException("AzureLib was never initialized! Please read the documentation!");

		return MODELS;
	}

	public static void registerReloadListener() {
		Minecraft mc = Minecraft.getMinecraft();

		if (!(mc.getResourceManager() instanceof IReloadableResourceManager))
			throw new RuntimeException("AzureLib was initialized too early!");

		IReloadableResourceManager reloadable = (IReloadableResourceManager) Minecraft.getMinecraft()
				.getResourceManager();
		reloadable.addReloadListener(AzureLibCache::reload);
	}

	private static CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
		Map<ResourceLocation, BakedAnimations> animations = new Object2ObjectOpenHashMap<>();
		Map<ResourceLocation, BakedGeoModel> models = new Object2ObjectOpenHashMap<>();

		return CompletableFuture.allOf(
				loadAnimations(backgroundExecutor, resourceManager, animations::put),
				loadModels(backgroundExecutor, resourceManager, models::put)).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync(empty -> {
			AzureLibCache.ANIMATIONS = animations;
			AzureLibCache.MODELS = models;
		}, gameExecutor);
	}

	private static CompletableFuture<Void> loadAnimations(Executor backgroundExecutor, IResourceManager resourceManager, BiConsumer<ResourceLocation, BakedAnimations> elementConsumer) {
		return loadResources(backgroundExecutor, resourceManager, "animations", resource -> FileLoader.loadAnimationsFile(resource, resourceManager), elementConsumer);
	}

	private static CompletableFuture<Void> loadModels(Executor backgroundExecutor, IResourceManager resourceManager, BiConsumer<ResourceLocation, BakedGeoModel> elementConsumer) {
		return loadResources(backgroundExecutor, resourceManager, "geo", resource -> BakedModelFactory.getForNamespace(resource.getResourceDomain()).constructGeoModel(GeometryTree.fromModel(FileLoader.loadModelFile(resource, resourceManager))), elementConsumer);
	}

	private static <T> CompletableFuture<Void> loadResources(Executor executor, IResourceManager resourceManager, String type, Function<ResourceLocation, T> loader, BiConsumer<ResourceLocation, T> map) {
		return CompletableFuture.supplyAsync(
						() -> resourceManager.getAllResources(type, fileName -> fileName.endsWith(".json")), executor)
				.thenApplyAsync(resources -> {
					Map<ResourceLocation, CompletableFuture<T>> tasks = new Object2ObjectOpenHashMap<>();

					for (ResourceLocation resource : resources) {
						CompletableFuture<T> existing = tasks.put(resource,
								CompletableFuture.supplyAsync(() -> loader.apply(resource), executor));

						if (existing != null) {// Possibly if this matters, the last one will win
							System.err.println("Duplicate resource for " + resource);
							existing.cancel(false);
						}
					}

					return tasks;
				}, executor).thenAcceptAsync(tasks -> {
					for (Entry<ResourceLocation, CompletableFuture<T>> entry : tasks.entrySet()) {
						if (!EXCLUDED_NAMESPACES.contains(entry.getKey().getResourceDomain().toLowerCase(Locale.ROOT)))
							map.accept(entry.getKey(), entry.getValue().join());
					}
				}, executor);
	}
}
