package mod.azure.azurelib.render.armor;

import mod.azure.azurelib.render.textures.AnimatableTexture;
import mod.azure.azurelib.model.AzBakedModel;
import mod.azure.azurelib.render.*;
import mod.azure.azurelib.render.armor.bone.AzArmorBoneContext;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import javax.vecmath.Matrix4f;

public class AzArmorRendererPipeline extends AzRendererPipeline<ItemStack> {

    private final AzArmorModel armorModel;

    private final AzArmorRenderer armorRenderer;

    protected Matrix4f entityRenderTranslations = new Matrix4f();

    public AzArmorRendererPipeline(AzRendererConfig<ItemStack> config, AzArmorRenderer armorRenderer) {
        super(config);
        this.armorModel = new AzArmorModel(this);
        this.armorRenderer = armorRenderer;
    }

    @Override
    protected AzRendererPipelineContext<ItemStack> createContext(AzRendererPipeline<ItemStack> rendererPipeline) {
        return new AzArmorRendererPipelineContext(rendererPipeline);
    }

    @Override
    protected AzModelRenderer<ItemStack> createModelRenderer(AzLayerRenderer<ItemStack> layerRenderer) {
        return new AzArmorModelRenderer(this, layerRenderer);
    }

    @Override
    protected AzLayerRenderer<ItemStack> createLayerRenderer(AzRendererConfig<ItemStack> config) {
        return new AzLayerRenderer<>(config::renderLayers);
    }

    @Override
    protected void updateAnimatedTextureFrame(ItemStack animatable) {
        Entity currentEntity = context().currentEntity();

        if (currentEntity != null) {
            ResourceLocation textureLocation = config().textureLocation(animatable);
            int frameTick = currentEntity.getEntityId() + currentEntity.ticksExisted;

            AnimatableTexture.setAndUpdate(textureLocation, frameTick);
        }
    }

    @Override
    public void preRender(AzRendererPipelineContext<ItemStack> context, boolean isReRender) {
        AzArmorRendererPipelineContext armorContext = (AzArmorRendererPipelineContext) context;
        ModelBiped baseModel = armorContext.baseModel();
        AzArmorBoneContext boneContext = armorContext.boneContext();
        AzArmorRendererConfig config = config();
        EntityEquipmentSlot currentSlot = armorContext.currentSlot();
        float scaleWidth = config.scaleWidth();
        float scaleHeight = config.scaleHeight();

        ItemStack animatable = armorContext.animatable();
        AzBakedModel model = armorRenderer.provider().provideBakedModel(animatable);

        this.entityRenderTranslations = new Matrix4f(RenderUtils.getCurrentMatrix());

        armorModel.applyBaseModel(baseModel);
        boneContext.grabRelevantBones(model, config.boneProvider());
        boneContext.applyBaseTransformations(baseModel);
        scaleModelForBaby(armorContext, isReRender);
        scaleModelForRender(context, scaleWidth, scaleHeight, isReRender);

        boneContext.applyBoneVisibilityBySlot(currentSlot);
    }

    @Override
    public void postRender(AzRendererPipelineContext<ItemStack> context, boolean isReRender) {}

    public void scaleModelForBaby(AzArmorRendererPipelineContext context, boolean isReRender) {
        if (!armorModel.isChild || isReRender) {
            return;
        }

        ModelBiped baseModel = context.baseModel();
        EntityEquipmentSlot currentSlot = context.currentSlot();

        if (currentSlot == EntityEquipmentSlot.HEAD) {
            if (baseModel.isChild) {
                float headScale = 1.5f / 0.0625F;
                GlStateManager.scale(headScale, headScale, headScale);
            }

            GlStateManager.translate(0, baseModel.bipedHead.offsetY / 16f, baseModel.bipedHead.offsetZ / 16f);
        } else {
            float bodyScale = 1 / 0.0625F;

            GlStateManager.scale(bodyScale, bodyScale, bodyScale);
            GlStateManager.translate(0, baseModel.bipedBody.offsetY / 16f, 0);
        }
    }

    public AzArmorModel armorModel() {
        return armorModel;
    }

    @Override
    public AzArmorRendererConfig config() {
        return (AzArmorRendererConfig) super.config();
    }

    @Override
    public AzArmorRendererPipelineContext context() {
        return (AzArmorRendererPipelineContext) super.context();
    }

    public AzArmorRenderer renderer() {
        return armorRenderer;
    }
}
