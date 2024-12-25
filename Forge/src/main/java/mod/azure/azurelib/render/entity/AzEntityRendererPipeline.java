package mod.azure.azurelib.render.entity;

import mod.azure.azurelib.render.textures.AnimatableTexture;
import mod.azure.azurelib.render.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.ResourceLocation;

/**
 * Represents a renderer pipeline specifically designed for rendering entities. This pipeline facilitates stages of
 * rendering where contextual work like pre-translations, texture animations, and leash rendering are managed within a
 * customizable structure.
 *
 * @param <T> The type of entity this renderer pipeline handles. Extends from the base {@link Entity}.
 */
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
            config.textureLocation(entity),
            entity.getEntityId() + entity.ticksExisted
        );
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling
     * and translating.<br>
     * {@link GlStateManager} translations made here are kept until the end of the render process
     */
    @Override
    public void preRender(AzRendererPipelineContext<T> context, boolean isReRender) {
        GlStateManager poseStack = context.glStateManager();
        this.entityRenderTranslations.set(poseStack.last().pose());

        AzEntityRendererConfig<T> config = entityRenderer.config();
        float scaleWidth = config.scaleWidth();
        float scaleHeight = config.scaleHeight();

        scaleModelForRender(context, scaleWidth, scaleHeight, isReRender);
    }

    @Override
    public void postRender(AzRendererPipelineContext<T> context, boolean isReRender) {}

    /**
     * Renders the final frame of the entity, including handling special cases such as entities with leashes.
     *
     * @param context the rendering context that contains all required data for rendering, such as the entity, pose
     *                stack, light information, and buffer source
     */
    @Override
    public void renderFinal(AzRendererPipelineContext<T> context) {
        T entity = context.animatable();
        float partialTick = context.partialTick();
        GlStateManager poseStack = context.glStateManager();

        if (!(entity instanceof EntityMob)) {
            return;
        }

        Entity leashHolder = ((EntityMob) entity).getLeashHolder();

        if (leashHolder == null) {
            return;
        }

        AzEntityLeashRenderUtil.renderLeash(entityRenderer, ((EntityMob) entity), partialTick, poseStack, leashHolder);
    }

    public AzEntityRenderer<T> getRenderer() {
        return entityRenderer;
    }
}
