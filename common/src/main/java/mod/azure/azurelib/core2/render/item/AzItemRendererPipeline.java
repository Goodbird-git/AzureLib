package mod.azure.azurelib.core2.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.common.internal.common.cache.texture.AnimatableTexture;
import mod.azure.azurelib.core2.render.AzLayerRenderer;
import mod.azure.azurelib.core2.render.AzRendererConfig;
import mod.azure.azurelib.core2.render.AzRendererPipeline;
import mod.azure.azurelib.core2.render.AzRendererPipelineContext;

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
        var poseStack = context.poseStack();
        this.itemRenderTranslations = new Matrix4f(poseStack.last().pose());

        var config = itemRenderer.config();
        var scaleWidth = config.scaleWidth();
        var scaleHeight = config.scaleHeight();
        scaleModelForRender(context, scaleWidth, scaleHeight, isReRender);

        if (!isReRender) {
            var useNewOffset = config.useNewOffset();
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
            Item.getId(animatable.getItem()) + (int) RenderUtils.getCurrentTick()
        );
    }

    public AzItemRenderer getRenderer() {
        return itemRenderer;
    }
}
