package mod.azure.azurelib.fabric.core2.example.entities.marauder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.api.common.ai.pathing.AzureNavigation;
import mod.azure.azurelib.core2.animation.AzAnimationDispatcher;
import mod.azure.azurelib.fabric.core2.example.MoveAnalysis;

public class MarauderEntity extends Monster {

    private final AzAnimationDispatcher animationDispatcher;

    private final MoveAnalysis moveAnalysis;

    public MarauderEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new AzAnimationDispatcher(this);
        this.moveAnalysis = new MoveAnalysis(this);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new AzureNavigation(this, level);
    }

    @Override
    public float maxUpStep() {
        return 2.0F;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 80 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void tick() {
        super.tick();
        moveAnalysis.update();

        if (this.level().isClientSide) {
            var isMovingOnGround = moveAnalysis.isMovingHorizontally() && onGround();
            String animName;
            if (!this.isAlive()) {
                animName = "death";
            } else if (this.tickCount < 300) {
                animName = "spawn";
            } else if (isMovingOnGround) {
                animName = this.isAggressive() ? "run" : "walk";
            } else {
                animName = "idle";
            }
            animationDispatcher.dispatchFromClient("base_controller", animName);
        } else {
            if (this.tickCount < 300 && this.isAlive()) {
                if (this.getNavigation() instanceof AzureNavigation azureNavigation) {
                    azureNavigation.hardStop();
                    azureNavigation.stop();
                }
                this.setYBodyRot(0);
                this.setYHeadRot(0);
                this.getEyePosition(90);
                this.setXRot(0);
                this.setYRot(0);
            }
        }
    }

    /**
     * TODO: Get longer Melee animations working
     */
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.2F));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.6F, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {}
}
