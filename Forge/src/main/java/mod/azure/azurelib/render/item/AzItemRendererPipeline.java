package mod.azure.azurelib.render.item;

import mod.azure.azurelib.client.texture.AnimatableTexture;
import mod.azure.azurelib.render.AzLayerRenderer;
import mod.azure.azurelib.render.AzRendererConfig;
import mod.azure.azurelib.render.AzRendererPipeline;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Extends the {@link AzRendererPipeline} to provide a specific implementation for rendering {@link ItemStack} objects.
 * This pipeline includes methods and configurations designed for item rendering and leverages additional utilities such
 * as translation matrices and scaling functionalities for accurate rendering.
 */
public class AzItemRendererPipeline extends AzRendererPipeline<ItemStack> {

    private final AzItemRenderer itemRenderer;

    protected Matrix4f itemRenderTranslations = new Matrix4f();

    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public AzItemRendererPipeline(AzItemRendererConfig config, AzItemRenderer itemRenderer) {
        super(config);
        this.itemRenderer = itemRenderer;
    }

    @Override
    protected AzRendererPipelineContext<ItemStack> createContext(AzRendererPipeline<ItemStack> rendererPipeline) {
        return new AzItemRendererPipelineContext(rendererPipeline);
    }

    @Override
    protected AzItemModelRenderer createModelRenderer(AzLayerRenderer<ItemStack> layerRenderer) {
        return new AzItemModelRenderer(this, layerRenderer);
    }

    @Override
    protected AzLayerRenderer<ItemStack> createLayerRenderer(AzRendererConfig<ItemStack> config) {
        return new AzLayerRenderer<>(config::renderLayers);
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling
     * and translating.<br>
     * {@link PoseStack} translations made here are kept until the end of the render process
     */
    @Override
    public void preRender(AzRendererPipelineContext<ItemStack> context, boolean isReRender) {
        PoseStack poseStack = context.poseStack();
        this.itemRenderTranslations = new Matrix4f(poseStack.last().pose());

        AzItemRendererConfig config = itemRenderer.config();
        float scaleWidth = config.scaleWidth();
        float scaleHeight = config.scaleHeight();
        scaleModelForRender(context, scaleWidth, scaleHeight, isReRender);

        if (!isReRender) {
            boolean useNewOffset = config.useNewOffset();
            poseStack.translate(0.5f, useNewOffset ? 0.0f : 0.51f, 0.5f);
        }
    }

    @Override
    public void postRender(AzRendererPipelineContext<ItemStack> context, boolean isReRender) {}

    /**
     * Update the current frame of a {@link AnimatableTexture potentially animated} texture used by this
     * GeoRenderer.<br>
     * This should only be called immediately prior to rendering, and only
     *
     * @see AnimatableTexture#setAndUpdate(ResourceLocation, int)
     */
    @Override
    public void updateAnimatedTextureFrame(ItemStack animatable) {
        AnimatableTexture.setAndUpdate(
            config.textureLocation(animatable),
            Item.getIdFromItem(animatable.getItem()) + (int) RenderUtils.getCurrentTick()
        );
    }

    public AzItemRenderer getRenderer() {
        return itemRenderer;
    }
}
