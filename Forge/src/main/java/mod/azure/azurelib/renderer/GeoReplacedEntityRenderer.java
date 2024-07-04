/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.constant.DataTickets;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.model.data.EntityModelData;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayersContainer;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Team.Visible;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.LightType;

import java.util.List;

/**
 * An alternate to {@link GeoEntityRenderer}, used specifically for replacing existing non-AzureLib entities with AzureLib rendering dynamically, without the need for an additional entity class
 */
public class GeoReplacedEntityRenderer<E extends Entity, T extends GeoAnimatable> extends EntityRenderer<E> implements GeoRenderer<T> {
    protected final GeoModel<T> model;
    protected final GeoRenderLayersContainer<T> renderLayers = new GeoRenderLayersContainer<>(this);
    protected final T animatable;

    protected E currentEntity;
    protected float scaleWidth = 1;
    protected float scaleHeight = 1;

    protected Matrix4f entityRenderTranslations = new Matrix4f();
    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public GeoReplacedEntityRenderer(EntityRendererManager renderManager, GeoModel<T> model, T animatable) {
        super(renderManager);

        this.model = model;
        this.animatable = animatable;

    }

    /**
     * Static rendering code for rendering a leash segment.<br>
     * It's a like-for-like from {@link net.minecraft.client.renderer.entity.MobRenderer#addVertexPair} that had to be duplicated here for flexible usage
     */
    private static void renderLeashPiece(IVertexBuilder buffer, Matrix4f positionMatrix, float xDif, float yDif, float zDif, int entityBlockLight, int holderBlockLight, int entitySkyLight, int holderSkyLight, float width, float yOffset, float xOffset, float zOffset, int segment, boolean isLeashKnot) {
        float piecePosPercent = segment / 24f;
        int lerpBlockLight = (int) MathHelper.lerp(piecePosPercent, entityBlockLight, holderBlockLight);
        int lerpSkyLight = (int) MathHelper.lerp(piecePosPercent, entitySkyLight, holderSkyLight);
        int packedLight = LightTexture.packLight(lerpBlockLight, lerpSkyLight);
        float knotColourMod = segment % 2 == (isLeashKnot ? 1 : 0) ? 0.7f : 1f;
        float red = 0.5f * knotColourMod;
        float green = 0.4f * knotColourMod;
        float blue = 0.3f * knotColourMod;
        float x = xDif * piecePosPercent;
        float y = yDif > 0.0f ? yDif * piecePosPercent * piecePosPercent : yDif - yDif * (1.0f - piecePosPercent) * (1.0f - piecePosPercent);
        float z = zDif * piecePosPercent;

        buffer.pos(positionMatrix, x - xOffset, y + yOffset, z + zOffset).color(red, green, blue, 1).lightmap(
                packedLight).endVertex();
        buffer.pos(positionMatrix, x + xOffset, y + width - yOffset, z - zOffset).color(red, green, blue, 1).lightmap(
                packedLight).endVertex();
    }

    /**
     * Gets the model instance for this renderer
     */
    @Override
    public GeoModel<T> getGeoModel() {
        return this.model;
    }

    /**
     * Gets the {@link GeoAnimatable} instance currently being rendered
     *
     * @see GeoReplacedEntityRenderer#getCurrentEntity()
     */
    @Override
    public T getAnimatable() {
        return this.animatable;
    }

    /**
     * Returns the current entity having its rendering replaced by this renderer
     *
     * @see GeoReplacedEntityRenderer#getAnimatable()
     */
    public E getCurrentEntity() {
        return this.currentEntity;
    }

    /**
     * Gets the id that represents the current animatable's instance for animation purposes. This is mostly useful for things like items, which have a single registered instance for all objects
     */
    @Override
    public long getInstanceId(T animatable) {
        return this.currentEntity.getEntityId();
    }
    
    /**
     * Shadowing override of {@link EntityRenderer#getEntityTexture(Entity)}.<br>
     * This redirects the call to {@link GeoRenderer#getTextureLocation}
     */
    @Override
    public ResourceLocation getTextureLocation(T animatable) {
        return GeoRenderer.super.getTextureLocation(animatable);
    }

    @Override
    public ResourceLocation getEntityTexture(E entity) {
        return GeoRenderer.super.getTextureLocation(animatable);
    }

    /**
     * Returns the list of registered {@link GeoRenderLayer GeoRenderLayers} for this renderer
     */
    @Override
    public List<GeoRenderLayer<T>> getRenderLayers() {
        return this.renderLayers.getRenderLayers();
    }

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoReplacedEntityRenderer<E, T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
        this.renderLayers.addLayer(renderLayer);

        return this;
    }

    /**
     * Sets a scale override for this renderer, telling AzureLib to pre-scale the model
     */
    public GeoReplacedEntityRenderer<E, T> withScale(float scale) {
        return withScale(scale, scale);
    }

    /**
     * Sets a scale override for this renderer, telling AzureLib to pre-scale the model
     */
    public GeoReplacedEntityRenderer<E, T> withScale(float scaleWidth, float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;

        return this;
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating.<br>
     * {@link MatrixStack} translations made here are kept until the end of the render process
     */
    @Override
    public void preRender(MatrixStack poseStack, T animatable, BakedGeoModel model, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.entityRenderTranslations = poseStack.getLast().getMatrix();

        scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick,
                packedLight, packedOverlay);
    }

    @Override
    public void render(E entity, float entityYaw, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight) {
        this.currentEntity = entity;

        defaultRender(poseStack, this.animatable, bufferSource, null, null, entityYaw, partialTick, packedLight);
    }

    /**
     * The actual render method that subtype renderers should override to handle their specific rendering tasks.<br>
     * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
     */
    @Override
    public void actuallyRender(MatrixStack poseStack, T animatable, BakedGeoModel model, RenderType renderType, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.push();

        LivingEntity livingEntity = this.currentEntity instanceof LivingEntity ? (LivingEntity) this.currentEntity : null;

        boolean shouldSit = this.currentEntity.isPassenger() && (this.currentEntity.getRidingEntity() != null && this.currentEntity.getRidingEntity().shouldRiderSit());
        float lerpBodyRot = livingEntity == null ? 0 : MathHelper.rotLerp(partialTick, livingEntity.prevRenderYawOffset,
                livingEntity.renderYawOffset);
        float lerpHeadRot = livingEntity == null ? 0 : MathHelper.rotLerp(partialTick, livingEntity.prevRotationYawHead,
                livingEntity.rotationYawHead);
        float netHeadYaw = lerpHeadRot - lerpBodyRot;

        if (shouldSit && this.currentEntity.getRidingEntity() instanceof LivingEntity) {
            lerpBodyRot = MathHelper.rotLerp(partialTick, ((LivingEntity) currentEntity.getRidingEntity()).prevRenderYawOffset,
                    ((LivingEntity) currentEntity.getRidingEntity()).renderYawOffset);
            netHeadYaw = lerpHeadRot - lerpBodyRot;
            float clampedHeadYaw = MathHelper.clamp(MathHelper.wrapDegrees(netHeadYaw), -85, 85);
            lerpBodyRot = lerpHeadRot - clampedHeadYaw;

            if (clampedHeadYaw * clampedHeadYaw > 2500f)
                lerpBodyRot += clampedHeadYaw * 0.2f;

            netHeadYaw = lerpHeadRot - lerpBodyRot;
        }

        if (this.currentEntity.getPose() == Pose.SLEEPING && livingEntity != null) {
            Direction bedDirection = livingEntity.getBedDirection();

            if (bedDirection != null) {
                float eyePosOffset = livingEntity.getEyeHeight(Pose.STANDING) - 0.1F;

                poseStack.translate(-bedDirection.getXOffset() * eyePosOffset, 0,
                        -bedDirection.getZOffset() * eyePosOffset);
            }
        }

        float nativeScale = livingEntity != null ? livingEntity.getRenderScale() : 1;
        float ageInTicks = this.currentEntity.ticksExisted + partialTick;
        float limbSwingAmount = 0;
        float limbSwing = 0;

        poseStack.scale(nativeScale, nativeScale, nativeScale);
        applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick, nativeScale);

        if (!shouldSit && this.currentEntity.isAlive() && livingEntity != null) {
            limbSwingAmount = MathHelper.lerp(partialTick, livingEntity.prevLimbSwingAmount, livingEntity.limbSwingAmount);
            limbSwing = livingEntity.limbSwing - livingEntity.limbSwingAmount * (1 - partialTick);

            if (livingEntity.isChild())
                limbSwing *= 3f;

            if (limbSwingAmount > 1f)
                limbSwingAmount = 1f;
        }

        float headPitch = MathHelper.lerp(partialTick, this.currentEntity.prevRotationPitch, this.currentEntity.rotationPitch);
        float motionThreshold = getMotionAnimThreshold(animatable);
        boolean isMoving;

        if (livingEntity != null) {
            Vec3d velocity = livingEntity.getMotion();
            float avgVelocity = (float) (Math.abs(velocity.x) + Math.abs(velocity.z)) / 2f;

            isMoving = avgVelocity >= motionThreshold && limbSwingAmount != 0;
        } else {
            isMoving = (limbSwingAmount <= -motionThreshold || limbSwingAmount >= motionThreshold);
        }

        if (!isReRender) {
            AnimationState<T> animationState = new AnimationState<T>(animatable, limbSwing, limbSwingAmount,
                    partialTick, isMoving);
            long instanceId = getInstanceId(animatable);

            animationState.setData(DataTickets.TICK, animatable.getTick(this.currentEntity));
            animationState.setData(DataTickets.ENTITY, this.currentEntity);
            animationState.setData(DataTickets.ENTITY_MODEL_DATA,
                    new EntityModelData(shouldSit, livingEntity != null && livingEntity.isChild(), -netHeadYaw,
                            -headPitch));
            this.model.addAdditionalStateData(animatable, instanceId, animationState::setData);
            this.model.handleAnimations(animatable, instanceId, animationState);
        }

        poseStack.translate(0, 0.01f, 0);
//		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));

        this.modelRenderTranslations = new Matrix4f(poseStack.getLast().getMatrix());

        if (!this.currentEntity.isInvisibleToPlayer(Minecraft.getInstance().player))
            GeoRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender,
                    partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.pop();
    }

    /**
     * Render the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer
     */
    @Override
    public void applyRenderLayers(MatrixStack poseStack, T animatable, BakedGeoModel model, RenderType renderType, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!this.currentEntity.isSpectator())
            GeoRenderer.super.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer,
                    partialTick, packedLight, packedOverlay);
    }

    /**
     * Call after all other rendering work has taken place, including reverting the {@link MatrixStack}'s state. This method is <u>not</u> called in {@link GeoRenderer#reRender re-render}
     */
    @Override
    public void renderFinal(MatrixStack poseStack, T animatable, BakedGeoModel model, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.render(this.currentEntity, 0, partialTick, poseStack, bufferSource, packedLight);

        if (this.currentEntity instanceof MobEntity) {
            Entity leashHolder = ((MobEntity) this.currentEntity).getLeashHolder();

            if (leashHolder != null)
                renderLeash(((MobEntity) this.currentEntity), partialTick, poseStack, bufferSource, leashHolder);
        }
    }

    /**
     * Renders the provided {@link GeoBone} and its associated child bones
     */
    @Override
    public void renderRecursively(MatrixStack poseStack, T animatable, GeoBone bone, RenderType renderType, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.push();
        RenderUtils.translateMatrixToBone(bone);
        RenderUtils.translateToPivotPoint(bone);
        RenderUtils.rotateMatrixAroundBone(bone);
        RenderUtils.scaleMatrixForBone(bone);

        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = poseStack.getLast().getMatrix().copy();
            Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);

            bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
            localMatrix.translate(new Vector3f(getRenderOffset(this.currentEntity, 1)));
            bone.setLocalSpaceMatrix(localMatrix);
            Matrix4f worldState = localMatrix.copy();

            worldState.translate(new Vector3f(this.currentEntity.getPositionVec()));
            bone.setWorldSpaceMatrix(worldState);
        }

        RenderUtils.translateAwayFromPivotPoint(bone);

        renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        if (!isReRender) {
            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick,
                    packedLight, packedOverlay);
            if (buffer instanceof BufferBuilder && !((BufferBuilder) buffer).isDrawing)
                buffer = bufferSource.getBuffer(renderType);
        }

        renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.pop();
    }

    /**
     * Applies rotation transformations to the renderer prior to render time to account for various entity states, default scale of 1
     */
    protected void applyRotations(T animatable, MatrixStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, 1);
    }

    /**
     * Applies rotation transformations to the renderer prior to render time to account for various entity states, scalable
     */
    protected void applyRotations(T animatable, MatrixStack poseStack, float ageInTicks, float rotationYaw,
                                  float partialTick, float nativeScale) {
        if (isShaking(animatable))
            rotationYaw += (float)(Math.cos(this.currentEntity.ticksExisted * 3.25d) * Math.PI * 0.4d);

        if (this.currentEntity.getPose() != Pose.SLEEPING)
            poseStack.rotate(Vector3f.YP.rotationDegrees(180f - rotationYaw));

        if (this.currentEntity instanceof LivingEntity) {
            if (((LivingEntity) this.currentEntity).deathTime > 0) {
                float deathRotation = (((LivingEntity) this.currentEntity).deathTime + partialTick - 1f) / 20f * 1.6f;

                poseStack.rotate(Vector3f.ZP.rotationDegrees(Math.min(MathHelper.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable)));
            }
            else if (((LivingEntity) this.currentEntity).isSpinAttacking()) {
                poseStack.rotate(Vector3f.XP.rotationDegrees(-90f - ((LivingEntity) this.currentEntity).rotationPitch));
                poseStack.rotate(Vector3f.YP.rotationDegrees((((LivingEntity) this.currentEntity).ticksExisted + partialTick) * -75f));
            }
            else if (this.currentEntity.getPose() == Pose.SLEEPING) {
                Direction bedOrientation = ((LivingEntity) this.currentEntity).getBedDirection();

                poseStack.rotate(Vector3f.YP.rotationDegrees(bedOrientation != null ? RenderUtils.getDirectionAngle(bedOrientation) : rotationYaw));
                poseStack.rotate(Vector3f.ZP.rotationDegrees(getDeathMaxRotation(animatable)));
                poseStack.rotate(Vector3f.YP.rotationDegrees(270f));
            }
            else if (isEntityUpsideDown(((LivingEntity) this.currentEntity))) {
                poseStack.translate(0, (this.currentEntity.getHeight() + 0.1f) / nativeScale, 0);
                poseStack.rotate(Vector3f.ZP.rotationDegrees(180f));
            }
        }
    }

    public static boolean isEntityUpsideDown(LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity || livingEntity.hasCustomName()) {
            String s = TextFormatting.getTextWithoutFormattingCodes(livingEntity.getName().getString());
            if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
                return !(livingEntity instanceof PlayerEntity) || ((PlayerEntity)livingEntity).isWearing(PlayerModelPart.CAPE);
            }
        }
        return false;
    }

    /**
     * Gets the max rotation value for dying entities.<br>
     * You might want to modify this for different aesthetics, such as a {@link net.minecraft.entity.monster.SpiderEntity Spider} flipping upside down on death.<br>
     * Functionally equivalent to {@link LivingRenderer#getDeathMaxRotation LivingEntityRenderer.getFlipDegrees}
     */
    protected float getDeathMaxRotation(T animatable) {
        return 90f;
    }

    /**
     * Whether the entity's nametag should be rendered or not.<br>
     * Pretty much exclusively used in {@link EntityRenderer#renderName}
     */
    @Override
    public boolean canRenderName(E entity) {
        if (!(entity instanceof LivingEntity))
            return super.canRenderName(entity);

        double nameRenderCutoff = entity.isDiscrete() ? 32d : 64d;

        if (this.renderManager.squareDistanceTo(entity) >= nameRenderCutoff * nameRenderCutoff)
            return false;

        if (entity instanceof MobEntity && (!entity.getAlwaysRenderNameTagForRender() && (!entity.hasCustomName() || entity != this.renderManager.pointedEntity)))
            return false;

        final Minecraft minecraft = Minecraft.getInstance();
        boolean visibleToClient = !entity.isInvisibleToPlayer(minecraft.player);
        Team entityTeam = entity.getTeam();

        if (entityTeam == null)
            return Minecraft.isGuiEnabled() && entity != minecraft.getRenderViewEntity() && visibleToClient && !entity.isBeingRidden();

        Team playerTeam = minecraft.player.getTeam();

        Visible tagVisibiility = entityTeam.getNameTagVisibility();

        return tagVisibiility == Visible.ALWAYS ? visibleToClient : tagVisibiility == Visible.HIDE_FOR_OTHER_TEAMS ? playerTeam == null ? visibleToClient : entityTeam.isSameTeam(
                playerTeam) && (entityTeam.getSeeFriendlyInvisiblesEnabled() || visibleToClient) : tagVisibiility == Visible.HIDE_FOR_OWN_TEAM && (playerTeam == null ? visibleToClient : !entityTeam.isSameTeam(
                playerTeam) && visibleToClient);

    }

    /**
     * Gets a packed overlay coordinate pair for rendering.<br>
     * Mostly just used for the red tint when an entity is hurt, but can be used for other things like the {@link CreeperEntity} white tint when exploding.
     */
    @Override
    public int getPackedOverlay(T animatable, float u) {
        if (!(this.currentEntity instanceof LivingEntity))
            return OverlayTexture.NO_OVERLAY;

        return OverlayTexture.getPackedUV(OverlayTexture.getU(u), OverlayTexture.getV(
                ((LivingEntity) this.currentEntity).hurtTime > 0 || ((LivingEntity) this.currentEntity).deathTime > 0));
    }

    /**
     * Gets a packed overlay coordinate pair for rendering.<br>
     * Mostly just used for the red tint when an entity is hurt, but can be used for other things like the {@link net.minecraft.entity.monster.CreeperEntity} white tint when exploding.
     */
    @Override
    public int getPackedOverlay(T animatable, float u, float partialTick) {
        return getPackedOverlay(animatable, u);
    }

    /**
     * Static rendering code for rendering a leash segment.<br>
     * It's a like-for-like from {@link net.minecraft.client.renderer.entity.MobRenderer#renderLeash} that had to be duplicated here for flexible usage
     */
    public <H extends Entity, M extends MobEntity> void renderLeash(M mob, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, H leashHolder) {
        poseStack.push();
        double d0 = MathHelper.lerp(partialTick * 0.5F, leashHolder.rotationYaw, leashHolder.prevRotationYaw) * ((float)Math.PI / 180F);
        double d1 = MathHelper.lerp(partialTick * 0.5F, leashHolder.rotationPitch, leashHolder.prevRotationPitch) * ((float)Math.PI / 180F);
        double d2 = Math.cos(d0);
        double d3 = Math.sin(d0);
        double d4 = Math.sin(d1);
        if (leashHolder instanceof HangingEntity) {
            d2 = 0.0D;
            d3 = 0.0D;
            d4 = -1.0D;
        }

        double d5 = Math.cos(d1);
        double d6 = MathHelper.lerp(partialTick, leashHolder.prevPosX, leashHolder.getPosX()) - d2 * 0.7D - d3 * 0.5D * d5;
        double d7 = MathHelper.lerp(partialTick, leashHolder.prevPosY + leashHolder.getEyeHeight() * 0.7D, leashHolder.getPosY() + leashHolder.getEyeHeight() * 0.7D) - d4 * 0.5D - 0.25D;
        double d8 = MathHelper.lerp(partialTick, leashHolder.prevPosZ, leashHolder.getPosZ()) - d3 * 0.7D + d2 * 0.5D * d5;
        double d9 = (MathHelper.lerp(partialTick, mob.renderYawOffset, mob.prevRenderYawOffset) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
        d2 = Math.cos(d9) * mob.getWidth() * 0.4D;
        d3 = Math.sin(d9) * mob.getWidth() * 0.4D;
        double d10 = MathHelper.lerp(partialTick, mob.prevPosX, mob.getPosX()) + d2;
        double d11 = MathHelper.lerp(partialTick, mob.prevPosY, mob.getPosY());
        double d12 = MathHelper.lerp(partialTick, mob.prevPosZ, mob.getPosZ()) + d3;
        poseStack.translate(d2, -(1.6D - mob.getHeight()) * 0.5D, d3);
        float f = (float)(d6 - d10);
        float f1 = (float)(d7 - d11);
        float f2 = (float)(d8 - d12);
        IVertexBuilder ivertexbuilder = bufferSource.getBuffer(RenderType.getLeash());
        Matrix4f matrix4f = poseStack.getLast().getMatrix();
        float f4 = MathHelper.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
        float f5 = f2 * f4;
        float f6 = f * f4;
        int i = this.getBlockLight(this.currentEntity, partialTick);
        int j = this.getBlockLight(leashHolder, partialTick);
        int k = mob.world.getLightFor(LightType.SKY, new BlockPos(mob.getEyePosition(partialTick)));
        int l = mob.world.getLightFor(LightType.SKY, new BlockPos(leashHolder.getEyePosition(partialTick)));

        for (int segment = 0; segment <= 24; ++segment) {
            GeoReplacedEntityRenderer.renderLeashPiece(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, segment,false);
        }

        for (int segment = 24; segment >= 0; --segment) {
            GeoReplacedEntityRenderer.renderLeashPiece(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, segment,true);
        }

        poseStack.pop();
    }

    protected int getBlockLight(Entity entityIn, float partialTicks) {
        return entityIn.isBurning() ? 15 : entityIn.world.getLightFor(LightType.BLOCK, new BlockPos(entityIn.getEyePosition(partialTicks)));
    }

    public boolean isShaking(T entity) {
        return false;
    }
}
