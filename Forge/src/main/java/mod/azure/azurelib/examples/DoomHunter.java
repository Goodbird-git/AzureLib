package mod.azure.azurelib.examples;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

public class DoomHunter extends EntityMob {

    private final DoomHunterAnimationDispatcher animationDispatcher;

    public DoomHunter(World level) {
        super(level);
        this.animationDispatcher = new DoomHunterAnimationDispatcher(this);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote) {
            animationDispatcher.chainsaw();
        }
    }
}
