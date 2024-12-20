package mod.azure.azurelib.core2.render.pipeline.item;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.common.internal.common.cache.texture.AnimatableTexture;
import mod.azure.azurelib.core2.render.item.AzItemRenderer;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;
import mod.azure.azurelib.core2.render.pipeline.AzLayerRenderer;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipeline;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipelineContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public class AzItemRendererPipeline extends AzRendererPipeline<ItemStack> {

    private final AzItemRenderer itemRenderer;

    protected Matrix4f itemRenderTranslations = new Matrix4f();

    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public AzItemRendererPipeline(AzItemRenderer itemRenderer) {
        super();
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
    protected AzLayerRenderer<ItemStack> createLayerRenderer() {
        return new AzLayerRenderer<>(this::getRenderLayers);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ItemStack animatable) {
        return itemRenderer.getTextureLocation(animatable);
    }

    @Override
    protected List<AzRenderLayer<ItemStack>> getRenderLayers() {
        return itemRenderer.getRenderLayers();
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

        scaleModelForRender(context, this.itemRenderer.getScaleWidth(), this.itemRenderer.getScaleHeight(), isReRender);

        if (!isReRender) {
            poseStack.translate(0.5f, this.useNewOffset() ? 0.0f : 0.51f, 0.5f);
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
            getTextureLocation(animatable),
            Item.getId(animatable.getItem()) + (int) RenderUtils.getCurrentTick()
        );
    }

    /**
     * Determines whether to apply the y offset for a model due to the change in BlockBench 4.11.
     *
     * @return {@code false} by default, meaning the Y-offset will be {@code 0.51f}. Override this method or change the
     *         return value to {@code true} to use the new Y-offset of {@code 0.0f} for anything created in 4.11+ of
     *         Blockbench.
     */
    public boolean useNewOffset() {
        return false;
    }

    public AzItemRenderer getRenderer() {
        return itemRenderer;
    }
}
