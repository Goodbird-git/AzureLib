package mod.azure.azurelib.fabric.core2.example;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class Drone extends Monster {

    public Drone(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }
}
