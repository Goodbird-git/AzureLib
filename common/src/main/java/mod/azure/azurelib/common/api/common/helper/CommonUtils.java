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
     * Call wherever you are firing weapon to place the half tick light-block, making sure do so only on the server.
     *
     * @param entity         Usually the player or mob that is using the weapon
     * @param isInWaterBlock Checks if it's in a water block to refresh faster.
     */
    public static void spawnLightSource(Entity entity, boolean isInWaterBlock) {
        BlockPos lightBlockPos = null;
        if (lightBlockPos == null) {
            lightBlockPos = AzureLibUtil.findFreeSpace(entity.level(), entity.blockPosition(), 2);
            if (lightBlockPos == null)
                return;
            entity.level()
                    .setBlockAndUpdate(
                            lightBlockPos,
                            AzureBlocksRegistry.TICKING_LIGHT_BLOCK.get().defaultBlockState()
                    );
        } else if (
                AzureLibUtil.checkDistance(
                        lightBlockPos,
                        entity.blockPosition(),
                        2
                ) && entity.level().getBlockEntity(lightBlockPos) instanceof TickingLightEntity tickingLightEntity
        ) {
            tickingLightEntity.refresh(isInWaterBlock ? 20 : 0);
        }
    }
}
