package mod.azure.azurelib.animation.impl;

import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.AzAnimatorConfig;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

/**
 * The {@code AzEntityAnimator} class extends {@link AzAnimator} to provide specialized animation management for
 * entities. This abstract class is designed to handle various animation-related requirements for entities in a game
 * framework, including the application of MoLang queries specific to entity-related properties such as position,
 * health, and motion state.
 *
 * @param <T> The type of entity this animator is designed to manage.
 */
public abstract class AzEntityAnimator<T extends Entity> extends AzAnimator<T> {

    protected AzEntityAnimator() {
        super();
    }

    protected AzEntityAnimator(AzAnimatorConfig config) {
        super(config);
    }

    /**
     * Applies MoLang queries specific to an entity in the animation system. These queries provide contextual
     * information about the entity's state and environment, such as its position, health, movement, and interaction
     * with the world. The method extends the baseline queries defined in the superclass with additional entity-specific
     * properties, particularly for living entities.
     *
     * @param entity   The entity for which the MoLang queries are being applied.
     * @param animTime The current animation time, in seconds, used for time-dependent queries.
     */
    @Override
    protected void applyMolangQueries(T entity, double animTime) {
        super.applyMolangQueries(entity, animTime);

        MolangParser parser = MolangParser.INSTANCE;
        Minecraft minecraft = Minecraft.getMinecraft();

        parser.setMemoizedValue(
            MolangQueries.DISTANCE_FROM_CAMERA,
            () -> ActiveRenderInfo.getCameraPosition().distanceTo(entity.getPositionVector())
        );
        parser.setMemoizedValue(MolangQueries.IS_ON_GROUND, () -> RenderUtils.booleanToFloat(entity.onGround));
        parser.setMemoizedValue(MolangQueries.IS_IN_WATER, () -> RenderUtils.booleanToFloat(entity.isInWater()));
        parser.setMemoizedValue(
            MolangQueries.IS_IN_WATER_OR_RAIN,
            () -> RenderUtils.booleanToFloat(entity.isInWater())
        );
        parser.setMemoizedValue(MolangQueries.IS_ON_FIRE, () -> RenderUtils.booleanToFloat(entity.canRenderOnFire()));

        if (entity instanceof EntityLiving) {
            parser.setMemoizedValue(MolangQueries.HEALTH, ((EntityLiving) entity)::getHealth);
            parser.setMemoizedValue(MolangQueries.MAX_HEALTH, ((EntityLiving) entity)::getMaxHealth);
            double dx = entity.motionX;
            double dz = entity.motionZ;
            parser.setMemoizedValue(MolangQueries.GROUND_SPEED, () -> MathHelper.sqrt((dx * dx) + (dz * dz)));
            parser.setMemoizedValue(MolangQueries.YAW_SPEED, () -> entity.rotationYaw - entity.prevRotationYaw);
        }
    }
}
