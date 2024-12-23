package mod.azure.azurelib.render.entity;

import mod.azure.azurelib.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.render.AzProvider;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

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
public abstract class AzEntityRenderer<T extends Entity> extends EntityRenderer {

    private final AzEntityRendererConfig<T> config;

    private final AzProvider<T> provider;

    private final AzEntityRendererPipeline<T> rendererPipeline;

    private AzEntityAnimator<T> reusedAzEntityAnimator;

    protected AzEntityRenderer(AzEntityRendererConfig<T> config, EntityRendererProvider.Context context) {
        super(context);
        this.config = config;
        this.provider = new AzProvider<>(config::createAnimator, config::modelLocation);
        this.rendererPipeline = new AzEntityRendererPipeline<>(config, this);
    }

    @Override
    public final ResourceLocation getTextureLocation(T animatable) {
        return config.textureLocation(animatable);
    }

    public void superRender(
        :T entity,
        float entityYaw,
        float partialTick,
        GlStateManager glStateManager,
        int packedLight
    ) {
        super.render(entity, entityYaw, partialTick, glStateManager, packedLight);
    }

    @Override
    public void render(
        T entity,
        float entityYaw,
        float partialTick,
        GlStateManager glStateManager,
        int packedLight
    ) {
        AzEntityAnimator<T> cachedEntityAnimator = (AzEntityAnimator<T>) provider.provideAnimator(entity);
        mod.azure.azurelib.model.AzBakedModel azBakedModel = provider.provideBakedModel(entity);

        if (cachedEntityAnimator != null && azBakedModel != null) {
            cachedEntityAnimator.setActiveModel(azBakedModel);
        }

        // Point the renderer's current animator reference to the cached entity animator before rendering.
        reusedAzEntityAnimator = cachedEntityAnimator;

        // Execute the render pipeline.
        rendererPipeline.render(
            glStateManager,
            azBakedModel,
            entity,
            entityYaw,
            partialTick,
            packedLight
        );
    }

    /**
     * Whether the entity's nametag should be rendered or not.<br>
     */
    @Override
    public boolean shouldShowName(T entity) {
        return AzEntityNameRenderUtil.shouldShowName(entityRenderDispatcher, entity);
    }

    // Proxy method override for super.getBlockLightLevel external access.
    @Override
    public int getBlockLightLevel(T entity, BlockPos pos) {
        return super.getBlockLightLevel(entity, pos);
    }

    public AzEntityAnimator<T> getAnimator() {
        return reusedAzEntityAnimator;
    }

    public AzEntityRendererConfig<T> config() {
        return config;
    }
}
