package mod.azure.azurelib.fabric.core2.example.entities.doomhunter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.api.common.ai.pathing.AzureNavigation;

public class DoomHunter extends Monster {

    private final DoomHunterAnimationDispatcher animationDispatcher;

    public DoomHunter(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new DoomHunterAnimationDispatcher(this);
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
    public void tick() {
        super.tick();

        if (this.level().isClientSide && !this.isAggressive()) {
            animationDispatcher.chainsaw();
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.6F, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(DoomHunter.class));
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {}
}
