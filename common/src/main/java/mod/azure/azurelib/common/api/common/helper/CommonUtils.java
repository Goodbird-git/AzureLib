package mod.azure.azurelib.common.api.common.helper;

import mod.azure.azurelib.common.internal.common.blocks.TickingLightEntity;
import mod.azure.azurelib.common.internal.common.registry.AzureBlocksRegistry;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelib.common.platform.Services;
import mod.azure.azurelib.common.platform.services.IPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record CommonUtils() {

    /**
     * Summons an Area of Effect Cloud with the set particle, y offset, radius, duration, and effect options.
     *
     * @param entity     The Entity summoning the AoE
     * @param particle   Sets the Particle
     * @param yOffset    Set the yOffset if wanted
     * @param duration   Sets the duration of the AoE
     * @param radius     Sets the radius of the AoE
     * @param hasEffect  Should this have an effect?
     * @param effect     If it should effect, what effect?
     * @param effectTime How long the effect should be applied for?
     */
    public static void summonAoE(
            LivingEntity entity,
            ParticleOptions particle,
            int yOffset,
            int duration,
            float radius,
            boolean hasEffect,
            @Nullable Holder<MobEffect> effect,
            int effectTime
    ) {
        var areaEffectCloudEntity = new AreaEffectCloud(
                entity.level(),
                entity.getX(),
                entity.getY() + yOffset,
                entity.getZ()
        );
        areaEffectCloudEntity.setRadius(radius);
        areaEffectCloudEntity.setDuration(duration);
        areaEffectCloudEntity.setParticle(particle);
        areaEffectCloudEntity.setRadiusPerTick(
                -areaEffectCloudEntity.getRadius() / areaEffectCloudEntity.getDuration()
        );
        if (hasEffect && effect != null && !entity.hasEffect(effect))
            areaEffectCloudEntity.addEffect(new MobEffectInstance(effect, effectTime, 0));
        entity.level().addFreshEntity(areaEffectCloudEntity);
    }

    /**
     * Spawns or refreshes a light source at the position of the firing entity. This method should
     * only be called on the server side to place a temporary light-block.
     *
     * @param entity         The entity (e.g., player or mob) using the weapon.
     * @param isInWaterBlock If true, the light-block will refresh faster when in water.
     */
    public static void spawnLightSource(Entity entity, boolean isInWaterBlock) {
        BlockPos lightBlockPos = AzureLibUtil.findFreeSpace(entity.level(), entity.blockPosition(), 2);

        // If no valid position for the light block is found, exit early
        if (lightBlockPos == null) {
            return;
        }

        // Check if there's already a ticking light block at the position and refresh it if needed
        if (entity.level().getBlockEntity(lightBlockPos) instanceof TickingLightEntity tickingLightEntity) {
            tickingLightEntity.refresh(isInWaterBlock ? 20 : 0);
        } else {
            // Otherwise, place a new ticking light block
            entity.level().setBlockAndUpdate(lightBlockPos, AzureBlocksRegistry.TICKING_LIGHT_BLOCK.get().defaultBlockState());
        }
    }

}
