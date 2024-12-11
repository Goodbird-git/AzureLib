package mod.azure.azurelib.fabric.core2.example;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import mod.azure.azurelib.core2.animation.AzAnimationDispatcher;

public class Drone extends Monster {

    private final AzAnimationDispatcher<Drone> animationDispatcher;

    public Drone(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new AzAnimationDispatcher<>(this);
    }

    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            animationDispatcher.dispatchFromClient("base_controller", "animation.idle");
        }
    }
}
