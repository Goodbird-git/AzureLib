package mod.azure.azurelib.fabric.core2.example;

import mod.azure.azurelib.core2.animation.AzAnimationDispatcher;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class Facehugger extends Monster {

    private final AzAnimationDispatcher<Facehugger> animationDispatcher;

    public Facehugger(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new AzAnimationDispatcher<>(this);
    }

    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            animationDispatcher.dispatchFromClient("base_controller", "animation.run");
        }
    }
}
