package mod.azure.azurelib.core2.render.pipeline.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.common.internal.common.cache.texture.AnimatableTexture;
import mod.azure.azurelib.core2.render.entity.AzEntityLeashRenderUtil;
import mod.azure.azurelib.core2.render.entity.AzEntityRenderer;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;
import mod.azure.azurelib.core2.render.pipeline.AzLayerRenderer;
import mod.azure.azurelib.core2.render.pipeline.AzModelRenderer;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipeline;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipelineContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public class AzEntityRendererPipeline<T extends Entity> extends AzRendererPipeline<T> {

    private final AzEntityRenderer<T> entityRenderer;

    protected Matrix4f entityRenderTranslations = new Matrix4f();

    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public AzEntityRendererPipeline(AzEntityRenderer<T> entityRenderer) {
        super();
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
    protected AzLayerRenderer<T> createLayerRenderer() {
        return new AzEntityLayerRenderer<>(this::getRenderLayers);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T animatable) {
        return entityRenderer.getTextureLocation(animatable);
    }

    @Override
    protected List<AzRenderLayer<T>> getRenderLayers() {
        return entityRenderer.getRenderLayers();
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

        scaleModelForRender(
            context,
            this.entityRenderer.getScaleWidth(),
            this.entityRenderer.getScaleHeight(),
            isReRender
        );
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

    /**
     * Gets the max rotation value for dying entities.<br>
     * You might want to modify this for different aesthetics, such as a
     * {@link net.minecraft.world.entity.monster.Spider} flipping upside down on death.<br>
     * Functionally equivalent to {@link net.minecraft.client.renderer.entity.LivingEntityRenderer#getFlipDegrees}
     */
    protected float getDeathMaxRotation(T entity) {
        return 90f;
    }

    public boolean isShaking(T entity) {
        return entity.isFullyFrozen();
    }

    public AzEntityRenderer<T> getRenderer() {
        return entityRenderer;
    }
}
