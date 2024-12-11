package mod.azure.azurelib.fabric.platform;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.cache.AzureLibCache;
import mod.azure.azurelib.common.platform.services.AzureLibInitializer;

public class FabricAzureLibInitializer implements AzureLibInitializer {

    @Override
    public void initialize() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(new IdentifiableResourceReloadListener() {

                @Override
                public ResourceLocation getFabricId() {
                    return AzureLib.modResource("models");
                }

                @Override
                public @NotNull CompletableFuture<Void> reload(
                    PreparableReloadListener.PreparationBarrier synchronizer,
                    ResourceManager manager,
                    ProfilerFiller prepareProfiler,
                    ProfilerFiller applyProfiler,
                    Executor prepareExecutor,
                    Executor applyExecutor
                ) {
                    return AzureLibCache.reload(
                        synchronizer,
                        manager,
                        prepareProfiler,
                        applyProfiler,
                        prepareExecutor,
                        applyExecutor
                    );
                }
            });
    }
}
