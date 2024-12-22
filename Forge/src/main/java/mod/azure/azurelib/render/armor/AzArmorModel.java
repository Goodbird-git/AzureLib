package mod.azure.azurelib.render.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;

public class AzArmorModel<E extends EntityLiving> extends LayerArmorBase<E> {

    private final AzArmorRendererPipeline rendererPipeline;

    public AzArmorModel(AzArmorRendererPipeline rendererPipeline) {
        super(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        this.rendererPipeline = rendererPipeline;
    }

    @Override
    public void renderToBuffer(
        PoseStack poseStack,
        VertexConsumer buffer,
        int packedLight,
        int packedOverlay,
        int var5
    ) {
        Minecraft mc = Minecraft.getMinecraft();
        AzArmorRendererPipelineContext context = rendererPipeline.context();
        Entity currentEntity = context.currentEntity();
        ItemStack currentStack = context.currentStack();
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
        mod.azure.azurelib.render.armor.bone.AzArmorBoneContext boneContext = rendererPipeline.context().boneContext();
        boneContext.setAllVisible(pVisible);
    }
}
