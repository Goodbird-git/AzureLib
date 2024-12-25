package mod.azure.azurelib.render.item;

import mod.azure.azurelib.animation.impl.AzItemAnimator;
import mod.azure.azurelib.model.AzBakedModel;
import mod.azure.azurelib.render.AzProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * AzItemRenderer is an abstract base class for rendering custom animated items in a game framework. It provides
 * utilities for handling item models, textures, and animations via a configurable pipeline and provider system. This
 * class supports rendering of items both in GUI contexts and in-world as entities, enabling advanced visual effects
 * such as custom animations and lighting. <br>
 * The rendering process utilizes a pipeline to manage render layers, textures, and baked models, integrating with game
 * frame components like PoseStack and MultiBufferSource.
 */
public abstract class AzItemRenderer {

    private final AzItemRendererConfig config;

    private final AzProvider<ItemStack> provider;

    private final AzItemRendererPipeline rendererPipeline;

    private AzItemAnimator reusedAzItemAnimator;

    protected AzItemRenderer(
        AzItemRendererConfig config
    ) {
        this.rendererPipeline = createPipeline(config);
        this.provider = new AzProvider<>(config::createAnimator, config::modelLocation);
        this.config = config;
    }

    protected AzItemRendererPipeline createPipeline(AzItemRendererConfig config) {
        return new AzItemRendererPipeline(config, this);
    }

    public void renderByGui(
        ItemStack stack,
        int packedLight
    ) {
        AzBakedModel model = provider.provideBakedModel(stack);

        prepareAnimator(stack, model);

        AzItemGuiRenderUtil.renderInGui(config, rendererPipeline, stack, model, stack, packedLight);
    }

    public void renderByItem(
        ItemStack stack,
        int packedLight
    ) {
        AzBakedModel model = provider.provideBakedModel(stack);
        int partialTick = Minecraft.getMinecraft().getFrameTimer().getIndex();

        prepareAnimator(stack, model);

        rendererPipeline.render(model, stack, 0, partialTick, packedLight);
    }

    private void prepareAnimator(ItemStack stack, AzBakedModel model) {
        AzItemAnimator cachedEntityAnimator = (AzItemAnimator) provider.provideAnimator(stack);

        if (cachedEntityAnimator != null && model != null) {
            cachedEntityAnimator.setActiveModel(model);
        }

        // Point the renderer's current animator reference to the cached entity animator before rendering.
        reusedAzItemAnimator = cachedEntityAnimator;
    }

    public AzItemAnimator getAnimator() {
        return reusedAzItemAnimator;
    }

    public AzItemRendererConfig config() {
        return config;
    }
}
