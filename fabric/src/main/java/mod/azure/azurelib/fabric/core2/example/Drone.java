package mod.azure.azurelib.fabric.core2.example;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import mod.azure.azurelib.core2.animation.AzAnimationDispatcher;

public class Drone extends Monster {

    private final AzAnimationDispatcher<Drone> animationDispatcher;
    private final MoveAnalysis moveAnalysis;

    public Drone(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new AzAnimationDispatcher<>(this);
        this.moveAnalysis = new MoveAnalysis(this);
    }

    public void tick() {
        super.tick();
        moveAnalysis.update();

        if (this.level().isClientSide) {
            var isMovingOnGround = moveAnalysis.isMovingHorizontally() && onGround();
            var animName = isMovingOnGround
                    ? "animation.walk"
                    : "animation.idle";
            animationDispatcher.dispatchFromClient("base_controller", animName);
        } else {
            // Doing other stuff server-side...
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.25F));
    }
}
