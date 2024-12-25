package mod.azure.azurelib.render.block;

import mod.azure.azurelib.animation.impl.AzBlockAnimator;
import mod.azure.azurelib.model.AzBakedModel;
import mod.azure.azurelib.render.AzProvider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * The {@code AzBlockEntityRenderer} class is an abstract base class for rendering custom block entities. It leverages
 * an animation and rendering pipeline mechanism to provide extended functionalities, such as dynamic animations and
 * model customization.
 *
 * @param <T> The specific type of {@link TileEntity} that this renderer processes.
 */
public abstract class AzBlockEntityRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

    private final AzProvider<T> provider;

    private final AzBlockEntityRendererPipeline<T> rendererPipeline;

    private AzBlockAnimator<T> reusedAzBlockAnimator;

    protected AzBlockEntityRenderer(AzBlockEntityRendererConfig<T> config) {
        this.provider = new AzProvider<>(config::createAnimator, config::modelLocation);
        this.rendererPipeline = createPipeline(config);
    }

    protected AzBlockEntityRendererPipeline<T> createPipeline(AzBlockEntityRendererConfig<T> config) {
        return new AzBlockEntityRendererPipeline<>(config, this);
    }

    @Override
    public void render(T entity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        AzBlockAnimator<T> cachedEntityAnimator = (AzBlockAnimator<T>) provider.provideAnimator(entity);
        AzBakedModel model = provider.provideBakedModel(entity);

        if (cachedEntityAnimator != null && model != null) {
            cachedEntityAnimator.setActiveModel(model);
        }

        // Point the renderer's current animator reference to the cached entity animator before rendering.
        reusedAzBlockAnimator = cachedEntityAnimator;

        // Execute the render pipeline.
        rendererPipeline.render(model, entity, 0, partialTicks, entity.getWorld().getCombinedLight(entity.getPos(), 0));
    }

    public AzBlockAnimator<T> getAnimator() {
        return reusedAzBlockAnimator;
    }
}
