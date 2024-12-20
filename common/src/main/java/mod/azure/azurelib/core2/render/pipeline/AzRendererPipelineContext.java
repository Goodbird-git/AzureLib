package mod.azure.azurelib.core2.render.pipeline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import mod.azure.azurelib.core.object.Color;
import mod.azure.azurelib.core2.model.AzBakedModel;

public abstract class AzRendererPipelineContext<T> {

    private final AzRendererPipeline<T> rendererPipeline;

    private T animatable;

    private AzBakedModel bakedModel;

    private MultiBufferSource multiBufferSource;

    private int packedLight;

    private int packedOverlay;

    private float partialTick;

    private PoseStack poseStack;

    private int renderColor;

    private @Nullable RenderType renderType;

    private VertexConsumer vertexConsumer;

    protected AzRendererPipelineContext(AzRendererPipeline<T> rendererPipeline) {
        this.rendererPipeline = rendererPipeline;
    }

    public void populate(
        T animatable,
        AzBakedModel bakedModel,
        MultiBufferSource multiBufferSource,
        int packedLight,
        float partialTick,
        PoseStack poseStack,
        RenderType renderType,
        VertexConsumer vertexConsumer
    ) {
        this.animatable = animatable;
        this.bakedModel = bakedModel;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.packedOverlay = getPackedOverlay(animatable, 0, partialTick);
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.renderType = renderType;
        this.vertexConsumer = vertexConsumer;
        this.renderColor = getRenderColor(animatable, partialTick, packedLight).argbInt();

        if (renderType == null) {
            var textureLocation = rendererPipeline.getTextureLocation(animatable);
            this.renderType = getDefaultRenderType(animatable, textureLocation, multiBufferSource, partialTick);
        }

        Objects.requireNonNull(this.renderType);

        if (vertexConsumer == null) {
            this.vertexConsumer = multiBufferSource.getBuffer(this.renderType);
        }
    }

    /**
     * Gets the {@link RenderType} to render the given animatable with.<br>
     * Uses the {@link RenderType#entityCutoutNoCull} {@code RenderType} by default.<br>
     * Override this to change the way a model will render (such as translucent models, etc)
     */
    public abstract @NotNull RenderType getDefaultRenderType(
        T animatable,
        ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource,
        float partialTick
    );

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
     * {@link net.minecraft.world.entity.monster.Creeper} white tint when exploding.
     */
    protected int getPackedOverlay(T animatable, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
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

    public MultiBufferSource multiBufferSource() {
        return multiBufferSource;
    }

    public int packedLight() {
        return packedLight;
    }

    public int packedOverlay() {
        return packedOverlay;
    }

    public float partialTick() {
        return partialTick;
    }

    public PoseStack poseStack() {
        return poseStack;
    }

    public int renderColor() {
        return renderColor;
    }

    public @Nullable RenderType renderType() {
        return renderType;
    }

    public VertexConsumer vertexConsumer() {
        return vertexConsumer;
    }

    public void setVertexConsumer(VertexConsumer vertexConsumer) {
        this.vertexConsumer = vertexConsumer;
    }
}
