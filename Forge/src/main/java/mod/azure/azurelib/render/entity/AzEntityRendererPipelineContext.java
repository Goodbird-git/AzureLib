package mod.azure.azurelib.render.entity;

import mod.azure.azurelib.render.AzRendererPipeline;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

/**
 * A context class specifically for rendering entities using a custom rendering pipeline. This class extends
 * {@code AzRendererPipelineContext} and provides implementations for methods to customize entity rendering, such as
 * determining default render types and packed overlay settings.
 *
 * @param <T> the type of entity being rendered, extending {@code Entity}
 */
public class AzEntityRendererPipelineContext<T extends Entity> extends AzRendererPipelineContext<T> {

    public AzEntityRendererPipelineContext(AzRendererPipeline<T> rendererPipeline) {
        super(rendererPipeline);
    }

//    @Override
//    public RenderType getDefaultRenderType(
//        T animatable,
//        ResourceLocation texture,
//        MultiBufferSource bufferSource,
//        float partialTick
//    ) {
//        return RenderType.entityCutoutNoCull(texture);
//    }

    /**
     * Gets a packed overlay coordinate pair for rendering.<br>
     * Mostly just used for the red tint when an entity is hurt, but can be used for other things like the
     * {@link net.minecraft.entity.monster.EntityCreeper} white tint when exploding.
     */
    @Override
    public int getPackedOverlay(T entity, float u, float partialTick) {
        if (!(entity instanceof EntityLiving)) {
            return OverlayTexture.NO_OVERLAY;
        }

        return OverlayTexture.pack(
            OverlayTexture.u(u),
            OverlayTexture.v(((EntityLiving) entity).hurtTime > 0 || ((EntityLiving) entity).deathTime > 0)
        );
    }
}
