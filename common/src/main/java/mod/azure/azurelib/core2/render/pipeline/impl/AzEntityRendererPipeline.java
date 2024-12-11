package mod.azure.azurelib.core2.render.pipeline.impl;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.azure.azurelib.common.api.client.renderer.layer.GeoRenderLayer;
import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.common.internal.common.cache.texture.AnimatableTexture;
import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.entity.AzEntityRenderer;
import mod.azure.azurelib.core2.render.entity.RenderLeashUtil;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class AzEntityRendererPipeline<T extends Entity> extends AzRendererPipeline<T> {

    private final AzEntityRenderer<T> azEntityRenderer;

    protected Matrix4f entityRenderTranslations = new Matrix4f();
    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public AzEntityRendererPipeline(AzEntityRenderer<T> azEntityRenderer) {
        this.azEntityRenderer = azEntityRenderer;
    }

    @Override
    protected @NotNull ResourceLocation getTextureLocation(@NotNull T animatable) {
        return azEntityRenderer.getTextureLocation(animatable);
    }

    @Override
    public RenderType getDefaultRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    /**
     * Update the current frame of a {@link AnimatableTexture potentially animated} texture used by this
     * GeoRenderer.<br>
     * This should only be called immediately prior to rendering, and only
     *
     * @see AnimatableTexture#setAndUpdate(ResourceLocation, int)
     */
    @Override
    public void updateAnimatedTextureFrame(T entity) {
        AnimatableTexture.setAndUpdate(
            getTextureLocation(entity),
            entity.getId() + entity.tickCount
        );
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling
     * and translating.<br>
     * {@link PoseStack} translations made here are kept until the end of the render process
     */
    @Override
    public void preRender(
        PoseStack poseStack,
        T animatable,
        AzBakedModel model,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        this.entityRenderTranslations = new Matrix4f(poseStack.last().pose());

        scaleModelForRender(
            this.azEntityRenderer.getScaleWidth(),
            this.azEntityRenderer.getScaleHeight(),
            poseStack,
            animatable,
            model,
            isReRender,
            partialTick,
            packedLight,
            packedOverlay
        );
    }

    /**
     * The actual render method that subtype renderers should override to handle their specific rendering tasks.<br>
     * {@link AzEntityRendererPipeline#preRender} has already been called by this stage, and {@link AzEntityRendererPipeline#postRender} will be
     * called directly after
     */
    @Override
    public void actuallyRender(
        PoseStack poseStack,
        T animatable,
        AzBakedModel model,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        poseStack.pushPose();

        LivingEntity livingEntity = animatable instanceof LivingEntity entity ? entity : null;

        boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null);
        float lerpBodyRot = livingEntity == null
            ? 0
            : Mth.rotLerp(
            partialTick,
            livingEntity.yBodyRotO,
            livingEntity.yBodyRot
        );
        float lerpHeadRot = livingEntity == null
            ? 0
            : Mth.rotLerp(
            partialTick,
            livingEntity.yHeadRotO,
            livingEntity.yHeadRot
        );
        float netHeadYaw = lerpHeadRot - lerpBodyRot;

        if (shouldSit && animatable.getVehicle() instanceof LivingEntity livingentity) {
            lerpBodyRot = Mth.rotLerp(partialTick, livingentity.yBodyRotO, livingentity.yBodyRot);
            netHeadYaw = lerpHeadRot - lerpBodyRot;
            float clampedHeadYaw = Mth.clamp(Mth.wrapDegrees(netHeadYaw), -85, 85);
            lerpBodyRot = lerpHeadRot - clampedHeadYaw;

            if (clampedHeadYaw * clampedHeadYaw > 2500f)
                lerpBodyRot += clampedHeadYaw * 0.2f;

            netHeadYaw = lerpHeadRot - lerpBodyRot;
        }

        if (animatable.getPose() == Pose.SLEEPING && livingEntity != null) {
            Direction bedDirection = livingEntity.getBedOrientation();

            if (bedDirection != null) {
                float eyePosOffset = livingEntity.getEyeHeight(Pose.STANDING) - 0.1F;

                poseStack.translate(
                    -bedDirection.getStepX() * eyePosOffset,
                    0,
                    -bedDirection.getStepZ() * eyePosOffset
                );
            }
        }

        float nativeScale = livingEntity != null ? livingEntity.getScale() : 1;
        float ageInTicks = animatable.tickCount + partialTick;
        float limbSwingAmount = 0;
        float limbSwing = 0;

        poseStack.scale(nativeScale, nativeScale, nativeScale);
        applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick, nativeScale);

        if (!shouldSit && animatable.isAlive() && livingEntity != null) {
            limbSwingAmount = Mth.lerp(
                partialTick,
                livingEntity.walkAnimation.speedOld,
                livingEntity.walkAnimation.speed()
            );
            limbSwing = livingEntity.walkAnimation.position() - livingEntity.walkAnimation.speed() * (1 - partialTick);

            if (livingEntity.isBaby())
                limbSwing *= 3f;

            if (limbSwingAmount > 1f)
                limbSwingAmount = 1f;
        }

        if (!isReRender) {
            // FIXME: Figure out what to do with this data stuff.
//            float headPitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
//            var velocity = animatable.getDeltaMovement();
//            float avgVelocity = (float) (Math.abs(velocity.x) + Math.abs(velocity.z) / 2f);
//
//            long instanceId = getInstanceId(animatable);
//
//            animationState.setData(DataTickets.TICK, animatable.getTick(animatable));
//            animationState.setData(DataTickets.ENTITY, animatable);
//            animationState.setData(
//                DataTickets.ENTITY_MODEL_DATA,
//                new EntityModelData(
//                    shouldSit,
//                    livingEntity != null && livingEntity.isBaby(),
//                    -netHeadYaw,
//                    -headPitch
//                )
//            );
//
//            this.model.addAdditionalStateData(animatable, instanceId, animationState::setData);

            var animator = azEntityRenderer.getAnimator();

            if (animator != null) {
                var animationState = animator.createAnimationState(animatable, limbSwing, limbSwingAmount, partialTick);
                animator.animate(animatable, animationState);
            }
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        if (!animatable.isInvisibleTo(Minecraft.getInstance().player)) {
            AzEntityRendererPipeline.super.actuallyRender(
                poseStack,
                animatable,
                model,
                renderType,
                bufferSource,
                buffer,
                isReRender,
                partialTick,
                packedLight,
                packedOverlay,
                colour
            );
        }

        poseStack.popPose();
    }

    /**
     * Render the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer
     */
    @Override
    public void applyRenderLayers(
        PoseStack poseStack,
        T animatable,
        AzBakedModel model,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        if (!animatable.isSpectator()) {
            AzEntityRendererPipeline.super.applyRenderLayers(
                poseStack,
                animatable,
                model,
                renderType,
                bufferSource,
                buffer,
                partialTick,
                packedLight,
                packedOverlay
            );
        }
    }

    /**
     * Renders the provided {@link AzBone} and its associated child bones
     */
    @Override
    public void renderRecursively(
        PoseStack poseStack,
        T entity,
        AzBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        poseStack.pushPose();
        RenderUtils.translateMatrixToBone(poseStack, bone);
        RenderUtils.translateToPivotPoint(poseStack, bone);
        RenderUtils.rotateMatrixAroundBone(poseStack, bone);
        RenderUtils.scaleMatrixForBone(poseStack, bone);

        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());
            Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);

            bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
            bone.setLocalSpaceMatrix(
                RenderUtils.translateMatrix(localMatrix, azEntityRenderer.getRenderOffset(entity, 1).toVector3f())
            );
            bone.setWorldSpaceMatrix(
                RenderUtils.translateMatrix(new Matrix4f(localMatrix), entity.position().toVector3f())
            );
        }

        RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

        if (!isReRender && buffer instanceof BufferBuilder builder && !builder.building)
            buffer = bufferSource.getBuffer(renderType);

        renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);

        if (!isReRender)
            applyRenderLayersForBone(
                poseStack,
                entity,
                bone,
                renderType,
                bufferSource,
                buffer,
                partialTick,
                packedLight,
                packedOverlay
            );

        renderChildBones(
            poseStack,
            entity,
            bone,
            renderType,
            bufferSource,
            buffer,
            isReRender,
            partialTick,
            packedLight,
            packedOverlay,
            colour
        );

        poseStack.popPose();
    }

    @Override
    public void renderFinal(PoseStack poseStack, T entity, AzBakedModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int colour) {
        azEntityRenderer.superRender(entity, 0, partialTick, poseStack, bufferSource, packedLight);

        if (entity instanceof Mob mob) {
            var leashHolder = mob.getLeashHolder();

            if (leashHolder != null) {
                RenderLeashUtil.renderLeash(azEntityRenderer, mob, partialTick, poseStack, bufferSource, leashHolder);
            }
        }
    }

    /**
     * Create and fire the relevant {@code CompileLayers} event hook for this renderer
     */
    @Override
    public void fireCompileRenderLayersEvent() {
        // FIXME:
//        Services.GEO_RENDER_PHASE_EVENT_FACTORY.fireCompileEntityRenderLayers(geoEntityRenderer);
    }

    /**
     * Create and fire the relevant {@code Pre-Render} event hook for this renderer.<br>
     *
     * @return Whether the renderer should proceed based on the cancellation state of the event
     */
    @Override
    public boolean firePreRenderEvent(
        PoseStack poseStack,
        AzBakedModel model,
        MultiBufferSource bufferSource,
        float partialTick,
        int packedLight
    ) {
        // FIXME:
        return true;
//        return Services.GEO_RENDER_PHASE_EVENT_FACTORY.fireEntityPreRender(geoEntityRenderer, poseStack, model, bufferSource, partialTick, packedLight);
    }

    /**
     * Create and fire the relevant {@code Post-Render} event hook for this renderer
     */
    @Override
    public void firePostRenderEvent(
        PoseStack poseStack,
        AzBakedModel model,
        MultiBufferSource bufferSource,
        float partialTick,
        int packedLight
    ) {
        // FIXME:
//        Services.GEO_RENDER_PHASE_EVENT_FACTORY.fireEntityPostRender(geoEntityRenderer, poseStack, model, bufferSource, partialTick, packedLight);
    }

    /**
     * Applies rotation transformations to the renderer prior to render time to account for various entity states, default scale of 1
     */
    protected void applyRotations(
        T animatable,
        PoseStack poseStack,
        float ageInTicks,
        float rotationYaw,
        float partialTick
    ) {
        applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, 1);
    }

    /**
     * Applies rotation transformations to the renderer prior to render time to account for various entity states, scalable
     */
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw,
                                  float partialTick, float nativeScale) {
        if (isShaking(animatable)) {
            rotationYaw += (float) (Math.cos(animatable.tickCount * 3.25d) * Math.PI * 0.4d);
        }

        if (!animatable.hasPose(Pose.SLEEPING)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotationYaw));
        }

        if (animatable instanceof LivingEntity livingEntity) {
            if (livingEntity.deathTime > 0) {
                float deathRotation = (livingEntity.deathTime + partialTick - 1f) / 20f * 1.6f;

                poseStack.mulPose(Axis.ZP.rotationDegrees(Math.min(Mth.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable)));
            }
            else if (livingEntity.isAutoSpinAttack()) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90f - livingEntity.getXRot()));
                poseStack.mulPose(Axis.YP.rotationDegrees((livingEntity.tickCount + partialTick) * -75f));
            }
            else if (animatable.hasPose(Pose.SLEEPING)) {
                Direction bedOrientation = livingEntity.getBedOrientation();

                poseStack.mulPose(Axis.YP.rotationDegrees(bedOrientation != null ? RenderUtils.getDirectionAngle(bedOrientation) : rotationYaw));
                poseStack.mulPose(Axis.ZP.rotationDegrees(getDeathMaxRotation(animatable)));
                poseStack.mulPose(Axis.YP.rotationDegrees(270f));
            }
            else if (LivingEntityRenderer.isEntityUpsideDown(livingEntity)) {
                poseStack.translate(0, (animatable.getBbHeight() + 0.1f) / nativeScale, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            }
        }
    }

    /**
     * Gets the max rotation value for dying entities.<br>
     * You might want to modify this for different aesthetics, such as a
     * {@link net.minecraft.world.entity.monster.Spider} flipping upside down on death.<br>
     * Functionally equivalent to {@link net.minecraft.client.renderer.entity.LivingEntityRenderer#getFlipDegrees}
     */
    protected float getDeathMaxRotation(T entity) {
        return 90f;
    }

    public boolean isShaking(T entity) {
        return entity.isFullyFrozen();
    }

    /**
     * Gets a packed overlay coordinate pair for rendering.<br>
     * Mostly just used for the red tint when an entity is hurt, but can be used for other things like the
     * {@link net.minecraft.world.entity.monster.Creeper} white tint when exploding.
     */
    @Override
    public int getPackedOverlay(T entity, float u, float partialTick) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return OverlayTexture.NO_OVERLAY;
        }

        return OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(livingEntity.hurtTime > 0 || livingEntity.deathTime > 0));
    }
}
