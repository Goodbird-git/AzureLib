package mod.azure.azurelib.core2.render.pipeline.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.core2.render.AzRendererConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import mod.azure.azurelib.common.internal.common.cache.texture.AnimatableTexture;
import mod.azure.azurelib.core2.render.AzEntityRendererConfig;
import mod.azure.azurelib.core2.render.entity.AzEntityLeashRenderUtil;
import mod.azure.azurelib.core2.render.entity.AzEntityRenderer;
import mod.azure.azurelib.core2.render.pipeline.AzLayerRenderer;
import mod.azure.azurelib.core2.render.pipeline.AzModelRenderer;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipeline;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipelineContext;

public class AzEntityRendererPipeline<T extends Entity> extends AzRendererPipeline<T> {

    private final AzEntityRenderer<T> entityRenderer;

    protected Matrix4f entityRenderTranslations = new Matrix4f();

    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public AzEntityRendererPipeline(AzEntityRendererConfig<T> config, AzEntityRenderer<T> entityRenderer) {
        super(config);
        this.entityRenderer = entityRenderer;
    }

    @Override
    protected AzRendererPipelineContext<T> createContext(AzRendererPipeline<T> rendererPipeline) {
        return new AzEntityRendererPipelineContext<>(this);
    }

    @Override
    protected AzModelRenderer<T> createModelRenderer(AzLayerRenderer<T> layerRenderer) {
        return new AzEntityModelRenderer<>(this, layerRenderer);
    }

    @Override
    protected AzLayerRenderer<T> createLayerRenderer(AzRendererConfig<T> config) {
        return new AzEntityLayerRenderer<>(config::renderLayers);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T animatable) {
        return entityRenderer.getTextureLocation(animatable);
    }

    /**
     * Update the current frame of a {@link AnimatableTexture potentially animated} texture used by this
     * GeoRenderer.<br>
     * This should only be called immediately prior to rendering, and only
     *
     * @see AnimatableTexture#setAndUpdate(ResourceLocation, int)
     */
    @Override
    public void updateAnimatedTextureFrame(T entity) {
        AnimatableTexture.setAndUpdate(
            getTextureLocation(entity),
            entity.getId() + entity.tickCount
        );
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling
     * and translating.<br>
     * {@link PoseStack} translations made here are kept until the end of the render process
     */
    @Override
    public void preRender(AzRendererPipelineContext<T> context, boolean isReRender) {
        var poseStack = context.poseStack();
        this.entityRenderTranslations.set(poseStack.last().pose());

        var config = entityRenderer.config();
        var scaleWidth = config.scaleWidth();
        var scaleHeight = config.scaleHeight();

        scaleModelForRender(context, scaleWidth, scaleHeight, isReRender);
    }

    @Override
    public void postRender(AzRendererPipelineContext<T> context, boolean isReRender) {}

    @Override
    public void renderFinal(AzRendererPipelineContext<T> context) {
        var bufferSource = context.multiBufferSource();
        var entity = context.animatable();
        var packedLight = context.packedLight();
        var partialTick = context.partialTick();
        var poseStack = context.poseStack();

        entityRenderer.superRender(entity, 0, partialTick, poseStack, bufferSource, packedLight);

        if (!(entity instanceof Mob mob)) {
            return;
        }

        var leashHolder = mob.getLeashHolder();

        if (leashHolder == null) {
            return;
        }

        AzEntityLeashRenderUtil.renderLeash(entityRenderer, mob, partialTick, poseStack, bufferSource, leashHolder);
    }

    public AzEntityRenderer<T> getRenderer() {
        return entityRenderer;
    }
}
