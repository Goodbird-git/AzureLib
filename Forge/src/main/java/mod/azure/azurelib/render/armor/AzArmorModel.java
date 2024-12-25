package mod.azure.azurelib.render.armor;

import mod.azure.azurelib.model.AzBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;

public class AzArmorModel extends ModelBiped {

    private final AzArmorRendererPipeline rendererPipeline;

    public AzArmorModel(AzArmorRendererPipeline rendererPipeline) {
        super(1);
        this.rendererPipeline = rendererPipeline;
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        Minecraft mc = Minecraft.getMinecraft();
        AzArmorRendererPipelineContext context = rendererPipeline.context();
        Entity currentEntity = context.currentEntity();
        ItemStack currentStack = context.currentStack();

        AzArmorRendererConfig config = rendererPipeline.config();
        ItemStack animatable = context.animatable();

        AzBakedModel model = rendererPipeline.renderer().provider().provideBakedModel(animatable);
        rendererPipeline.render(model, animatable, 0, mc.getRenderPartialTicks(), context.packedLight());
    }

    /**
     * Applies settings and transformations pre-render based on the default model
     */
    public void applyBaseModel(ModelBiped baseModel) {
        this.isChild = baseModel.isChild;
        this.isSneak = baseModel.isSneak;
        this.isRiding = baseModel.isRiding;
        this.rightArmPose = baseModel.rightArmPose;
        this.leftArmPose = baseModel.leftArmPose;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        mod.azure.azurelib.render.armor.bone.AzArmorBoneContext boneContext = rendererPipeline.context().boneContext();
        boneContext.setAllVisible(visible);
    }
}
