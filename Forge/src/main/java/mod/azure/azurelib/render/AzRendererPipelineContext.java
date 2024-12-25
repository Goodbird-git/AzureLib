package mod.azure.azurelib.render;

import mod.azure.azurelib.core.object.Color;
import mod.azure.azurelib.model.AzBakedModel;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;

/**
 * An abstract base class representing the rendering context for a custom rendering pipeline. This class provides
 * generic rendering properties and behavior that can be extended to customize rendering for different types of
 * animatable objects.
 *
 * @param <T> the type of the animatable object being rendered
 */
public abstract class AzRendererPipelineContext<T> {

    private final AzRendererPipeline<T> rendererPipeline;

    private T animatable;

    private AzBakedModel bakedModel;

    private int packedLight;

    private int packedOverlay;

    private float partialTick;

    private BufferBuilder vertexConsumer;

    private int renderColor;

    protected AzRendererPipelineContext(AzRendererPipeline<T> rendererPipeline) {
        this.rendererPipeline = rendererPipeline;
    }

    /**
     * Populates the rendering context with all necessary parameters required to render a specific animatable object.
     * This method initializes the rendering pipeline with data such as the model, buffer source, lighting, and other
     * associated properties for rendering the specified animatable object.
     *
     * @param animatable        The animatable object that is being rendered.
     * @param bakedModel        The pre-baked 3D model associated with the animatable object.
     * @param packedLight       The packed light value for controlling light effects during rendering.
     * @param partialTick       The partial tick value for interpolating animations or movements.
     */
    public void populate(
        T animatable,
        AzBakedModel bakedModel,
        int packedLight,
        float partialTick,
        BufferBuilder vertexConsumer
    ) {
        this.animatable = animatable;
        this.bakedModel = bakedModel;
        this.packedLight = packedLight;
        this.packedOverlay = getPackedOverlay(animatable, 0, partialTick);
        this.partialTick = partialTick;
        this.renderColor = getRenderColor(animatable, partialTick, packedLight).getColor();
        this.vertexConsumer = vertexConsumer;
    }

    /**
     * Gets a tint-applying color to render the given animatable with.<br>
     * Returns {@link Color#WHITE} by default
     */
    protected Color getRenderColor(T animatable, float partialTick, int packedLight) {
        return Color.WHITE;
    }

    /**
     * Gets a packed overlay coordinate pair for rendering.<br>
     * Mostly just used for the red tint when an entity is hurt, but can be used for other things like the
     * {@link net.minecraft.entity.monster.EntityCreeper} white tint when exploding.
     */
    protected int getPackedOverlay(T animatable, float u, float partialTick) {
        return pack(0, 10);
    }

    private int pack(int u, int v) {
        return u | v << 16;
    }

    public AzRendererPipeline<T> rendererPipeline() {
        return rendererPipeline;
    }

    public T animatable() {
        return animatable;
    }

    public AzBakedModel bakedModel() {
        return bakedModel;
    }

    public int packedLight() {
        return packedLight;
    }

    public void setPackedLight(int packedLight) {
        this.packedLight = packedLight;
    }

    public int packedOverlay() {
        return packedOverlay;
    }

    public void setPackedOverlay(int packedOverlay) {
        this.packedOverlay = packedOverlay;
    }

    public float partialTick() {
        return partialTick;
    }

    public int renderColor() {
        return renderColor;
    }

    public void setRenderColor(int renderColor) {
        this.renderColor = renderColor;
    }

    public BufferBuilder vertexConsumer() {
        return vertexConsumer;
    }
}
