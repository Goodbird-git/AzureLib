package mod.azure.azurelib.core2.animation.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorConfig;

public abstract class AzEntityAnimator<T extends Entity> extends AzAnimator<T> {

    protected AzEntityAnimator(AzAnimatorConfig config) {
        super(config);
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
}
