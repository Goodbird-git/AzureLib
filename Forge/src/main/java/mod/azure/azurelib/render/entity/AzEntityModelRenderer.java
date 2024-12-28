package mod.azure.azurelib.render.entity;

import mod.azure.azurelib.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core.utils.Interpolations;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzLayerRenderer;
import mod.azure.azurelib.render.AzModelRenderer;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

/**
 * AzEntityModelRenderer is a class responsible for rendering animated 3D entity models in a pipeline-based rendering
 * setup. Extends the {@link AzModelRenderer} class and utilizes the {@link AzEntityRendererPipeline} to handle various
 * rendering tasks, such as applying model transformations and managing animated states in the rendering lifecycle. <br>
 *
 * @param <T> The type of entity that this renderer applies to, extends the {@link Entity} class.
 */
public class AzEntityModelRenderer<T extends Entity> extends AzModelRenderer<T> {

    private final AzEntityRendererPipeline<T> entityRendererPipeline;

    public AzEntityModelRenderer(AzEntityRendererPipeline<T> entityRendererPipeline, AzLayerRenderer<T> layerRenderer) {
        super(entityRendererPipeline, layerRenderer);
        this.entityRendererPipeline = entityRendererPipeline;
    }

    /**
     * The actual render method that subtype renderers should override to handle their specific rendering tasks.<br>
     * {@link AzEntityRendererPipeline#preRender} has already been called by this stage, and
     * {@link AzEntityRendererPipeline#postRender} will be called directly after
     */
    @Override
    public void render(AzRendererPipelineContext<T> context, boolean isReRender) {
        T animatable = context.animatable();
        float partialTick = context.partialTick();

        GlStateManager.pushMatrix();

        EntityLiving livingEntity = animatable instanceof EntityLiving ? (EntityLiving) animatable : null;

        boolean shouldSit = animatable.isPassenger(animatable) && (animatable.getRidingEntity() != null);
        float lerpBodyRot = livingEntity == null
            ? 0
            : mod.azure.azurelib.core.utils.MathHelper.rotLerp(
                partialTick,
                livingEntity.prevRenderYawOffset,
                livingEntity.renderYawOffset
            );
        float lerpHeadRot = livingEntity == null
            ? 0
            : mod.azure.azurelib.core.utils.MathHelper.rotLerp(
                partialTick,
                livingEntity.prevRotationYawHead,
                livingEntity.rotationYawHead
            );
        float netHeadYaw = lerpHeadRot - lerpBodyRot;

        if (shouldSit && animatable.getRidingEntity() instanceof EntityLiving) {
            lerpBodyRot = mod.azure.azurelib.core.utils.MathHelper.rotLerp(partialTick, ((EntityLiving) animatable).prevRenderYawOffset, ((EntityLiving) animatable).renderYawOffset);
            netHeadYaw = lerpHeadRot - lerpBodyRot;
            float clampedHeadYaw = MathHelper.clamp(MathHelper.wrapDegrees(netHeadYaw), -85, 85);
            lerpBodyRot = lerpHeadRot - clampedHeadYaw;

            if (clampedHeadYaw * clampedHeadYaw > 2500f)
                lerpBodyRot += clampedHeadYaw * 0.2f;

            netHeadYaw = lerpHeadRot - lerpBodyRot;
        }

        float nativeScale = 1;
        float ageInTicks = animatable.ticksExisted + partialTick;
        float limbSwingAmount = 0;
        float limbSwing = 0;

        GlStateManager.scale(nativeScale, nativeScale, nativeScale);

        if (!shouldSit && animatable.isEntityAlive() && livingEntity != null) {
            limbSwingAmount = Interpolations.lerp(
                livingEntity.prevLimbSwingAmount,
                livingEntity.limbSwingAmount,
                partialTick
            );
            limbSwing = livingEntity.limbSwing - livingEntity.limbSwingAmount * (1 - partialTick);

            if (livingEntity.isChild()) {
                limbSwing *= 3f;
            }

            if (limbSwingAmount > 1f) {
                limbSwingAmount = 1f;
            }
        }

        if (!isReRender) {
            AzEntityAnimator<T> animator = entityRendererPipeline.getRenderer().getAnimator();

            if (animator != null) {
                animator.animate(animatable, context.partialTick());
            }
        }
        if (!animatable.isInvisibleToPlayer(Minecraft.getMinecraft().player)) {
            super.render(context, isReRender);
        }

        GlStateManager.popMatrix();
    }

    /**
     * Renders the provided {@link AzBone} and its associated child bones
     */
    @Override
    public void renderRecursively(AzRendererPipelineContext<T> context, AzBone bone, boolean isReRender) {
        T entity = context.animatable();

        GlStateManager.pushMatrix();
        RenderUtils.translateMatrixToBone(bone);
        RenderUtils.translateToPivotPoint(bone);
        RenderUtils.rotateMatrixAroundBone(bone);
        RenderUtils.scaleMatrixForBone(bone);
        RenderUtils.translateAwayFromPivotPoint(bone);

        renderCubesOfBone(context, bone);

        if (!isReRender) {
            layerRenderer.applyRenderLayersForBone(context, bone);
        }

        renderChildBones(context, bone, isReRender);

        GlStateManager.popMatrix();
    }
}
