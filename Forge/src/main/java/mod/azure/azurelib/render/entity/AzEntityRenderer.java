package mod.azure.azurelib.render.entity;

import mod.azure.azurelib.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.model.AzBakedModel;
import mod.azure.azurelib.render.AzProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * AzEntityRenderer is an abstract class responsible for rendering entities in the game. It extends the base
 * functionality of {@link EntityRenderer} to provide additional rendering capabilities specific to animated and custom
 * entities. This class is parameterized with a generic type {@code T}, which must extend {@link Entity}. It integrates
 * several abstractions such as animation management, model caching, and advanced rendering pipelines for handling
 * complex rendering behavior. Users are expected to configure this renderer using an {@link AzEntityRendererConfig}.
 * Key components: - {@link AzEntityRendererConfig}: Defines configuration options such as textures, models, and
 * animator providers. - {@link AzProvider}: Supplies baked models and animators for entities. -
 * {@link AzEntityRendererPipeline}: Manages rendering logic through a custom pipeline.
 */
public abstract class AzEntityRenderer<T extends Entity> extends Render<T> {

    private final AzEntityRendererConfig<T> config;

    private final AzProvider<T> provider;

    private final AzEntityRendererPipeline<T> rendererPipeline;

    private AzEntityAnimator<T> reusedAzEntityAnimator;

    protected AzEntityRenderer(AzEntityRendererConfig<T> config, RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.config = config;
        this.provider = new AzProvider<>(config::createAnimator, config::modelLocation);
        this.rendererPipeline = createPipeline(config);
    }

    protected AzEntityRendererPipeline<T> createPipeline(AzEntityRendererConfig<T> config) {
        return new AzEntityRendererPipeline<>(config, this);
    }

    @Override
    public final ResourceLocation getEntityTexture(T animatable) {
        return config.textureLocation(animatable);
    }

    public void superRender(
            T entity,
            float entityYaw,
            float partialTick
    ) {
        super.doRender(entity, entity.posX, entity.posY, entity.posZ, entityYaw, partialTick);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTick) {
        AzEntityAnimator<T> cachedEntityAnimator = (AzEntityAnimator<T>) provider.provideAnimator(entity);
        AzBakedModel azBakedModel = provider.provideBakedModel(entity);

        GlStateManager.pushMatrix();
        if (cachedEntityAnimator != null && azBakedModel != null) {
            cachedEntityAnimator.setActiveModel(azBakedModel);
        }

        // Point the renderer's current animator reference to the cached entity animator before rendering.
        reusedAzEntityAnimator = cachedEntityAnimator;

        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(getEntityTexture(entity));
        // Execute the render pipeline.
        rendererPipeline.render(
                azBakedModel,
                entity,
                entityYaw,
                partialTick,
                -1
        );
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    /**
     * Whether the entity's nametag should be rendered or not.<br>
     */
    @Override
    protected boolean canRenderName(T entity) {
        return AzEntityNameRenderUtil.shouldShowName(renderManager, entity);
    }

    public AzEntityAnimator<T> getAnimator() {
        return reusedAzEntityAnimator;
    }

    public AzEntityRendererConfig<T> config() {
        return config;
    }
}
