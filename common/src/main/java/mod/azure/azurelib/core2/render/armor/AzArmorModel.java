package mod.azure.azurelib.core2.render.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AzArmorModel<E extends LivingEntity> extends HumanoidModel<E> {

    private final AzArmorRendererPipeline rendererPipeline;

    public AzArmorModel(AzArmorRendererPipeline rendererPipeline) {
        super(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        this.rendererPipeline = rendererPipeline;
    }

    @Override
    public void renderToBuffer(
        @NotNull PoseStack poseStack,
        @Nullable VertexConsumer buffer,
        int packedLight,
        int packedOverlay,
        int var5
    ) {
        var mc = Minecraft.getInstance();
        var context = rendererPipeline.context();
        var currentEntity = context.currentEntity();
        var currentStack = context.currentStack();
        MultiBufferSource bufferSource = Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();

        var shouldOutline = Minecraft.getInstance().levelRenderer.shouldShowEntityOutlines() && mc
            .shouldEntityAppearGlowing(
                currentEntity
            );

        if (shouldOutline) {
            bufferSource = Minecraft.getInstance().levelRenderer.renderBuffers.outlineBufferSource();
        }

        var config = rendererPipeline.config();
        var animatable = context.animatable();
        var partialTick = mc.getTimer().getGameTimeDeltaTicks();
        var textureLocation = config.textureLocation(animatable);
        var renderType = context.getDefaultRenderType(animatable, textureLocation, bufferSource, partialTick);
        buffer = ItemRenderer.getArmorFoilBuffer(bufferSource, renderType, currentStack.hasFoil());

        var model = rendererPipeline.renderer().provider().provideBakedModel(animatable);
        rendererPipeline.render(poseStack, model, animatable, bufferSource, null, buffer, 0, partialTick, packedLight);
    }

    /**
     * Applies settings and transformations pre-render based on the default model
     */
    public void applyBaseModel(HumanoidModel<?> baseModel) {
        this.young = baseModel.young;
        this.crouching = baseModel.crouching;
        this.riding = baseModel.riding;
        this.rightArmPose = baseModel.rightArmPose;
        this.leftArmPose = baseModel.leftArmPose;
    }

    @Override
    public void setAllVisible(boolean pVisible) {
        super.setAllVisible(pVisible);
        var boneContext = rendererPipeline.context().boneContext();
        boneContext.setAllVisible(pVisible);
    }
}
