package mod.azure.azurelib.core2.animation.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import mod.azure.azurelib.common.api.common.animatable.GeoBlockEntity;
import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.core2.animation.AzAnimationState;
import mod.azure.azurelib.core2.animation.AzAnimator;

public abstract class AzEntityAnimator<T extends Entity> extends AzAnimator<T> {

    public AzAnimationState<T> createAnimationState(
        T animatable,
        float limbSwing,
        float limbSwingAmount,
        float partialTick
    ) {
        var velocity = animatable.getDeltaMovement();
        var avgVelocity = (float) (Math.abs(velocity.x) + Math.abs(velocity.z) / 2f);
        var motionThreshold = getMotionAnimThreshold(animatable);

        return new AzAnimationState<>(
            animatable,
            limbSwing,
            limbSwingAmount,
            partialTick,
            avgVelocity >= motionThreshold && limbSwingAmount != 0
        );
    }

    @Override
    protected void applyMolangQueries(T entity, double animTime) {
        super.applyMolangQueries(entity, animTime);

        var parser = MolangParser.INSTANCE;
        var minecraft = Minecraft.getInstance();

        parser.setMemoizedValue(
            MolangQueries.DISTANCE_FROM_CAMERA,
            () -> minecraft.gameRenderer.getMainCamera().getPosition().distanceTo(entity.position())
        );
        parser.setMemoizedValue(MolangQueries.IS_ON_GROUND, () -> RenderUtils.booleanToFloat(entity.onGround()));
        parser.setMemoizedValue(MolangQueries.IS_IN_WATER, () -> RenderUtils.booleanToFloat(entity.isInWater()));
        parser.setMemoizedValue(
            MolangQueries.IS_IN_WATER_OR_RAIN,
            () -> RenderUtils.booleanToFloat(entity.isInWaterOrRain())
        );
        parser.setMemoizedValue(MolangQueries.IS_ON_FIRE, () -> RenderUtils.booleanToFloat(entity.isOnFire()));

        if (entity instanceof LivingEntity livingEntity) {
            parser.setMemoizedValue(MolangQueries.HEALTH, livingEntity::getHealth);
            parser.setMemoizedValue(MolangQueries.MAX_HEALTH, livingEntity::getMaxHealth);
            parser.setMemoizedValue(MolangQueries.GROUND_SPEED, () -> {
                var velocity = livingEntity.getDeltaMovement();
                return Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
            });
            parser.setMemoizedValue(MolangQueries.YAW_SPEED, () -> livingEntity.getYRot() - livingEntity.yRotO);
        }
    }

    /**
     * Determines the threshold value before the animatable should be considered moving for animation purposes.<br>
     * The default value and usage for this varies depending on the renderer.<br>
     * <ul>
     * <li>For entities, it represents the averaged lateral velocity of the object.</li>
     * <li>For {@link GeoBlockEntity Tile Entities} and {@link GeoItem Items}, it's currently unused</li>
     * </ul>
     * The lower the value, the more sensitive the {@link AzAnimationState#isMoving()} check will be.<br>
     * Particularly low values may have adverse effects however
     */
    protected float getMotionAnimThreshold(T animatable) {
        return 0.015f;
    }
}
