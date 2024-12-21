package mod.azure.azurelib.core2.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.core2.animation.impl.AzBlockAnimator;
import mod.azure.azurelib.core2.render.AzProvider;

public abstract class AzBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    private final AzProvider<T> provider;

    private final AzBlockEntityRendererPipeline<T> rendererPipeline;

    @Nullable
    private AzBlockAnimator<T> reusedAzBlockAnimator;

    protected AzBlockEntityRenderer(AzBlockEntityRendererConfig<T> config) {
        this.provider = new AzProvider<>(config::createAnimator, config::modelLocation);
        this.rendererPipeline = new AzBlockEntityRendererPipeline<>(config, this);
    }

    @Override
    public void render(
        @NotNull T entity,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource source,
        int packedLight,
        int packedOverlay
    ) {
        var cachedEntityAnimator = (AzBlockAnimator<T>) provider.provideAnimator(entity);
        var model = provider.provideBakedModel(entity);

        if (cachedEntityAnimator != null && model != null) {
            cachedEntityAnimator.setActiveModel(model);
        }

        // Point the renderer's current animator reference to the cached entity animator before rendering.
        reusedAzBlockAnimator = cachedEntityAnimator;

        // Execute the render pipeline.
        rendererPipeline.render(poseStack, model, entity, source, null, null, 0, partialTick, packedLight);
    }

    public AzBlockAnimator<T> getAnimator() {
        return reusedAzBlockAnimator;
    }
}
